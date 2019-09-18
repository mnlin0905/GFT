package com.linktech.gft.base;

import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;

import com.linktech.gft.R;
import com.linktech.gft.plugins.DaggerComponent;
import com.linktech.gft.plugins.DaggerConfigsKt;
import com.linktech.gft.plugins.RxBus;
import com.linktech.gft.util.Const;
import com.linktech.gft.util.DefaultPreferenceUtil;

import org.jetbrains.annotations.NotNull;

import kotlin.NotImplementedError;
import skin.support.content.res.SkinCompatResources;

/**
 * function : 基础View，MVP专用
 *
 * @author LinkTech
 * @date 2017/12/1
 */

public interface BaseView {
    /////////////////////////////////////////////
    /////////////////////////////////////////// 通用部分
    ////////////////////////////////////////////

    /**
     * @return 获取活动对象
     */
    @NotNull
    default BaseActivity getBaseActivity() {
        throw new NotImplementedError("error");
    }

    /**
     * @return 获取碎片管理器
     */
    @NotNull
    default FragmentManager getSFManager() {
        throw new NotImplementedError("error");
    }

    /**
     * @param msg 显示toast
     * @return 恒为 false,默认的返回值
     */
    default boolean showToast(String msg) {
        RxBus.Companion.getInstance().post(new BaseEvent(Const.SHOW_TOAST, msg == null ? "null" : msg));
        return false;
    }

    /**
     * 恒为 false,默认的返回值
     */
    default void showToast(@StringRes int resId, Object... formatArgs) {
        showToast(dispatchGetString(resId, formatArgs));
    }

    /**
     * @param msg 需要显示的toast消息(比较积极的情况下)
     */
    default void showBigToast(String msg) {
        RxBus.Companion.getInstance().post(new BaseEvent(Const.SHOW_BIG_TOAST, msg == null ? "null" : msg));
    }

    /**
     * 需要显示的toast消息(比较积极的情况下)
     */
    default void showBigToast(@StringRes int resId, Object... formatArgs) {
        showBigToast(dispatchGetString(resId, formatArgs));
    }

    /**
     * dagger框架自动注入
     */
    default void daggerAutoInject(DaggerComponent component) {
        DaggerConfigsKt.autoInjectBaseView(this, component);
    }

    /////////////////////////////////////////////
    /////////////////////////////////////////// 用于获取资源文件
    ////////////////////////////////////////////

    /**
     * 获取drawable
     */
    default String dispatchGetString(@StringRes int resId, Object... formatArgs) {
        return BaseApplication.app.getResources().getString(resId, formatArgs);
    }

    /**
     * 获取drawable
     */
    default Drawable dispatchGetDrawable(@DrawableRes int resId) {
        if (Build.VERSION.SDK_INT >= 21) {
            return BaseApplication.app.getDrawable(resId);
        } else {
            return BaseApplication.app.getResources().getDrawable(resId);
        }
    }


    /**
     * 获取颜色值
     */
    default int dispatchGetColor(@ColorRes int resId) {
        if (Build.VERSION.SDK_INT < 23) {
            return BaseApplication.app.getResources().getColor(resId);
        } else {
            return BaseApplication.app.getResources().getColor(resId, null);
        }
    }

    /**
     * 获取颜色值
     */
    default int dispatchGetDimen(@DimenRes int resId) {
        return BaseApplication.app.getResources().getDimensionPixelSize(resId);
    }

    /**
     * 获取stateList
     */
    default ColorStateList dispatchGetColorStateList(@ColorRes int resId) {
        if (Build.VERSION.SDK_INT < 23) {
            return BaseApplication.app.getResources().getColorStateList(resId);
        } else {
            return BaseApplication.app.getResources().getColorStateList(resId, null);
        }
    }

    /**
     * 获取系统属性中某个值
     */
    default int getThemeColorAttribute(int styleRes, int colorId) {
        int defaultColor = dispatchGetColor(R.color.transparent);
        int[] attrsArray = {colorId};
        TypedArray typedArray = BaseApplication.app.obtainStyledAttributes(styleRes, attrsArray);
        int color = typedArray.getColor(0, defaultColor);

        typedArray.recycle();
        return color;
    }

    /////////////////////////////////////////////
    /////////////////////////////////////////// 网络调用时获取隐藏数据信息
    ////////////////////////////////////////////

    /**
     * 快捷获取username和signature
     */
    @Nullable
    default String get_username() {
        return DefaultPreferenceUtil.getInstance().getUsername();
    }

    /**
     * 快捷获取username和signature
     */
    @Nullable
    default void set_username(String username) {
        DefaultPreferenceUtil.getInstance().setUsername(username);
    }

    /**
     * 快捷获取username和signature
     */
    @Nullable
    default String get_signature() {
        return DefaultPreferenceUtil.getInstance().getSignature();
    }

    /**
     * 昵称
     */
    @Nullable
    default String get_nickName() {
        return BasePresenter.singleUserInfo.getNickname();
    }

    /**
     * 手机号
     */
    @Nullable
    default String get_phone() {
        return BasePresenter.singleUserInfo.getMobile();
    }

    /**
     * 用户id
     */
    @Nullable
    default int get_user_id() {
        return BasePresenter.singleUserInfo.getUserId();
    }

    /**
     * 是否签到
     */
    @Nullable
    default boolean is_signIn() {
        return BasePresenter.singleUserInfo.is_signIn();
    }

    /**
     * 真实姓名，首位*
     */
    @Nullable
    default String get_name() {
        return "*" + BasePresenter.singleUserInfo.getName().substring(1);
    }

    /**
     * 邮箱
     */
    @Nullable
    default String get_email() {
        return BasePresenter.singleUserInfo.getEmail();
    }

    /**
     * 是否为登录状态
     */
    default boolean get_status_login() {
        return DefaultPreferenceUtil.getInstance().getLogin();
    }

    /**
     * 是否绑定手机号
     */
    default boolean get_status_moblie() {
        return BasePresenter.singleUserInfo.getHas_mobile();
    }

    /**
     * 是否设置安全问题
     */
    @Deprecated
    default boolean get_status_has_safe_question() {
        return false;
    }

    /**
     * 是否设置交易密码
     */
    default boolean get_status_set_pay_pwd() {
        return BasePresenter.singleUserInfo.getHas_payPwd();
    }

    /**
     * 是否实名认证
     */
    @Deprecated
    default boolean get_status_identification_success() {
        return false;
    }

    /**
     * 获取实名认证状态  0-未认证，1-已认证 2:认证失败  3:待认证
     */
    default int get_identification_status() {
        return BasePresenter.singleUserInfo.getCertification_status();
    }

    /**
     * 获取实名认证状态  0-未认证，1-已认证 2:认证失败  3:审核中
     */
    default boolean get_status_identification() {
        int status = BasePresenter.singleUserInfo.getCertification_status();
        return status == 1;
    }

    /**
     * 获取券商加密key
     */
    default String get_trade_key() {
        return BaseApplication.tradeKey;
    }

    /**
     * 获取登录返回标志
     */
    default String get_auth_code() {
        return BaseApplication.authCode;
    }

    /**
     * 是否登录券商
     */
    default boolean is_trade_login() {
        return BaseApplication.isTradeLogin;
    }

    /**
     * 是否红涨绿跌
     */
    default boolean is_red_up() {
        return DefaultPreferenceUtil.getInstance().isRedUpMode();
    }

    /**
     * 获取红涨绿跌|绿涨红跌的颜色
     */
    @ColorRes
    default int getStockColor(String text) {
        if (TextUtils.isEmpty(text)) {
            return 0;
        } else if (text.contains("-")) {
            return is_red_up() ? R.color.dark_color_20bf7c : R.color.dark_color_f85d5a;
        } else {
            return is_red_up() ? R.color.dark_color_f85d5a : R.color.dark_color_20bf7c;
        }
    }

    /**
     * 获取颜色值
     */
    @ColorInt
    default int dispatchGetStockColor(@Nullable String text) {
        if (TextUtils.isEmpty(text)) {
            return SkinCompatResources.getColor(BaseApplication.app,R.color.skin_body1_color );
        } else if (text.contains("-")) {
            return dispatchGetColor(is_red_up() ? R.color.dark_color_20bf7c : R.color.dark_color_f85d5a);
        } else {
            return dispatchGetColor(is_red_up() ? R.color.dark_color_f85d5a : R.color.dark_color_20bf7c);
        }
    }

    /**
     * 获取股票背景应该取值的效果
     */
    @Nullable
    default Drawable dispatchGetStockColorDrawable(Boolean isRateUp) {
        if (isRateUp == null) {
            return null;
        } else{
            return is_red_up() == isRateUp ? BaseApplication.app.RED_DRAWABLE : BaseApplication.app.GREEN_DRAWABLE;
        }
    }

    /**
     * 根据类型和当前价格匹配最小变动价格
     */
    default double getUintPrice(String type, Double price) {
        if (type == null) return 0.050;
        if (type.equals("03")) return 0.050;
        if (type.equals("01")) {
            if (price >= 0.01 && price <= 0.25) return 0.001;
            if (price > 0.25 && price <= 0.50) return 0.005;
            if (price > 0.50 && price <= 10.00) return 0.010;
            if (price > 10.00 && price <= 20.00) return 0.020;
            if (price > 20.00 && price <= 100.00) return 0.050;
            if (price > 100.00 && price <= 200.00) return 0.100;
            if (price > 200.00 && price <= 500.00) return 0.200;
            if (price > 500.00 && price <= 1000.00) return 0.500;
            if (price > 1000.00 && price <= 2000.00) return 1.000;
            if (price > 2000.00 && price <= 5000.00) return 2.000;
            if (price > 5000.00 && price <= 9995.00) return 5.000;
        }
        return 0.000;
    }

    /**
     * 获取券商类型的显示
     */
    default String getTradeTypeStr() {
        String str = "";
        switch (BaseApplication.tradeType) {
            case "0":
                str = "港股现金";
                break;
            case "1":
                str = "港股融资";
                break;
        }
        return str;
    }

    /**
     * 获取语言目录选项
     */
    @NotNull
    default String get_h5_language_str() {
        int current = DefaultPreferenceUtil.getInstance().getLocaleLanguageSwitch();
        if (current == 1) {
            return "zh";
        } else
            return "tw";
    }
    /**
     * 登陆券商的账号
     */
    @Nullable
    default String get_account() {
        return BaseApplication.account;
    }
}
