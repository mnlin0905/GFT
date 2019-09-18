package com.linktech.gft.util;

import android.text.InputFilter;
import android.widget.EditText;
import java.util.Locale;

public class NumberUtil {

    /**
     * 默认得显示方式 ，先保留5位小数，再去掉多余得0
     *
     * @param number string 类型
     */
    public static String getDefaultShow(String number) {
        return getDefaultShow(Double.parseDouble(number));
    }

    /**
     * 默认得显示方式 ，先保留5位小数，再去掉多余得0
     *
     * @param number double 类型
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
        return String.format(Locale.CHINA, "%." + digit + "s", number);
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
}
