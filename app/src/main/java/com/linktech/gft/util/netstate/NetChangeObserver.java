package com.linktech.gft.util.netstate;

/**
 * Created by zhangying on 2017/8/22.
 */

public interface NetChangeObserver
{
    /**
     * @Description 网络连接连接时调用
     * @author zhangyang
     * @param type
     */
    void onConnect(int type);

    /**
     * @Description
     * @author zhangyang
     */
    void onDisConnect();
}
