package com.linktech.gft.interfaces;


/**
 * Created on 2018/3/2
 * function : 过滤监听
 *
 * @author LinkTech
 */
public interface OnFilterResultListener {
    /**
     * @param position 点击的位置
     */
    void onFilterSelectItem(int position);

    /**
     * @param filter 输入的用于过滤的信息
     */
    void onFilterInputKeys(String filter);
}