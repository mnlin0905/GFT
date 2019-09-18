package com.linktech.gft.window;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.blankj.utilcode.util.ScreenUtils;
import com.linktech.gft.R;
import com.linktech.gft.base.BaseApplication;
import com.linktech.gft.base.BasePresenter;
import com.linktech.gft.plugins.ARouterConst;
import com.linktech.gft.plugins.PlusFunsPluginsKt;
import com.linktech.gft.util.Const;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created on 2018/2/5
 * function : 登录超时弹出框
 *
 * @author LinkTech
 */

public class DoLoginDialog extends Dialog {
    /**
     * 保持同一个activity只会弹出一个窗口
     */
    private static DoLoginDialog singleInstance;

    @BindView(R.id.iv_close)
    ImageView mIvClose;
    @BindView(R.id.tv_title)
    TextView mTvTitle;
    @BindView(R.id.tv_confirm)
    TextView mTvConfirm;
    private View rootView;

    private Context context;

    /**
     * Creates a dialog window that uses the default dialog theme.
     * <p>
     * The supplied {@code context} is used to obtain the window manager and
     * base theme used to present the dialog.
     *
     * @param context the context in which the dialog should run
     */
    private DoLoginDialog(@NonNull Context context) {
        this(context, R.style.ActivityDialogStyle);
    }

    /**
     * Creates a dialog window that uses a custom dialog style.
     * <p>
     * The supplied {@code context} is used to obtain the window manager and
     * base theme used to present the dialog.
     * <p>
     * The supplied {@code theme} is applied on top of the context's theme. See
     * <a href="{@docRoot}guide/topics/dataResources/available-dataResources.html#stylesandthemes">
     * Style and Theme MyResources</a> for more information about defining and
     * using styles.
     *
     * @param context    the context in which the dialog should run
     * @param themeResId a style resource describing the theme to use for the
     *                   window, or {@code 0} to use the default dialog theme
     */
    public DoLoginDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        this.context = context;
        init();
    }

    public synchronized static DoLoginDialog getInstance(@NonNull Context context) {
        if (singleInstance == null || context != singleInstance.getSelfContext()) {
            singleInstance = new DoLoginDialog(context);
        }
        return singleInstance;
    }

    /**
     * 有些界面在登录后,是需要处理数据刷新事件的
     */
    public static void onLoginResult(int requestCode, int resultCode, Intent data, BasePresenter.HttpCallback<Object> callback) {
        if (requestCode == Const.REQUEST_CODE_LOGIN_REFRESH && resultCode == Activity.RESULT_OK) {
            if (callback != null) {
                callback.run(null);
            }
        }
    }

    /**
     * 无论true或者false,都可以执行
     *
     * 有些界面在登录后,是需要处理数据刷新事件的
     */
    public static void onLoginResult(int requestCode, BasePresenter.HttpCallback<Object> callback) {
        if (requestCode == Const.REQUEST_CODE_LOGIN_REFRESH ) {
            if (callback != null) {
                callback.run(null);
            }
        }
    }

    /**
     * @return 获取自身上下文
     */
    public Context getSelfContext() {
        return context;
    }

    /**
     * 初始化界面
     */
    private void init() {
        //初始化界面
        rootView = LayoutInflater.from(context).inflate(R.layout.dialog_fragment_alert_close, null);
        ButterKnife.bind(this, rootView);
        mTvTitle.setText(R.string.dialog_do_login_timeout);
        mTvConfirm.setText(R.string.function_to_login);
    }

    @OnClick({R.id.iv_close, R.id.tv_confirm})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_close:
                break;
            case R.id.tv_confirm://默认已startForResult方式启动activity
                ARouter.getInstance()
                        .build(ARouterConst.Activity_LoginActivity)
                        .navigation(BaseApplication.app.topActivity, Const.REQUEST_CODE_LOGIN_REFRESH);
                break;
        }

        dismiss();
    }

    @Override
    public synchronized void dismiss() {
        super.dismiss();

        //释放引用,保证不会内存泄漏
        if (singleInstance != null) {
            singleInstance = null;
        }
    }

    @Override
    public synchronized void show() {
        try {
            if (!isShowing()) {
                Window window = getWindow();
                WindowManager.LayoutParams attributes = window.getAttributes();
                attributes.gravity = Gravity.CENTER;
                window.setAttributes(attributes);
                window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
                ViewGroup.LayoutParams params = new FrameLayout.LayoutParams((int) (ScreenUtils.getScreenWidth() * 0.75),
                        ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
                setContentView(rootView, params);
                setCancelable(false);
                super.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            PlusFunsPluginsKt.toast(null, R.string.dialog_do_login_again);
        }
    }
}
