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
import android.widget.TextView;

import com.blankj.utilcode.util.ScreenUtils;
import com.jungly.gridpasswordview.GridPasswordView;
import com.linktech.gft.R;
import com.linktech.gft.base.BaseEvent;
import com.linktech.gft.plugins.PlusFunsPluginsKt;
import com.linktech.gft.plugins.RxBus;
import com.linktech.gft.util.Const;
import com.linktech.gft.view.DividerView;
import com.linktech.gft.view.PasswordView;

import java.util.Locale;

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

public class PasswordDialogFragment extends DialogFragment implements GridPasswordView.OnPasswordChangedListener {

    @BindView(R.id.label_amount)
    TextView mLabelAmount;
    @BindView(R.id.tv_amount)
    TextView mTvAmount;
    @BindView(R.id.tv_unit)
    TextView mTvUnit;
    @BindView(R.id.tv_title)
    TextView mTvTitle;
    @BindView(R.id.gpv_password)
    PasswordView mGpvPassword;
    @BindView(R.id.tv_cancel)
    TextView mTvCancel;
    @BindView(R.id.tv_confirm)
    TextView mTvConfirm;
    @BindView(R.id.divider)
    DividerView mDivider;

    /**
     * 全局变量
     */
    private View rootView;
    private Unbinder unbinder;
    private onPasswordListener listener;
    private String amount;
    private String unit;
    private String title = PlusFunsPluginsKt.getString(null,R.string.dialog_password_input_title );
    private String confirm;
    private String cancel;
    private boolean appearHeader;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //去除标题
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        //添加view
        rootView = inflater.inflate(R.layout.dialog_password_input, null, false);
        unbinder = ButterKnife.bind(this, rootView);

        //设置密码变化监听器
        mGpvPassword.setOnPasswordChangedListener(this);

        //header显示
        mLabelAmount.setVisibility(appearHeader ? View.VISIBLE : View.GONE);
        mTvAmount.setVisibility(appearHeader ? View.VISIBLE : View.GONE);
        mTvUnit.setVisibility(appearHeader ? View.VISIBLE : View.GONE);
        mDivider.setVisibility(appearHeader ? View.VISIBLE : View.GONE);

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
        mTvAmount.setText(amount);
        mTvUnit.setText(unit);
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
            dialog.setCanceledOnTouchOutside(false);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        //如果activity实现了对应接口,则默认添加上点击事件
        if (getActivity() instanceof onPasswordListener && listener == null) {
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
    public PasswordDialogFragment setOnSelectTurnOutListener(onPasswordListener listener) {
        this.listener = listener;
        return this;
    }

    /**
     * @param amount 转账数量
     */
    public PasswordDialogFragment setAmount(String amount) {
        this.amount = amount;
        return this;
    }

    /**
     * @param unit 单位
     */
    public PasswordDialogFragment setUnit(String unit) {
        this.unit = unit;
        return this;
    }

    /**
     * @param title 标题
     */
    public PasswordDialogFragment setTitle(String title) {
        this.title = title;
        return this;
    }

    /**
     * @param cancel 取消按钮
     */
    public PasswordDialogFragment setCancel(String cancel) {
        this.cancel = cancel;
        return this;
    }

    /**
     * @param confirm 确认
     */
    public PasswordDialogFragment setConfirm(String confirm) {
        this.confirm = confirm;
        return this;
    }

    /**
     * 设置header不显示
     */
    public PasswordDialogFragment setHeaderVisible(boolean appearHeader) {
        this.appearHeader = appearHeader;
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
                String passWord = mGpvPassword.getPassWord();
                if (TextUtils.isEmpty(passWord)) {
                    RxBus.Companion.getInstance().post(new BaseEvent(Const.SHOW_TOAST, getString(R.string.dialog_password_input_no_pwd)));
                    return;
                }
                if (!passWord.matches(String.format(Locale.CHINA, "^[\\d]{%d}$", mGpvPassword.getPasswordLength()))) {
                    RxBus.Companion.getInstance().post(new BaseEvent(Const.SHOW_TOAST, getString(R.string.dialog_password_input_pwd_error)));
                    return;
                }
                if (listener.onPasswordConfirm(getDialog(), passWord)) {
                    return;
                }
        }

        getDialog().dismiss();
    }

    /**
     * Invoked when the password changed.
     *
     * @param psw new text
     */
    @Override
    public void onTextChanged(String psw) {
        if (listener != null) {
            listener.onPasswordChanged(getDialog(), psw);
        }
    }

    /**
     * Invoked when the password is at the maximum length.
     *
     * @param psw complete text
     */
    @Override
    public void onInputFinish(String psw) {

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

        /**
         * 当密码框中内容有变化时回调
         * <p>
         * 可以主动判断是否输入了足够的位数,然后自动进行界面跳转
         *
         * @param dialog 弹出框
         * @param psw    密码
         */
        default void onPasswordChanged(Dialog dialog, String psw) {
        }
    }
}
