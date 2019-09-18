package com.linktech.gft.base;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.android.arouter.launcher.ARouter;
import com.knowledge.mnlin.sdialog.widgets.IncludeDialogViewGroup;
import com.orhanobut.logger.Logger;
import com.linktech.gft.R;
import com.linktech.gft.interfaces.EditTextChangedListener;
import com.linktech.gft.interfaces.ListenerOnPagerChange;
import com.linktech.gft.plugins.APTPlugins;
import com.linktech.gft.plugins.DaggerFragmentComponent;
import com.linktech.gft.plugins.DisableAPTProcess;
import com.linktech.gft.plugins.FragmentModule;
import com.linktech.gft.plugins.InjectLayoutRes;
import com.linktech.gft.retrofit.HttpInterface;
import com.linktech.gft.view.WatchInputEditText;

import java.lang.annotation.Annotation;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Administrator on 3/9/17.
 */
public abstract class BaseFragment<T extends BasePresenter> extends Fragment implements BaseView, ListenerOnPagerChange<Object>, EditTextChangedListener {
    /**
     * 根部局
     */
    @Inject
    public ViewGroup rootView;
    /**
     * 是否为第一次初始化碎片
     * <p>
     * 该对象(如果是未初始化)只能去判断一次，第二次会失效
     */
    public boolean isRequireInit = true;
    /**
     * 上下文对象
     */
    @Inject
    protected BaseActivity<?> baseActivity;
    @Inject
    protected T mPresenter;
    @Inject
    protected HttpInterface httpInterface;

    /**
     * 用于在fragment销毁时接触绑定
     */
    Unbinder unbinder;

    /**
     * 默认所有插件开关为打开状态
     */
    private int PLUGIN_FLAGS = 0xFFFFFFFF;

    /**
     * 布局id
     */
    private int layoutId = getContentViewId();

    /**
     *
     */
    private FragmentManager singleChildFragmentManager;

    /**
     * 如果是第一次加载碎片，则返回true，同时，修改状态为 false
     * 如果非第一次，返回false
     */
    protected Boolean ifRequireInitAndResetFalse() {
        if (isRequireInit) {
            isRequireInit = false;
            return true;
        } else {
            return false;
        }
    }

    /**
     * 数据处理
     *
     * @param savedInstanceState 已存储值
     */
    @CallSuper
    protected void initData(@Nullable Bundle savedInstanceState) {
    }

    /**
     * 获取xml布局文件
     */
    @LayoutRes
    protected int getContentViewId() {
        return 0;
    }

    @Override
    public void onAttach(Context context) {
        Logger.v("onAttach: " + getClass().getSimpleName());

        //当没有dagger时,主动注入activity
        baseActivity = (BaseActivity<?>) context;

        super.onAttach(context);
    }

    @Override
    final public void onCreate(@Nullable Bundle savedInstanceState) {
        Logger.v("onCreate: " + getClass().getSimpleName());
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    final public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
            Bundle savedInstanceState) {
        Logger.v("onCreateView: " + getClass().getSimpleName());

        //添加布局
        if (rootView == null) {
            //获取注解的值
            for (Annotation annotation : this.getClass().getAnnotations()) {
                if (annotation instanceof InjectLayoutRes) {
                    layoutId = ((InjectLayoutRes) annotation).layoutResId();
                    continue;
                }
                if (annotation instanceof DisableAPTProcess) {
                    for (APTPlugins disable : ((DisableAPTProcess) annotation).disables()) {
                        PLUGIN_FLAGS &= ~disable.getValue();
                    }
                    continue;
                }
            }

            if ((layoutId & 0xFFFF0000) == (R.layout.activity_main & 0xFFFF0000)) {
                //如果获取值大于0,则可以setContent成功
                return inflater.inflate(layoutId, null, false);
            }
        }
        return rootView;
    }

    @Override
    final public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Logger.v("onActivityCreated: " + getClass().getSimpleName());
        super.onActivityCreated(savedInstanceState);

        //只有第一次初始化view时,才会进行如下操作
        if (rootView == null) {
            if ((PLUGIN_FLAGS & APTPlugins.DAGGER.getValue()) == APTPlugins.DAGGER.getValue()) {
                daggerAutoInject(DaggerFragmentComponent.builder().applicationComponent(BaseApplication.getApplicationComponent()).fragmentModule(new FragmentModule(this)).build());
            }

            if (mPresenter != null) {
                mPresenter.mView = this;
            }
            if ((PLUGIN_FLAGS & APTPlugins.BUTTERKNIF.getValue()) == APTPlugins.BUTTERKNIF.getValue()) {
                unbinder = ButterKnife.bind(this, rootView);
            }

            //注入路由ARouter框架
            if ((PLUGIN_FLAGS & APTPlugins.AROUTER.getValue()) == APTPlugins.AROUTER.getValue()) {
                ARouter.getInstance().inject(this);
            }

            initData(savedInstanceState);
        }
    }

    /**
     * 需要在onActivityCreated之前就设置,否则不起作用
     * <p>
     * 用于打开插件
     */
    final protected void enablePlugin(int plugin) {
        PLUGIN_FLAGS |= plugin;
        mPresenter.mView = null;
    }

    @Override
    public void onResume() {
        Logger.v("onResume: " + getClass().getSimpleName());
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        Logger.v("onDestroyView: " + getClass().getSimpleName());
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        Logger.v("onDestroy: " + getClass().getSimpleName());
        if (mPresenter != null) {
            mPresenter.removeDisposable();
            mPresenter.mView = null;
        }
        if (unbinder != null) {
            unbinder.unbind();
        }
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        Logger.v("onDetach: " + getClass().getSimpleName());
        super.onDetach();
    }

    @NonNull
    @Override
    public BaseActivity getBaseActivity() {
        return (BaseActivity) getActivity();
    }

    @NonNull
    @Override
    public FragmentManager getSFManager() {
        synchronized (BaseView.class) {
            return baseActivity.getSupportFragmentManager();
        }
    }

    @NonNull
    public FragmentManager getChildSFManager() {
        if (singleChildFragmentManager == null) {
            synchronized (BaseView.class) {
                singleChildFragmentManager = getChildFragmentManager();
            }
        }
        return singleChildFragmentManager;
    }

    /**
     * 当fragment可见或者重新可见时调用
     *
     * @param obj pager或者fragment父布局或activity中传出的值
     */
    @Override
    @CallSuper
    public void onPagerFragmentChange(@Nullable Object obj) {

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        Logger.v(getClass().getSimpleName() + "监听到:环境发生变化:%s", newConfig.toString());
    }

    /**
     * 当文本发生变化
     */
    @Override
    public void onExistEditTextChanged(@NonNull WatchInputEditText editText, @NonNull Editable s) {

    }

    /**
     * @return IncludeDialogViewGroup对象
     */
    @Nullable
    public IncludeDialogViewGroup getIncludeDialog() {
        return baseActivity.getIncludeDialog();
    }
}
