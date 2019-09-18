package com.linktech.gft.base;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.LocaleList;
import android.os.StrictMode;
import android.support.annotation.CallSuper;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.knowledge.mnlin.rregister.util.HttpCallback;
import com.knowledge.mnlin.rregister.util.ListenerInActivity;
import com.knowledge.mnlin.sdialog.base.SDActivity;
import com.linktech.gft.R;
import com.linktech.gft.drawable.ColorTextDrawable;
import com.linktech.gft.interfaces.EditTextChangedListener;
import com.linktech.gft.plugins.APTPlugins;
import com.linktech.gft.plugins.ActivityModule;
import com.linktech.gft.plugins.DaggerActivityComponent;
import com.linktech.gft.plugins.DisableAPTProcess;
import com.linktech.gft.plugins.InjectActivityTitle;
import com.linktech.gft.plugins.InjectLayoutRes;
import com.linktech.gft.plugins.InjectMenuRes;
import com.linktech.gft.plugins.PlusFunsPluginsKt;
import com.linktech.gft.util.ActivityUtil;
import com.linktech.gft.util.Const;
import com.linktech.gft.util.L;
import com.linktech.gft.util.Position;
import com.linktech.gft.util.ScreenUtils;
import com.linktech.gft.view.WatchInputEditText;
import com.orhanobut.logger.Logger;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Annotation;
import java.util.Locale;
import java.util.Stack;

import javax.inject.Inject;

import butterknife.ButterKnife;
import skin.support.content.res.SkinCompatResources;

import static android.view.KeyEvent.KEYCODE_MENU;

/**
 * Created by Administrator on 17-1-22.
 *
 * @param <T> MVP模式中,presenter类,
 */
public abstract class BaseActivity<T extends BasePresenter> extends SDActivity implements BaseView, ListenerInActivity, EditTextChangedListener {
    /**
     * 可能为null,用于统一 操作tooblar
     */
    public Toolbar toolbar;
    /**
     * 上次手指事件在屏幕的坐标
     */
    public Position lastPosition = new Position();
    /**
     * 判断当前activity可见时是否处于被返回状态
     * true:是被返回
     * false:非被返回,为主动创建
     */
    protected boolean isBackVisible = false;
    /**
     * p网络层
     */
    @Inject
    protected T mPresenter;
    /**
     * 状态位:是否可以操作界面
     */
    protected boolean canOperateView = true;
    /**
     * 存储activity创建时候保存的locale
     */
    protected Locale currentLocale;
    /**
     * 默认所有插件开关为打开状态
     */
    private int PLUGIN_FLAGS = 0xFFFFFFFF;
    /**
     * 注册监听器,当销毁或者是暂停的时候,用于执行回调
     */
    private HttpCallback<Object> callbackOnDestroy, callbackOnStop;
    /**
     * 布局id
     * 菜单id
     */
    private int layoutId;
    private int menuResId;

    @Override
    @SuppressWarnings("all")
    final protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.v(getString(R.string.base_total_lifecycle), getClass().getName(), "onCreate");

        currentLocale = PlusFunsPluginsKt.getApplicationLocale(null, null);

        /**
         * 启用严格模式
         */
        if (BaseApplication.isStrictMode) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads()
                    .detectDiskWrites().detectNetwork().penaltyLog().build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects()
                    .detectLeakedClosableObjects().penaltyLog().penaltyDeath().build());
        }

        /**
         * 设置window效果,键盘效果,屏幕方向,输入法显示等
         */
        try {
            supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
            //判断,如果是在版本超过8.0的手机上,该方法可能会出现异常:Only fullscreen activities can request orientation?
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN | WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        } catch (Exception e) {
            Logger.t(Const.TAG_LOGGER_REPORT_NET).e(e, "BaseActivity:onCreate:设置window效果");
        }

        /**
         * 获取注解的值
         */
        for (Annotation annotation : this.getClass().getAnnotations()) {
            if (annotation instanceof InjectLayoutRes) {
                layoutId = ((InjectLayoutRes) annotation).layoutResId();
                continue;
            }
            if (annotation instanceof InjectMenuRes) {
                menuResId = ((InjectMenuRes) annotation).menuResId();
                continue;
            }
            if (annotation instanceof DisableAPTProcess) {
                for (APTPlugins disable : ((DisableAPTProcess) annotation).disables()) {
                    PLUGIN_FLAGS &= ~disable.getValue();
                }
                continue;
            }
            if (annotation instanceof InjectActivityTitle) {
                int titleRes = ((InjectActivityTitle) annotation).titleRes();
                String title = ((InjectActivityTitle) annotation).title();
                if (titleRes > 0) {
                    setTitle(titleRes);
                } else {
                    setTitle(title);
                }
                continue;
            }
        }

        /**
         * 设置支持动画过渡效果
         */
        ActivityUtil.setActivityContentTransitions(this);
        //getWindow().setExitTransition(new Fade());

        /**
         * 设置statusBar透明
         */
        ActivityUtil.setDecorTransparent(this);

        /**
         * 添加内容布局
         *
         * 如果使用anko,则需要getContentOrViewId返回0
         */
        if (((layoutId = getContentOrViewId() != 0 ? getContentOrViewId() : layoutId) & 0xFFFF0000) == (R.layout.activity_main & 0xFFFF0000)) {
            //如果确定为layout文件,则可以加载
            setContentView(layoutId);
        }

        /**
         * ButterKnife绑定数据
         */
        if ((PLUGIN_FLAGS & APTPlugins.BUTTERKNIF.getValue()) == APTPlugins.BUTTERKNIF.getValue()) {
            ButterKnife.bind(this);
        }

        /**
         * 设置ContentFrameLayout的第一个子view,即当前xml文件对应根布局FitsSystemWindows为true
         */
        //ViewGroup contentFrameLayout = getWindow().getDecorView().findViewById(android.R.id.content);
        //contentFrameLayout.getChildAt(0).setFitsSystemWindows(true);

        /**
         * 设置Toolbar效果
         *
         * 设置toolbar和statusBar颜色;当点击navigation时默认退出活动
         * 设置toolbar与activity绑定
         */
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            //更换图标
            setSupportActionBar(toolbar);

            // 有toolbar 时,再设置点击事件
            if (toolbar.getNavigationIcon() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                toolbar.setNavigationOnClickListener(v -> onBackPressed());
            }

            //默认toolbar 白色
            setStatusTextBlack(!SkinCompatResources.getInstance().isDefaultSkin());
        }

        /**
         * 注入dagger框架
         */
        if ((PLUGIN_FLAGS & APTPlugins.DAGGER.getValue()) == APTPlugins.DAGGER.getValue()) {
            daggerAutoInject(DaggerActivityComponent.builder().applicationComponent(BaseApplication.getApplicationComponent()).activityModule(new ActivityModule(this)).build());
        }

        /**
         * 注入路由Arouter框架
         */
        if ((PLUGIN_FLAGS & APTPlugins.AROUTER.getValue()) == APTPlugins.AROUTER.getValue()) {
            ARouter.getInstance().inject(this);
        }

        /**
         * 绑定presenter和activity
         */
        if (mPresenter != null) {
            mPresenter.mView = this;
        }

        /**
         * 初始化内容
         *
         * 供子类自定义初始化界面元素等信息
         * (不可进行长时间耗时操作)
         */
        initData(savedInstanceState);
    }

    /**
     * 可以默认不实现
     *
     * @return 获取布局文件
     */
    @LayoutRes
    protected int getContentOrViewId() {
        return 0;
    }

    /**
     * 对于 singleTop等形式创建的activity,可能intent已经改变,但是值并没有赋值成功
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        // 更新intent
        setIntent(intent);
        ARouter.getInstance().inject(this);

        //注入路由Arouter框架
        if ((PLUGIN_FLAGS & APTPlugins.AROUTER.getValue()) == APTPlugins.AROUTER.getValue()) {
            ARouter.getInstance().inject(this);
        }
    }

    /**
     * 初始化数据(可默认不实现)
     *
     * @param savedInstanceState 已存储对象
     */
    @CallSuper
    protected void initData(@Nullable Bundle savedInstanceState) {

    }

    /**
     * 当前未加载完数据时,拦截所有的触碰事件
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        lastPosition = new Position(((int) ev.getRawX()), ((int) ev.getRawY()), null);
        return !canOperateView || super.dispatchTouchEvent(ev);
    }

    /**
     * 加载布局文件
     */
    @Override
    @CallSuper
    public boolean onCreateOptionsMenu(Menu menu) {
        if ((menuResId & 0xFFFF0000) == (R.menu.menu_save_text & 0xFFFF0000)) {
            //如果获取值为menu资源,则加载
            getMenuInflater().inflate(menuResId, menu);
        }
        return true;
    }

    @Override
    public void setTitle(int titleId) {
        super.setTitle(titleId);
    }

    /**
     * 设置顶部字体颜色为黑色/取消
     */
    public final void setStatusTextBlack(boolean black) {
        //设置更改状态栏字体图标颜色,如果未成功,则再将状态栏设置为半模糊状态
        if (black) {
            if (ScreenUtils.StatusBarLightMode(this, true) == 0) {
                //为0表示设置失败,则需要定义一下状态栏的颜色
                setStatusBarColor(dispatchGetColor(R.color.transparent_mask));
            }
        } else {
            ScreenUtils.StatusBarLightMode(this, false);
        }
    }

    /**
     * 设置状态栏颜色
     */
    @SuppressWarnings("all")
    public final void setStatusBarColor(@ColorInt int color) {
        ActivityUtil.setStatusBarColor(this, color);
    }

    /**
     * 设置导航栏颜色
     */
    protected final void setToolbarColor(int color) {
        toolbar.setBackgroundColor(color);
    }

    /**
     * 将一个已有的颜色值加深
     */
    private int getDeepColor(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        //改变亮度
        hsv[2] = (float) (hsv[2] * 0.8);
        return Color.HSVToColor(hsv);
    }

    /**
     * 显示snackbar
     */
    public final void showSnackbar(String msg, String button, @Nullable View.OnClickListener onClickButton) {
        Snackbar singleSnackbar = Snackbar.make(toolbar == null ? findViewById(android.R.id.content) : toolbar, msg, Snackbar.LENGTH_LONG);
        ((TextView) singleSnackbar.getView().findViewById(android.support.design.R.id.snackbar_text)).setMaxLines(10);
        ((TextView) singleSnackbar.getView().findViewById(android.support.design.R.id.snackbar_text)).setLineSpacing(0, 1.5F);
        ((TextView) singleSnackbar.getView().findViewById(android.support.design.R.id.snackbar_text)).setTextColor(dispatchGetColor(R.color.main_color_white));
        singleSnackbar.getView().setAlpha(0.8f);
        singleSnackbar.setActionTextColor(getThemeColorAttribute(R.style.TextInputLayout_HintTextAppearance, android.R.attr.textColor));
        singleSnackbar.setAction(button, v -> {
            if (onClickButton != null) {
                onClickButton.onClick(v);
            }
            singleSnackbar.dismiss();
        }).show();
        singleSnackbar.getView().setOnClickListener(v -> singleSnackbar.dismiss());
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) { // 8.0需要使用createConfigurationContext处理
            newBase = updateResources(newBase);
        }

        super.attachBaseContext(newBase);
    }

    @TargetApi(Build.VERSION_CODES.N)
    public Context updateResources(Context context) {
        Locale locale = PlusFunsPluginsKt.getApplicationLocale(null, null);
        Configuration configuration = context.getResources().getConfiguration();
        configuration.setLocale(locale);
        configuration.setLocales(new LocaleList(locale));
        return context.createConfigurationContext(configuration);
    }

    /**
     * 当点击menu键时屏蔽任何操作
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KEYCODE_MENU) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Logger.v(getString(R.string.base_total_lifecycle), getClass().getName(), "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Logger.v(getString(R.string.base_total_lifecycle), getClass().getName(), "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();

        Logger.v(getString(R.string.base_total_lifecycle), getClass().getName(), "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (callbackOnStop != null) {
            callbackOnStop.run(null);
        }

        Logger.v(getString(R.string.base_total_lifecycle), getClass().getName(), "onStop");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        isBackVisible = true;
        Logger.v(getString(R.string.base_total_lifecycle), getClass().getName(), "onRestart");
    }

    /**
     * 当activity销毁时候,关闭资源
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.removeDisposable();
            mPresenter.mView = null;
        }
        if (callbackOnDestroy != null) {
            callbackOnDestroy.run(null);
        }

        Logger.v(getString(R.string.base_total_lifecycle), getClass().getName(), "onDestroy");
    }

    /**
     * 管理activity实例
     */
    public void manageAddActivity() {
        boolean isExistStack = false;
        for (int i = 0; i < BaseApplication.activityManager.size(); i++) {
            Stack<BaseActivity> temp = BaseApplication.activityManager.get(i);
            if (temp.get(0).getTaskId() == getTaskId()) {
                temp.push(this);
                isExistStack = true;
                break;
            }
        }
        if (!isExistStack) {
            Stack<BaseActivity> stack = new Stack<>();
            stack.push(this);
            BaseApplication.activityManager.add(stack);
        }
    }

    /**
     * 移除需要销毁的activity实例
     */
    public void manageRemoveActivity() {
        for (int i = 0; i < BaseApplication.activityManager.size(); i++) {
            Stack<BaseActivity> temp = BaseApplication.activityManager.get(i);
            if (temp.get(0).getTaskId() == getTaskId()) {
                temp.pop();
                if (temp.size() == 0) {
                    BaseApplication.activityManager.remove(temp);
                }
                break;
            }
        }
    }

    /**
     * 打印活动栈
     */
    protected void logStack() {
        for (int i = 0; i < BaseApplication.activityManager.size(); i++) {
            Stack<BaseActivity> temp = BaseApplication.activityManager.get(i);
            L.i(getClass().getSimpleName() + " : " + "\n栈id：" + temp.get(0).getTaskId() + "\n栈底->" + temp.toString() + "栈顶");
        }
    }

    /**
     * 设置toolbar的navigation为text
     */
    protected final void setToolbarNavText(@NotNull Toolbar t, @Nullable String text, @ColorRes int color) {
        t.post(() -> {
            ColorTextDrawable textDrawable = new ColorTextDrawable(getBaseContext())
                    .setText(TextUtils.isEmpty(text) ? getString(R.string.function_close) : text)
                    .setColor(dispatchGetColor(color))
                    .setTextSize(getResources().getDimensionPixelSize(R.dimen.text_size_title_15sp))
                    .setDefaultBounds();
            t.setNavigationIcon(textDrawable);
        });
    }

    /**
     * 设置toolbar的navigation为text
     */
    protected final void setDefaultToolbarNavText() {
        setToolbarNavText(toolbar, null, R.color.main_color_white);
    }

    /**
     * 设置toolbar的menu为text
     */
    protected final void setToolbarMenuText(@NotNull Menu menu, @Nullable String text, @ColorInt int color) {
        SpannableString function = new SpannableString(text);
        function.setSpan(new ForegroundColorSpan(color), 0, function.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        function.setSpan(new AbsoluteSizeSpan(dispatchGetDimen(R.dimen.text_size_normal_14sp)), 0, function.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        menu.getItem(0).setTitle(function);
    }

    /**
     * 设置toolbar的menu为text
     */
    protected final void setDefaultToolbarMenuText(@NotNull Menu menu) {
        setToolbarMenuText(menu, dispatchGetString(R.string.function_close), SkinCompatResources.getColor(this, R.color.skin_body1_color));
    }

    @NonNull
    @Override
    public final FragmentManager getSFManager() {
        synchronized (BaseView.class) {
            return getSupportFragmentManager();
        }
    }

    @NonNull
    @Override
    public BaseActivity getBaseActivity() {
        return this;
    }

    /**
     * 当activity被关掉时,添加监听
     */
    @Override
    public void listenerOnDestroy(HttpCallback<Object> callback) {
        callbackOnDestroy = callback;
    }

    /**
     * 当activity不可见时,添加监听
     */
    @Override
    public void listenerOnStop(HttpCallback<Object> callback) {
        callbackOnStop = callback;
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        Logger.v(getClass().getSimpleName() + "监听到:环境发生变化:%s", newConfig.toString());
    }

    /**
     * 当文本发生变化
     */
    @CallSuper
    @Override
    public void onExistEditTextChanged(@NotNull WatchInputEditText editText, @NotNull Editable s) {
        // 如果不需要参数值,则直接调用无参回调
        onExistEditTextChanged();
    }

    /**
     * 当文本发生变化
     */
    protected void onExistEditTextChanged() {
        // TODO 文本变化时,所有界面做统一的操作
    }
}


