package com.linktech.gft.ws;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.linktech.gft.base.BaseWSBean;
import com.linktech.gft.interfaces.WSCallback;
import com.linktech.gft.interfaces.WSJudgeDataTypeI;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;

/**
 * Created on 2019/6/13  15:45
 * function : 监听回调
 * <p>
 * [classType] 数据类型,用于自动处理json->bean
 * [type] ws 数据类型
 * [callback]  通过回调处理请求
 */
public class BaseWSListener<T extends BaseWSBean> {

    @NotNull
    public Type classType;

    @NotNull
    public WSCallback<T> callback;

    @Nullable
    public WSJudgeDataTypeI<T> judgeI;

    public BaseWSListener(@NonNull Type classType, @NonNull WSCallback<T> callback) {
        this.classType = classType;
        this.callback = callback;
    }

    public BaseWSListener(@NonNull Type classType, @NonNull WSCallback<T> callback, @Nullable WSJudgeDataTypeI<T> judgeI) {
        this.classType = classType;
        this.callback = callback;
        this.judgeI = judgeI;
    }
}
