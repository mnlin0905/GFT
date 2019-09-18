package com.linktech.gft.util;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.linktech.gft.R;
import com.linktech.gft.base.BaseApplication;
import com.linktech.gft.base.BasePresenter;
import com.linktech.gft.bean.UserBaseInfoBean;
import com.linktech.gft.plugins.PlusFunsPluginsKt;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

/**
 * Created on 2018/1/18
 * function :
 *
 * @author MNLIN
 */

public class DefaultPreferenceUtil {
    /**
     * 登录状态:是否已经登录
     * 登录的signature:登录有效期
     * 账户:手机号/邮箱
     * 自己的id
     * 正事/测试环境
     * 是否备份了私钥
     * 系统默认语言环境
     */
    private static final String LOGIN_STATUS = "account_login_status";
    private static final String LOGIN_SIGNATURE = "account_login_signature";
    private static final String LOGIN_ACCOUNT = "login_account";
    private static final String RY_TOKEN = "ry_token";
    private static final String IS_OFFICIAL_MODE = "IS_OFFICIAL_MODE";
    private static final String USER_INFO_BEAN = "user_info_bean";
    private static final String LOCALE_LANGUAGE_SWITCH = "locale_language_switch";

    /**
     * 锁屏方式:
     * <p>
     * 指纹
     * 刷脸
     * 手势
     * 手势轨迹
     */
    private static final String LOCK_SCREEN_FINGERPRINT = "lock_screen_fingerprint_";
    private static final String LOCK_SCREEN_FACE = "lock_screen_face_";
    private static final String LOCK_SCREEN_GESTURE = "lock_screen_gesture_";
    private static final String LOCK_SCREEN_GESTURE_TRAVEL = "lock_screen_gesture_travel_";

    /**
     * 上次开启时,指纹摘要
     */
    private static final String KEY_FINGERPRINT_MD5 = "key_fingerprint_md5";

    /**
     * 是否为第一次导出keystore
     */
    private static final String IS_FIRST_EXPORT_KEYSTORE = "is_first_export_keystore";
    private static final String IS_FIRST_INSTALL = "is_first_install";

    /**
     * 当前主账户钱包地址
     */
    private static final String CURRENCY_ADDRESS = "CURRENCY_ADDRESS";

    /**
     * 是否白天模式
     */
    private static final String IS_WHITE_MODEL = "is_white_model";
    private static final String IS_RED_UP = "is_red_up";

    private static DefaultPreferenceUtil singleInstance = new DefaultPreferenceUtil();

    /**
     * 编辑对象
     */
    private final SharedPreferences.Editor edit;
    private final SharedPreferences preferences;

    @SuppressLint("CommitPrefEdits")
    private DefaultPreferenceUtil() {
        preferences = PreferenceManager.getDefaultSharedPreferences(BaseApplication.app);
        edit = preferences.edit();
    }

    /**
     * @return 获取单例对象
     */
    @NotNull
    public static DefaultPreferenceUtil getInstance() {
        return singleInstance;
    }

    /**
     * @param context 上下文
     * @param path    后缀路径
     * @return 随机生成存储图片的文件
     */
    @Nullable
    public static File getRandomPictureFile(Context context, String path, boolean forbidPhotoWithoutSD) {
        return getRandomPictureFile(context, path, String.valueOf(System.currentTimeMillis()), forbidPhotoWithoutSD);
    }

    /**
     * @param context 上下文
     * @param path    后缀路径
     * @param name    文件名
     * @return 随机生成存储图片的文件
     */
    @Nullable
    public static File getRandomPictureFile(Context context, String path, String name, boolean forbidPhotoWithoutSD) {
        if (TextUtils.isEmpty(name)) {
            return null;
        }

        //优先选择外部存储,没有的话选择内部存储
        String basePath;
        if (Environment.isExternalStorageEmulated()) {
            basePath = Environment.getExternalStorageDirectory().getPath();
        } else {
            if (forbidPhotoWithoutSD) {
                PlusFunsPluginsKt.toast(null, R.string.common_cannot_create_file);
                return null;
            }
            basePath = context.getCacheDir().getPath();
        }

        try {
            File parent = new File(basePath + path);
            if (!parent.exists()) {
                if (!parent.mkdirs()) {
                    PlusFunsPluginsKt.toast(null, R.string.common_cannot_create_file);
                    return null;
                }
            }
            File file = new File(basePath + path + "/" + name + ".jpg");
            if (!file.exists()) {
                if (!file.createNewFile()) {
                    PlusFunsPluginsKt.toast(null, R.string.common_cannot_create_file);
                    return null;
                }
            }
            com.orhanobut.logger.Logger.d("新生成文件路径为:" + file.getPath());
            return file;
        } catch (IOException e) {
            PlusFunsPluginsKt.toast(null, R.string.common_cannot_create_file);
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @param context 上下文
     * @param path    后缀路径
     * @return 随机生成存储图片的文件
     */
    @Nullable
    public static File getRandomExportFile(Context context, String path) {
        //优先选择外部存储,没有的话选择内部存储
        String basePath;
        if (Environment.isExternalStorageEmulated()) {
            basePath = Environment.getExternalStorageDirectory().getPath();
        } else if (context.getExternalCacheDir() != null) {
            basePath = context.getExternalCacheDir().getPath();
        } else if (context.getExternalFilesDir(DIRECTORY_DOWNLOADS) != null) {
            basePath = context.getExternalFilesDir(DIRECTORY_DOWNLOADS).getPath();
        } else {
            return null;
        }

        try {
            File parent = new File(basePath + path);
            if (!parent.exists()) {
                if (!parent.mkdirs()) {
                    return null;
                }
            }
            File file = new File(basePath
                    + path
                    + "/export_"
                    + new SimpleDateFormat("yyyy_MM", Locale.CHINA).format(new Date())
                    + ".txt");
            if (!file.exists()) {
                if (!file.createNewFile()) {
                    return null;
                }
            }
            com.orhanobut.logger.Logger.d("新生成文件路径为:" + file.getPath());
            return file;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @param context 上下文
     * @param path    后缀路径
     * @return 随机生成存储图片的文件
     */
    @Nullable
    public static File getRandomPictureFile(Context context, String path) {
        return getRandomPictureFile(context, path, false);
    }

    /**
     * 手势锁屏
     */
    @Nullable
    public String getScreenGestureLocked(int userId) {
        return preferences.getString(LOCK_SCREEN_GESTURE + userId, null);
    }

    /**
     * 手势锁屏
     */
    public synchronized boolean setScreenGestureLocked(int userId, String gesture) {
        return edit.putString(LOCK_SCREEN_GESTURE + userId, gesture).commit();
    }

    /**
     * 手势锁屏 轨迹
     */
    public boolean isScreenGestureLockedTravel(int userId) {
        return preferences.getBoolean(LOCK_SCREEN_GESTURE_TRAVEL + userId,true);
    }

    /**
     * 手势锁屏 轨迹
     */
    public synchronized boolean setScreenGestureLockedTravel(int userId, boolean gesture) {
        return edit.putBoolean(LOCK_SCREEN_GESTURE_TRAVEL + userId, gesture).commit();
    }

    /**
     * 脸部锁屏
     */
    public boolean isScreenFaceLocked(int userId) {
        return preferences.getBoolean(LOCK_SCREEN_FACE + userId, false);
    }

    /**
     * 脸部锁屏
     */
    public synchronized boolean setScreenFaceLocked(int userId, boolean bool) {
        return edit.putBoolean(LOCK_SCREEN_FACE + userId, bool).commit();
    }

    /**
     * 指纹锁屏
     */
    public boolean isScreenFingerprintLocked(int userId) {
        return preferences.getBoolean(LOCK_SCREEN_FINGERPRINT + userId, false);
    }

    /**
     * 指纹锁屏
     */
    public synchronized boolean setScreenFingerprintLocked(int userId, boolean bool) {
        return edit.putBoolean(LOCK_SCREEN_FINGERPRINT + userId, bool).commit();
    }

    /**
     * @return true 表示已经登录
     */
    public boolean getLogin() {
        return preferences.getBoolean(LOGIN_STATUS, false);
    }

    /**
     * 设置或者清除登录状态
     *
     * @param login true表示设置为登录状态
     * @return true if the new values were successfully written to persistent storage
     */
    public synchronized boolean setLogin(boolean login) {
        return edit.putBoolean("account_login_status", login).commit();
    }

    /**
     * 指纹md5
     */
    @Nullable
    public synchronized String getFingerprintMD5() {
        return preferences.getString(KEY_FINGERPRINT_MD5, null);
    }

    /**
     * 指纹md5
     */
    public synchronized boolean setFingerprintMD5(String md5) {
        return edit.putString(KEY_FINGERPRINT_MD5, md5).commit();
    }


    /**
     * @return String类型, signature,null表示没有保存signature或者signature失效
     */
    @Nullable
    public synchronized String getSignature() {
        return preferences.getString(LOGIN_SIGNATURE, null);
    }

    /**
     * 设置或者清除登录signature
     *
     * @param signature null表示清除signature
     * @return true if the new values were successfully written to persistent storage
     */
    public synchronized boolean setSignature(String signature) {
        return edit.putString(LOGIN_SIGNATURE, signature).commit();
    }

    /**
     * @return String类型, account,null表示没有保存帐号
     */
    @Nullable
    public String getUsername() {
        return preferences.getString(LOGIN_ACCOUNT, null);
    }

    /**
     * 设置或者清除登录帐号
     *
     * @param username null表示清除帐号(用户名,可能为手机或者邮箱)
     * @return true if the new values were successfully written to persistent storage
     */
    public synchronized boolean setUsername(String username) {
        return edit.putString(LOGIN_ACCOUNT, username).commit();
    }

    /**
     * 设置或者清除memberId
     *
     * @return true if the new values were successfully written to persistent storage
     */
    public synchronized boolean setRYToken(String token) {
        return edit.putString(RY_TOKEN, token).commit();
    }

    /**
     * @return String类型, token,null表示没有保存帐号
     */
    public String getRYToken() {
        return preferences.getString(RY_TOKEN, null);
    }

    /**
     * 默认是正式环境
     *
     * @return true表示正事环境, false表示测试环境
     */
    public boolean isOfficialMode() {
        return preferences.getBoolean(IS_OFFICIAL_MODE, BaseApplication.app.getResources().getBoolean(R.bool.switch_official_mode));
    }

    /**
     * 是否为初次启动
     *
     * @return true表示正事环境, false表示测试环境
     */
    public boolean isFirstInstall() {
        return preferences.getBoolean(IS_FIRST_INSTALL, true);
    }

    /**
     * 初次启动
     */
    public boolean setFirstInstall(boolean isFirst) {
        return edit.putBoolean(IS_FIRST_INSTALL, isFirst).commit();
    }

    /**
     * 获取钱包地址
     */
    @Nullable
    public String getCurrencyAddress() {
        return preferences.getString(CURRENCY_ADDRESS, null);
    }

    /**
     * 是否保存成功
     */
    public boolean setCurrencyAddress(@Nullable String address) {
        return edit.putString(CURRENCY_ADDRESS, address).commit();
    }

    /**
     * 第一次导出keystore
     */
    @Nullable
    public boolean getIsFirstExportKeystore() {
        return preferences.getBoolean(IS_FIRST_EXPORT_KEYSTORE, true);
    }

    /**
     * 第一次导出keystore
     */
    public boolean setIsFirstExportKeystore(boolean isFirst) {
        return edit.putBoolean(IS_FIRST_EXPORT_KEYSTORE, isFirst).commit();
    }

    /**
     * @return 从xml中恢复对象
     */
    public synchronized UserBaseInfoBean createUserInfoBean() {
        String string = preferences.getString(USER_INFO_BEAN, null);
        if (TextUtils.isEmpty(string)) {
            return new UserBaseInfoBean();
        }
        return new Gson().fromJson(string, UserBaseInfoBean.class);
    }

    /**
     * 保存对象
     */
    public synchronized void saveUserInfoBean(UserBaseInfoBean bean, @NonNull BasePresenter.HttpCallback<Exception> callback) {
        edit.putString(USER_INFO_BEAN, new Gson().toJson(bean)).commit();
        callback.run(null);
    }

    /**
     * 切換語言環境
     * <p>
     * 0:未设置 或 跟随系统变化
     * 1:简体中文-中国大陆
     * 2:繁体中文-中国台湾
     * 3:繁体中文-中国香港
     * 4:英语-全体-English
     */
    @NotNull
    public int getLocaleLanguageSwitch() {
        return preferences.getInt(LOCALE_LANGUAGE_SWITCH, 3);
    }

    /**
     * 切換語言環境
     * <p>
     * 中文/英文/台湾/香港/...
     */
    public boolean setLocaleLanguageSwitch(@NotNull @IntRange(from = 0, to = 4) int locale) {
        return edit.putInt(LOCALE_LANGUAGE_SWITCH, locale).commit();
    }

    /**
     * 是否白天模式 默认false
     */
    public boolean setWhiteMode(boolean isWhite) {
        return edit.putBoolean(IS_WHITE_MODEL, isWhite).commit();
    }

    /**
     * 是否白天模式 默认false
     */
    public boolean isWhiteMode() {
        return preferences.getBoolean(IS_WHITE_MODEL, false);
    }

    /**
     * 是否是红涨绿跌 默认true
     */
    public boolean setRedUpMode(boolean isRedUp) {
        return edit.putBoolean(IS_RED_UP, isRedUp).commit();
    }

    /**
     * 是否是红涨绿跌 默认true
     */
    public boolean isRedUpMode() {
        return preferences.getBoolean(IS_RED_UP, true);
    }


}
