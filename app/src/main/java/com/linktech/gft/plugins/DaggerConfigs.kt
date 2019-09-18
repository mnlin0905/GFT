package com.linktech.gft.plugins

import android.os.Build
import android.view.ViewGroup
import com.blankj.utilcode.util.DeviceUtils
import com.facebook.stetho.okhttp3.StethoInterceptor
import com.google.gson.GsonBuilder
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import com.linktech.gft.R
import com.linktech.gft.activity.financing.common.*
import com.linktech.gft.activity.financing.establish.*
import com.linktech.gft.activity.financing.market.*
import com.linktech.gft.activity.financing.person.*
import com.linktech.gft.activity.login.*
import com.linktech.gft.activity.other.ChooseCountryActivity
import com.linktech.gft.activity.other.CommonAgreementActivity
import com.linktech.gft.activity.other.SwitchLocaleActivity
import com.linktech.gft.activity.person.*
import com.linktech.gft.activity.safety.*
import com.linktech.gft.base.BaseActivity
import com.linktech.gft.base.BaseApplication
import com.linktech.gft.base.BaseFragment
import com.linktech.gft.base.BaseView
import com.linktech.gft.fragment.financing.*
import com.linktech.gft.retrofit.HttpInterface
import com.linktech.gft.util.L
import com.linktech.gft.util.ListTypeAdapterFactory
import com.linktech.gft.window.BigToast
import com.linktech.gft.wxapi.BindWXRegisterActivity
import com.linktech.gft.wxapi.WXEntryActivity
import com.umeng.analytics.MobclickAgent
import dagger.Component
import dagger.Module
import dagger.Provides
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import okio.Buffer
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.lang.reflect.Method
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.text.NumberFormat
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.logging.Logger
import javax.inject.Scope
import javax.inject.Singleton
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.X509TrustManager
import kotlin.collections.HashMap

/**************************************
 * function : dagger配置文件
 *
 * Created on 2018/6/27  11:53
 * @author mnlin
 **************************************/

/**
 * 全局缓存反射的对象,用于保证性能
 */
private val _dagger_invoke_methods_map = WeakHashMap<String, Method>()

/**
 * 定义统一管理，父类实现子类方法,activity自动注入
 */
fun autoInjectBaseView(view: BaseView, component: DaggerComponent) {
    view.javaClass.name.let { className ->
        _dagger_invoke_methods_map[className].run {
            this ?: try {
                //如果未缓存,则进行一次缓存
                component.javaClass.getMethod("inject", view.javaClass).also { _dagger_invoke_methods_map[className] = it }
            } catch (e: Exception) {
                empty(TODO = getString(R.string.plugins_dagger_no_register, view.javaClass.name))
                null
            }
        }?.run {
            invoke(component, view)
        }
    }
}

//////////////////////////////////////////
///////////////////////////// dagger组件化
//////////////////////////////////////////

/**
 * 功能----activity组件,提供清单文件
 *
 * Created by LinkTech on 2017/9/22.
 */
@PerActivity
@Component(modules = [(ActivityModule::class)], dependencies = [(ApplicationComponent::class)])
interface ActivityComponent : DaggerComponent {
    fun inject(activity: LoginActivity)
    fun inject(activity: RegisterActivity)
    fun inject(activity: SoftSettingActivity)
    fun inject(activity: PersonInformationActivity)
    fun inject(activity: ChangeNickNameActivity)
    fun inject(activity: BeginCertificationActivity)
    fun inject(activity: UploadCertificationPhotoActivity)
    fun inject(activity: ShowCertificationResultActivity)
    fun inject(activity: ForgetPasswordActivity)
    fun inject(activity: FindPasswordActivity)
    fun inject(activity: ChangePasswordActivity)
    fun inject(activity: SetTransactionPasswordActivity)
    fun inject(activity: BindEmailActivity)
    fun inject(activity: BindMobileActivity)
    fun inject(activity: ChangeEmailActivity)
    fun inject(activity: ChangeMobileActivity)
    fun inject(activity: InputCodeActivity)
    fun inject(activity: VerifyCardNumActivity)
    fun inject(activity: ShowEmailActivity)
    fun inject(activity: ShowMobileActivity)
    fun inject(activity: AboutActivity)
    fun inject(activity: SignActivity)
    fun inject(activity: SignRewardActivity)
    fun inject(activity: InputOldTransPasswordActivity)
    fun inject(activity: ChangeTransPasswordActivity)
    fun inject(activity: SelectChangeTransPasswordTypeActivity)
    fun inject(activity: RecommendRecordActivity)
    fun inject(activity: ShowTransResultActivity)
    fun inject(activity: ChangeTransSuccessActivity)
    fun inject(activity: CommonAgreementActivity)
    fun inject(activity: InviteRewardActivity)
    fun inject(activity: ShowInviteCodeActivity)
    fun inject(activity: ChooseCountryActivity)
    fun inject(activity: FinancingActivity)
    fun inject(activity: DealActivity)
    fun inject(recodeActivity: StockWarnRecodeActivity)
    fun inject(recodeActivity: StockWarnActivity)
    fun inject(recodeActivity: RemindFrequencyActivity)
    fun inject(activity: KLineActivity)
    fun inject(activity: BuySellActivity)
    fun inject(activity: TradeActivity)
    fun inject(activity: ChooseTradeActivity)
    fun inject(activity: LoginTradeActivity)
    fun inject(activity: NoDataActivity)
    fun inject(activity: AboutInnerActivity)
    fun inject(activity: MessageAlertActivity)
    fun inject(activity: NewsActivity)
    fun inject(activity: RefreshRateActivity)
    fun inject(activity: SafetyLockActivity)
    fun inject(activity: SettingActivity)
    fun inject(activity: SwitchColorActivity)
    fun inject(activity: SwitchLocaleActivity)
    fun inject(activity: SwitchModeActivity)
    fun inject(activity: AddSharesActivity)
    fun inject(activity: OrderChatActivity)
    fun inject(activity: MoneyCenterActivity)
    fun inject(activity: SwitchLocaleInnerActivity)
    fun inject(activity: CommonAgreementInnerActivity)
    fun inject(activity: FeedbackActivity)
    fun inject(activity: MyCollectionActivity)
    fun inject(activity: SafetyProtectActivity)
    fun inject(activity: ResetPasswordActivity)
    fun inject(activity: RegisterSuccessActivity)
    fun inject(activity: FaceLockActivity)
    fun inject(activity: FingerprintLockActivity)
    fun inject(activity: ScreenLockActivity)
    fun inject(activity: GestureSettingActivity)
    fun inject(activity: GestureLockActivity)
    fun inject(activity: KLineLandActivity)
    fun inject(activity: KLineExponentActivity)
    fun inject(activity: TurbineActivity)
    fun inject(activity: BullBearSyndromeActivity)
    fun inject(activity: FinancableStockActivity)
    fun inject(activity: AllMarketStocksActivity)
    fun inject(activity: ConceptualPlateActivity)
    fun inject(activity: IndustrySectorActivity)
    fun inject(activity: IndustryPartActivity)
    fun inject(activity: SectionChangeRateActivity)
    fun inject(activity: KLineComponentActivity)
    fun inject(activity: NewStockActivity)
    fun inject(activity: KLineLandExponentActivity)
    fun inject(activity: GoldenActivity)
    fun inject(activity: WithdrawActivity)
    fun inject(activity: WXEntryActivity)

    fun inject(activity: AgreementDeclareActivity)
    fun inject(activity: CommitInfoBasicActivity)
    fun inject(activity: CommitInfoMoreActivity)
    fun inject(activity: FinancialPositionActivity)
    fun inject(activity: InvestmentExperienceActivity)
    fun inject(activity: OpenPreparationActivity)
    fun inject(activity: RiskDisclosureActivity)
    fun inject(activity: SelectAccountActivity)
    fun inject(activity: SignatureAgreementActivity)
    fun inject(activity: UploadIdCardActivity)
    fun inject(activity: VerifyBankCardActivity)
    fun inject(activity: VerifyPasswordActivity)
    fun inject(activity: UploadPhotoActivity)
    fun inject(activity: SignatureNameActivity)
    fun inject(activity: SignatureNameConfirmActivity)
    fun inject(activity: EstablishFinishActivity)
    fun inject(activity: BindWXRegisterActivity)
}

/**
 * 功能----应用的组件
 *
 * Created by LinkTech on 2017/9/22.
 */
@Singleton
@Component(modules = [(ApplicationModule::class)])
interface ApplicationComponent : DaggerComponent {
    fun inject(application: BaseApplication)

    fun initHttpInterface(): HttpInterface

    fun initBigToast(): BigToast
}

/**
 * 功能----碎片组件,用于注入dagger
 *
 * Created by LinkTech on 2017/9/23.
 */
@PerFragment
@Component(modules = [(FragmentModule::class)], dependencies = [(ApplicationComponent::class)])
interface FragmentComponent : DaggerComponent {
    fun inject(fragment: MarketFragment)
    fun inject(fragment: MarketItemFragment)
    fun inject(fragment: InformationFragment)
    fun inject(fragment: InformationItemFragment)
    fun inject(fragment: PersonFragment)
    fun inject(fragment: ExchangeFragment)
    fun inject(fragment: AssetsFragment)
    fun inject(fragment: TradeDetailFragment)
    fun inject(fragment: KillOrderFragment)
    fun inject(fragment: EntrustSearchFragment)
    fun inject(fragment: DealFragment)
    fun inject(fragment: KNewsItemFragment)
    fun inject(fragment: KF10ItemFragment)
    fun inject(fragment: KLineComponentFragment)
}

/**
 * 公共的component接口,用于反射注入数据
 */
interface DaggerComponent

//////////////////////////////////////////////////
////////////////////////////////// module内容模块
/////////////////////////////////////////////////

/**
 * 功能----Application的module,为ApplicationComponent提供对象生成器
 * Created by LinkTech on 2017/9/22
 */
@Singleton
@Module
class ApplicationModule(private val application: BaseApplication) {

    /**
     * 全局唯一的toast
     */
    @Provides
    @Singleton
    internal fun provideBigToast(): BigToast {
        return BigToast.getInstance()
    }

    @Provides
    @Singleton
    internal fun provideHttpInterface(): HttpInterface {
        //网络请求的Host
        val baseUrl = BaseApplication.app.baseWeUrl

        // 设备唯一id
        val deviceId = "${DeviceUtils.getAndroidID()}${DeviceUtils.getMacAddress()}".toMD5()
                ?: "Default"

        //生成JSON转换的库
        val gson = GsonBuilder()
                .serializeNulls()
                .registerTypeAdapterFactory(ListTypeAdapterFactory())//对空列表处理
                .setDateFormat("yyyy:MM:dd HH:mm:ss")
                .registerTypeAdapter(String::class.java, ZeroDeleteAdapter())//0.00值处理
                .create()
        val gsonConverterFactory = GsonConverterFactory.create(gson)

        //生成RxJava转换的adapter
        val rxJava2CallAdapterFactory = RxJava2CallAdapterFactory.create()
        // RxJavaCallAdapterFactory rxJavaCallAdapterFactory=RxJavaCallAdapterFactory.create();

        //生成OkHttp网络传输的客户端
        val cookieStore = HashMap<String, List<Cookie>>()
        val okHttpClient = OkHttpClient.Builder()
                //cookie
                .cookieJar(object : CookieJar {
                    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
                        cookieStore[url.host()] = cookies
                    }

                    override fun loadForRequest(url: HttpUrl): List<Cookie> {
                        val cookies = cookieStore[url.host()]
                        return cookies ?: ArrayList()
                    }
                })
                //header
                .addInterceptor { chain ->
                    val request = chain.request()
                            .newBuilder()
                            .addHeader("mining-channel", "liantongshuzi")
                            .addHeader("SDK", Build.VERSION.SDK_INT.toString() + "")
                            .build()
                    chain.proceed(request)
                }
                //全局 map 参数
                .addInterceptor { chain ->
                    //content_type类型
                    val contentType = "application/x-www-form-urlencoded"
                    chain.proceed(chain.request().run {
                        //如果为表单形式,则默认添加一个client字段
                        contentType.equals(body()?.contentType().toString(), ignoreCase = true).kindAnyReturn({ this }) {
                            FormBody.Builder()
                                    .add("client", "1")
                                    .add("macId", deviceId)
                                    .build()
                                    .let { bodyToString(it) + "&" + bodyToString(body()) }
                                    .let { newBuilder().post(RequestBody.create(MediaType.parse("$contentType;charset=UTF-8"), it)).build() }
                        }
                    })
                }
                // 通过不同header判断,重写不同url
                .addInterceptor { chain ->
                    chain.proceed(chain.request().also {
                        val changeUrl = { replaceUrl: String ->
                            HttpUrl.parse(replaceUrl)?.apply {
                                val host = HttpUrl::class.java.getDeclaredField("host")
                                val port = HttpUrl::class.java.getDeclaredField("port")
                                val url = HttpUrl::class.java.getDeclaredField("url")
                                val scheme = HttpUrl::class.java.getDeclaredField("scheme")
                                host.isAccessible = true
                                port.isAccessible = true
                                url.isAccessible = true
                                scheme.isAccessible = true
                                host.set(it.url(), this.host())
                                port.setInt(it.url(), this.port())
                                url.set(it.url(), (url.get(it.url()) as String).replace(BaseApplication.app.baseWeUrl, replaceUrl))
                                scheme.set(it.url(), it.url().url().protocol)
                            }
                        }
                        when (it.header("type")) {
                            "zj" means "尊嘉股票环境" -> {
                                changeUrl(BaseApplication.app.baseZJUrl)
                                Logger.getAnonymousLogger()
                            }
                        }
                    })
                }
                .addInterceptor(HttpLoggingInterceptor { message ->
                    L.i("HttpLog   $message")
                }.setLevel(HttpLoggingInterceptor.Level.BODY))
                .addNetworkInterceptor(StethoInterceptor())
                .connectTimeout(5000, TimeUnit.MILLISECONDS)
                .readTimeout(12000, TimeUnit.MILLISECONDS)
                .writeTimeout(12000, TimeUnit.MILLISECONDS)
                .apply {
                    //配置https,默认可以不配置
                    sslSocketFactory(sslSocketFactory())
                    hostnameVerifier(hostnameVerifier())
                }
                .build()

        //设置最大的线程数量(默认是5)
        okHttpClient.dispatcher().maxRequestsPerHost = 3

        //最后组合成Retrofit对象
        val retrofit = Retrofit.Builder()
                .addConverterFactory(gsonConverterFactory)
                .addCallAdapterFactory(rxJava2CallAdapterFactory)
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .build()

        //将注解后的interface请求接口转换为真正可用的网络请求对象
        return retrofit.create(HttpInterface::class.java)
    }

    /**
     * 将body变成流
     */
    private fun bodyToString(request: RequestBody?): String {
        try {
            val buffer = Buffer()
            if (request != null)
                request.writeTo(buffer)
            else
                return ""
            return buffer.readUtf8()
        } catch (e: IOException) {
            return "did not work"
        }

    }

    /**
     * 获取这个SSLSocketFactory
     */
    private fun sslSocketFactory(): SSLSocketFactory {
        return try {
            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null,
                    arrayOf(object : X509TrustManager {
                        override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {}

                        override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {}

                        override fun getAcceptedIssuers(): Array<X509Certificate> {
                            return arrayOf()
                        }
                    }), SecureRandom())
            sslContext.socketFactory
        } catch (e: Exception) {
            e.printStackTrace()
            MobclickAgent.reportError(BaseApplication.app, e)
            throw e
        }
    }

    /**
     * 获取HostnameVerifier
     */
    private fun hostnameVerifier(): HostnameVerifier {
        return HostnameVerifier { _, _ -> true }
    }

    /**
     * 用于去除多余的0的adapter
     */
    internal class ZeroDeleteAdapter : TypeAdapter<String>() {
        private var instance: NumberFormat? = null

        init {
            instance = NumberFormat.getInstance()
            instance!!.maximumFractionDigits = 6
            instance!!.minimumFractionDigits = 0
        }

        /**
         * Writes one JSON value (an array, object, string, number, boolean or null)
         * for `value`.
         *
         * @param out
         * @param value the Java object to write. May be null.
         */
        @Throws(IOException::class)
        override fun write(out: JsonWriter, value: String) {
            out.value(value)
        }

        /**
         * Reads one JSON value (an array, object, string, number, boolean or null)
         * and converts it to a Java object. Returns the converted object.
         *
         * @param in
         * @return the converted Java object. May be null.
         */
        @Throws(IOException::class)
        override fun read(`in`: JsonReader): String? {
            val peek = `in`.peek()
            if (peek == JsonToken.NULL) {
                `in`.nextNull()
                return null
            }
            if (peek == JsonToken.BOOLEAN) {
                return java.lang.Boolean.toString(`in`.nextBoolean())
            }
            val value = `in`.nextString()

            //如果string为double格式,则将小数点后多余的0去掉
            if (value.matches("^[\\d]+\\.[\\d]*0$".toRegex())) {
                try {
                    return instance!!.format(java.lang.Double.parseDouble(value)).replace(",", "")
                } catch (ignored: Exception) {

                }
            }
            return value
        }
    }
}

/**
 * 功能----为activity提供生命周期的对象
 *
 *
 * Created by LinkTech on 2017/9/22.
 */
@PerActivity
@Module
class ActivityModule(private val activity: BaseActivity<*>)

/**
 * 功能----碎片实例提供器
 *
 *
 * Created by LinkTech on 2017/9/23.
 */
@PerFragment
@Module
class FragmentModule(private val baseFragment: BaseFragment<*>) {

    /**
     * 为fragment提供上下文
     */
    @Provides
    @PerFragment
    internal fun provideBaseActivity(): BaseActivity<*> {
        return baseFragment.activity as BaseActivity<*>
    }

    /**
     * 为fragment设定根部局
     */
    @Provides
    @PerFragment
    internal fun provideViewGroup(): ViewGroup {
        return baseFragment.view as ViewGroup
    }
}

//////////////////////////////////////////////////
////////////////////////////////// dagger生命周期管理模块
/////////////////////////////////////////////////

/**
 * 功能----定义每个activity的生命周期,供dagger框架使用
 *
 *
 * Created by LinkTech on 2017/9/22.
 */
@Scope
@MustBeDocumented
@Retention(value = AnnotationRetention.RUNTIME)
annotation class PerActivity

/**
 * 功能----fragment对应dagger的生命周期控制
 *
 *
 * Created by LinkTech on 2017/9/23.
 */
@Scope
@MustBeDocumented
@Retention(value = AnnotationRetention.RUNTIME)
annotation class PerFragment



