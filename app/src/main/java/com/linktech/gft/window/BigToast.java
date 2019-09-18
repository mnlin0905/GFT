package com.linktech.gft.window;


import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.linktech.gft.R;
import com.linktech.gft.base.BaseApplication;


/**
 * 功能----大型toast
 * <p>
 * Created by LinkTech on 2017/9/19.
 */

public class BigToast {
    private static Toast toast;

    private static final BigToast single=new BigToast();

    public synchronized static BigToast getInstance() {
        return single;
    }

    private BigToast() {
        BaseApplication context = BaseApplication.app;
        if (toast == null) {
            toast = new Toast(context);
            LinearLayout view = (LinearLayout) LayoutInflater.from(context.getApplicationContext()).inflate(R.layout.layout_toast_success, null);
            toast.setView(view);
            toast.setGravity(Gravity.CENTER, 0, 0);
        }
        ((TextView) toast.getView().findViewById(R.id.msg)).setText("");
        toast.setDuration(Toast.LENGTH_SHORT);
    }

    /**
     * @param msg 设置文字信息
     */
    public BigToast setText(String msg){
        ((TextView) toast.getView().findViewById(R.id.msg)).setText(msg);
        return this;
    }

    /**
     * 显示
     */
    public void show(){
        toast.show();
    }
}
