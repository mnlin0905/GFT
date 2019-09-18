package com.linktech.gft.interfaces;

import com.linktech.gft.view.tablayout.CustomTabLayout;


public abstract class MyTabSelectedListener implements CustomTabLayout.OnTabSelectedListener {
    /**
     * Called when a tab exits the selected state.
     *
     * @param tab The tab that was unselected
     */
    @Override
    public void onTabUnselected(CustomTabLayout.Tab tab) {

    }

    /**
     * Called when a tab that is already selected is chosen again by the user. Some applications
     * may use this action to return to the top level of a category.
     *
     * @param tab The tab that was reselected.
     */
    @Override
    public void onTabReselected(CustomTabLayout.Tab tab) {

    }
}
