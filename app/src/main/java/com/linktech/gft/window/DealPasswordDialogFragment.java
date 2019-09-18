package com.linktech.gft.window;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.jungly.gridpasswordview.GridPasswordView;
import com.linktech.gft.R;
import com.linktech.gft.view.PasswordView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created on 2018/1/9
 * function : 密码框
 *
 * @author LinkTech
 */

public class DealPasswordDialogFragment extends DialogFragment implements GridPasswordView.OnPasswordChangedListener {
    @BindView(R.id.gpv_password)
    PasswordView gpvPassword;
    private Unbinder unbinder;
    private DealPasswordListener listener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //去除标题
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        //添加view
        View rootView = inflater.inflate(R.layout.dialog_deal_password, null, false);
        unbinder = ButterKnife.bind(this, rootView);

        //设置密码变化监听器
        gpvPassword.setOnPasswordChangedListener(this);

        return rootView;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new Dialog(getActivity(), R.style.ActivityDialogStyle);
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
            //设置密码框与其他弹出框不同,不可以被取消
            dialog.setCanceledOnTouchOutside(false);
        }
    }

    public DealPasswordDialogFragment show(FragmentManager manager) {
        super.show(manager, "tag");
        return this;
    }

    public void showInput() {
        gpvPassword.performClick();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        if (listener != null) {
            listener = null;
        }
    }

    /**
     * @param listener 监听器
     */
    public DealPasswordDialogFragment setOnListener(DealPasswordListener listener) {
        this.listener = listener;
        return this;
    }

    @Override
    public void onTextChanged(String psw) {
    }

    @Override
    public void onInputFinish(String psw) {
        if (listener != null) {
            if (!listener.onFinish(getDialog(), psw))
                dismiss();

        }
    }

    @OnClick({R.id.iv_close, R.id.tv_forget})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_close:
                dismiss();
                break;
            case R.id.tv_forget:
                dismiss();
                if (listener != null) {
                    listener.onForget(getDialog());
                }
                break;
        }
    }

    /**
     * 密码框的各种事件回调
     */
    public interface DealPasswordListener {

        /**
         * 当输入密码完毕后回调
         *
         * @param dialog   弹出框
         * @param passWord 密码
         * @return true表示已处理了弹出框事件, 不需要默认关闭弹出框的行为
         */
        boolean onFinish(Dialog dialog, String passWord);

        void onForget(Dialog dialog);
    }
}
