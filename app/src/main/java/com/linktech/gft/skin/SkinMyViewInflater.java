package com.linktech.gft.skin;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import skin.support.app.SkinLayoutInflater;

public class SkinMyViewInflater implements SkinLayoutInflater {
    @Override
    public View createView(Context context, final String name, AttributeSet attrs) {
        View view = null;
        switch (name) {
            case "com.wallet.gft.skin.MyLineMenuView":
                view = new MyLineMenuView(context, attrs);
                break;
            case "com.wallet.gft.skin.MyTitleCenterToolbar":
                view = new MyTitleCenterToolbar(context, attrs);
                break;
            case "com.wallet.gft.skin.MyNewsConstraintLayout":
                view = new MyNewsConstraintLayout(context, attrs);
                break;
            case "com.wallet.gft.skin.MyBottomNavigationViewEx":
                view = new MyBottomNavigationViewEx(context, attrs);
                break;
        }
        return view;
    }
}
