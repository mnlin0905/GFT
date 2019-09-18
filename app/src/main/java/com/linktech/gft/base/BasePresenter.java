package com.linktech.gft.base;

import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.text.TextUtils;

import com.blankj.utilcode.util.ActivityUtils;
import com.google.gson.JsonSyntaxException;
import com.linktech.gft.R;
import com.linktech.gft.activity.financing.common.FinancingActivity;
import com.linktech.gft.bean.UserBaseInfoBean;
import com.linktech.gft.plugins.PlusFunsPluginsKt;
import com.linktech.gft.plugins.RxBus;
import com.linktech.gft.retrofit.CommonHttpInterface;
import com.linktech.gft.retrofit.HttpInterface;
import com.linktech.gft.util.Const;
import com.linktech.gft.util.ConstValuesKt;
import com.linktech.gft.util.DataFormatException;
import com.linktech.gft.util.DefaultPreferenceUtil;
import com.linktech.gft.window.ProgressDialog;
import com.orhanobut.logger.Logger;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.TimeoutException;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.HttpException;

/**
 * 功能----基础中间键,mvp模式
 * <p>
 * Created by LinkTech on 2017/9/22.
 */

public class BasePresenter<T extends BaseView> implements CommonHttpInterface {
    /**
     * 网络成功返回,用0表示
     */
    protected static final int RESULT_OK = 0;

    /**
     * 会员信息
     * <p>
     * 设置默认值,放置空指针异常
     */
    @NotNull
    public static UserBaseInfoBean singleUserInfo = DefaultPreferenceUtil.getInstance().createUserInfoBean();
    public T mView;
    public volatile int taskCount;
    /**
     * 网络请求对象
     */
    @Inject
    protected HttpInterface httpInterface;
    /**
     * 存储rxjava网络请求,用于取消事件
     * <p>
     * 管理网络的事件队列
     * 当前任务数量
     */
    private CompositeDisposable group;

    @Inject
    public BasePresenter() {

    }

    /**
     * 如果加载框存在,则让其消失
     */
    public void disappearLoading() {
        //如果loading还在显示,则将弹出框隐去(如果还有其他任务,则不消失)
        if (taskCount <= 0) {
            //由于mview等原因可能无法关闭,但必须尝试关闭一次,否则可能导致弹出框一直在显示状态
            ProgressDialog.getInstance(mView.getBaseActivity()).dismiss();
        }
    }

    /**
     * 显示加载框
     *
     * @param loadingMessage 为null时不显示加载框
     */
    public void appearLoading(String loadingMessage) {
        //判断是否显示进度框
        if (loadingMessage == null) {
            return;
        }

        //如果activity不在栈顶,则不显示(同一个类加载器只会对同一个类加载一次)
        if (ActivityUtils.getTopActivity().getClass() != mView.getBaseActivity().getClass()) {
            return;
        }

        //如果当前线程不是ui线程,则post显示
        if (Thread.currentThread().getId() == Looper.getMainLooper().getThread().getId()) {
            //如果baseActivity中还未添加loading碎片,则显示进度框
            ProgressDialog.getInstance(mView.getBaseActivity())
                    .setMessage(loadingMessage)
                    .show();
        } else {
            //线程切换来显示
            mView.getBaseActivity().runOnUiThread(() ->
                    ProgressDialog.getInstance(mView.getBaseActivity())
                            .setMessage(loadingMessage)
                            .show());
        }
    }

    /**
     * 显示错误信息
     */
    private BaseHttpBean replaceHttpResError(BaseHttpBean bean) {
        if (TextUtils.isEmpty(bean.msg = ConstValuesKt.getERROR_CODES().invoke(bean.stateCode))) {
            //此时的错误码,会上传到融云
            Logger.t(Const.TAG_LOGGER_REPORT_NET)
                    .e(String.format(Locale.CHINA, "exist unknown error code:：%d", bean.stateCode));
            bean.msg = mView.dispatchGetString(R.string.base_total_exist_known_error_code, bean.stateCode);
        }
        return bean;
    }


    /**
     * 进行网络请求
     */
    public synchronized final <BEAN extends BaseHttpBean> void requestHttp(@NonNull Observable<BEAN> observable, @NonNull Consumer<BEAN> onNextListener) {
        requestHttp(null, observable, onNextListener, null);
    }

    /**
     * 进行网络请求
     */
    public synchronized final <BEAN extends BaseHttpBean> void requestHttp(@NonNull Observable<BEAN> observable, @NonNull Consumer<BEAN> onNextListener, @Nullable Consumer<Throwable> onErrorListener) {
        requestHttp(null, observable, onNextListener, onErrorListener);
    }

    /**
     * 进行网络请求
     */
    public synchronized final <BEAN extends BaseHttpBean> void requestHttp(@NotNull @StringRes int loadingMessageRes, @NonNull Observable<BEAN> observable, @NonNull Consumer<BEAN> onNextListener) {
        requestHttp(mView.dispatchGetString(loadingMessageRes), observable, onNextListener, null);
    }

    /**
     * 进行网络请求
     */
    public synchronized final <BEAN extends BaseHttpBean> void requestHttp(@Nullable String loadingMessage, @NonNull Observable<BEAN> observable, @NonNull Consumer<BEAN> onNextListener) {
        requestHttp(loadingMessage, observable, onNextListener, null);
    }


    /**
     * 进行网络请求
     *
     * @param loadingMessage  null mesons no loading
     * @param observable      observable
     * @param onNextListener  listener to deal error_code
     * @param onErrorListener DataFormatException or throwable
     */
    public synchronized final <BEAN extends BaseHttpBean> void requestHttp(@Nullable String loadingMessage, @NonNull Observable<BEAN> observable, @NonNull Consumer<BEAN> onNextListener, @Nullable Consumer<Throwable> onErrorListener) {
        //onError回调
        Consumer<Throwable> onError = throwable -> {
            try {
                //如果不是数据异常,而是网络异常,则提示网络错误
                if (!(throwable instanceof DataFormatException)) {
                    if (throwable instanceof JsonSyntaxException) {
                        mView.showToast(R.string.base_total_json_syntax_exception);
                    } else if (throwable instanceof NullPointerException) {
                        mView.showToast(R.string.base_total_null_pointer_exception);
                    } else if (throwable instanceof IOException ||
                            throwable instanceof TimeoutException ||
                            throwable instanceof HttpException) {
                        mView.showToast(R.string.base_total_io_exception);
                    } else {
                        mView.showToast(String.format(Locale.CHINA, "%s", throwable.getMessage()));
                    }
                }
                if (onErrorListener != null) {
                    onErrorListener.accept(throwable);
                } else {
                    throwable.printStackTrace();
                }
            } catch (Exception e) {
                //统一处理各种未知的异常情况
                e.printStackTrace();
                mView.showToast(R.string.base_total_other_exception);
            } finally {
                taskCount--;
                disappearLoading();
            }
        };

        //onNext回调
        Consumer<BEAN> onNext = beanBaseHttpBean -> {
            //如果视图丢失,则不处理网络事件
            if (mView != null && beanBaseHttpBean != null) {
                if (beanBaseHttpBean.stateCode == 122) {
                    mView.showToast(R.string.common_no_data);
                }
                switch (beanBaseHttpBean.getCode()) {
                    case 0:
                    case 122:
                    case 1026 : // 表示当前微信授权的code未绑定帐号,需要新绑定一个手机号
                        onNextListener.accept(beanBaseHttpBean);
                        taskCount--;
                        disappearLoading();
                        return;
                    case 5:
                    case 1001://参数错误 TODO
                        PlusFunsPluginsKt.toast(null,"参数错误" );
                        break;
                    case 1016:
                        DefaultPreferenceUtil.getInstance().setSignature(null);
                        DefaultPreferenceUtil.getInstance().setLogin(false);
                        //首页不进行弹出显示,只能主动去调用登录接口
                        if (!(mView.getBaseActivity() instanceof FinancingActivity)) {
                            RxBus.Companion.getInstance().post(new BaseEvent(Const.SHOW_LOGIN_DIALOG, null));
                        }
                        taskCount--;
                        disappearLoading();
                        return;
                    case 10012://查询需要limit
                    case 202102: // 股票未查找到,不进行任何处理
                        taskCount--;
                        disappearLoading();
                        return;
                    default:
                        mView.showToast(replaceHttpResError(beanBaseHttpBean).getMsg());
                        break;
                }
                onError.accept(new DataFormatException("ERROR", beanBaseHttpBean));
            }
        };

        //设定请求关系
        taskCount++;
        addDisposable(observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposable -> appearLoading(loadingMessage))
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(onNext, onError));
    }

    /**
     * 移除rx监听
     */
    public void removeDisposable() {
        if (group != null) {
            group.dispose();
        }
    }

    /**
     * 添加到监听组
     */
    public void addDisposable(Disposable disposable) {
        if (group == null) {
            group = new CompositeDisposable();
        }
        group.add(disposable);
    }

    @Override
    public BaseView getBaseView() {
        return mView;
    }

    @Override
    public <E extends BaseView> BasePresenter<E> getBasePresenter() {
        return (BasePresenter<E>) this;
    }

    @Override
    public HttpInterface getHttpInterface() {
        return httpInterface;
    }

    /**
     * 网络请求成功回调接口
     * <p>
     * 如果有该callback,则网络请求成功后将执行该callback方法
     */
    public interface HttpCallback<CB> {
        /**
         * @param tag 回调时用于区分某个请求,tag为null则表示dataSet字段自身为null或异常
         */
        void run(@Nullable CB tag);
    }
}
