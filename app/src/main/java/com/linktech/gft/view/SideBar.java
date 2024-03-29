package com.linktech.gft.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.linktech.gft.R;
import com.linktech.gft.plugins.PlusFunsPluginsKt;

public class SideBar extends View {
    public static String[] b = new String[]{"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
    private SideBar.OnTouchingLetterChangedListener onTouchingLetterChangedListener;
    private int choose = -1;
    private Paint paint = new Paint();
    private TextView mTextDialog;

    public SideBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public SideBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SideBar(Context context) {
        super(context);
    }

    public void setTextView(TextView mTextDialog) {
        this.mTextDialog = mTextDialog;
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int height = this.getHeight();
        int width = this.getWidth();
        int singleHeight = height / b.length;

        for (int i = 0; i < b.length; ++i) {
            this.paint.setColor(PlusFunsPluginsKt.dispatchGetColor(R.color.skin_accent_color));
            this.paint.setTypeface(Typeface.DEFAULT);
            this.paint.setAntiAlias(true);
            this.paint.setTextSize(26.0F);
            if (i == this.choose) {
                this.paint.setColor(PlusFunsPluginsKt.dispatchGetColor(R.color.skin_body1_color));
                this.paint.setFakeBoldText(true);
            }

            float xPos = (float) (width / 2) - this.paint.measureText(b[i]) / 2.0F;
            float yPos = (float) (singleHeight * i + singleHeight);
            canvas.drawText(b[i], xPos, yPos, this.paint);
            this.paint.reset();
        }

    }

    public boolean dispatchTouchEvent(MotionEvent event) {
        int action = event.getAction();
        float y = event.getY();
        int oldChoose = this.choose;
        SideBar.OnTouchingLetterChangedListener listener = this.onTouchingLetterChangedListener;
        int c = (int) (y / (float) this.getHeight() * (float) b.length);
        switch (action) {
            case 1:
                this.setBackgroundDrawable(new ColorDrawable(0));
                this.choose = -1;
                this.invalidate();
                if (this.mTextDialog != null) {
                    this.mTextDialog.setVisibility(4);
                }
                break;
            default:
                this.setBackgroundResource(R.drawable.rc_bg_sidebar);
                if (oldChoose != c && c >= 0 && c < b.length) {
                    if (listener != null) {
                        listener.onTouchingLetterChanged(b[c]);
                    }

                    if (this.mTextDialog != null) {
                        this.mTextDialog.setText(b[c]);
                        this.mTextDialog.setVisibility(0);
                    }

                    this.choose = c;
                    this.invalidate();
                }
        }

        return true;
    }

    public void setOnTouchingLetterChangedListener(SideBar.OnTouchingLetterChangedListener onTouchingLetterChangedListener) {
        this.onTouchingLetterChangedListener = onTouchingLetterChangedListener;
    }

    public interface OnTouchingLetterChangedListener {
        void onTouchingLetterChanged(String var1);
    }
}

