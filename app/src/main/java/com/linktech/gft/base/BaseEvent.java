package com.linktech.gft.base;

import org.jetbrains.annotations.Nullable;

/**
 * 功能----RxBus传递对象,用于基本的显示toast等信息的传递
 * <p>
 * Created by LinkTech on 2017/9/23.
 */

public class BaseEvent {
    //operateCode 类型,EventBus传递过来时,判断对应的类型
    public int operateCode;

    public Object data;

    public BaseEvent(int operateCode, @Nullable Object data) {
        this.operateCode = operateCode;
        this.data = data == null ? "" : data;
    }
}
