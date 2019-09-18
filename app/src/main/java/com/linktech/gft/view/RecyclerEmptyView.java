package com.linktech.gft.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.linktech.gft.R;


/**
 * 功能----空布局;适配RecycleView空列表
 * <p>
 * Created by LinkTech on 2017/9/18.
 */

public class RecyclerEmptyView extends LinearLayout {
    private int type; //  0--white ,1--black

    public RecyclerEmptyView(Context context) {
        super(context, null);
    }

    public RecyclerEmptyView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RecyclerEmptyView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public RecyclerEmptyView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr);
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RecyclerEmptyView, 0, 0);
            type = a.getInt(R.styleable.RecyclerEmptyView_empty_type, 0);
            a.recycle();//回收内存
        }
        View view = inflate(context, R.layout.layout_empty_view, this);
        ImageView imageView = view.findViewById(R.id.iv_image);
        TextView textView = view.findViewById(R.id.tv_text);
        imageView.setImageResource(type == 0 ? R.drawable.empty_white : R.drawable.empty_white);
        textView.setTextColor(type == 0 ? getResources().getColor(R.color.black_text_c0c2c6) : getResources().getColor(R.color.black_text_5b5a70));
        MarginLayoutParams params = new MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        setLayoutParams(params);
        setGravity(Gravity.CENTER);
        setOrientation(VERTICAL);
        setVisibility(ViewGroup.GONE);
    }
}
