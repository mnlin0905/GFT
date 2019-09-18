package com.linktech.gft.util;


import com.linktech.gft.base.BaseHttpBean;

/**
 * 当返回数据类型错误或字段错误时,上报异常
 */
public final class DataFormatException extends RuntimeException{
    public BaseHttpBean bean;

    public DataFormatException(String message, BaseHttpBean bean) {
        super(message);
        this.bean = bean;
    }
}
