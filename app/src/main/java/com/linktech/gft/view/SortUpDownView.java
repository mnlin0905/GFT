package com.linktech.gft.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.linktech.gft.R;
import com.linktech.gft.base.BasePresenter;

/**
 * Created on 2019/1/8  11:29
 * function : 可排序的view
 *
 * @author mnlin
 */
public class SortUpDownView extends LinearLayout {
    /**
     * 排序方式
     * <p>
     * 默认
     * 升序
     * 降序
     */
    public static final int SORT_DEFAULT = 0;
    public static final int SORT_ASC = 1;
    public static final int SORT_DESC = 2;

    /**
     * 当前显示的排序的方式
     */
    private int sortType = SORT_DEFAULT;
    private int[] imgRes = new int[]{
            R.drawable.dark_icon_sort_default,
            R.drawable.dark_icon_sort_asc,
            R.drawable.dark_icon_sort_desc,
    };

    /**
     * 类型改变 回调
     */
    private BasePresenter.HttpCallback<Integer> callback;

    /**
     * 视图
     */
    public ImageView iv_sort;

    public SortUpDownView(Context context) {
        this(context, null);
    }

    public SortUpDownView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SortUpDownView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.layout_sort_up_down, this, true);

        iv_sort = findViewById(R.id.iv_sort);
        iv_sort.setOnClickListener((view) -> {
            switch (sortType) {
                case SORT_DEFAULT:
                    sortType = SORT_ASC;
                    break;
                case SORT_ASC:
                    sortType = SORT_DESC;
                    break;
                case SORT_DESC:
                    sortType = SORT_DEFAULT;
                    break;
            }
            ((ImageView) view).setImageResource(imgRes[sortType]);

            //回调
            if (callback != null) {
                callback.run(sortType);
            }
        });
    }

    /**
     * 获取升序方式
     */
    public int getSortType() {
        return sortType;
    }

    /**
     * 还原(不执行回调)
     */
    public void resetNoCallback() {
        sortType = SORT_DEFAULT;
        iv_sort.setImageResource(imgRes[sortType]);
    }

    /**
     * 设置回调
     */
    public void setOnSortChangeListener(BasePresenter.HttpCallback<Integer> callback) {
        this.callback = callback;
    }
}
