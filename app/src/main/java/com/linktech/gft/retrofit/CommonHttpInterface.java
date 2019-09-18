package com.linktech.gft.retrofit;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.linktech.gft.BuildConfig;
import com.linktech.gft.R;
import com.linktech.gft.base.BaseHttpBean;
import com.linktech.gft.base.BasePresenter;
import com.linktech.gft.base.LoginTradeResponse;
import com.linktech.gft.base.OrderResponse;
import com.linktech.gft.base.TradeTypeResponse;
import com.linktech.gft.bean.AllAsset;
import com.linktech.gft.bean.AssetRecode;
import com.linktech.gft.bean.BankCard;
import com.linktech.gft.bean.BannerBean;
import com.linktech.gft.bean.CashFlowBean;
import com.linktech.gft.bean.CertificationResultBean;
import com.linktech.gft.bean.CommonAgreementBean;
import com.linktech.gft.bean.CompanyProfile;
import com.linktech.gft.bean.CountryBean;
import com.linktech.gft.bean.DealDetailBean;
import com.linktech.gft.bean.DeputeResponse;
import com.linktech.gft.bean.EquityBean;
import com.linktech.gft.bean.EstablishStatusBean;
import com.linktech.gft.bean.ExchangeRate;
import com.linktech.gft.bean.ExchangeRecord;
import com.linktech.gft.bean.ExchangeResponse;
import com.linktech.gft.bean.ExponentKLineBean;
import com.linktech.gft.bean.ExponentMinHour;
import com.linktech.gft.bean.FHBean;
import com.linktech.gft.bean.FinanceSummaryBean;
import com.linktech.gft.bean.HoldBean;
import com.linktech.gft.bean.IndustryListBean;
import com.linktech.gft.bean.IndustryPartBean;
import com.linktech.gft.bean.InformationDetailBean;
import com.linktech.gft.bean.InformationRecordBean;
import com.linktech.gft.bean.InviteCodeBean;
import com.linktech.gft.bean.InviteListBean;
import com.linktech.gft.bean.InviteStatistics;
import com.linktech.gft.bean.LatestVersionBean;
import com.linktech.gft.bean.LoginBean;
import com.linktech.gft.bean.MyAsset;
import com.linktech.gft.bean.NewStockResponse;
import com.linktech.gft.bean.OptionalListBean;
import com.linktech.gft.bean.OtherCurrency;
import com.linktech.gft.bean.PBBean;
import com.linktech.gft.bean.PEBean;
import com.linktech.gft.bean.ProfitBean;
import com.linktech.gft.bean.RecentTransferRecordBean;
import com.linktech.gft.bean.RewardList;
import com.linktech.gft.bean.SBBean;
import com.linktech.gft.bean.SearchStockResponse;
import com.linktech.gft.bean.ServiceBean;
import com.linktech.gft.bean.SheetBean;
import com.linktech.gft.bean.SignLevel;
import com.linktech.gft.bean.StockAccountInfo;
import com.linktech.gft.bean.StockBasicInfo;
import com.linktech.gft.bean.StockInfo;
import com.linktech.gft.bean.StockKLineBean;
import com.linktech.gft.bean.StockMinHour;
import com.linktech.gft.bean.StockRulesBean;
import com.linktech.gft.bean.TakeInBean;
import com.linktech.gft.bean.TakeInRecordBean;
import com.linktech.gft.bean.TradeBean;
import com.linktech.gft.bean.TrustListBean;
import com.linktech.gft.bean.UploadCerImgBean;
import com.linktech.gft.bean.UploadIdCardBean;
import com.linktech.gft.bean.UsableBean;
import com.linktech.gft.bean.UserBaseInfoBean;
import com.linktech.gft.bean.UserSignInfo;
import com.linktech.gft.bean.WSBuySellDeepBean;
import com.linktech.gft.bean.WSExponentChangeBean;
import com.linktech.gft.bean.WSMarketStatus;
import com.linktech.gft.bean.WXLoginBean;
import com.linktech.gft.bean.WalletTakeOutBean;
import com.linktech.gft.interfaces.CommonProviderInterface;
import com.linktech.gft.util.DefaultPreferenceUtil;
import com.linktech.gft.util.L;
import com.orhanobut.logger.Logger;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;
import java.util.Random;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import static com.linktech.gft.plugins.PlusFunsPluginsKt.getLocaleForRequest;
import static com.linktech.gft.plugins.PlusFunsPluginsKt.getLocaleInt;

/**
 * Created on 2018/1/19
 * function : http请求中多个界面都需要用到的接口,会放入这里
 *
 * @author ACChain
 */

public interface CommonHttpInterface extends CommonProviderInterface {
    /**
     * 获取生态服务列表
     */
    default void getServiceList(BasePresenter.HttpCallback<List<ServiceBean>> callback) {
        getBasePresenter().requestHttp(getHttpInterface().getServiceList(getLocaleForRequest(null, null)),
                tag -> {
                    List<ServiceBean> list = tag.getData();
                    if (list != null) {
                        for (ServiceBean serviceBean : list) {
                            serviceBean.setService(tag.server);
                        }
                    }
                    callback.run(list);
                });
    }

    /**
     * 获取通用服务协议
     * <p>
     * 1:钱包服务协议 2:生态服务协议 3:生态版权信息 4:锁仓释放规则 5:挖矿奖励规则
     */
    default void getCommonAgreementList(int category, BasePresenter.HttpCallback<CommonAgreementBean> callback) {
        getBasePresenter().requestHttp(getHttpInterface().getCommonAgreementList(category),
                tag -> callback.run(tag.getData()));
    }

    /**
     * 获取banner
     * client 客户端类型 1:PC 2:APP
     */
    default void getBanner(BasePresenter.HttpCallback<List<BannerBean>> callback) {
        getBasePresenter().requestHttp(getHttpInterface().getBanner(getLocaleInt(null, null)),
                tag -> callback.run(tag.getData()));
    }

    /**
     * 1.0.1 登录
     *
     * @param username 用户名
     * @param pwd      密码
     */
    default void doLogin(String username, String pwd, @NotNull BasePresenter.HttpCallback<LoginBean> callback) {
        getBasePresenter().requestHttp(getBaseView().dispatchGetString(R.string.common_login_ing) + "...", getHttpInterface().login(username, pwd),
                it -> callback.run(it.getData()));
    }

    /**
     * 6.0.4动态登录
     *
     * @param username 用户名
     * @param random   验证码
     */
    default void dynamicLogin(String username, String random, @NotNull BasePresenter.HttpCallback<LoginBean> callback) {
        getBasePresenter().requestHttp(getBaseView().dispatchGetString(R.string.common_login_ing) + "...", getHttpInterface().dynamicLogin(username, random),
                it -> callback.run(it.getData()));
    }

    /**
     * 1.9 获取会员账户信息
     */
    default void getMemberInfo(String signature, String username, @NonNull BasePresenter.HttpCallback<UserBaseInfoBean> cb, @NonNull BasePresenter.HttpCallback<Throwable> onError) {
        getBasePresenter().requestHttp(getHttpInterface().getMemberInfo(signature, username),
                bean -> {
                    if (bean != null && bean.getData() != null) {
                        cb.run(BasePresenter.singleUserInfo = bean.getData());

                        //正事环境时,记录个人资料信息,以免出错无法找回数据
                        if (!BuildConfig.DEBUG) {
                            Logger.e("user info:" + bean.getData().toString());
                        }
                    } else {
                        onError.run(new RuntimeException(getBaseView().dispatchGetString(R.string.base_total_not_get_user_info)));
                    }
                }, onError::run);
    }

    /**
     * 2.1.0 获取用户实名信息
     *
     * @return 请求返回信息
     */
    default void getExamineInfo(String username, String signature, @NonNull BasePresenter.HttpCallback<CertificationResultBean> cb) {
        getBasePresenter().requestHttp(getHttpInterface().getExamineInfo(username, signature),
                bean -> cb.run(bean.getData()));
    }

    /**
     * 3.0.1查看用户基本信息
     */
    default void getMemberInfo(String signature, String username, @NonNull BasePresenter.HttpCallback<UserBaseInfoBean> callback) {
        getMemberInfo(signature, username, tag -> {
            BasePresenter.singleUserInfo = tag;
            callback.run(tag);
        }, tag -> {
            //空操作
        });
    }

    /**
     * 1.0.5 注册
     */
    default void register(String username, String random, String password, String confirmpassword, String referrer, String nationCode, @NonNull BasePresenter.HttpCallback callback) {
        getBasePresenter().requestHttp(getHttpInterface().register(username, random, password, confirmpassword, referrer, nationCode),
                loginBean -> callback.run(null));
    }

    /**
     * 1.0.6 激活钱包地址
     */
    default void activeAddress(String address, String deviceId, String macId, String currency, @NonNull BasePresenter.HttpCallback<Boolean> callback) {
        getBasePresenter().requestHttp(getHttpInterface().activeAddress(address, deviceId + (BuildConfig.DEBUG ? new Random().nextDouble() + "" : ""), macId, currency),
                loginBean -> callback.run(true),
                throwable -> callback.run(false));
    }

    /**
     * 获取会员邀请码
     *
     * @return 请求返回信息
     */
    default void findInvitedCode(String signature, String username, @NonNull BasePresenter.HttpCallback<InviteCodeBean> callback) {
        getBasePresenter().requestHttp(getHttpInterface().findInvitedCode(signature, username),
                bean -> callback.run(bean.getData()));
    }

    /**
     * 注册奖励列表
     * <p>
     * 是否领取 0-否 1-是 （不传获取所有
     */
    default void findInvitedList(String username, String signature, int pageIndex, int pageSize, @Nullable Integer type, @NonNull BasePresenter.HttpCallback<List<InviteListBean>> callback) {
        getBasePresenter().requestHttp(getHttpInterface().findInvitedList(username, signature, pageIndex, pageSize, type),
                bean -> callback.run(bean.getData()),
                bean -> callback.run(null));
    }

    /**
     * 3.0.10 发送验证码 手机号
     */
    default void sendInformation(String phone, String r_reason, @NonNull BasePresenter.HttpCallback callback) {
        sendInformation(phone, r_reason, null, callback);
    }

    /**
     * 3.0.10 发送验证码 手机号
     */
    default void sendInformation(String phone, String r_reason, String nationCode, @NonNull BasePresenter.HttpCallback callback) {
        getBasePresenter().requestHttp(getHttpInterface().sendInformation(phone, r_reason, nationCode),
                loginBean -> {
                    getBaseView().showToast(R.string.base_total_send_code_any_type, getBaseView().dispatchGetString(R.string.common_mobile));
                    callback.run(null);
                });
    }

    /**
     * 3.0.12 发送验证码 邮箱
     */
    default void sendEmail(String email, String r_reason, @NonNull BasePresenter.HttpCallback callback) {
        getBasePresenter().requestHttp(getHttpInterface().sendEmail(email, r_reason, DefaultPreferenceUtil.getInstance().getLocaleLanguageSwitch() == 4 ? 1 : 0),
                loginBean -> {
                    getBaseView().showToast(R.string.base_total_send_code_any_type, getBaseView().dispatchGetString(R.string.common_email));
                    callback.run(null);
                });
    }


    /**
     * 3.0.5图片上传(上传 头像,传liu)
     */
    default void uploadImg(String username, String signature, File file, @NonNull BasePresenter.HttpCallback<String> callback) {
        final MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("client", "1")
                .addFormDataPart("usave", "true")
                .addFormDataPart("key", "__UPLOADS");

        if (!TextUtils.isEmpty(username)) {
            builder.addFormDataPart("username", username);
        }
        if (!TextUtils.isEmpty(signature)) {
            builder.addFormDataPart("signature", signature);
        }

        builder.addFormDataPart("files", System.currentTimeMillis() + "file.jpg", RequestBody.create(MediaType.parse("application/octet-stream"), file));

        getBasePresenter().requestHttp(R.string.common_update_, getHttpInterface().appUploadImg(builder.build()),
                bean -> {
                    List<UploadCerImgBean> data = bean.getData();
                    if (data != null && data.size() >= 1) {
                        callback.run(data.get(0).getUrl());
                    } else {
                        getBaseView().showToast(R.string.base_total_upload_picture_error);
                    }
                });
    }

    /**
     * 3.0.5图片上传(上传 头像，默认图片，传byte[])
     */
    default void uploadImg(String username, String signature, byte[] file, @NonNull BasePresenter.HttpCallback<String> callback) {
        final MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("client", "1")
                .addFormDataPart("usave", "true")
                .addFormDataPart("key", "__UPLOADS");

        if (!TextUtils.isEmpty(username)) {
            builder.addFormDataPart("username", username);
        }
        if (!TextUtils.isEmpty(signature)) {
            builder.addFormDataPart("signature", signature);
        }

        builder.addFormDataPart("files", System.currentTimeMillis() + "file.jpg", RequestBody.create(MediaType.parse("application/octet-stream"), file));

        getBasePresenter().requestHttp(R.string.common_update_, getHttpInterface().appUploadImg(builder.build()),
                bean -> {
                    List<UploadCerImgBean> data = bean.getData();
                    if (data != null && data.size() >= 1) {
                        callback.run(data.get(0).getUrl());
                    } else {
                        getBaseView().showToast(R.string.base_total_upload_picture_error);
                    }
                });
    }

    /**
     * 3.0.14实名认证图片上传
     */
    default void appUploadImg(String username, String signature, String idcard_positive, String idcard_contrary, String idcard_hord, @NonNull BasePresenter.HttpCallback<List<UploadCerImgBean>> callback) {
        L.i("idcard_positive=" + idcard_positive + "\nidcard_contrary=" + idcard_contrary + "\nidcard_hord=" + idcard_hord);
        final MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("client", "1")
                .addFormDataPart("files", System.currentTimeMillis() + "file.jpg", RequestBody.create(MediaType.parse("application/octet-stream"), new File(idcard_positive)))
                .addFormDataPart("files", System.currentTimeMillis() + "file.jpg", RequestBody.create(MediaType.parse("application/octet-stream"), new File(idcard_contrary)))
                .addFormDataPart("files", System.currentTimeMillis() + "file.jpg", RequestBody.create(MediaType.parse("application/octet-stream"), new File(idcard_hord)));

        L.i("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");
        L.i("idcard_positive=" + idcard_positive + "\nidcard_contrary=" + idcard_contrary + "\nidcard_hord=" + idcard_hord);

        if (!TextUtils.isEmpty(username)) {
            builder.addFormDataPart("username", username);
        }
        if (!TextUtils.isEmpty(signature)) {
            builder.addFormDataPart("signature", signature);
        }

        getBasePresenter().requestHttp(R.string.common_deal_, getHttpInterface().appUploadImg(builder.build()),
                bean -> {
                    List<UploadCerImgBean> data = bean.getData();
                    if (data != null && data.size() == 3) {
                        callback.run(data);
                    } else {
                        getBaseView().showToast(R.string.base_total_upload_picture_error);
                    }
                });
    }

    /**
     * 3.0.14 通用图片上传接口
     */
    default void commonUploadPicture(String username, String signature, File[] files, @NonNull BasePresenter.HttpCallback<List<UploadCerImgBean>> callback) {
        final MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("client", "1");

        for (File file : files) {
            if (file != null) {
                builder.addFormDataPart("files", System.currentTimeMillis() + "file.jpg", RequestBody.create(MediaType.parse("application/octet-stream"), file));
            }
        }

        if (!TextUtils.isEmpty(username)) {
            builder.addFormDataPart("username", username);
        }
        if (!TextUtils.isEmpty(signature)) {
            builder.addFormDataPart("signature", signature);
        }

        getBasePresenter().requestHttp(R.string.common_deal_, getHttpInterface().appUploadImg(builder.build()),
                bean -> {
                    List<UploadCerImgBean> data = bean.getData();
                    callback.run(data);
                });
    }

    /**
     * 3.0.3 修改个人资料
     */
    default void saveUserBaseInfo(String username, String signature, String nickname, Integer gender, @NonNull BasePresenter.HttpCallback callback) {
        getBasePresenter().requestHttp(R.string.common_change_ing, getHttpInterface().saveUserBaseInfo(username, signature, nickname, gender),
                bean -> callback.run(null));
    }


    /**
     * 3.0.1 获取个人资产
     */
    default void getAsset(String username, String signature, @NonNull BasePresenter.HttpCallback<List<MyAsset>> callback) {
        getBasePresenter().requestHttp(getHttpInterface().getAsset(username, signature),
                bean -> callback.run(bean.getData()));
    }

    /**
     * 3.0.2 获取各类资产   int	1-众筹 2-糖果 3-挖矿
     */
    default void findALLAssets(String username, String signature, int type, @NonNull BasePresenter.HttpCallback<AllAsset> callback) {
        getBasePresenter().requestHttp(getHttpInterface().findALLAssets(username, signature, type),
                bean -> callback.run(bean.getData()));
    }

    /**
     * 3.0.3 众筹记录列表
     */
    default void findAssetsAllot(String username, String signature, int pageIndex, int pageSize, @NonNull BasePresenter.HttpCallback<List<AssetRecode>> callback) {
        getBasePresenter().requestHttp(getHttpInterface().findAssetsAllot(username, signature, pageIndex, pageSize),
                bean -> callback.run(bean.getData()));
    }

    /**
     * 3.0.11修改登录密码
     *
     * @param oldPwd 旧密码(sha256)
     * @param newPwd 新密码(sha256)
     */
    default void changepwd(String username, String signature, String oldPwd, String newPwd, String twicepwd, @NonNull BasePresenter.HttpCallback callback) {
        getBasePresenter().requestHttp(getHttpInterface().changepwd(username, signature, oldPwd, newPwd, twicepwd),
                bean -> callback.run(null));
    }

    /**
     * 3.0.15实名认证
     */
    default void beginAuth(String username, String signature, String country, String name, String card, int cardType, String cardPositive, String cardContrary, String cardHold, String mobile, String code, @NonNull BasePresenter.HttpCallback callback) {
        getBasePresenter().requestHttp(R.string.common_certification_,
                getHttpInterface().beginAuth(username, signature, country, name, card, cardType, cardPositive, cardHold, cardContrary, mobile, code),
                bean -> callback.run(null));
    }


    /**
     * 1.0.37 校验手机号和证件号后4位（修改手机号）
     */
    default void verifyMobileAndCardCode(String username, String signature, String phoneNo, String cardCode, String loginPwd, @NonNull BasePresenter.HttpCallback<String> callback) {
        getBasePresenter().requestHttp(getHttpInterface().verifyMobileAndCardCode(username, signature, phoneNo, cardCode, loginPwd), bean -> callback.run(bean.msg));
    }

    /**
     * 1.0.37 校验手机号和证件号后4位（绑定手机号）
     */
    default void verifyIMGCodeAndMobile(String username, String signature, String phoneNo, String code, @NonNull BasePresenter.HttpCallback<String> callback) {
        getBasePresenter().requestHttp(getHttpInterface().verifyIMGCodeAndMobile(username, signature, phoneNo, code), bean -> callback.run(bean.msg));
    }

    /**
     * 1.0.36 校验图形验证码和邮箱（绑定邮箱）
     */
    default void verifyIMGCodeAndEmail(String username, String signature, String email, String code, @NonNull BasePresenter.HttpCallback<String> callback) {
        getBasePresenter().requestHttp(getHttpInterface().verifyIMGCodeAndEmail(username, signature, email, code), bean -> callback.run(bean.msg));
    }

    /**
     * 1.0.38 校验邮箱和证件号后4位（修改邮箱）
     */
    default void verifyEmailAndCardCode(String username, String signature, String email, String cardCode, String loginPws, @NonNull BasePresenter.HttpCallback<String> callback) {
        getBasePresenter().requestHttp(getHttpInterface().verifyEmailAndCardCode(username, signature, email, cardCode, loginPws), bean -> callback.run(bean.msg));
    }

    /**
     * 1.0.13 绑定、修改手机号
     */
    default void updateMobile(String username, String signature, String phoneNo, String random, String verifyCode, String nationCode, @NonNull BasePresenter.HttpCallback callback) {
        getBasePresenter().requestHttp(R.string.common_operate_ing, getHttpInterface().updateMobile(username, signature, phoneNo, random, verifyCode, nationCode),
                bean -> callback.run(null));
    }

    /**
     * 3.0.12 绑定、修改邮箱
     */
    default void updateEmail(String username, String signature, String emailNo, String random, String verifyCode, @NonNull BasePresenter.HttpCallback callback) {
        getBasePresenter().requestHttp(R.string.common_operate_ing, getHttpInterface().updateEmail(username, signature, emailNo, random, verifyCode),
                bean -> callback.run(null));
    }

    /**
     * 7.0.1 app版本更新
     */
    default void findLatestVersion(@NonNull BasePresenter.HttpCallback<LatestVersionBean> callback) {
        getBasePresenter().requestHttp(getHttpInterface().findLatestVersion(2),
                bean -> callback.run(bean.getData()));
    }

    /**
     * 2.0.8 设置昵称
     */
    default void setNickname(String username, String signature, String nickname, @NonNull BasePresenter.HttpCallback callback) {
        getBasePresenter().requestHttp(getHttpInterface().setNickname(username, signature, nickname),
                bean -> callback.run(null));
    }

    /**
     * 2.1.6 修改用户头像
     */
    default void updateUserLogo(String username, String signature, String url, @NonNull BasePresenter.HttpCallback callback) {
        getBasePresenter().requestHttp(getHttpInterface().updateUserLogo(username, signature, url),
                bean -> callback.run(null));
    }


    /**
     * 2.1.7 修改用户性别
     */
    default void updateUserGender(String username, String signature, int gender, @NonNull BasePresenter.HttpCallback callback) {
        getBasePresenter().requestHttp(getHttpInterface().updateUserGender(username, signature, gender),
                bean -> callback.run(null));
    }

    /**
     * 1.0.7找回登录密码
     */
    default void resetPwd(String username, String random, String password, String confirmPassword, @NonNull BasePresenter.HttpCallback<String> callback) {
        getBasePresenter().requestHttp(getHttpInterface().resetPwd(username, random, password, confirmPassword),
                bean -> callback.run(bean.getMsg()));
    }

    /**
     * 2.1.4 校验验证码
     */
    default void verificationRandomCode(String username, String code, String reason, @NonNull BasePresenter.HttpCallback callback) {
        getBasePresenter().requestHttp(getHttpInterface().verificationRandomCode(username, code, reason),
                bean -> callback.run(null));
    }

    /**
     * 6.0.1设置修改交易密码
     */
    default void updatePayPwd(String username, String signature, String password, String password2, @Nullable String randomCode, int type, @Nullable String verifyCode, @Nullable String verifyType, @NonNull BasePresenter.HttpCallback callback) {
        getBasePresenter().requestHttp(getHttpInterface().updatePayPwd(username, signature, password, password2, randomCode, type, verifyCode, verifyType),
                bean -> callback.run(null));
    }

    /**
     * 5.0.2 获取各级别签到奖励
     */
    default void getSignInRewardInfo(String username, String signature, @NonNull BasePresenter.HttpCallback<SignLevel> callback) {
        getBasePresenter().requestHttp(getHttpInterface().getSignInRewardInfo(username, signature),
                bean -> callback.run(bean.getData()));
    }

    /**
     * 5.0.3 获取签到奖励爪力信息
     */
    default void getUserSignInInfo(String username, String signature, @NonNull BasePresenter.HttpCallback<UserSignInfo> callback) {
        getUserSignInInfo(username, signature, 1, callback);
    }

    /**
     * 5.0.3 获取签到奖励爪力信息
     */
    default void getUserSignInInfo(String username, String signature, int pageIndex, @NonNull BasePresenter.HttpCallback<UserSignInfo> callback) {
        getBasePresenter().requestHttp(getHttpInterface().getUserSignInInfo(username, signature, pageIndex),
                bean -> callback.run(bean.getData()));
    }

    /**
     * 2.0.5 邀请记录统计
     */
    default void findInviteStatistics(String username, String signature, @NonNull BasePresenter.HttpCallback<InviteStatistics> callback) {
        getBasePresenter().requestHttp(getHttpInterface().findInviteStatistics(username, signature),
                bean -> callback.run(bean.getData()));
    }


    /**
     * 2.0.4 奖励列表
     */
    default void findRewardList(String username, String signature, int type, int pageIndex, int pageSize, @NonNull BasePresenter.HttpCallback<List<RewardList>> callback) {
        getBasePresenter().requestHttp(getHttpInterface().findRewardList(username, signature, type, pageIndex, pageSize),
                bean -> callback.run(bean.getData()));
    }


    /**
     * 5.0.1 用户签到
     */
    default void doSignIn(String username, String signature, @NonNull BasePresenter.HttpCallback<String> callback) {
        getBasePresenter().requestHttp(getHttpInterface().doSignIn(username, signature),
                bean -> callback.run(bean.getData()));
    }

    /**
     * 1.0.28 校验验证码
     */
    default void verifyRandomCode(String username, String signature, String randomCode, String reason, @NonNull BasePresenter.HttpCallback<String> callback) {
        getBasePresenter().requestHttp(getHttpInterface().verifyRandomCode(username, signature, randomCode, reason),
                bean -> callback.run(bean.msg));
    }

    /**
     * 1.0.30 校验证件号码
     */
    default void verifyIdCard(String username, String signature, String cardCode, String verifyCode, @NonNull BasePresenter.HttpCallback<String> callback) {
        getBasePresenter().requestHttp(getHttpInterface().verifyIdCard(username, signature, cardCode, verifyCode),
                bean -> callback.run(bean.msg));
    }

    /**
     * 1.0.29 校验交易密码
     */
    default void verifyPayPwd(String username, String signature, String oldPassword, String reason, @NonNull BasePresenter.HttpCallback<String> callback) {
        getBasePresenter().requestHttp(getHttpInterface().verifyPayPwd(username, signature, oldPassword, reason),
                bean -> callback.run(bean.msg));
    }


    /**
     * 1.1.3 获取转出详情
     */
    default void getTransferOutDetail(String username, String signature, int pageIndex, int pageSize, @NonNull BasePresenter.HttpCallback<WalletTakeOutBean> callback) {
        getBasePresenter().requestHttp(getHttpInterface().getTransferOutDetail(username, signature, pageIndex, pageSize),
                bean -> callback.run(bean.getData()),
                throwable -> callback.run(null));
    }

    /**
     * 1.1.4 转出(提币)申请
     */
    default void transferOut(String username, String signature, String amount, String payPwd, String address, @NonNull BasePresenter.HttpCallback callback) {
        getBasePresenter().requestHttp(getHttpInterface().transferOut(username, signature, amount, payPwd, address),
                bean -> callback.run(null));
    }

    /**
     * 1.1.5 撤销转出申请
     * <p>
     * 提示:转出申请处理状态>=3时不可撤销
     */
    default void canceledTransferOut(String username, String signature, long id, @NonNull BasePresenter.HttpCallback callback) {
        getBasePresenter().requestHttp(getHttpInterface().canceledTransferOut(username, signature, id),
                bean -> callback.run(null));
    }

    /**
     * 6.0.1 获取国际短信编码
     */
    default void findInternationSmsList(@NonNull BasePresenter.HttpCallback<List<CountryBean>> callback) {
        getBasePresenter().requestHttp(R.string.activity_contact_list_loading, getHttpInterface().findInternationSmsList(),
                bean -> callback.run(bean.getData()));
    }

    /**
     * 获取可置信列表
     */
    default void getTrustList(@NonNull BasePresenter.HttpCallback<List<TrustListBean>> callback) {
        getBasePresenter().requestHttp(getHttpInterface().getTrustList(),
                bean -> {
                    String server = bean.server;
                    for (TrustListBean trustListBean : bean.getData()) {
                        String logo = trustListBean.getLogo();
                        if (!TextUtils.isEmpty(logo)) {
                            trustListBean.setLogo(server + logo);
                        }
                    }
                    callback.run(bean.getData());
                },
                err -> callback.run(null));
    }

    /**
     * 7.0.4 获取汇率
     */
    default void exchangeRate(String fromCode, String toCode, @NonNull BasePresenter.HttpCallback<ExchangeRate> callback) {
        getBasePresenter().requestHttp(getHttpInterface().exchangeRate(fromCode, toCode), tag -> callback.run(tag.getData()));
    }

    /**
     * 7.0.5 获取非USDP币种列表
     */
    default void findCurrencyList(@NonNull BasePresenter.HttpCallback<List<OtherCurrency>> callback) {
        getBasePresenter().requestHttp(getHttpInterface().findCurrencyList(), tag -> callback.run(tag.getData()));

    }

    /**
     * 7.0.6 获取用户USDP可用资产
     */
    default void findAssetsUSDP(String username, String signature, BasePresenter.HttpCallback<UsableBean> callback) {
        getBasePresenter().requestHttp(getHttpInterface().findAssetsUsdp(username, signature), tag -> callback.run(tag.getData()));
    }

    /**
     * 7.0.7 币种兑换
     */
    default void exchangeCurrency(String username, String signature, int currencyId, String amount, String exchangeAmount, String exchangeRate, int bankCardId, @NonNull BasePresenter.HttpCallback<Object> callback) {
        getBasePresenter().requestHttp(getHttpInterface().exchangeCurrency(username, signature, currencyId, amount, exchangeAmount, exchangeRate, bankCardId), tag -> callback.run(null));
    }

    /**
     * 7.0.8 币种兑换记录列表
     */
    default void exchangeList(String username, String signature, int pageIndex, int pageSize, @NonNull BasePresenter.HttpCallback<List<ExchangeRecord>> callback) {
        getBasePresenter().requestHttp(getHttpInterface().exchangeList(username, signature, pageIndex, pageSize), tag -> callback.run(tag.getData()));
    }

    /**
     * 7.0.9 兑换记录撤销
     */
    default void exchangeBackOut(String username, String signature, int id, @NonNull BasePresenter.HttpCallback<Object> callback) {
        getBasePresenter().requestHttp(getHttpInterface().exchangeBackOut(username, signature, id), tag -> callback.run(null));
    }


    /**
     * 7.0.1 添加银行卡
     */
    default void addBankCard(String username, String signature, int currencyId, String name, String country, String deposit_bank, String branch_name, String card_number, BasePresenter.HttpCallback callback) {
        getBasePresenter().requestHttp(getHttpInterface().addBankCard(username, signature, currencyId, name, country, deposit_bank, branch_name, card_number, card_number), tag -> callback.run(null));
    }

    /**
     * 7.0.2 用户银行卡列表
     */
    default void findBankCardList(String username, String signature, Integer currencyId, BasePresenter.HttpCallback<List<BankCard>> callback) {
        getBasePresenter().requestHttp(getHttpInterface().findBankCardList(username, signature, currencyId), tag -> callback.run(tag.getData()));
    }


    /**
     * 7.0.3 用户银行卡删除
     */
    default void deleteBankCard(String username, String signature, int id, BasePresenter.HttpCallback callback) {
        getBasePresenter().requestHttp(getHttpInterface().deleteBankCard(username, signature, id), tag -> callback.run(null));
    }


    /**
     * 1.1.5 获取充值提币交易记录
     */
    default void getTxList(String username, String signature, int pageIndex, int pageSize, @NonNull BasePresenter.HttpCallback<List<RecentTransferRecordBean>> callback) {
        getBasePresenter().requestHttp(getHttpInterface().getTxList(username, signature, pageIndex, pageSize),
                tag -> callback.run(tag.getData()),
                tag -> callback.run(null));
    }

    /**
     * 1.1.5 获取钱包地址
     */
    default void getList(String username, String signature, @NonNull BasePresenter.HttpCallback<TakeInBean> callback) {
        getBasePresenter().requestHttp(getHttpInterface().getList(username, signature),
                tag -> {
                    if (tag.getData().size() != 0) {
                        callback.run(tag.getData().get(0));
                    }
                });
    }

    /**
     * 1.0.2 获取钱包充值记录
     */
    default void getRechargeRecord(String username, String signature, int pageIndex, int pageSize, @NonNull BasePresenter.HttpCallback<List<TakeInRecordBean>> callback) {
        getBasePresenter().requestHttp(getHttpInterface().getRechargeRecord(username, signature, pageIndex, pageSize),
                tag -> callback.run(tag.getData()),
                tag -> callback.run(null));
    }

    /***************************************************股票接口*********************************************************/
    /**
     * 1.0.1 获取券商信息
     */
    default void getTradeList(@NonNull BasePresenter.HttpCallback<List<TradeBean>> callback) {
        getBasePresenter().requestHttp(getHttpInterface().getTradeList(1), tag -> callback.run(tag.getData().getData()));
    }

    /**
     * 1.0.2 尊嘉用户登录
     */
    default void stockLogin(int trade_id, String account, String app_version, int height, String language, int width,
                            String os_version, String model, String platform, String uuid, int version, String password,
                            @NonNull BasePresenter.HttpCallback<LoginTradeResponse> callback) {
        getBasePresenter().requestHttp(getHttpInterface().stockLogin(trade_id, account, app_version, height, language, width, os_version, model, platform, uuid, version, password),
                tag -> callback.run(tag.getData()));
    }

    /**
     * 2.2 资金信息（包含持仓信息）
     */
    default void getAccountInfo(String requestStr, @NonNull BasePresenter.HttpCallback<StockAccountInfo> callback) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), requestStr.replaceAll("\\s", ""));
        getBasePresenter().requestHttp(getHttpInterface().getAccountInfo(requestBody), tag -> callback.run(tag.getResponse_data()));
    }

    /**
     * 2.3 下单
     */
    default void stockOrder(String requestStr, @NonNull BasePresenter.HttpCallback<OrderResponse> callback) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), requestStr.replaceAll("\\s", ""));
        getBasePresenter().requestHttp(getHttpInterface().stockOrder(requestBody), tag -> callback.run(tag.getResponse_data()));
    }

    /**
     * 2.4 搜索股票
     */
    default void searchStock(String requestStr, @NonNull BasePresenter.HttpCallback<List<SearchStockResponse>> callback) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), requestStr.replaceAll("\\s", ""));
        getBasePresenter().requestHttp(getHttpInterface().searchStock(requestBody), tag -> callback.run(tag.getResponse_data()));
    }

    /**
     * 2.5 当日/历史委托 支持委托和可撤查询
     */
    default void getDepute(String requestStr, @NonNull BasePresenter.HttpCallback<DeputeResponse> callback) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), requestStr.replaceAll("\\s", ""));
        getBasePresenter().requestHttp(getHttpInterface().getDepute(requestBody), tag -> callback.run(tag.getResponse_data()), error -> callback.run(null));
    }


    /**
     * 2.6 撤单
     */
    default void stockOrderCancel(String requestStr, @NonNull BasePresenter.HttpCallback callback) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), requestStr.replaceAll("\\s", ""));
        getBasePresenter().requestHttp(getHttpInterface().stockOrderCancel(requestBody), tag -> callback.run(null));
    }

    /**
     * 2.7 获取交易类型
     */
    default void getTradeType(String requestStr, @NonNull BasePresenter.HttpCallback<TradeTypeResponse> callback) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), requestStr.replaceAll("\\s", ""));
        getBasePresenter().requestHttp(getHttpInterface().getTradeType(requestBody), tag -> callback.run(tag.getResponse_data()));
    }


    /**
     * 2.8 当日成交
     */
    default void getExchangeDetail(String requestStr, @NonNull BasePresenter.HttpCallback<ExchangeResponse> callback) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), requestStr.replaceAll("\\s", ""));
        getBasePresenter().requestHttp(getHttpInterface().getExchangeDetail(requestBody), tag -> callback.run(tag.getResponse_data()));
    }

    /**
     * 2.9 获取股票基本面信息
     */
    default void getStockBasicInfo(String requestStr, @NonNull BasePresenter.HttpCallback<StockBasicInfo> callback) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), requestStr.replaceAll("\\s", ""));
        getBasePresenter().requestHttp(getHttpInterface().getStockBasicInfo(requestBody), tag -> callback.run(tag.getResponse_data()));
    }

    /**
     * 2.10 可申购新股列表接口
     */
    default void getHKNewStockList(String requestStr, @NonNull BasePresenter.HttpCallback<NewStockResponse> callback) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), requestStr.replaceAll("\\s", ""));
        getBasePresenter().requestHttp(getHttpInterface().getHKNewStockList(requestBody), tag -> callback.run(tag.getResponse_data()));
    }

    /**
     * 2.14 获取新股申购列表（包含即将上市和已上市）
     */
    default void getOnLineStockList(String requestStr, @NonNull BasePresenter.HttpCallback<NewStockResponse> callback) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), requestStr.replaceAll("\\s", ""));
        getBasePresenter().requestHttp(getHttpInterface().getOnLineStockList(requestBody), tag -> callback.run(tag.getResponse_data()));
    }

    /**
     * 1.0.3 提交意见反馈信息
     */
    default void addFeedBack(String username, String signature, String content, String mobile, @NonNull BasePresenter.HttpCallback callback) {
        getBasePresenter().requestHttp(getHttpInterface().addFeedBack(username, signature, content, mobile),
                tag -> callback.run(null));
    }

    /**
     * 1.0.4 获取资讯列表
     */
    default void getNewsList(int pageIndex, int pageSize, int type, String title, @NonNull BasePresenter.HttpCallback<List<InformationRecordBean>> callback) {
        getBasePresenter().requestHttp(getHttpInterface().getNewsList(pageIndex, pageSize, type, title),
                tag -> callback.run(tag.getData().getData()),
                err -> callback.run(null));
    }

    /**
     * 1.0.5 获取资讯详情
     */
    default void getNewsDetail(int id, String username, String signature, @NonNull BasePresenter.HttpCallback<InformationDetailBean> callback) {
        getBasePresenter().requestHttp(getHttpInterface().getNewsDetail(id, signature, username),
                tag -> callback.run(tag.getData().getData()));
    }

    /**
     * 1.0.6 获取资讯收藏列表
     */
    default void getCollectList(int pageIndex, int pageSize, String signature, String username, @NonNull BasePresenter.HttpCallback<List<InformationRecordBean>> callback) {
        getBasePresenter().requestHttp(getHttpInterface().getCollectList(pageIndex, pageSize, signature, username),
                tag -> callback.run(tag.getData().getData()),
                err -> callback.run(null));
    }

    /**
     * 1.0.7 新增/删除资讯收藏
     */
    default void collectionNews(int id, String signature, String username, @NonNull BasePresenter.HttpCallback callback) {
        getBasePresenter().requestHttp(getHttpInterface().collectionNews(id, signature, username),
                tag -> callback.run(null));
    }

    /**
     * 1.0.8 点赞/取消点赞资讯
     */
    default void newsLike(int id, String signature, String username, @NonNull BasePresenter.HttpCallback callback) {
        getBasePresenter().requestHttp(getHttpInterface().newsLike(id, signature, username),
                tag -> callback.run(null));
    }


    /**
     * 1.0.2 股票基本信息查询
     */
    default void getById(int id, @NonNull BasePresenter.HttpCallback<StockInfo> callback) {
        getBasePresenter().requestHttp(getHttpInterface().getById(id), tag -> callback.run(tag.getData()));
    }

    /**
     * 1.0.2 股票基本信息查询 - 返回字段获取
     */
    default void getByIdOptional(String username, String signature, Integer id, String securityCode, @NonNull BasePresenter.HttpCallback<OptionalListBean> callback) {
        getBasePresenter().requestHttp(getHttpInterface().getByIdOptional(username, signature, id, securityCode), tag -> callback.run(tag.getData()));
    }

    /**
     * 1.0.5 添加/取消自选
     *
     * @param code 证券编码
     */
    default void addOptional(String username, String signature, String code, @NonNull BasePresenter.HttpCallback callback) {
        getBasePresenter().requestHttp(getHttpInterface().addOptional(username, signature, code),
                tag -> callback.run(null));
    }

    /**
     * 1.0.6 自选列表
     */
    default void optionalList(String username, String signature, int pageIndex, int pageSize, @NonNull BasePresenter.HttpCallback<List<OptionalListBean>> callback) {
        getBasePresenter().requestHttp(getHttpInterface().optionalList(username, signature, pageIndex, pageSize),
                tag -> callback.run(tag.getData().getData()));
    }

    /**
     * 1.0.1 股票列表查询/窝轮牛熊/融资股票
     *
     * @param pageIndex    Y	int	分页页码
     * @param pageSize     Y	int	页面数据量
     * @param securityName N	String	证券编码/证券简体名称/证券繁体名称
     * @param type         N	int	1-窝轮 0-牛熊 2-融资股票 3-主板 4-创业板
     * @param sort         N	1-涨跌幅降序 2-涨跌幅升序 3-实时价格降序 4-实时价格升序 5-涨跌额降序 7 搜索时主板在前
     */
    default void getSecurityList(String securityName, @Nullable Integer type, int pageIndex, int pageSize, Integer sort, @NonNull BasePresenter.HttpCallback<List<OptionalListBean>> callback) {
        getBasePresenter().requestHttp(getHttpInterface().getSecurityList(securityName, type, pageIndex, pageSize, sort),
                tag -> callback.run(tag.getData().getData()));
    }

    /**
     * 1.0.1 股票列表查询/窝轮牛熊/融资股票
     *
     * @param pageIndex Y	int	分页页码
     * @param pageSize  Y	int	页面数据量
     * @param type      N	1-涨幅 2-跌幅
     */
    default void getPriceLimit(@Nullable Integer type, int pageIndex, int pageSize, Integer sort, @NonNull BasePresenter.HttpCallback<List<OptionalListBean>> callback) {
        getBasePresenter().requestHttp(getHttpInterface().getPriceLimit(type, pageIndex, pageSize, sort),
                tag -> callback.run(tag.getData().getData()));
    }

    /**
     * 1.0.13 热门行业股票列表
     *
     * @param sortDir  A 表示升序, 即ascend  D 表示降序, 即descend
     * @param induCode 分页时用于定位上下文(第一次可不传)，下一页传上一页列表最后一条记录的induCode
     * @param count    Y	int	页面数据量
     */
    default void industryStockList(@Nullable String sortDir, @Nullable String induCode, int count, @NonNull BasePresenter.HttpCallback<List<IndustryListBean>> callback) {
        getBasePresenter().requestHttp(getHttpInterface().industryStockList(sortDir == null ? "D" : sortDir, induCode, count),
                tag -> callback.run(tag.getData()));
    }

    /**
     * 1.0.14 行业成分股列表
     */
    default void industryComponentsList(@NonNull String induCode, int count, String assetId, Integer sortField, String sortDir, @NonNull BasePresenter.HttpCallback<List<IndustryPartBean>> callback) {
        getBasePresenter().requestHttp(getHttpInterface().industryComponentsList(induCode, count, assetId, sortField, sortDir),
                tag -> callback.run(tag.getData()));
    }

    /**
     * 1.0.4 交易规则
     */
    default void rulesList(@NonNull BasePresenter.HttpCallback<List<StockRulesBean>> callback) {
        getBasePresenter().requestHttp(getHttpInterface().rulesList(),
                tag -> callback.run(tag.getData()));
    }

    /**
     * 1.0.7 股票买卖盘列表
     *
     * @param securityCode 证券编码
     */
    default void buySellOrderList(String securityCode, @NonNull BasePresenter.HttpCallback<WSBuySellDeepBean> callback) {
        getBasePresenter().requestHttp(getHttpInterface().buySellOrderList(securityCode),
                tag -> callback.run(tag.getData()));
    }

    /**
     * 1.0.16 获取股票成交明细列表
     *
     * @param securityCode 证券编码
     */
    default void transactionDetailList(String securityCode, int pageIndex, int pageSize, @NonNull BasePresenter.HttpCallback<DealDetailBean> callback) {
        getBasePresenter().requestHttp(getHttpInterface().transactionDetailList(securityCode, null, pageIndex, pageSize),
                tag -> callback.run(tag.getData()));
    }

    /**
     * 1.0.8 股票分时
     *
     * @param securityCode 证券编码
     */
    default void stockTimeShareList(String securityCode, @NonNull BasePresenter.HttpCallback<StockMinHour> callback) {
        getBasePresenter().requestHttp(getHttpInterface().stockTimeShareList(securityCode),
                tag -> callback.run(tag.getData()));
    }

    /**
     * 1.0.9 股票日K/周K/月K
     *
     * @param dateType 时间类型 1-日K 2-周K 3-月K
     */
    default void klineList(String securityCode, int dateType, @NonNull BasePresenter.HttpCallback<List<StockKLineBean>> callback) {
        getBasePresenter().requestHttp(getHttpInterface().klineList(securityCode, dateType),
                tag -> callback.run(tag.getData()));
    }

    /**
     * 1.0.10 指数列表（恒生/国企/港通）
     */
    default void indexDataList(@NonNull BasePresenter.HttpCallback<List<WSExponentChangeBean>> callback) {
        getBasePresenter().requestHttp(getHttpInterface().indexDataList(),
                tag -> callback.run(tag.getData()));
    }

    /**
     * 1.0.11 指数分时
     */
    default void exponentTimeShareList(int type, @NonNull BasePresenter.HttpCallback<ExponentMinHour> callback) {
        getBasePresenter().requestHttp(getHttpInterface().exponentTimeShareList(type),
                tag -> callback.run(tag.getData()));
    }

    /**
     * 1.0.12 指数日K/周K/月K
     *
     * @param type     1-HSI 2-HSCEI 3-港通精选100指数
     * @param dateType 时间类型 1-日K 2-周K 3-月K
     */
    default void exponentKlineList(int type, int dateType, @NonNull BasePresenter.HttpCallback<List<ExponentKLineBean>> callback) {
        getBasePresenter().requestHttp(getHttpInterface().exponentKlineList(type, dateType),
                tag -> callback.run(tag.getData()));
    }

    /**
     * 1.0.14 获取市场状态
     */
    default void marketStatus(@NonNull BasePresenter.HttpCallback<List<WSMarketStatus>> callback) {
        getBasePresenter().requestHttp(getHttpInterface().marketStatus(),
                tag -> callback.run(tag.getData()));
    }

    /**
     * 2.2.3 微信登录
     */
    default void weChatLogin(String code, @NonNull BasePresenter.HttpCallback<WXLoginBean> callback, @NonNull BasePresenter.HttpCallback onError) {
        getBasePresenter().requestHttp(getHttpInterface().weChatLogin(code),
                callback::run,
                err -> onError.run(null));
    }

    /**
     * 2.2.4 绑定微信账号
     */
    default void weChatBind(String code, String mobile, String random, @NonNull BasePresenter.HttpCallback<WXLoginBean> callback, @NonNull BasePresenter.HttpCallback onError) {
        getBasePresenter().requestHttp(getHttpInterface().weChatBind(code, mobile, random),
                callback::run,
                err -> onError.run(null));
    }

    /**
     * 2.2.4 绑定微信账号
     */
    default void weChatBind(String code, String mobile, String random, @NonNull BasePresenter.HttpCallback<WXLoginBean> callback) {
        weChatBind(code, mobile, random, callback, tag -> {
        });
    }

    /**
     * 3.0.1 公司概要
     */
    default void corpSumm(String securityCode, @NonNull BasePresenter.HttpCallback<CompanyProfile> callback) {
        getBasePresenter().requestHttp(getHttpInterface().corpSumm(securityCode),
                tag -> callback.run(tag.getData()));
    }

    /**
     * 3.0.2 主要指标-市盈率列表（图表数据）
     */
    default void peList(String securityCode, int type, @NonNull BasePresenter.HttpCallback<List<PEBean>> callback) {
        getBasePresenter().requestHttp(getHttpInterface().peList(securityCode, type),
                tag -> callback.run(tag.getData()));
    }

    /**
     * 3.0.3 主要指标-市净率最新数据
     */
    default void pe(String securityCode, @NonNull BasePresenter.HttpCallback<PBBean> callback) {
        getBasePresenter().requestHttp(getHttpInterface().pe(securityCode),
                tag -> callback.run(tag.getData()));
    }

    /**
     * 3.0.4 利润表
     */
    default void plInfo(String securityCode, @NonNull BasePresenter.HttpCallback<ProfitBean> callback) {
        getBasePresenter().requestHttp(getHttpInterface().plInfo(securityCode),
                tag -> callback.run(tag.getData()));
    }

    /**
     * 3.0.5 资产负债表
     */
    default void balanceSheet(String securityCode, @NonNull BasePresenter.HttpCallback<SheetBean> callback) {
        getBasePresenter().requestHttp(getHttpInterface().balanceSheet(securityCode),
                tag -> callback.run(tag.getData()));
    }

    /**
     * 3.0.6 现金流量表最新数据
     */
    default void cashFlow(String securityCode, @NonNull BasePresenter.HttpCallback<CashFlowBean> callback) {
        getBasePresenter().requestHttp(getHttpInterface().cashFlow(securityCode),
                tag -> callback.run(tag.getData()));
    }

    /**
     * 3.0.7 现金流量列表（图表数据）
     */
    default void cashFlowList(String securityCode, int type, @NonNull BasePresenter.HttpCallback<List<CashFlowBean>> callback) {
        getBasePresenter().requestHttp(getHttpInterface().cashFlowList(securityCode, type),
                tag -> callback.run(tag.getData()));
    }

    /**
     * 3.0.8 财务概要（同主要指标其他信息）
     */
    default void financeSumm(String securityCode, @NonNull BasePresenter.HttpCallback<FinanceSummaryBean> callback) {
        getBasePresenter().requestHttp(getHttpInterface().financeSumm(securityCode),
                tag -> callback.run(tag.getData()));
    }

    /**
     * 3.0.9 分红送转(派息记录)
     */
    default void dh(String securityCode, @NonNull BasePresenter.HttpCallback<List<FHBean.BonusBean>> callback) {
        getBasePresenter().requestHttp(getHttpInterface().dh(securityCode),
                tag -> callback.run(tag.getData().getDfList()));
    }

    /**
     * 3.1.0 回购
     */
    default void sb(String securityCode, Integer pageIndex, Integer pageSize, @NonNull BasePresenter.HttpCallback<List<SBBean.BuyBackBean>> callback) {
        getBasePresenter().requestHttp(getHttpInterface().sb(securityCode, pageIndex, pageSize),
                tag -> callback.run(tag.getData().getSbList()));
    }

    /**
     * 3.1.1 股本信息
     */
    default void equityInfo(String securityCode, @NonNull BasePresenter.HttpCallback<EquityBean> callback) {
        getBasePresenter().requestHttp(getHttpInterface().equityInfo(securityCode),
                tag -> callback.run(tag.getData()));
    }

    /**
     * 3.1.2 股东明细
     */
    default void shareholderList(String securityCode, @NonNull BasePresenter.HttpCallback<List<HoldBean>> callback) {
        getBasePresenter().requestHttp(getHttpInterface().shareholderList(securityCode),
                tag -> callback.run(tag.getData()));
    }

    /**
     * 4.0.13 获取开户信息
     */
    default void getByUserIdAccount(String username, String signature, @NonNull BasePresenter.HttpCallback<EstablishStatusBean> callback) {
        getBasePresenter().requestHttp(getHttpInterface().getByUserIdAccount(username, signature),
                tag -> {
                    EstablishStatusBean bean = tag.getData();
                    if (bean != null) {
                        bean.setImgServerUrl(tag.imgServerUrl);
                    }
                    callback.run(bean);
                });
    }

    /**
     * 4.0.1 身份证明
     */
    default void saveIdcard(String username, String signature, String idcardPositive, String idcardContrary, @NonNull BasePresenter.HttpCallback<UploadIdCardBean> callback) {
        getBasePresenter().requestHttp(getHttpInterface().saveIdcard(username, signature, idcardPositive, idcardContrary),
                tag -> callback.run(tag.getData()));
    }


    /**
     * 4.0.2 确认身份信息
     *
     * @param name      Y	String	姓名
     * @param lastName  Y	String	姓的拼音
     * @param firstName Y	String	名的拼音
     * @param sex       Y	Int	性别 1：男 2：女
     * @param birth     Y	String	生日
     * @param address   Y	String	地址
     * @param idcard    Y	String	身份证号
     */
    default void saveIdentity(String username, String signature, String name, String lastName, String firstName, int sex, String birth, String address, String idcard, @NonNull BasePresenter.HttpCallback<BaseHttpBean> callback) {
        getBasePresenter().requestHttp(getHttpInterface().saveIdentity(username, signature, name, lastName, firstName, sex, birth, address, idcard),
                tag -> callback.run(null));
    }

    /**
     * 4.0.3 填写个人资料
     *
     * @param contactAddress   Y	String	通讯地址 1 相同, 2 不同
     * @param email            Y	String	邮箱
     * @param proofOfAddress   Y	String	住址证明
     * @param educationType    Y	Int	学历类型 1：中学或以下 2：大专 3：本科 4：硕士或以上
     * @param jobType          Y	Int	就业类型 1：受雇 2：自雇 3：退休 4：自由投资人
     * @param industryCategory Y	Int	行业分类 1：计算机/互联网/通信/电子技术 2：生产制造/物流运输 3：销售/贸易 3：金融/银行/保险 4：医药/化工 5：餐饮/娱乐/美容 6：广告公关/媒体/艺术文化 7：教育/法律 8：建筑/房地产 9：政府/事业机构 10：其他
     * @param companyName      Y	String	公司名称
     * @param dutyType         Y	Int	职务类型 1：高管 2：中层 3：普通员工
     * @param taxState         Y	String	税务国家/地区
     * @param taxNumber        Y	String	税务编号
     */
    default void savePersonalData(String username, String signature, boolean contactAddress, String email, String proofOfAddress, int educationType, int jobType, int industryCategory, String companyName, int dutyType, String taxState, String taxNumber, @NonNull BasePresenter.HttpCallback<BaseHttpBean> callback) {
        getBasePresenter().requestHttp(getHttpInterface().savePersonalData(username, signature, contactAddress ? "1" : "2", email, proofOfAddress, educationType, jobType, industryCategory, companyName, dutyType, taxState, taxNumber),
                tag -> callback.run(null));
    }


    /**
     * 4.0.4 银行卡验证
     *
     * @param name     Y	String	姓名
     * @param idcard   Y	String	身份证号
     * @param bankName Y	String	银行名称
     * @param cardNo   Y	String	银行卡号
     */
    default void saveBankCard(String username, String signature, String name, String idcard, String bankName, String cardNo, @NonNull BasePresenter.HttpCallback<BaseHttpBean> callback) {
        getBasePresenter().requestHttp(getHttpInterface().saveBankCard(username, signature, name, idcard, bankName, cardNo),
                tag -> callback.run(null));
    }

    /**
     * 4.0.5 个人声明
     *
     * @param personalStatement Y	String	个人声明 格式：1,2,3
     */
    default void savePersonalState(String username, String signature, String personalStatement, @NonNull BasePresenter.HttpCallback<BaseHttpBean> callback) {
        getBasePresenter().requestHttp(getHttpInterface().savePersonalState(username, signature, personalStatement),
                tag -> callback.run(null));
    }

    /**
     * 4.0.6 投资经验
     *
     * @param securitiesInvestmentExType Y	Int	证券投资经验类型 1：0-1年  2：1-3年  3：3-5年  4：5年以上
     * @param fundInvestmentExType       Y	Int	基金投资经验类型 1：0-1年  2：1-3年  3：3-5年  4：5年以上
     * @param foreignInvestmentExType    Y	Int	外汇投资经验类型 1：0-1年  2：1-3年  3：3-5年  4：5年以上
     * @param fixedInvestmentExType      Y	Int	固定收益产品投资经验类型 1：0-1年  2：1-3年  3：3-5年  4：5年以上
     * @param otherInvestmentExType      Y	Int	其他投资经验类型 1：0-1年  2：1-3年  3：3-5年  4：5年以上
     */
    default void saveInvestmentExperience(String username, String signature, int securitiesInvestmentExType, int fundInvestmentExType, int foreignInvestmentExType, int fixedInvestmentExType, int otherInvestmentExType, @NonNull BasePresenter.HttpCallback<BaseHttpBean> callback) {
        getBasePresenter().requestHttp(getHttpInterface().saveInvestmentExperience(username, signature, securitiesInvestmentExType, fundInvestmentExType, foreignInvestmentExType, fixedInvestmentExType, otherInvestmentExType),
                tag -> callback.run(null));
    }

    /**
     * 4.0.7 财务状况
     *
     * @param householdIncomeType     Y	Int	家庭年收入 1：<20w  2：20-50w 3：50-100w  4：>100w
     * @param householdNetWorthType   Y	Int	家庭净资产 1：<50w  2：50-200w 3：200-1000w  4：>1000w
     * @param riskToleranceType       Y	Int	风险承受能力 1：高(30%以上損失)  2：中(20%-30%損失) 3：低(10%-20%損失) 4：很低(10%以下損失)
     * @param investmentObjectiveType Y	Int	投资目标 1：資本增值 2：股息回报 3：投机 4：对冲 5：其他
     */
    default void saveFinancialSituation(String username, String signature, int householdIncomeType, int householdNetWorthType, int riskToleranceType, int investmentObjectiveType, @NonNull BasePresenter.HttpCallback<BaseHttpBean> callback) {
        getBasePresenter().requestHttp(getHttpInterface().saveFinancialSituation(username, signature, householdIncomeType, householdNetWorthType, riskToleranceType, investmentObjectiveType),
                tag -> callback.run(null));
    }

    /**
     * 4.0.8 选择账户
     *
     * @param openAccountType                 Y	String	选择开通的账户类型 格式 1,2  1-融资账户 2-现金账户
     * @param openMarketType                  Y	String	选择开通的市场类型 格式1,2 1：港股（必选）2：中华通 3：美股
     * @param ifPromotionOfDerivatives        Y	Int	是否推行衍生品投资 0-是 1-否
     * @param derivativesInvestmentExperience Y	String	衍生品投资经历 格式：1,2  1：我曾受過有關衍生品的培訓課程 2：具有與衍生品相關的工作經驗 3：在過去三年，至少產生過5次有關衍生品交易
     */
    default void saveSelectAccount(String username, String signature, int openAccountType, String openMarketType, int ifPromotionOfDerivatives, String derivativesInvestmentExperience, @NonNull BasePresenter.HttpCallback<BaseHttpBean> callback) {
        getBasePresenter().requestHttp(getHttpInterface().saveSelectAccount(username, signature, openAccountType, openMarketType, ifPromotionOfDerivatives, derivativesInvestmentExperience),
                tag -> callback.run(null));
    }

    /**
     * 4.0.9 密码设置
     *
     * @param password        Y	String	密码(md5加密)
     * @param confirmPassword Y	String	确认密码(md5加密)
     */
    default void savePassword(String username, String signature, String password, String confirmPassword, @NonNull BasePresenter.HttpCallback<BaseHttpBean> callback) {
        getBasePresenter().requestHttp(getHttpInterface().savePassword(username, signature, password, confirmPassword),
                tag -> callback.run(null));
    }

    /**
     * 4.0.10 上传手持证件照
     *
     * @param idcardHord Y	String	手持证件照
     */
    default void saveHoldIdcard(String username, String signature, String idcardHord, @NonNull BasePresenter.HttpCallback<BaseHttpBean> callback) {
        getBasePresenter().requestHttp(getHttpInterface().saveHoldIdcard(username, signature, idcardHord),
                tag -> callback.run(null));
    }

    /**
     * 4.0.11 身份检测（保存人脸识别照）
     *
     * @param faceVerification Y	String	人脸识别照片
     */
    default void savefaceVerification(String username, String signature, String faceVerification, @NonNull BasePresenter.HttpCallback<BaseHttpBean> callback) {
        getBasePresenter().requestHttp(getHttpInterface().savefaceVerification(username, signature, faceVerification),
                tag -> callback.run(null));
    }

    /**
     * 4.0.12 确认签名照
     *
     * @param signaturePic Y	String	人脸识别照片
     */
    default void saveSignature(String username, String signature, String signaturePic, @NonNull BasePresenter.HttpCallback<BaseHttpBean> callback) {
        getBasePresenter().requestHttp(getHttpInterface().saveSignature(username, signature, signaturePic),
                tag -> callback.run(null));
    }

    /**
     * 1.0.18 据正股获取涡轮牛熊股票列表
     *
     * @param signaturePic Y	String	人脸识别照片
     */
    default void underlyingStockList(int pageIndex, int pageSize, String securityCode, int type, int productType, Integer sort, String username, @NonNull BasePresenter.HttpCallback<List<OptionalListBean>> callback) {
        getBasePresenter().requestHttp(getHttpInterface().underlyingStockList(pageIndex,  pageSize,  securityCode,  type,  productType, sort,  username),
                tag -> callback.run(tag.getData().getData()));
    }
}
