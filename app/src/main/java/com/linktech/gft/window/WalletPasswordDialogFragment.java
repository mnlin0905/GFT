package com.linktech.gft.window;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.blankj.utilcode.util.ScreenUtils;
import com.linktech.gft.R;
import com.linktech.gft.base.BaseEvent;
import com.linktech.gft.plugins.PlusFunsPluginsKt;
import com.linktech.gft.plugins.RxBus;
import com.linktech.gft.util.Const;
import com.linktech.gft.util.RegexConst;

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

public class WalletPasswordDialogFragment extends DialogFragment {

    @BindView(R.id.tv_title)
    TextView mTvTitle;
    @BindView(R.id.et_password)
    EditText mEtPassword;
    @BindView(R.id.tv_cancel)
    TextView mTvCancel;
    @BindView(R.id.tv_confirm)
    TextView mTvConfirm;

    /**
     * 全局变量
     */
    private View rootView;
    private Unbinder unbinder;
    private onPasswordListener listener;
    private String cancel;
    private String confirm;
    private String title = PlusFunsPluginsKt.getString(null,R.string.dialog_password_input_title );

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //去除标题
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        //添加view
        rootView = inflater.inflate(R.layout.dialog_wallet_password_input, null, false);
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
        mTvTitle.setText(title);
        mTvCancel.setText(cancel);
        mTvConfirm.setText(confirm);

        if (getDialog() != null) {
            Dialog dialog = getDialog();
            Window window = dialog.getWindow();
            WindowManager.LayoutParams attributes = window.getAttributes();
            window.setAttributes(attributes);
            window.setGravity(Gravity.CENTER);
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams
                    .FLAG_FULLSCREEN);
            window.setLayout((int) (ScreenUtils.getScreenWidth() * 0.75), WindowManager.LayoutParams.WRAP_CONTENT);
            //设置密码框与其他弹出框不同,不可以被取消
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        //如果activity实现了对应接口,则默认添加上点击事件
        if (getActivity() instanceof onPasswordListener&& listener == null) {
            listener = (onPasswordListener) getActivity();
        }
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
    public WalletPasswordDialogFragment setOnSelectTurnOutListener(onPasswordListener listener) {
        this.listener = listener;
        return this;
    }

    /**
     * @param title 标题
     */
    public WalletPasswordDialogFragment setTitle(String title) {
        this.title = title;
        return this;
    }

    /**
     * @param cancel 取消按钮
     */
    public WalletPasswordDialogFragment setCancel(String cancel) {
        this.cancel = cancel;
        return this;
    }

    /**
     * @param confirm 确认
     */
    public WalletPasswordDialogFragment setConfirm(String confirm) {
        this.confirm = confirm;
        return this;
    }


    @OnClick({R.id.tv_cancel, R.id.tv_confirm})
    public void onViewClicked(View view) {
        if (listener == null) {
            return;
        }
        switch (view.getId()) {
            case R.id.tv_cancel:// 取消
                listener.onPasswordCancel(getDialog());
                break;
            case R.id.tv_confirm://确定
                String passWord = mEtPassword.getText().toString();
                if (TextUtils.isEmpty(passWord)) {
                    RxBus.Companion.getInstance().post(new BaseEvent(Const.SHOW_TOAST, getString(R.string.dialog_wallet_password_input_no_pwd)));
                    return;
                }
                if (!passWord.matches(RegexConst.REGEX_PASSWORD)) {
                    RxBus.Companion.getInstance().post(new BaseEvent(Const.SHOW_TOAST, getString(R.string.dialog_wallet_password_input_error_pwd)));
                    return;
                }
                if (listener.onPasswordConfirm(getDialog(), passWord)) {
                    return;
                }
        }

        getDialog().dismiss();
    }


    /**
     * 密码框的各种事件回调
     */
    public interface onPasswordListener {
        /**
         * 当点击取消时回调
         *
         * @param dialog 弹出框
         */
        default void onPasswordCancel(Dialog dialog) {
        }

        /**
         * 当输入密码完毕后回调
         *
         * @param dialog   弹出框
         * @param passWord 密码
         * @return true表示已处理了弹出框事件, 不需要默认关闭弹出框的行为
         */
        boolean onPasswordConfirm(Dialog dialog, String passWord);
    }
}
