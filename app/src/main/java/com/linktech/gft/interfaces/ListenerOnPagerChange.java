package com.linktech.gft.interfaces;


/**
 * Created on 2018/6/2  14:19
 * function : 当fragment或者page变化,则通知实现类或嵌套布局动态刷新
 *
 * @author mnlin
 */
public interface ListenerOnPagerChange<T> {
    /**
     * @param obj pager或者fragment父布局或activity中传出的值
     */
    void onPagerFragmentChange(T obj);
}
