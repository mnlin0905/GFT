package com.linktech.gft.bean;

import java.util.List;

/**
 * Created on 2018/3/29
 * function : 邀请或奖励信息
 *
 * @author LinkTech
 */

public class InviteInfoBean {

    /**
     * rewardRecords : [{"createTime":"2018-01-18 19:48:23","has_mobile":"13650807553","has_email":null},{"createTime":"2018-01-18 20:15:43","has_mobile":"15625294838","has_email":null},{"createTime":"2018-01-19 09:38:28","has_mobile":"18337138008","has_email":null},{"createTime":"2018-02-02 13:52:35","has_mobile":"13112521212","has_email":null},{"createTime":"2018-03-02 14:42:03","has_mobile":"18118795330","has_email":null},{"createTime":"2018-03-09 15:12:42","has_mobile":"18902468610","has_email":null}]
     * rewardItemBean : [{"createTime":"2018-03-28 10:31:51","num":1.99,"name":"注册奖励","rewardCode":"register","currency":"DGAS"},{"createTime":"2018-03-28 10:31:51","num":1,"name":"签到奖励","rewardCode":"sign","currency":"DGAS"}]
     * downloadUrl : https://upgrade.linktech.org/app/A3.apk
     * registerRewardCurrency : DGAS
     * inviteRewardCurrency : DGAS
     * signReward : 1
     * inviteReward : 10
     * signRewardCurrency : DGAS
     * currency : DGAS
     * rewards : 2.99
     * registerReward : 1.99
     */

    private String downloadUrl;

    private double registerReward;
    private double signReward;
    private double inviteReward;

    private String signRewardCurrency;
    private String registerRewardCurrency;
    private String inviteRewardCurrency;

    private String currency;
    private double rewards;

    private List<RecommendItemBean> rewardRecords;
    private List<RewardItemBean> rewardList;

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getRegisterRewardCurrency() {
        return registerRewardCurrency;
    }

    public void setRegisterRewardCurrency(String registerRewardCurrency) {
        this.registerRewardCurrency = registerRewardCurrency;
    }

    public String getInviteRewardCurrency() {
        return inviteRewardCurrency;
    }

    public void setInviteRewardCurrency(String inviteRewardCurrency) {
        this.inviteRewardCurrency = inviteRewardCurrency;
    }

    public double getSignReward() {
        return signReward;
    }

    public void setSignReward(double signReward) {
        this.signReward = signReward;
    }

    public double getInviteReward() {
        return inviteReward;
    }

    public void setInviteReward(double inviteReward) {
        this.inviteReward = inviteReward;
    }

    public String getSignRewardCurrency() {
        return signRewardCurrency;
    }

    public void setSignRewardCurrency(String signRewardCurrency) {
        this.signRewardCurrency = signRewardCurrency;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public double getRewards() {
        return rewards;
    }

    public void setRewards(double rewards) {
        this.rewards = rewards;
    }

    public double getRegisterReward() {
        return registerReward;
    }

    public void setRegisterReward(double registerReward) {
        this.registerReward = registerReward;
    }

    public List<RecommendItemBean> getRewardRecords() {
        return rewardRecords;
    }

    public void setRewardRecords(List<RecommendItemBean> rewardRecords) {
        this.rewardRecords = rewardRecords;
    }

    public List<RewardItemBean> getRewardList() {
        return rewardList;
    }

    public void setRewardList(List<RewardItemBean> rewardList) {
        this.rewardList = rewardList;
    }

    public static class RecommendItemBean {
        /**
         * createTime : 2018-01-18 19:48:23
         * has_mobile : 13650807553
         * has_email : null
         */

        private String createTime;
        private String mobile;
        private String email;

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }

    public static class RewardItemBean {
        /**
         * createTime : 2018-03-28 10:31:51
         * num : 1.99
         * name : 注册奖励
         * rewardCode : register
         * currency : DGAS
         */

        private String createTime;
        private double num;
        private String name;
        private String rewardCode;
        private String currency;

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public double getNum() {
            return num;
        }

        public void setNum(double num) {
            this.num = num;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getRewardCode() {
            return rewardCode;
        }

        public void setRewardCode(String rewardCode) {
            this.rewardCode = rewardCode;
        }

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }
    }
}
