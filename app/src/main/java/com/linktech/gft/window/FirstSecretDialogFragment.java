package com.linktech.gft.window;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.blankj.utilcode.util.ScreenUtils;
import com.linktech.gft.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created on 2018/1/17
 * function :第一次导出 secret
 *
 * @author LinkTech
 */

public class FirstSecretDialogFragment extends DialogFragment {
    Unbinder unbinder;
    @BindView(R.id.tv_confirm)
    TextView mTvConfirm;

    private OnAlertListener listener;
    private View rootView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //去除标题
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        //添加view
        rootView = inflater.inflate(R.layout.dialog_fragment_first_secret, null, false);
        unbinder = ButterKnife.bind(this, rootView);

        return rootView;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new Dialog(getActivity(), R.style.ActivityDialogStyle);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null) {
            Dialog dialog = getDialog();
            Window window = dialog.getWindow();
            WindowManager.LayoutParams attributes = window.getAttributes();
            window.setAttributes(attributes);
            window.setGravity(Gravity.CENTER);
            window.setDimAmount(0.6F);
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams
                    .FLAG_FULLSCREEN);
            window.setLayout(ScreenUtils.getScreenWidth(), WindowManager.LayoutParams.WRAP_CONTENT);
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        //如果activity实现了对应接口,则默认添加上点击事件
        if (getActivity() instanceof OnAlertListener && listener == null) {
            listener = (OnAlertListener) getActivity();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    ////////////////////////////////////////

    public FirstSecretDialogFragment setOnAlertListener(OnAlertListener listener) {
        this.listener = listener;
        return this;
    }

    @OnClick({R.id.tv_confirm})
    public void onViewClicked(View view) {
        if (listener == null) {
            getDialog().dismiss();
            return;
        }

        boolean consume = false;
        switch (view.getId()) {
            case R.id.tv_confirm:
                consume = listener.onConfirm(getDialog());
                break;
        }

        if (!consume) {
            getDialog().dismiss();
        }
    }

    ////////////////////////////////////////


    /**
     * 监听listener
     */
    public interface OnAlertListener {
        /**
         * 点击按钮
         *
         * @param dialog dialog
         * @return 返回true则默认不会关闭dialog
         */
        boolean onConfirm(Dialog dialog);
    }
}
