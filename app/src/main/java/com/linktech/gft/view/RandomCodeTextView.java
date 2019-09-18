package com.linktech.gft.view;

import android.content.Context;
import android.os.CountDownTimer;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.linktech.gft.R;

import java.util.Locale;

import static android.util.TypedValue.COMPLEX_UNIT_PX;

/**
 * 获取验证码控件
 * Created by rsp on 2018/1/9.
 */
public class RandomCodeTextView extends AppCompatTextView {
    private CountDownTimer countDownTimer;
    private CharSequence currentText;

    public RandomCodeTextView(Context context) {
        this(context, null);
    }

    public RandomCodeTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RandomCodeTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        if (TextUtils.isEmpty(getText())) {
            setText(R.string.function_get_random_code);
        }
        currentText = getText();
        setTextSize(COMPLEX_UNIT_PX,getResources().getDimensionPixelSize(R.dimen.text_size_normal_14sp));
        setTextColor(getResources().getColorStateList(R.color.skin_accent_color));
    }

    public void startCount() {
        if (countDownTimer == null) {
            countDownTimer = new CountDownTimer(1000 * 60, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    setText(String.format(Locale.CHINA, "(%ds)", millisUntilFinished / 1000));
                }

                @Override
                public void onFinish() {
                    setEnabled(true);
                    setText(currentText);
                    if (countDownTimer != null) {
                        countDownTimer = null;
                    }
                }
            };
            setEnabled(false);
            countDownTimer.start();
        }
    }

    public void stopCount() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
        setText(R.string.function_get_random_code);
        setEnabled(true);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    @Override
    public boolean performClick() {
        return !isEnabled() || super.performClick();
    }
}
