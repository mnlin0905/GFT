package com.linktech.gft.bean;

import com.linktech.gft.base.BaseBean;

/**
 * Created on 2018/2/1
 * function : 修改交易密码当前所处的状态
 *
 * @author LinkTech
 */

public class TransPasswordStatus extends BaseBean {

    /**
     * id : 1
     * memberId : 19
     * positive : http://192.168.1.253:90/group1/M00/00/03/wKgB_-E523.jpg
     * back : http://192.168.1.253:90/group1/M00/00/03/wKgB_.png
     * handheld : http://192.168.1.253:90/group1/M00/00/03/wKgB_.jpg
     * has_mobile : 18676389508
     * notPassing : null
     * createTime : 1517380231000
     * updateTime : null
     * status : 0 //0:待审核 1:审核通过 2:审核未通过 3:已撤销 4:已超时
     * auditor : 0
     * deleteFlag : 0
     * standby1 : null
     * standby2 : null
     * standby3 : null
     */

    private int id;
    private int memberId;
    private String positive;
    private String back;
    private String handheld;
    private String mobile;
    private Object notPassing;
    private String createTime;
    private String updateTime;
    private int status;
    private int auditor;
    private int deleteFlag;
    private int cardType;
    private Object standby1;
    private Object standby2;
    private Object standby3;

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public int getCardType() {
        return cardType;
    }

    public void setCardType(int cardType) {
        this.cardType = cardType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMemberId() {
        return memberId;
    }

    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }

    public String getPositive() {
        return positive;
    }

    public void setPositive(String positive) {
        this.positive = positive;
    }

    public String getBack() {
        return back;
    }

    public void setBack(String back) {
        this.back = back;
    }

    public String getHandheld() {
        return handheld;
    }

    public void setHandheld(String handheld) {
        this.handheld = handheld;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public Object getNotPassing() {
        return notPassing;
    }

    public void setNotPassing(Object notPassing) {
        this.notPassing = notPassing;
    }

    public String getCreateTime() {
        return createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getAuditor() {
        return auditor;
    }

    public void setAuditor(int auditor) {
        this.auditor = auditor;
    }

    public int getDeleteFlag() {
        return deleteFlag;
    }

    public void setDeleteFlag(int deleteFlag) {
        this.deleteFlag = deleteFlag;
    }

    public Object getStandby1() {
        return standby1;
    }

    public void setStandby1(Object standby1) {
        this.standby1 = standby1;
    }

    public Object getStandby2() {
        return standby2;
    }

    public void setStandby2(Object standby2) {
        this.standby2 = standby2;
    }

    public Object getStandby3() {
        return standby3;
    }

    public void setStandby3(Object standby3) {
        this.standby3 = standby3;
    }
}
