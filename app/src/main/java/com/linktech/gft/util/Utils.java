package com.linktech.gft.util;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

import com.blankj.utilcode.util.StringUtils;
import com.linktech.gft.R;
import com.linktech.gft.plugins.PlusFunsPluginsKt;

import org.jetbrains.annotations.Contract;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.RegEx;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * @author 小任
 * @date 2017/4/29
 * version 1.0
 * 描述:
 */

public class Utils {

    /**
     * drawable 拿到路径
     */
    public static String getResourcesUrl(Context context, @DrawableRes int id) {
        Resources resources = context.getResources();
        String uriPath = ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
                resources.getResourcePackageName(id) + "/" +
                resources.getResourceTypeName(id) + "/" +
                resources.getResourceEntryName(id);

        L.i("uriPath=" + uriPath);
        return uriPath;
    }

    /**
     * 设置状态栏字体图标为深色，需要MIUIV6以上
     *
     * @param window 需要设置的窗口
     * @param dark   是否把状态栏字体及图标颜色设置为深色
     * @return boolean 成功执行返回true
     */
    public static boolean MIUISetStatusBarLightMode(Window window, boolean dark) {
        boolean result = false;
        if (window != null) {
            Class clazz = window.getClass();
            try {
                int darkModeFlag = 0;
                Class layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
                Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
                darkModeFlag = field.getInt(layoutParams);
                Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
                if (dark) {
                    extraFlagField.invoke(window, darkModeFlag, darkModeFlag);//状态栏透明且黑色字体
                } else {
                    extraFlagField.invoke(window, 0, darkModeFlag);//清除黑色字体
                }
                result = true;
            } catch (Exception e) {

            }
        }
        return result;
    }

    /**
     * 设置状态栏字体图标为深色，需要MIUIV6以上
     *
     * @param window 需要设置的窗口
     * @param dark   是否把状态栏字体及图标颜色设置为深色
     * @return boolean 成功执行返回true
     */
    public static void MIUISetStatusBarLightMode2(Window window, boolean dark) {
        if (dark) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        } else {
            int flag = window.getDecorView().getSystemUiVisibility() & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            window.getDecorView().setSystemUiVisibility(flag);
        }
    }

    /**
     * 设置状态栏图标为深色和魅族特定的文字风格
     * 可以用来判断是否为Flyme用户
     *
     * @param window 需要设置的窗口
     * @param dark   是否把状态栏字体及图标颜色设置为深色
     * @return boolean 成功执行返回true
     */
    public static boolean FlymeSetStatusBarLightMode(Window window, boolean dark) {
        boolean result = false;
        if (window != null) {
            try {
                WindowManager.LayoutParams lp = window.getAttributes();
                Field darkFlag = WindowManager.LayoutParams.class
                        .getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
                Field meizuFlags = WindowManager.LayoutParams.class
                        .getDeclaredField("meizuFlags");
                darkFlag.setAccessible(true);
                meizuFlags.setAccessible(true);
                int bit = darkFlag.getInt(null);
                int value = meizuFlags.getInt(lp);
                if (dark) {
                    value |= bit;
                } else {
                    value &= ~bit;
                }
                meizuFlags.setInt(lp, value);
                window.setAttributes(lp);
                result = true;
            } catch (Exception e) {

            }
        }
        return result;
    }

    public static void androidBarLightMode(Window window, boolean dark) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (dark) {
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            } else {
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            }
        }
    }

    public static boolean setStatusBarLightMode(Window window, boolean dark) {
        boolean b = MIUISetStatusBarLightMode(window, dark);
        MIUISetStatusBarLightMode2(window, dark);
        boolean b1 = FlymeSetStatusBarLightMode(window, dark);
        if (!(b || b1)) {
            androidBarLightMode(window, dark);
        }
        return b || b1;
    }

    //不用科学计数法
    public static String formatBalance(double price) {
        NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setGroupingUsed(false);
        numberFormat.setMaximumFractionDigits(6);
        String format = numberFormat.format(price);
        return format;
    }

    public static String formatOtherCurrency(String balance, double precision) {
        //icon_return formatBalance(CalculateUtil.divide(Double.valueOf(balance), Math.pow(10, precision)));
        BigDecimal divide = new BigDecimal(balance).divide(new BigDecimal(Math.pow(10, precision)));
        return divide.toString();
    }

    /**
     * 乘以10的n次方
     *
     * @param balance
     * @param precision
     */
    public static String formatCurrency(String balance, double precision) {
        //icon_return formatBalance(CalculateUtil.divide(Double.valueOf(balance), Math.pow(10, precision)));
        BigDecimal divide = new BigDecimal(balance).multiply(new BigDecimal(Math.pow(10, precision)));
        return divide.toString();
    }

    /**
     * 获取状态栏高度
     *
     * @param context context
     * @return 状态栏高度
     */
    public static int getStatusBarHeight(Context context) {
        // 获得状态栏高度
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        return context.getResources().getDimensionPixelSize(resourceId);
    }

    /**
     * 默认得显示方式 ，先保留5位小数，再去掉多余得0
     */
    public static String getDefaultShow(String number) {
        return getDefaultShow(Double.parseDouble(number));
    }

    /**
     * 默认得显示方式 ，先保留5位小数，再去掉多余得0
     */
    public static String getDefaultShow(double number) {
        String doubleString = getDoubleString(number, 5);
        return subZeroAndDot(doubleString);
    }

    /**
     * 人名币默认得显示方式 ，先保留2位小数，再去掉多余得0
     */
    public static String getRmbShow(double number) {
        String doubleString = getDoubleString(number, 2);
        return subZeroAndDot(doubleString);
    }

    /**
     * 对一个数取指定的小数位，并转为String形式
     *
     * @param number 需要被转换的数
     * @param digit  小数位数
     * @return 字符串形式的数
     */
    public static String getDoubleString(double number, int digit) {
        return String.format(Locale.CHINA, "%." + digit + "f", number);
    }

    /**
     * 对一个数取指定的小数位，并转为String形式
     *
     * @param number 需要被转换的数
     * @param digit  小数位数
     * @return 字符串形式的数
     */
    public static String getDoubleString(String number, int digit) {
        try {
            return getDoubleString(Double.parseDouble(number), digit);
        } catch (Exception e) {
            return number;
        }
    }

    /**
     * 使用java正则表达式去掉多余的.与0
     *
     * @param s
     * @return
     */
    public static String subZeroAndDot(String s) {
        if (s.indexOf(".") > 0) {
            s = s.replaceAll("0+?$", "");//去掉多余的0
            s = s.replaceAll("[.]$", "");//如最后一位是.则去掉
        }
        return s;
    }

    /**
     * 浮点数格式化
     *
     * @param isPercentage 是否为分数
     * @param needSign     是否需要正号
     * @param isMoney      是否使用金钱格式(每3位用","分隔)
     * @param digit        需要保留的位数
     * @param num          需要格式化的值(int、float、double、String、byte均可)
     */
    @NonNull
    public static String format(boolean isPercentage, boolean needSign, boolean isMoney, int digit, Object num) {
        digit = digit < 0 ? 0 : digit;
        if (num == null) num = 0;

        double converted;//需求大多四舍五入  float保留位数会舍去后面的
        try {
            converted = Double.parseDouble(num.toString());
        } catch (Throwable e) {
            Log.e("FormatErr", "Input number is not kind number");
            converted = 0d;
        }
        StringBuilder sb = new StringBuilder("%");

        if (needSign && (converted > 0)) {
            sb.append("+");
        }

        if (isMoney) {
            sb.append(",");
        }

        sb.append(".").append(digit).append("f");

        if (isPercentage) {
            sb.append("%%");
        }

        return String.format(Locale.getDefault(), sb.toString(), converted);
    }

    public static String numFormat(Object num, int digit) {
        return numFormat(false, digit, num);
    }

    public static String numFormat(boolean needSign, int digit, Object num) {
        return format(false, needSign, false, digit, num);
    }

    /**
     * 保留四位小数，直接去掉尾数
     */
    public static double getFourDouble(double d) {
        BigDecimal b = new BigDecimal(d);
        d = b.setScale(4, BigDecimal.ROUND_DOWN).doubleValue();
        return d;
    }

    /**
     * 保留四位位小数且不用科学计数法，若没有小数不补0
     *
     * @param d 数据源
     * @return 字符串
     */
    public static String formateDouble(double d) {
        DecimalFormat df = new DecimalFormat("#.####");
        String value = df.format(d);
        return value;
    }

    /**
     * 创建double类型的输入过滤器
     * <p>
     * 可通过设置如下属性保证EditText无法粘贴和选取
     * edittext.setLongClickable(false);
     * edittext.setTextIsSelectable(false);
     *
     * @param digit 小数最多为digit位
     * @return 返回过滤器
     */
    @NonNull
    @Contract(pure = true)
    public static InputFilter createDoubleInputFilter(final int digit) {
        return (source, start, end, dest, dstart, dend) -> {
            try {
                String destStr = dest.toString();
                String sourceStr = source.toString();

                //禁止粘贴
                /*if (sourceStr.length() > 1) {
                    return "";
                }*/

                //判断输入的字符是否为数字或者小数点
                if (!sourceStr.matches("[\\d.]*")) {
                    return "";
                }

                //目标将要生成的字符串
                String reg;
                if (digit > 0) {
                    reg = "^(([1-9][^.]*)|0)(\\.[\\d]{0," + digit + "})?$";
                } else {
                    reg = "^(([1-9][^.]*)|0)$";
                }
                String finalString = destStr.substring(0, dstart) + source + destStr.substring(dend);
                if (!finalString.matches(reg)) {
                    //如果组合后生成字符串不匹配规则,则也禁止输入
                    return "";
                }

                return source;
            } catch (Exception e) {
                e.printStackTrace();
                return "";
            }
        };
    }

    /**
     * 创建最小单位输入过滤器
     */
    @NonNull
    @Contract(pure = true)
    public static InputFilter createUnitFilter(final double unit) {
        return new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                try {
                    String destStr = dest.toString();
                    String sourceStr = source.toString();

                    //判断输入的字符是否为数字或者小数点
                    if (!sourceStr.matches("[\\d.]*")) {
                        return "";
                    }

//                    //目标将要生成的字符串
//                    String reg;
//                    if (digit > 0) {
//                        reg = "^(([1-9][^.]*)|0)(\\.[\\d]{0," + digit + "})?$";
//                    } else {
//                        reg = "^(([1-9][^.]*)|0)$";
//                    }
//                    String finalString = destStr.substring(0, dstart) + source + destStr.substring(dend);
//                    if (!finalString.matches(reg)) {
//                        //如果组合后生成字符串不匹配规则,则也禁止输入
//                        return "";
//                    }

                    return source;
                } catch (Exception e) {
                    e.printStackTrace();
                    return "";
                }
            }
        };
    }

    /**
     * 给edittext设置过滤器 过滤emojilayout_line_menu
     */
    @NonNull
    @Contract(pure = true)
    public static InputFilter setFilter() {
        return (source, start, end, dest, dstart, dend) -> {
            Pattern pattern = Pattern.compile("[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]", Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(source);
            if (matcher.find()) {
                PlusFunsPluginsKt.toast(null, R.string.activity_change_nick_name_tip);
                return "";
            } else return source;
        };
    }

    /**
     * @return 将int数组以split为间隔组合成字符串
     */
    @Contract(value = "null, _ -> !null; !null, null -> !null", pure = true)
    public static String intArrayJionWithSplit(int[] array, String split) {
        if (array != null && split != null && array.length > 0) {
            String result = array[0] + "";
            for (int i = 1; i < array.length; i++) {
                result += split + array[i];
            }
            return result;
        }
        return "";
    }

    /**
     * @return 将int数组以split为间隔组合成字符串
     */
    @Contract("null, _ -> !null; !null, null -> !null")
    public static <T extends Object> String objectsJionWithSplit(ArrayList<T> array, String split) {
        if (array != null && split != null && array.size() > 0) {
            String result = array.get(0).toString();
            for (int i = 1; i < array.size(); i++) {
                result += split + array.get(i).toString();
            }
            return result;
        }
        return "";
    }

    /**
     * @return 将String以split为间隔分割为int数组
     */
    public static int[] stringSplitToIntArray(String result, @RegEx String split) {
        try {
            if (result != null && split != null && result.length() > 0) {
                String[] array = result.split(split);
                if (array.length > 0) {
                    int[] int_array = new int[array.length];
                    for (int i = 0; i < array.length; i++) {
                        int_array[i] = Integer.parseInt(array[i]);
                    }
                    return int_array;
                }
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return new int[0];
    }

    public static boolean matchLoginPwd(String password) {
        String regex = "[a-zA-Z0-9]{6,12}";
        return password.matches(regex) && !password.matches("^[\\d]+$|^[a-zA-Z]+$");
    }

    /**
     * 隐藏邮箱：只显示@前面的首位和末位
     *
     * @param email
     * @return
     */
    public static String emailHide(String email) {
        return StringUtils.isEmpty(email) ? "" : email.replaceAll("(\\w{2})(\\w+)(\\w)(@\\w+\\.[a-z]+(\\.[a-z]+)?)", "$1*****$3$4");
    }

    /**
     * 禁止EditText输入空格
     *
     * @param editText
     */
    public static void setEditTextInhibitInputSpace(EditText editText) {
        InputFilter filter = (source, start, end, dest, dstart, dend) -> {
            if (" ".equals(source)) {
                return "";
            } else {
                return null;
            }
        };
        editText.setFilters(new InputFilter[]{filter});
    }

    /**
     * 判断钱包地址是否正确
     */
    public static boolean isCorrectWalletAddress(String currency, String wallet) {
        String regACC = "^[A][A-Za-z\\d]{32,33}$";
        String regBTC = "^[1,3][A-Za-z0-9]{33}$";
        String regETH = "^[0][x][A-Za-z0-9]{40}$";

        switch (currency) {
            case "ACC":
                return wallet.matches(regACC);
            case "BTC":
                return wallet.matches(regBTC);
            case "ETH":
                return wallet.matches(regETH);
        }
        return false;
    }

    /**
     * make a drawable to a bitmap
     *
     * @param drawable drawable you want convert
     * @return converted bitmap
     */
    public static Bitmap drawableToBitmap(Drawable drawable, float width, float hight) {
        Bitmap bitmap = null;
        float scaleWidth = width > 0 ? width : drawable.getIntrinsicWidth();
        float scaleHeight = hight > 0 ? hight : drawable.getIntrinsicHeight();
        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            bitmap = bitmapDrawable.getBitmap();
            if (bitmap != null && bitmap.getHeight() > 0) {
                Matrix matrix = new Matrix();
                matrix.postScale(scaleWidth, scaleHeight);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                return bitmap;
            }
        }
        bitmap = Bitmap.createBitmap((int) scaleWidth, (int) scaleHeight, Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }
}
