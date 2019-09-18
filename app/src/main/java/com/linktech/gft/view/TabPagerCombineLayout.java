package com.linktech.gft.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.linktech.gft.R;
import com.linktech.gft.adapter.ActivityViewPagerAdapter;
import com.linktech.gft.base.BaseFragment;
import com.linktech.gft.view.tablayout.CustomTabLayout;

import java.lang.reflect.Field;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created on 2018/1/3
 * function : 将TabLayout和ViewPager进行组合，形成一个layout
 *
 * @author ACChain
 */

public class TabPagerCombineLayout extends LinearLayout implements CustomTabLayout.OnTabSelectedListener {
    @BindView(R.id.tl_tab)
    DisallowClickTabLayout mTlTab;
    @BindView(R.id.vp_pager)
    DisableScrollViewPager mVpPager;

    /**
     * 碎片布局:
     */
    private List<? extends BaseFragment> fragments;

    /**
     * 适配器
     */
    private FragmentStatePagerAdapter pagerAdapter;

    /**
     * 标题
     */
    private List<CharSequence> titles;

    /**
     * 布局管理
     */
    private FragmentManager manager;

    /**
     * 监听器
     */
    private onTabPagerListener listener;

    /**
     * 当前显示的位置
     */
    private int currentPosition;

    public TabPagerCombineLayout(Context context) {
        this(context, null);
    }

    public TabPagerCombineLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TabPagerCombineLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        //初始化自身
        setOrientation(VERTICAL);

        //获取需要加载的布局
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.TabPagerCombineLayout);
        int layoutId = array.getResourceId(R.styleable.TabPagerCombineLayout_layout, R.layout.layout_tab_pager_combine_layout);

        //加载组合布局
        LayoutInflater.from(getContext()).inflate(layoutId, this, true);
        ButterKnife.bind(this);

        //监听事件
        mTlTab.addOnTabSelectedListener(this);
    }

    /**
     * @param manageScrollInterface 提供管理是否可以滑动的接口
     */
    public TabPagerCombineLayout provideManageScrollInterface(DisableScrollViewPager.ManageScrollInterface manageScrollInterface) {
        mVpPager.setManageScrollInterface(manageScrollInterface);
        return this;
    }

    /**
     * @param manageClickInterface 提供接口,管理是否可以点击tablayout
     */
    public TabPagerCombineLayout provideManageClickInterface(DisallowClickTabLayout.ManageClickInterface manageClickInterface) {
        mTlTab.setManageClickInterface(manageClickInterface);
        return this;
    }

    /**
     * 设置碎片
     */
    public TabPagerCombineLayout provideFragments(List<? extends BaseFragment> fragments) {
        this.fragments = fragments;
        return this;
    }

    /**
     * 提供碎片管理器
     */
    public TabPagerCombineLayout provideFragmentManager(FragmentManager manager) {
        this.manager = manager;
        return this;
    }

    /**
     * 设置默认选中的位置
     */
    public TabPagerCombineLayout provideDefaultPosition(int position) {
        currentPosition = position;
        return this;
    }

    /**
     * 设置标题
     */
    public TabPagerCombineLayout provideTitles(List<CharSequence> titles) {
        this.titles = titles;
        return this;
    }

    /**
     * 设置监听
     */
    public TabPagerCombineLayout provideListener(onTabPagerListener listener) {
        this.listener = listener;
        return this;
    }

    /**
     * 设置预加载数量
     */
    public TabPagerCombineLayout provideOffscreenPageLimit(int limit) {
        mVpPager.setOffscreenPageLimit(limit);
        return this;
    }

    /**
     * 调用方法完成设置
     */
    public void combine() {
        if (fragments == null || titles == null || titles.size() != fragments.size()) {
            throw new RuntimeException("datas cannot be null and tabs' length should be equal to fragments' size");
        }
        pagerAdapter = new CombinePagerAdapter(manager);
        mVpPager.setAdapter(pagerAdapter);
        mTlTab.setupWithViewPager(mVpPager);
        mTlTab.setScrollPosition(currentPosition, 0, true);
        mVpPager.setCurrentItem(currentPosition);

        //设置标签单行显示
        try {
            for (int i = 0; i < titles.size(); i++) {
                CustomTabLayout.Tab tab = mTlTab.getTabAt(0);
                Field field_mView = CustomTabLayout.Tab.class.getDeclaredField("mView");
                field_mView.setAccessible(true);
                Object mView = field_mView.get(tab);
                Field field_mDefaultMaxLines = mView.getClass().getDeclaredField("mDefaultMaxLines");
                field_mDefaultMaxLines.setAccessible(true);
                field_mDefaultMaxLines.setInt(mView, 1);
            }
        } catch (Exception e) {
            //当无法改变行数时,不进行处理
            e.printStackTrace();
        }
    }

    /**
     * 碎片数量变化
     * <p>
     * notifyDataSetChange
     */
    public void notifyDataSetChanged() {
        pagerAdapter.notifyDataSetChanged();
    }

    /**
     * 提供数据源
     */
    public ViewPager getViewPager() {
        return mVpPager;
    }

    /**
     * 提供数据源
     */
    public CustomTabLayout getTabLayout() {
        return mTlTab;
    }

    @Override
    public void onTabSelected(CustomTabLayout.Tab tab) {
        if (listener != null) {
            post(() -> {
                if (fragments.get(tab.getPosition()).rootView != null) {
                    currentPosition = tab.getPosition();
                    listener.onPagerAppear(tab.getPosition());
                } else {
                    onTabSelected(tab);
                }
            });
        }
    }

    @Override
    public void onTabUnselected(CustomTabLayout.Tab tab) {
        if (listener != null) {
            post(() -> {
                if (fragments.get(tab.getPosition()).rootView != null) {
                    listener.onPagerDisappear(tab.getPosition());
                } else {
                    onTabSelected(tab);
                }
            });
        }
    }

    @Override
    public void onTabReselected(CustomTabLayout.Tab tab) {
        if (listener != null) {
            post(() -> {
                if (fragments.get(tab.getPosition()).rootView != null) {
                    listener.onPagerReAppear(tab.getPosition());
                } else {
                    onTabSelected(tab);
                }
            });
        }
    }

    public int getCurrentPosition() {
        return currentPosition;
    }

    /**
     * 监听事件
     */
    public interface onTabPagerListener {
        /**
         * @param position 第position位置的页面显示
         */
        void onPagerAppear(int position);

        /**
         * @param position 第position位置被重复选中
         */
        default void onPagerReAppear(int position) {
        }

        /**
         * @param position 消失
         */
        default void onPagerDisappear(int position) {
        }
    }

    /**
     * 适配器
     */
    private class CombinePagerAdapter extends ActivityViewPagerAdapter {
        public CombinePagerAdapter(FragmentManager fm) {
            super(fm,fragments);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
    }
}
