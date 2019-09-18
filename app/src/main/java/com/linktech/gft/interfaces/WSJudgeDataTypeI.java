package com.linktech.gft.interfaces;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 2019/6/19  10:30
 * function : 判断ws 返回数据类型信息
 *
 * @author mnlin
 */
public interface WSJudgeDataTypeI<T> {
    /**
     * @param receivedData 收到的数据
     * @return 是否满足类型判断
     */
    boolean ifFitTypeData(@NotNull T receivedData);
}
