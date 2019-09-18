package com.linktech.gft.base;


/**
 * 功能----基础model
 * <p>
 * Created by ACChain on 2017/9/25.
 * <p>
 * 数据真实的model类型
 */

public class BaseHttpBean extends BaseBean {
    /**
     * 结果码,请求状态码
     */
    public int error_code = -1;

        /**
     * 自动添加字段,用于表示错误信息
     */
    public String msg;

    /**
     * 服务器列表
     */
    public String server;

    /**
     * 图片路径
     */
    public String imgServerUrl;

    /**
     * 总页数
     */
    public int totalPageCount;

    /**
     * 股票的接口请求状态码
     */
    public int stateCode = -1;

    /**
     * 股票的接口状态码描述
     */
    public String messages;

    public String getServer() {
        return server;
    }

    public BaseHttpBean setServer(String server) {
        this.server = server;
        return this;
    }

    public int getError_code() {
        return error_code;
    }

    public void setError_code(int error_code) {
        this.error_code = error_code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getTotalPageCount() {
        return totalPageCount;
    }

    public void setTotalPageCount(int totalPageCount) {
        this.totalPageCount = totalPageCount;
    }

    /**
     * 状态码
     */
    public int getCode() {
        return error_code == -1 ? stateCode : error_code;
    }

    public void setStateCode(int stateCode) {
        this.stateCode = stateCode;
    }

    public String getMessages() {
        return messages;
    }

    public void setMessages(String messages) {
        this.messages = messages;
    }

    @Override
    public String toString() {
        return getClass().getName() + ":" + this.hashCode();
    }
}
