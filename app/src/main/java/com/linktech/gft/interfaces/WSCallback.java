package com.linktech.gft.interfaces;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 2019/6/19  11:14
 * function : 网络请求成功回调接口
 *
 * 如果有该callback,则网络请求成功后将执行该callback方法
 *
 * @author mnlin
 */
public interface WSCallback<CB> {
    /**
     * @param tag 回调时用于区分某个请求
     */
    void run(@NotNull CB tag);
}
