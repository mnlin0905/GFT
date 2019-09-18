package com.linktech.gft.base;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.app.Dialog;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.support.multidex.MultiDex;
import android.support.v7.app.AppCompatDelegate;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.launcher.ARouter;
import com.blankj.utilcode.util.Utils;
import com.linktech.gft.BuildConfig;
import com.linktech.gft.R;
import com.linktech.gft.activity.login.LoginActivity;
import com.linktech.gft.activity.login.RegisterActivity;
import com.linktech.gft.interfaces.IIpChinaInterface;
import com.linktech.gft.plugins.ARouterConst;
import com.linktech.gft.plugins.ARouterNoPermission;
import com.linktech.gft.plugins.ApplicationComponent;
import com.linktech.gft.plugins.ApplicationModule;
import com.linktech.gft.plugins.DaggerApplicationComponent;
import com.linktech.gft.plugins.PlusFunsPluginsKt;
import com.linktech.gft.plugins.RxBus;
import com.linktech.gft.retrofit.HttpInterface;
import com.linktech.gft.skin.SkinMyViewInflater;
import com.linktech.gft.util.Const;
import com.linktech.gft.util.DefaultPreferenceUtil;
import com.linktech.gft.util.ScreenUtils;
import com.linktech.gft.view.NoDataBallPulseFooter;
import com.linktech.gft.window.BigToast;
import com.linktech.gft.window.CancelConfirmDialogFragment;
import com.linktech.gft.window.CountryPickerViewDialog;
import com.linktech.gft.window.DoLoginDialog;
import com.linktech.gft.ws.WSManager;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.LogStrategy;
import com.orhanobut.logger.LogcatLogStrategy;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;
import com.scwang.smartrefresh.header.StoreHouseHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.squareup.leakcanary.LeakCanary;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tencent.smtt.sdk.QbSdk;
import com.umeng.analytics.MobclickAgent;
import com.uuzuche.lib_zxing.DisplayUtil;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.litepal.LitePal;
import org.litepal.tablemanager.Connector;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Stack;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.plugins.RxJavaPlugins;
import skin.support.SkinCompatManager;
import skin.support.app.SkinCardViewInflater;
import skin.support.constraint.app.SkinConstraintViewInflater;
import skin.support.design.app.SkinMaterialViewInflater;
import skin.support.utils.Slog;
import zlc.season.rxdownload3.core.DownloadConfig;

import static com.tencent.smtt.sdk.WebView.getCrashExtraMessage;


/**
 * Created by Administrator on 17-1-22.
 * <p>
 * 将蓝牙4.0服务与application进行绑定，保证在内存足够的情况下，对象不会被回收
 */
public class BaseApplication extends Application implements BaseView {
    /**
     * context
     */
    @SuppressLint("StaticFieldLeak")
    public static BaseApplication app;

    /**
     * 全局使用drawable,背景色渐变
     */
    Drawable RED_DRAWABLE;
    Drawable GREEN_DRAWABLE;
    Drawable TRANS_DRAWABLE;

    /**
     * 选择券商后保存key
     */
    public static String tradeKey = "cea7562aa4bd98b7";
    /**
     * 登录获取到的，替换后续请求中header里面的auth_code
     */
    public static String authCode = "024DFA28402EA5FC521F27B4D51D98E3";

    /**
     * 账户信息
     */
    public static String account = "68850298";

    /**
     * 登录获取到的，券商的type
     */
    public static String tradeType = "";

    public static boolean isTradeLogin = false;
    /**
     * 活动管理
     */
    static ArrayList<Stack<BaseActivity>> activityManager;
    /**
     * 当前是否处于严格模式
     */
    static boolean isStrictMode = false;
    /**
     * dagger 维持全局的对象
     */
    private static ApplicationComponent applicationComponent;
    /**
     * BigToast
     */
    @Inject
    public BigToast singleBigToast;
    /**
     * 网络请求
     */
    @Inject
    public HttpInterface httpInterface;
    /**
     * 记录最顶部的activity
     */
    public Activity topActivity;
    /**
     * 记录操作系统默认的语言(非应用语言)
     */
    public Locale systemLocale = Locale.getDefault();
    /**
     * 可能存在的远程服务接口
     */
    @Nullable
    public IIpChinaInterface ipAddressI;
    /**
     * 微信操作入口
     */
    @Nullable
    public IWXAPI mWxApi;
    protected Toast singleSmallToast;
    /**
     * 记录上次显示toast登录框的时间
     * <p>
     * 如果时间未超过1min钟,则只会toast,不会dialog
     */
    private long lastToastLoginTime;
    /**
     * 标志位,记录当前是否有activity 处在 前台可见范围
     */
    private int currentExistActivityCount;
    /**
     * 锁屏 service 检测
     */
    private FingerprintManager fingerprintManager;
    private KeyguardManager keyguardManager;

    /**
     * activity/fragment dagger注入
     */
    public static ApplicationComponent getApplicationComponent() {
        return applicationComponent;
    }

    public HttpInterface getHttpInterface() {
        return httpInterface;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        //当方法数量超过65535后，需要变成两个dex包
        MultiDex.install(this);
    }

    @Override

    public void onCreate() {
        super.onCreate();
        app = this;
        RED_DRAWABLE = this.dispatchGetDrawable(R.drawable.fg_red_alpha_gradient);
        GREEN_DRAWABLE = this.dispatchGetDrawable(R.drawable.fg_green_alpha_gradient);
        TRANS_DRAWABLE = new ColorDrawable(0x00000000);

        // 主进程,才会执行一些初始化操作
        if (getCurrentProcessName().equals(getPackageName())) {

            if (Build.VERSION.SDK_INT >= 23) {
                fingerprintManager = (FingerprintManager) getSystemService(Context.FINGERPRINT_SERVICE);
            }
            keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);

            //初始化其他框架
            init();
        } else {
            // 否则,表示另外一个进行,主要处理多余的逻辑

            //Logger
            initLogger();
        }
    }

    /**
     * 获取当前进程名
     */
    private String getCurrentProcessName() {
        int pid = android.os.Process.myPid();
        String processName = "";
        ActivityManager manager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo process : manager.getRunningAppProcesses()) {
            if (process.pid == pid) {
                processName = process.processName;
            }
        }
        return processName;
    }

    /**
     * 初始化全局变量等信息
     */
    @SuppressLint("ShowToast")
    private void init() {
        //初始化皮肤框架
        Slog.DEBUG = true;
        SkinCompatManager.withoutActivity(this)                         // 基础控件换肤初始化
                .addInflater(new SkinMaterialViewInflater())            // material design 控件换肤初始化[可选]
                .addInflater(new SkinConstraintViewInflater())          // ConstraintLayout 控件换肤初始化[可选]
                .addInflater(new SkinCardViewInflater())                // CardView v7 控件换肤初始化[可选]
                .addInflater(new SkinMyViewInflater())
                .setSkinStatusBarColorEnable(false)                     // 关闭状态栏换肤，默认打开[可选]
                .setSkinWindowBackgroundEnable(false)                   // 关闭windowBackground换肤，默认打开[可选]
                .loadSkin();
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        //设置默认环境
        Locale target = Const.INSTANCE.getLOCALE_LANGUAGE_TYPES()[DefaultPreferenceUtil.getInstance().getLocaleLanguageSwitch()];
        if (target != null) {
            setLocale(target);
        }

        //Utils框架初始化
        Utils.init(this);

        //注入dagger框架
        daggerAutoInject(applicationComponent = DaggerApplicationComponent.builder().applicationModule(new ApplicationModule(this)).build());

        //活动管理对象初始化
        activityManager = new ArrayList<>();

        //简单的toast
        singleSmallToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
        ((TextView) singleSmallToast.getView().findViewById(android.R.id.message)).setTextColor(getResources().getColor(R.color.main_color_white));
        singleSmallToast.getView().setBackgroundColor(getResources().getColor(R.color.main_color_gray));
        singleSmallToast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 100);

        //捕获RxBus发出的消息,用于显示toast等
        initRxBus();

        //Logger
        initLogger();

        //初始化内存泄漏工具 TODO
        LeakCanary.install(this);

        //二维码扫描工具
        DisplayMetrics dm = getResources().getDisplayMetrics();
        DisplayUtil.density = dm.density;
        DisplayUtil.densityDPI = dm.densityDpi;
        DisplayUtil.screenWidthPx = (int) (dm.widthPixels * 1.5);
        DisplayUtil.screenhightPx = (int) (dm.heightPixels * 1.5);
        DisplayUtil.screenWidthDip = DisplayUtil.px2dip(this, dm.widthPixels) * 1.5F;
        DisplayUtil.screenHightDip = DisplayUtil.px2dip(this, dm.heightPixels) * 1.5F;

        //初始化第三方webview
        initTengXunWebView();

        //路由跳转框架初始化
        if (BuildConfig.DEBUG) {
            // 这两行必须写在init之前，否则这些配置在init过程中将无效
            ARouter.openLog();
            ARouter.openDebug();   // 开启调试模式(如果在InstantRun模式下运行，必须开启调试模式！线上版本需要关闭,否则有安全风险)
        }
        ARouter.init(this); // 尽可能早，推荐在Application中初始化

        //友盟统计
        initYouMeng();

        //弹出框初始化
        CountryPickerViewDialog.getInstance(null);

        //屏幕信息初始化
        ScreenUtils.init(this);

        //初始化litePal框架,创建数据库
        LitePal.initialize(this);
        Connector.getDatabase();

        //RxDownload配置
        DownloadConfig.Builder builder = DownloadConfig.Builder.Companion.create(this)
                .enableDb(true)
                .enableService(true)
                .enableNotification(true);
        DownloadConfig.INSTANCE.init(builder);

        //初始化下拉上拉加载功能
        initSmartRefresh();

        //当rxjava中onerror出现异常时进行处理
        RxJavaPlugins.setErrorHandler(throwable -> {
            throwable.printStackTrace();
            showToast(R.string.common_unknown_exception);
            Logger.t(Const.TAG_LOGGER_REPORT_NET).e(throwable, "RxJava");
        });

        //android9 关闭 hide方法被调用的 提示
        disableAPIDialog();

        //ws
        WSManager.Companion.init();

        // 微信第三方登录功能
        registerToWX();
    }

    /**
     * 微信接入
     */
    private void registerToWX() {
        try {
            String wxAppID = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA).metaData.getString("WX_APP_ID");

            // 将该app注册到微信
            mWxApi = WXAPIFactory.createWXAPI(this, wxAppID, false);
            mWxApi.registerApp(wxAppID);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 只能刷新框架
     */
    private void initSmartRefresh() {
        SmartRefreshLayout.setDefaultRefreshHeaderCreator((context, layout) -> {
            layout.setPrimaryColorsId(R.color.transparent, R.color.dark_main_text_8144e5);
            return new StoreHouseHeader(context).initWithString(getString(R.string.common_list_refresh_data));
        });
        SmartRefreshLayout.setDefaultRefreshFooterCreator((context, layout) -> {
            //指定为经典Footer，默认是 BallPulseFooter
            return new NoDataBallPulseFooter(context)
                    .setAnimatingColor(dispatchGetColor(R.color.skin_accent_color))
                    .setNormalColor(dispatchGetColor(R.color.black_text_666));
        });
    }

    /**
     * 日志框架
     */
    private void initLogger() {
        LogStrategy strategy = new LogcatLogStrategy();
        FormatStrategy formatStrategy = PrettyFormatStrategy.newBuilder()
                .methodOffset(7)        // (Optional) Hides internal method calls up to offset. Default 5
                .logStrategy(strategy) // (Optional) Changes the log strategy to print out. Default LogCat
                .tag(getString(R.string.app_name))   // (Optional) Global tag for every log. Default PRETTY_LOGGER
                .build();
        Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy) {
            @Override
            public boolean isLoggable(int priority, String tag) {
                return getResources().getBoolean(R.bool.switch_open_log) || Const.TAG_LOGGER_REPORT_NET.equals(tag);
            }

            @Override
            public void log(int priority, String tag, String message) {
                if (priority == Logger.ERROR && Const.TAG_LOGGER_REPORT_NET.equals(tag)) {
                    //如果error级别,且tag为上报异常,则会向友盟提供数据上报服务
                    MobclickAgent.reportError(BaseApplication.this, message);
                }
                super.log(priority, tag, message);
            }
        });
    }

    /**
     * 友盟统计
     */
    private void initYouMeng() {
        MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType.E_UM_NORMAL);
        //友盟设置场景类型;session统计间隔设置为1分钟
        MobclickAgent.setSessionContinueMillis(60000);

        //如果开发者调用Process.kill或者System.exit之类的方法杀死进程，请务必在此之前调用MobclickAgent.onKillProcess(Context context)方法，用来保存统计数据

        //设置调试模式下日志检测
        MobclickAgent.setDebugMode(false);

        //友盟主动上报错误
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                //PushAgent.getInstance(activity).onAppStart();

            }

            @Override
            @SuppressLint("NewApi")
            public void onActivityStarted(Activity activity) {
                // 前台 + 已登录 + 已开启指纹解锁 + sdk以及硬件等支持+ 锁屏指纹解锁打开状态
                boolean fSupport = Build.VERSION.SDK_INT >= 23
                        && fingerprintManager != null
                        && keyguardManager != null
                        && fingerprintManager.isHardwareDetected();
                boolean fEnable = fSupport &&
                        DefaultPreferenceUtil.getInstance().isScreenFingerprintLocked(get_user_id())
                        && keyguardManager.isKeyguardSecure()
                        && fingerprintManager.hasEnrolledFingerprints();

                boolean gEnable = DefaultPreferenceUtil.getInstance().getScreenGestureLocked(get_user_id()) != null;

                if (currentExistActivityCount == 0
                        && DefaultPreferenceUtil.getInstance().getLogin()
                        && (fEnable || gEnable)) {
                    //刚进入前台模式,之前处于后台,此时强制跳转到 解锁界面
                    Postcard a = PlusFunsPluginsKt.routeCustom(null, ARouterConst.Activity_ScreenLockActivity);
                    a.withBoolean(Const.KEY_COMMON_VALUE_ONE, gEnable);
                    a.withBoolean(Const.KEY_COMMON_VALUE_TWO, fSupport);
                    a.withBoolean(Const.KEY_COMMON_VALUE_THREE, fEnable);
                    a.navigation();
                }

                currentExistActivityCount++;
            }

            @Override
            public void onActivityResumed(Activity activity) {
                MobclickAgent.onResume(activity);
                topActivity = activity;
            }

            @Override
            public void onActivityPaused(Activity activity) {
                MobclickAgent.onPause(activity);
            }

            @Override
            public void onActivityStopped(Activity activity) {
                currentExistActivityCount--;
                if (currentExistActivityCount == 0) {
                    // app进入后台,可提示"应用进入后台模式"
                    //PlusFunsPluginsKt.toast(null, "港付通已经进入后台");
                }
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        });
    }

    /**
     * 腾讯浏览服务
     */
    private void initTengXunWebView() {
        QbSdk.initX5Environment(this, new QbSdk.PreInitCallback() {
            @Override
            public void onCoreInitFinished() {

            }

            @Override
            public void onViewInitFinished(boolean b) {
                if (b) {
                    //加载x5内核成功,会使用x5内核
                    Logger.d("x5 kernel init success");
                } else {
                    //上报异常
                    Logger.t(Const.TAG_LOGGER_REPORT_NET).e("x5 kernel init error%s", getCrashExtraMessage(BaseApplication.this));
                }
            }
        });
        QbSdk.setDownloadWithoutWifi(true);
    }

    /**
     * 初始化RXBus检测信息
     */
    @SuppressLint("CheckResult")
    private void initRxBus() {
        RxBus.Companion.getInstance().toObservable(BaseEvent.class).observeOn(AndroidSchedulers.mainThread()).subscribe(baseEvent -> {
            switch (baseEvent.operateCode) {
                //弹出提示
                case Const.SHOW_TOAST: {
                    singleSmallToast.setText(baseEvent.data.toString());
                    singleSmallToast.show();
                    break;
                }

                //显示登录超时
                case Const.SHOW_LOGIN_DIALOG: {
                    //在频繁触发登录提示的时候,进行限制
                    //在当前栈顶不是登录界面情况下,再去弹出登录窗口
                    if (topActivity instanceof LoginActivity
                            || topActivity instanceof RegisterActivity
                            || System.currentTimeMillis() - lastToastLoginTime < 10 * 1000) {
                        showToast(R.string.base_total_please_login_again);
                    } else {
                        DoLoginDialog.getInstance(topActivity).show();
                        lastToastLoginTime = System.currentTimeMillis();
                    }
                    break;
                }

                //成功后显示提示
                case Const.SHOW_BIG_TOAST: {
                    singleBigToast.setText(baseEvent.data.toString()).show();
                    break;
                }
            }
        });

        //监听权限降级策略
        RxBus.Companion.getInstance().toObservable(ARouterNoPermission.class).observeOn(AndroidSchedulers.mainThread()).subscribe(
                aRouterNoPermission -> new CancelConfirmDialogFragment()
                        .setConfirmText(aRouterNoPermission.getRightText())
                        .setCancelText(aRouterNoPermission.getLeftText())
                        .setTitle(aRouterNoPermission.getTitle())
                        .setOnOperateListener(new CancelConfirmDialogFragment.OnOperateListener() {
                            @Override
                            public boolean onCancel(Dialog dialog) {
                                return false;
                            }

                            @Override
                            public boolean onConfirm(Dialog dialog) {
                                aRouterNoPermission.getCallback().run(null);
                                return false;
                            }
                        }).show(((BaseActivity) topActivity).getSupportFragmentManager(), "no_permission"),
                throwable -> showToast(R.string.base_total_no_permission));
    }


    /**
     * 反射 禁止弹窗
     */
    private void disableAPIDialog() {
        if (Build.VERSION.SDK_INT < 28) return;
        try {
            Class clazz = Class.forName("android.app.ActivityThread");
            Method currentActivityThread = clazz.getDeclaredMethod("currentActivityThread");
            currentActivityThread.setAccessible(true);
            Object activityThread = currentActivityThread.invoke(null);
            Field mHiddenApiWarningShown = clazz.getDeclaredField("mHiddenApiWarningShown");
            mHiddenApiWarningShown.setAccessible(true);
            mHiddenApiWarningShown.setBoolean(activityThread, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 当系统内存严重不足时，系统会调用该方法
     * <p>
     * Activity对象和Application对象都实现了ComponentCallbacks接口，该接口内有抽象的onLowMemory方法，因此在activity和application中都可以通过实现该方法来处理内存不足的事件
     */
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (Integer.parseInt(System.getProperty("java.vm.version").split("\\.")[0]) < 2) {
            System.gc();
        }
    }

    /**
     * 获取我们平台的地址
     */
    @NotNull
    public String getBaseWeUrl() {
        return DefaultPreferenceUtil.getInstance().isOfficialMode() ? getResources().getString(R.string.base_we_address) : getResources().getString(R.string.base_test_we_address);
    }

    /**
     * 获取尊嘉的地址
     */
    @NotNull
    public String getBaseZJUrl() {
        return DefaultPreferenceUtil.getInstance().isOfficialMode() ? getResources().getString(R.string.base_zj_address) : getResources().getString(R.string.base_test_zj_address);
    }

    /**
     * 获取ws地址
     */
    @NotNull
    public String getWebSocketUrl() {
        return DefaultPreferenceUtil.getInstance().isOfficialMode() ? getResources().getString(R.string.base_web_socket_address) : getResources().getString(R.string.base_test_web_socket_address);
    }

    /**
     * 获取golden地址
     */
    @NotNull
    public String getGoldenUrl() {
        return DefaultPreferenceUtil.getInstance().isOfficialMode() ? getResources().getString(R.string.base_golden_address) : getResources().getString(R.string.base_test_golden_address);
    }

    /**
     * 获取开户协议地址
     */
    @NotNull
    public String getEstablishProtocolUrl() {
        return DefaultPreferenceUtil.getInstance().isOfficialMode() ? getResources().getString(R.string.base_protocol_address) : getResources().getString(R.string.base_test_protocol_address);
    }

    /**
     * 获取 信任网关地址
     */
    @NotNull
    public String getIssueTrustAddress() {
        return "";
    }

    /**
     * application监听到环境发生变化时,需要 根据情况来判断是否切换语言环境
     * <p>
     * 1.如果当前应用设置了语言环境(非跟随系统变化) ,则不会通知应用切换语言(使用自身默认的语言)
     * 2.如果当前应用设置跟随系统变化,或者未设置默认语言环境(两者可做统一处理),则判断当前与系统语言是否相同,不同则进行切换(默认不操作)
     */
    @Override
    public synchronized void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        //记录系统当前的语言
        systemLocale = PlusFunsPluginsKt.getApplicationLocale(null, newConfig);

        Logger.v(getClass().getSimpleName() + "监听到:环境发生变化:%s", newConfig.toString());

        int current = DefaultPreferenceUtil.getInstance().getLocaleLanguageSwitch();

        switch (current) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
                Locale target = Const.INSTANCE.getLOCALE_LANGUAGE_TYPES()[current];
                if (target != null && !target.equals(systemLocale)) {
                    setLocale(target);
                }
                break;
            default:
                showToast("error language!!!");
                System.exit(0);
        }
    }

    /**
     * 设置语言对象
     */
    @SuppressLint("ObsoleteSdkInt")
    public void setLocale(@NotNull Locale target) {
        // 获得res资源对象
        Resources resources = getResources();

        // 获得设置对象
        Configuration config = resources.getConfiguration();

        // 获得屏幕参数：主要是分辨率，像素等。
        DisplayMetrics dm = resources.getDisplayMetrics();

        // 语言
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            config.setLocale(target);
        } else {
            config.locale = target;
        }
        resources.updateConfiguration(config, dm);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
