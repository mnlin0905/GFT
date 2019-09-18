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

import com.linktech.gft.R;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created on 2018/1/15
 * function : 设置性别
 *
 * @author LinkTech
 */

public class ChangeSexDialogFragment extends DialogFragment {

    private OnChangeSexListener listener;
    private View rootView;
    private Unbinder unbinder;
    private boolean hideSecret = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //去除标题
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        //添加view
        rootView = inflater.inflate(R.layout.dialog_fragment_change_sex, null, false);
        rootView.findViewById(R.id.tv_secret).setVisibility(hideSecret ? View.GONE : View.VISIBLE);
        rootView.findViewById(R.id.dv_secret).setVisibility(hideSecret ? View.GONE : View.VISIBLE);
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
            window.setGravity(Gravity.BOTTOM);
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams
                    .FLAG_FULLSCREEN);
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            window.setWindowAnimations(R.style.ActivityBottomViewAnimation);
            dialog.setCanceledOnTouchOutside(false);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        //如果activity实现了对应接口,则默认添加上点击事件
        if (getActivity() instanceof OnChangeSexListener && listener == null) {
            listener = (OnChangeSexListener) getActivity();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    public ChangeSexDialogFragment setOnChangeSexListener(OnChangeSexListener listener) {
        this.listener = listener;
        return this;
    }

    public ChangeSexDialogFragment setSecretHide() {
        hideSecret = true;
        return this;
    }

    @OnClick({R.id.tv_man, R.id.tv_woman, R.id.tv_secret, R.id.tv_cancel})
    public void onViewClicked(View view) {
        if (listener == null) {
            return;
        }
        boolean consume = false;
        switch (view.getId()) {
            case R.id.tv_man:
                consume = listener.onClickMan(getDialog());
                break;
            case R.id.tv_woman:
                consume = listener.onClickWoman(getDialog());
                break;
            case R.id.tv_secret:
                consume = listener.onClickSecret(getDialog());
                break;
            case R.id.tv_cancel:
                consume = listener.onSexCancel(getDialog());
                break;
        }
        if (!consume) {
            getDialog().dismiss();
        }
    }

    /**
     * 当点击弹出框中按钮时进行回调
     */
    public interface OnChangeSexListener {
        /**
         * 返回true表示不需要默认操作
         */
        default boolean onClickMan(Dialog dialog) {
            return false;
        }

        default boolean onClickWoman(Dialog dialog) {
            return false;
        }

        default boolean onClickSecret(Dialog dialog) {
            return false;
        }

        default boolean onSexCancel(Dialog dialog) {
            return false;
        }
    }
}
