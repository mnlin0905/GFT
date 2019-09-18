package com.linktech.gft.util;


import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Created on 2017/12/27
 * function : 用于生成富文本的text
 * <p>
 * Create an italic "hello, " a red "world",
 * and bold the entire sequence.
 * <p>
 * CharSequence text = bold(italic(res.getString(R.string.hello)),
 * color(Color.RED, res.getString(R.string.world)));
 *
 * @author LinkTech
 */

public class SpanStringUtil {
    /**
     * Returns a CharSequence that concatenates the specified array of CharSequence
     * objects and then applies a stockInfo of zero or more tags to the entire range.
     *
     * @param content an array of character sequences to apply a style to
     * @param tags    the styled span objects to apply to the content
     *                such as android.text.style.StyleSpan
     */
    private static CharSequence apply(CharSequence[] content, Object... tags) {
        SpannableStringBuilder text = new SpannableStringBuilder();
        openTags(text, tags);
        for (CharSequence item : content) {
            text.append(item);
        }
        closeTags(text, tags);
        return text;
    }

    /**
     * Iterates over an array of tags and applies them to the beginning of the specified
     * Spannable object so that future text appended to the text will have the styling
     * applied to it. Do not call this method directly.
     */
    private static void openTags(Spannable text, Object[] tags) {
        for (Object tag : tags) {
            text.setSpan(tag, 0, 0, Spannable.SPAN_MARK_MARK);
        }
    }

    /**
     * "Closes" the specified tags on a Spannable by updating the spans to be
     * endpoint-exclusive so that future text appended to the end will not take
     * on the same styling. Do not call this method directly.
     */
    private static void closeTags(Spannable text, Object[] tags) {
        int len = text.length();
        for (Object tag : tags) {
            if (len > 0) {
                text.setSpan(tag, 0, len, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else {
                text.removeSpan(tag);
            }
        }
    }

    /**
     * Returns a CharSequence that applies boldface to the concatenation
     * of the specified CharSequence objects.
     */
    public static CharSequence bold(CharSequence... content) {
        return apply(content, new StyleSpan(Typeface.BOLD));
    }

    /**
     * Returns a CharSequence that applies italics to the concatenation
     * of the specified CharSequence objects.
     */
    public static CharSequence italic(CharSequence... content) {
        return apply(content, new StyleSpan(Typeface.ITALIC));
    }

    /**
     * Returns a CharSequence that applies a foreground color to the
     * concatenation of the specified CharSequence objects.
     */
    public static CharSequence color(int color, CharSequence... content) {
        return apply(content, new ForegroundColorSpan(color));
    }

    /**
     * 不用科学计数法
     */
    public static String formatBalance(double price) {
        NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setGroupingUsed(false);
        numberFormat.setMaximumFractionDigits(6);
        return  numberFormat.format(price);
    }

    /**
     * 获取小数位数
     *
     * @param d 数据源
     * @return 小数位数
     */
    public static int getDecimals(Double d) {
        int decimals = 0;
        String balanceStr = String.valueOf(d);
        int indexOf = balanceStr.indexOf(".");
        if (indexOf > 0) {
            decimals = balanceStr.length() - 1 - indexOf;
        }
        return decimals;
    }

    /**
     * 保留四位小数且不用科学计数法，若没有小数不补0
     *
     * @param d 数据源
     * @return 字符串
     */
    public static String formateDouble(double d) {
        DecimalFormat df = new DecimalFormat("#.####");
        String value = df.format(d);
        return value;
    }
}
