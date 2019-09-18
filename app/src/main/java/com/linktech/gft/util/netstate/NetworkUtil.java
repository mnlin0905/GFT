package com.linktech.gft.util.netstate;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.telephony.TelephonyManager;

import com.linktech.gft.base.BaseApplication;

/**
 * Created by zhangying on 2017/8/22.
 */

public class NetworkUtil {
    /**
     * @return boolean
     * @Description 网路是否可用
     * @author zhangyang
     */
    public static boolean isNetworkAvailable() {
        ConnectivityManager mgr = (ConnectivityManager) BaseApplication.app.getSystemService(Context.CONNECTIVITY_SERVICE);
        //sdk21以上获取网络状态方法
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Network[] networks = mgr.getAllNetworks();
            NetworkInfo info;
            for (Network network : networks) {
                info = mgr.getNetworkInfo(network);
                if (info != null && info.getState().equals(NetworkInfo.State.CONNECTED)) {
                    return true;
                }
            }
        } else {
            NetworkInfo[] info = mgr.getAllNetworkInfo();
            if (null != info) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * @return boolean
     * @Description 判断是否有网路连接
     * @author zhangyang
     */
    public static boolean isNetworkConnected() {
        ConnectivityManager mgr = (ConnectivityManager) BaseApplication.app
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = mgr.getActiveNetworkInfo();
        if (null != info) {
            return info.isAvailable();
        }
        return false;
    }

    /**
     * @param context
     * @return boolean
     * @Description 判断wifi网路是否可用
     * @author sunhaitao
     */
    public static boolean isWifiConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (null != activeNetworkInfo) {
            if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                return activeNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    /**
     * @param context
     * @return boolean
     * @Description 判断mobile网路是否可用
     * @author sunhaitao
     */
    public static boolean isMobileConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (null != activeNetworkInfo) {
            if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                return activeNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    /**
     * @param context
     * @return int
     * @Description 获取当前网络连接的类型信息
     * @author sunhaitao
     */
    public static int getConnectedType(Context context) {
        if (context != null) {
            ConnectivityManager connectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager
                    .getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isAvailable()) {
                return networkInfo.getType();
            }
        }
        return -1;
    }

    /**
     * @param context
     * @return NetType
     * @Description 获取当前的网络状态
     * @author sunhaitao
     */
    public static int getSouNeType(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo == null) {
            return TelephonyManager.NETWORK_TYPE_UNKNOWN;
        }
        int nType = networkInfo.getType();
        if (nType == ConnectivityManager.TYPE_MOBILE) {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            return telephonyManager.getNetworkType();
        } else if (nType == ConnectivityManager.TYPE_WIFI) {
            return 1; //wifi
        }
        return TelephonyManager.NETWORK_TYPE_UNKNOWN;
    }

    public static NetType getAPNType(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo == null) {
            return NetType.unKnow;
        }
        int nType = networkInfo.getType();
        if (nType == ConnectivityManager.TYPE_MOBILE) {
            int itype = isFastMobileNetwork(context);
            switch (itype) {
                case 2:
                    return NetType.G2;
                case 3:
                    return NetType.G3;
                case 4:
                    return NetType.G4;
                case 5:
                    return NetType.unKnow;
                default:
                    break;
            }
        } else if (nType == ConnectivityManager.TYPE_WIFI) {
            return NetType.wifi;
        }
        return NetType.unKnow;
    }

    private static int isFastMobileNetwork(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        switch (telephonyManager.getNetworkType()) {
            case TelephonyManager.NETWORK_TYPE_1xRTT:
                return 2; // ~ 50-100 kbps
            case TelephonyManager.NETWORK_TYPE_CDMA:
                return 2; // ~ 14-64 kbps
            case TelephonyManager.NETWORK_TYPE_EDGE:
                return 3; // ~ 50-100 kbps
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
                return 3; // ~ 400-1000 kbps
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
                return 3; // ~ 600-1400 kbps
            case TelephonyManager.NETWORK_TYPE_GPRS:
                return 2; // ~ 100 kbps
            case TelephonyManager.NETWORK_TYPE_HSDPA:
                return 3; // ~ 2-14 Mbps
            case TelephonyManager.NETWORK_TYPE_HSPA:
                return 3; // ~ 700-1700 kbps
            case TelephonyManager.NETWORK_TYPE_HSUPA:
                return 3; // ~ 1-23 Mbps
            case TelephonyManager.NETWORK_TYPE_UMTS:
                return 3; // ~ 400-7000 kbps
            case TelephonyManager.NETWORK_TYPE_EHRPD:
                return 3; // ~ 1-2 Mbps
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
                return 3; // ~ 5 Mbps
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                return 3; // ~ 10-20 Mbps
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return 2; // ~25 kbps
            case TelephonyManager.NETWORK_TYPE_LTE:
                return 4; // ~ 10+ Mbps
            case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                return 5;  //暂时把未知类型，归为2G
            default:
                return 5;   //不在里面类型里面的暂时归为3G
        }
    }

    public enum NetType {
        wifi, G2, G3, G4, unKnow
    }
}
