package com.linktech.gft.adapter;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.view.ViewGroup;

import com.linktech.gft.base.BaseFragment;

import java.util.List;

/**
 * Created on 2017/12/20
 * function : Viewpager适配器(用于Fragment)
 *
 * @author LinkTech
 */
public class ActivityViewPagerAdapter extends FragmentStatePagerAdapter {
    private FragmentManager fm;

    private List<? extends BaseFragment> data;

    public ActivityViewPagerAdapter(FragmentManager fm, List<? extends BaseFragment> data) {
        super(fm);
        this.fm = fm;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Fragment getItem(int position) {
        return data.get(position);
    }

    public Fragment instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container,
                position);
        fm.beginTransaction().show(fragment).commit();
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        if (data.indexOf(object) != -1) {
            Fragment fragment = (Fragment) object;
            fm.beginTransaction().hide(fragment).commit();
        } else {
            super.destroyItem(container, position, object);
        }
    }


    @Override
    public int getItemPosition(@NonNull Object object) {
        return PagerAdapter.POSITION_NONE;
    }
}
