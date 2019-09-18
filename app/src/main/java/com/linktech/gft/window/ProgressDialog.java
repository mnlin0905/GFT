package com.linktech.gft.window;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.cunoraz.gifview.library.GifView;
import com.linktech.gft.R;

/**
 * Created on 2018/2/5
 * function : 加载进度框
 *
 * @author ACChain
 */

public class ProgressDialog extends Dialog {
    private static ProgressDialog singleInstance;
    private Context context;
    private View rootView;
    private String progressText;
    private TextView mTvText;
    private GifView mIvLoading;
    private onBackPressListener onBackPress;
    private boolean msgVisible;

    public ProgressDialog(@NonNull Context context) {
        this(context, R.style.ProgressDialogStyle);
    }

    public ProgressDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        this.context = context;
        init();
    }

    public synchronized static ProgressDialog getInstance(@NonNull Context context) {
        if (singleInstance == null) {
            singleInstance = new ProgressDialog(context);
        } else if (context != singleInstance.getSelfContext()) {
            singleInstance.dismiss();
            singleInstance = new ProgressDialog(context);
        }
        return singleInstance;
    }

    /**
     * @return 获取自身上下文
     */
    public Context getSelfContext() {
        return context;
    }

    /**
     * 设置文本
     */
    public ProgressDialog setMessage(String text) {
        progressText = text;
        return this;
    }

    /**
     * 设置文本
     */
    public ProgressDialog setMessageBackgroundVisible(boolean visible) {
        msgVisible = visible;
        return this;
    }

    /**
     * 设置文本
     */
    public ProgressDialog setOnBackPress(onBackPressListener onBackPress) {
        this.onBackPress = onBackPress;
        return this;
    }

    /**
     * 初始化界面
     */
    private void init() {
        //初始化界面
        rootView = LayoutInflater.from(context).inflate(R.layout.dialog_fragment_progress_backup, null);
        mTvText = rootView.findViewById(R.id.tv_text);
        mIvLoading = rootView.findViewById(R.id.iv_loading);
        if (mIvLoading != null) {
            mIvLoading.setGifResource(R.mipmap.gif_progress_loading);
        }
    }

    @Override
    public synchronized void dismiss() {
        if (mIvLoading != null) {
            mIvLoading.pause();
        }

        super.dismiss();
        context = null;
        rootView = null;
        mTvText = null;
        singleInstance = null;
    }

    @Override
    public synchronized void show() {
        try {
            //修改进度显示内容
            if (mTvText != null) {
                mTvText.setVisibility(msgVisible ? View.VISIBLE : View.GONE);
                mTvText.setText(progressText);
            }

            //如果是image,则显示动画
            if (mIvLoading != null) {
                mIvLoading.play();
            }

            //显示bg颜色
            if (msgVisible) {
                rootView.setBackgroundResource(R.drawable.shape_bg_mask_radius_5dp);
            } else {
                rootView.setBackground(null);
            }

            if (!isShowing()) {
                Window window = getWindow();
                WindowManager.LayoutParams attributes = window.getAttributes();
                //attributes.y = context.getResources().getDimensionPixelSize(R.dimen.view_padding_margin_48dp);
                attributes.gravity = Gravity.CENTER;
                window.setAttributes(attributes);
                window.setWindowAnimations(R.style.ActivityProgressViewAnimation);
                window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
                ViewGroup.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
                setContentView(rootView, params);
                setCanceledOnTouchOutside(false);
                setCancelable(true);
                super.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        if (onBackPress != null && onBackPress.onBackPress()) {
            return;
        }
        super.onBackPressed();
    }

    /**
     * 监听返回键
     */
    public interface onBackPressListener {
        /**
         * @return false表示执行原来默认的操作
         */
        boolean onBackPress();
    }
}
