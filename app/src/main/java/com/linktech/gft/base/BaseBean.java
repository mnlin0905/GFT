package com.linktech.gft.base;

import java.io.Serializable;

/**
 * Created on 2018/1/2
 * function : model模型基础
 *
 * @author ACChain
 */
public class BaseBean implements Serializable {
    /**
     * 记录当前model是否为选中状态
     * <p>
     * 适用与在ListView等需要多选时记录model所处的状态
     * <p>
     * 默认为false,表示未选中
     */
    private boolean SELECTED_STATUS;

    /**
     * 展开时的原始高度,当值大于0时,不要重新赋值
     */
    private float originViewHeight;

    public float getOriginViewHeight() {
        return originViewHeight;
    }

    /**
     * 当新的高度超过时,才会重新赋值
     */
    public BaseBean setOriginViewHeight(float originViewHeight) {
        if (originViewHeight > this.originViewHeight) {
            this.originViewHeight = originViewHeight;
        }
        return this;
    }

    public boolean isSELECTED_STATUS() {
        return SELECTED_STATUS;
    }

    public void setSELECTED_STATUS(boolean SELECTED_STATUS) {
        this.SELECTED_STATUS = SELECTED_STATUS;
    }

    /**
     * 反选选中状态
     *
     * @return 返回当前的选中状态
     */
    public boolean toggleSELECTED_STATUS() {
        return SELECTED_STATUS = !SELECTED_STATUS;
    }
}
