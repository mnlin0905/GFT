package com.linktech.gft.view;

import android.content.Context;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;

import com.linktech.gft.interfaces.FormatDoubleInterface;

import java.text.NumberFormat;

import skin.support.widget.SkinCompatTextView;

/**
 * Created on 2018/4/16
 * function : 走马灯形式文本/格式化double类型数据
 *
 * @author LinkTech
 */

public class MarqueeTextView extends SkinCompatTextView implements FormatDoubleInterface {
    private NumberFormat instance;

    {
        instance = NumberFormat.getInstance();
        instance.setGroupingUsed(false);
        instance.setMaximumFractionDigits(6);
        instance.setMinimumFractionDigits(1);
    }

    public MarqueeTextView(Context context) {
        this(context, null);
    }

    public MarqueeTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MarqueeTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        //设置跑马灯效果
        setEllipsize(TextUtils.TruncateAt.MARQUEE);
        setSingleLine();
        setMarqueeRepeatLimit(-1);
    }

    @Override
    public boolean isFocused() {
        return true;
    }

    @Override
    public void setText(CharSequence text, TextView.BufferType type) {
        try {
            if (!TextUtils.isEmpty(text) && text.length() < 100 && text.toString().matches("^.*?[0-9]+.*?$")) {
                if (text instanceof String) {
                    text = dispatchString(instance, text);
                } else if (text instanceof SpannableString) {
                    text = dispatchSpannableString(instance, (SpannableString) text);
                } else if (text instanceof SpannableStringBuilder) {
                    text = dispatchSpannableStringBuilder(instance, (SpannableStringBuilder) text);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.setText(text, type);
    }
}
