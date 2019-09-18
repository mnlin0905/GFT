package com.linktech.gft.retrofit;

import android.support.annotation.Nullable;

import com.linktech.gft.base.BaseHttpBean;
import com.linktech.gft.base.BaseHttpBeanWithData;
import com.linktech.gft.base.BaseStockBean;
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

import java.util.List;

import io.reactivex.Observable;
import kotlin.Unit;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * 功能----使用retrofit框架与http交换数据
 * <p>
 * 内嵌功能需要添加:
 * 接口请求头需要添加: mining-channel : liantongshuzi
 * 帐号:
 * 交易账号：58995899
 * 交易密码：Zq129007074
 *
 * <p>
 * Created by LinkTech on 2017/9/25.
 * <p>
 * 注:短信/邮箱 验证码
 */

public interface HttpInterface {
    /**
     * 获取通用服务协议
     * <p>
     * 1:钱包服务协议 2:生态服务协议 3:生态版权信息 4:锁仓释放规则 5:挖矿奖励规则
     */
    @POST("serviceAgreement/getList")
    @FormUrlEncoded
    Observable<BaseHttpBeanWithData<CommonAgreementBean>> getCommonAgreementList(@Field("category") int category);

    /**
     * 获取生态服务列表
     */
    @POST("IndexService/getServiceList")
    @FormUrlEncoded
    Observable<BaseHttpBeanWithData<List<ServiceBean>>> getServiceList(@Field("language") String language);

    /**
     * 获取banner
     */
    @POST("index/getBanner")
    @FormUrlEncoded
    Observable<BaseHttpBeanWithData<List<BannerBean>>> getBanner(@Field("language") int language);

    /**
     * 1.0.1 登录
     *
     * @param username 用户名
     * @param password 密码
     * @return 登录返回对象
     */
    @POST("api/member/login")
    @FormUrlEncoded
    Observable<BaseHttpBeanWithData<LoginBean>> login(@Field("username") String username,
                                                      @Field("password") String password);

    /**
     * 6.0.4动态登录
     *
     * @param username 用户名
     * @param random   验证码
     * @return 登录返回对象
     */
    @POST("api/member/dynamicLogin")
    @FormUrlEncoded
    Observable<BaseHttpBeanWithData<LoginBean>> dynamicLogin(@Field("username") String username,
                                                             @Field("random") String random);

    /**
     * 获取会员邀请码
     *
     * @return 请求返回信息
     */
    @POST("invitedRecord/findInvitedCode")
    @FormUrlEncoded
    Observable<BaseHttpBeanWithData<InviteCodeBean>> findInvitedCode(@Field("signature") String signature,
                                                                     @Field("username") String username);

    /**
     * 注册奖励列表
     * <p>
     * 是否领取 0-否 1-是 （不传获取所有
     *
     * @return 请求返回信息
     */
    @POST("invitedRecord/findInvitedList")
    @FormUrlEncoded
    Observable<BaseHttpBeanWithData<List<InviteListBean>>> findInvitedList(@Field("username") String username,
                                                                           @Field("signature") String signature,
                                                                           @Field("pageIndex") int pageIndex,
                                                                           @Field("pageSize") int pageSize,
                                                                           @Field("type") @Nullable Integer type);

    /**
     * 1.0.7基本信息
     *
     * @return 请求返回信息
     */
    @POST("api/member/getMemberInfo")
    @FormUrlEncoded
    Observable<BaseHttpBeanWithData<UserBaseInfoBean>> getMemberInfo(@Field("signature") String signature,
                                                                     @Field("username") String username);

    /**
     * 2.1.0 获取用户实名信息
     *
     * @return 请求返回信息
     */
    @POST("api/member/getExamineInfo")
    @FormUrlEncoded
    Observable<BaseHttpBeanWithData<CertificationResultBean>> getExamineInfo(@Field("username") String username,
                                                                             @Field("signature") String signature);

    /**
     * 1.0.5 注册
     */
    @POST("api/member/register")
    @FormUrlEncoded
    Observable<BaseHttpBean> register(@Field("username") String username,
                                      @Field("random") String random,
                                      @Field("password") String password,
                                      @Field("confirmPassword") String confirmpassword,
                                      @Field("referrer") String referrer,
                                      @Field("nationCode") String nationCode);

    /**
     * 1.0.6 激活钱包地址
     */
    @POST("pawsWallet/activeAddress")
    @FormUrlEncoded
    Observable<BaseHttpBean> activeAddress(@Field("address") String address,
                                           @Field("deviceId") String deviceId,
                                           @Field("macId") String macId,
                                           @Field("currency") String currency);

    /**
     * 发送手机验证码
     */
    @POST("api/send/sms")
    @FormUrlEncoded
    Observable<BaseHttpBean> sendInformation(@Field("username") String phone,
                                             @Field("reason") String r_reason,
                                             @Field("nationCode") String nationCode);

    /**
     * 邮箱验证码
     */
    @POST("api/send/email")
    @FormUrlEncoded
    Observable<BaseHttpBean> sendEmail(@Field("username") String email,
                                       @Field("reason") String r_reason,
                                       @Field("language") int language);

    /**
     * 3.0.14 实名认证图片上传
     */
    @POST("api/doUpload")
    Observable<BaseHttpBeanWithData<List<UploadCerImgBean>>> appUploadImg(@Body MultipartBody body);

    /**
     * 3.0.3 修改个人资料
     *
     * @param gender int 1 nan 2 nv 3 保密
     */
    @POST("account/saveUserBaseInfo")
    @FormUrlEncoded
    Observable<BaseHttpBean> saveUserBaseInfo(@Field("username") String username,
                                              @Field("signature") String signature,
                                              @Field("nickname") String nickname,
                                              @Field("gender") Integer gender);

    /**
     * 3.0.1 获取个人资产
     */
    @POST("invitedRecord/getAsset")
    @FormUrlEncoded
    Observable<BaseHttpBeanWithData<List<MyAsset>>> getAsset(@Field("username") String username,
                                                             @Field("signature") String signature);

    /**
     * 3.0.2 获取各类资产
     */
    @POST("asset/findALLAssets")
    @FormUrlEncoded
    Observable<BaseHttpBeanWithData<AllAsset>> findALLAssets(@Field("username") String username,
                                                             @Field("signature") String signature,
                                                             @Field("type") int type);

    /**
     * 3.0.3 众筹记录列表
     */
    @POST("asset/findAssetsAllot")
    @FormUrlEncoded
    Observable<BaseHttpBeanWithData<List<AssetRecode>>> findAssetsAllot(@Field("username") String username,
                                                                        @Field("signature") String signature,
                                                                        @Field("pageIndex") int type,
                                                                        @Field("pageSize") int pageSize);

    /**
     * 3.0.11修改登录密码
     *
     * @param oldPwd 旧密码(sha256)
     * @param newPwd 新密码(sha256)
     */
    @POST("api/member/updateLoginPwd")
    @FormUrlEncoded
    Observable<BaseHttpBean> changepwd(@Field("username") String username,
                                       @Field("signature") String signature,
                                       @Field("oldPassword") String oldPwd,
                                       @Field("password") String newPwd,
                                       @Field("confirmPassword") String confirmPassword);

    /**
     * 3.0.15实名认证
     */
    @POST("api/member/addExamine")
    @FormUrlEncoded
    Observable<BaseHttpBean> beginAuth(@Field("username") String username,
                                       @Field("signature") String signature,
                                       @Field("country") String country,
                                       @Field("name") String name,
                                       @Field("card") String card,
                                       @Field("cardType") int cardType,
                                       @Field("cardPositive") String cardPositive,
                                       @Field("cardContrary") String cardContrary,
                                       @Field("cardHold") String cardHold,
                                       @Field("mobile") String mobile,
                                       @Field("code") String code);

    /**
     * 1.0.37 校验手机号和证件号后4位（修改手机号）
     */
    @POST("account/verifyMobileAndCardCode")
    @FormUrlEncoded
    Observable<BaseHttpBeanWithData<String>> verifyMobileAndCardCode(@Field("username") String username,
                                                                     @Field("signature") String signature,
                                                                     @Field("phoneNo") String phoneNo,
                                                                     @Field("cardCode") String cardCode,
                                                                     @Field("loginPwd") String loginPwd);

    /**
     * 1.0.31 校验图形验证码和手机号（绑定手机号）
     */
    @POST("account/verifyIMGCodeAndMobile")
    @FormUrlEncoded
    Observable<BaseHttpBeanWithData<String>> verifyIMGCodeAndMobile(@Field("username") String username,
                                                                    @Field("signature") String signature,
                                                                    @Field("phoneNo") String phoneNo,
                                                                    @Field("code") String code);

    /**
     * 1.0.36 校验图形验证码和邮箱（绑定邮箱）
     */
    @POST("account/verifyIMGCodeAndEmail")
    @FormUrlEncoded
    Observable<BaseHttpBeanWithData<String>> verifyIMGCodeAndEmail(@Field("username") String username,
                                                                   @Field("signature") String signature,
                                                                   @Field("email") String email,
                                                                   @Field("code") String code);


    /**
     * 1.0.38 校验邮箱和证件号和登录密码（修改邮箱）
     */
    @POST("account/verifyEmailAndCardCode")
    @FormUrlEncoded
    Observable<BaseHttpBeanWithData<String>> verifyEmailAndCardCode(@Field("username") String username,
                                                                    @Field("signature") String signature,
                                                                    @Field("email") String email,
                                                                    @Field("cardCode") String cardCode,
                                                                    @Field("loginPwd") String loginPwd);

    /**
     * 1.0.13 绑定、修改手机号
     */
    @POST("account/updateMobile")
    @FormUrlEncoded
    Observable<BaseHttpBean> updateMobile(@Field("username") String username,
                                          @Field("signature") String signature,
                                          @Field("phoneNo") String phoneNo,
                                          @Field("random") String random,
                                          @Field("verifyCode") String verifyCode,
                                          @Field("nationCode") String nationCode);

    /**
     * 1.0.12 绑定、修改邮箱
     */
    @POST("account/updateEmail")
    @FormUrlEncoded
    Observable<BaseHttpBean> updateEmail(@Field("username") String username,
                                         @Field("signature") String signature,
                                         @Field("emailNo") String emailNo,
                                         @Field("random") String random,
                                         @Field("verifyCode") String verifyCode);

    /**
     * 7.0.1 app版本更新
     */
    @POST("api/getAppVersionInfo")
    @FormUrlEncoded
    Observable<BaseHttpBeanWithData<LatestVersionBean>> findLatestVersion(@Field("type") int type);

    /**
     * 2.1.4 校验验证码
     */
    @POST("api/verificationRandomCode")
    @FormUrlEncoded
    Observable<BaseHttpBean> verificationRandomCode(@Field("username") String username,
                                                    @Field("code") String code,
                                                    @Field("reason") String reason);

    /**
     * 2.0.8 设置昵称
     */
    @POST("api/member/setNickname")
    @FormUrlEncoded
    Observable<BaseHttpBean> setNickname(@Field("username") String username,
                                         @Field("signature") String signature,
                                         @Field("nickname") String nickname);

    /**
     * 2.1.6 修改用户头像
     */
    @POST("api/member/updateUserLogo")
    @FormUrlEncoded
    Observable<BaseHttpBean> updateUserLogo(@Field("username") String username,
                                            @Field("signature") String signature,
                                            @Field("url") String url);


    /**
     * 2.1.7 修改用户性别
     */
    @POST("api/member/updateUserGender")
    @FormUrlEncoded
    Observable<BaseHttpBean> updateUserGender(@Field("username") String username,
                                              @Field("signature") String signature,
                                              @Field("gender") int gender);

    /**
     * 1.0.7找回登录密码
     */
    @POST("api/member/forgetPassword")
    @FormUrlEncoded
    Observable<BaseHttpBean> resetPwd(@Field("username") String username,
                                      @Field("random") String random,
                                      @Field("password") String password,
                                      @Field("confirmPassword") String confirmPassword);

    /**
     * 6.0.1设置修改交易密码
     *
     * @param type       操作类型 0：设置交易密码 1:修改交易密码
     * @param verifyCode 校验标识（修改交易密码必传）
     * @param verifyType 修改方式（0:验证码校验  1：密码校验）修改交易密码必传
     */
    @POST("account/updatePayPwd")
    @FormUrlEncoded
    Observable<BaseHttpBean> updatePayPwd(@Field("username") String username,
                                          @Field("signature") String signature,
                                          @Field("password1") String password1,
                                          @Field("password2") String password2,
                                          @Field("randomCode") @Nullable String randomCode,
                                          @Field("type") int type,
                                          @Field("verifyCode") @Nullable String verifyCode,
                                          @Field("verifyType") @Nullable String verifyType);

    /**
     * 5.0.2 获取各级别签到奖励
     */
    @POST("wallet/getSignInRewardInfo")
    @FormUrlEncoded
    Observable<BaseHttpBeanWithData<SignLevel>> getSignInRewardInfo(@Field("username") String username,
                                                                    @Field("signature") String signature);

    /**
     * 5.0.3 获取签到奖励爪力信息
     */
    @POST("wallet/getUserSignInInfo")
    @FormUrlEncoded
    Observable<BaseHttpBeanWithData<UserSignInfo>> getUserSignInInfo(@Field("username") String username,
                                                                     @Field("signature") String signature,
                                                                     @Field("pageIndex") int pageIndex);

    /**
     * 2.0.5 邀请记录统计
     */
    @POST("invitedRecord/findInviteStatistics")
    @FormUrlEncoded
    Observable<BaseHttpBeanWithData<InviteStatistics>> findInviteStatistics(@Field("username") String username,
                                                                            @Field("signature") String signature);

    /**
     * 5.0.3 获取签到奖励爪力信息 type  1:邀请  2:签到 （不传获取所有）
     */
    @POST("invitedRecord/findRewardList")
    @FormUrlEncoded
    Observable<BaseHttpBeanWithData<List<RewardList>>> findRewardList(@Field("username") String username,
                                                                      @Field("signature") String signature,
                                                                      @Field("type") int type,
                                                                      @Field("pageIndex") int pageIndex,
                                                                      @Field("pageSize") int pageSize);

    /**
     * 5.0.1 用户签到
     */
    @POST("wallet/doSignIn")
    @FormUrlEncoded
    Observable<BaseHttpBeanWithData<String>> doSignIn(@Field("username") String username,
                                                      @Field("signature") String signature);


    /**
     * 1.0.28 校验验证码
     */
    @POST("account/verifyRandomCode")
    @FormUrlEncoded
    Observable<BaseHttpBeanWithData<String>> verifyRandomCode(@Field("username") String username,
                                                              @Field("signature") String signature,
                                                              @Field("randomCode") String randomCode,
                                                              @Field("reason") String reason);

    /**
     * 1.0.30 校验证件号码
     */
    @POST("account/verifyIdCard")
    @FormUrlEncoded
    Observable<BaseHttpBeanWithData<String>> verifyIdCard(@Field("username") String username,
                                                          @Field("signature") String signature,
                                                          @Field("cardCode") String cardCode,
                                                          @Field("verifyCode") String verifyCode);

    /**
     * 1.0.29 校验交易密码
     */
    @POST("account/verifyPayPwd")
    @FormUrlEncoded
    Observable<BaseHttpBeanWithData<String>> verifyPayPwd(@Field("username") String username,
                                                          @Field("signature") String signature,
                                                          @Field("oldPassword") String oldPassword,
                                                          @Field("reason") String reason);

    /**
     * 1.1.3 获取转出详情
     */
    @POST("pawsWallet/getTransferOutDetail")
    @FormUrlEncoded
    Observable<BaseHttpBeanWithData<WalletTakeOutBean>> getTransferOutDetail(@Field("username") String username,
                                                                             @Field("signature") String signature,
                                                                             @Field("pageIndex") int pageIndex,
                                                                             @Field("pageSize") int pageSize);

    /**
     * 1.1.4 转出(提币)申请
     */
    @POST("pawsWallet/transferOut")
    @FormUrlEncoded
    Observable<BaseHttpBean> transferOut(@Field("username") String username,
                                         @Field("signature") String signature,
                                         @Field("amount") String amount,
                                         @Field("payPwd") String payPwd,
                                         @Field("address") String address);

    /**
     * 1.1.5 撤销转出申请
     * <p>
     * 提示:转出申请处理状态>=3时不可撤销
     */
    @POST("pawsWallet/canceledTransferOut")
    @FormUrlEncoded
    Observable<BaseHttpBean> canceledTransferOut(@Field("username") String username,
                                                 @Field("signature") String signature,
                                                 @Field("id") long id);

    /**
     * 6.0.1 获取国际短信编码
     */
    @POST("api/countryCode")
    Observable<BaseHttpBeanWithData<List<CountryBean>>> findInternationSmsList();

    /**
     * 获取可置信列表
     */
    @POST("wallet/getTrustList")
    Observable<BaseHttpBeanWithData<List<TrustListBean>>> getTrustList();

    /**
     * 7.0.4获取汇率/兑换金额
     */
    @POST("exchange/exchangeRateList")
    @FormUrlEncoded
    Observable<BaseHttpBeanWithData<ExchangeRate>> exchangeRate(@Field("fromCode") String fromCode, @Field("toCode") String toCode);

    /**
     * 7.0.5获取非USDP币种列表
     */
    @POST("exchange/findCurrencyList")
    Observable<BaseHttpBeanWithData<List<OtherCurrency>>> findCurrencyList();


    /**
     * 7.0.6 获取用户USDP可用资产
     */
    @POST("exchange/findAssetsUSDP")
    @FormUrlEncoded
    Observable<BaseHttpBeanWithData<UsableBean>> findAssetsUsdp(@Field("username") String username,
                                                                @Field("signature") String signature);

    /**
     * 7.0.7币种兑换
     */
    @POST("exchange/exchangeCurrency")
    @FormUrlEncoded
    Observable<BaseHttpBean> exchangeCurrency(@Field("username") String username,
                                              @Field("signature") String signature,
                                              @Field("currencyId") int currencyId,
                                              @Field("amount") String amount,
                                              @Field("exchangeAmount") String exchangeAmount,
                                              @Field("exchangeRate") String exchangeRate,
                                              @Field("bankCardId") int bankCardId);

    /**
     * 1.1.5 获取充值提币交易记录
     */
    @POST("pawsWallet/getTxList")
    @FormUrlEncoded
    Observable<BaseHttpBeanWithData<List<RecentTransferRecordBean>>> getTxList(@Field("username") String username,
                                                                               @Field("signature") String signature,
                                                                               @Field("pageIndex") int pageIndex,
                                                                               @Field("pageSize") int pageSize);

    /**
     * 1.1.5 获取钱包地址
     */
    @POST("pawsWallet/getList")
    @FormUrlEncoded
    Observable<BaseHttpBeanWithData<List<TakeInBean>>> getList(@Field("username") String username,
                                                               @Field("signature") String signature);

    /**
     * 1.0.2 获取钱包充值记录
     */
    @POST("pawsWallet/getRechargeRecord")
    @FormUrlEncoded
    Observable<BaseHttpBeanWithData<List<TakeInRecordBean>>> getRechargeRecord(@Field("username") String username,
                                                                               @Field("signature") String signature,
                                                                               @Field("pageIndex") int pageIndex,
                                                                               @Field("pageSize") int pageSize);

    /**
     * 7.0.1 添加银行卡
     */
    @POST("bank/addBankCard")
    @FormUrlEncoded
    Observable<BaseHttpBean> addBankCard(@Field("username") String username,
                                         @Field("signature") String signature,
                                         @Field("currencyId") int currencyId,
                                         @Field("name") String name,
                                         @Field("country") String country,
                                         @Field("deposit_bank") String deposit_bank,
                                         @Field("branch_name") String branch_name,
                                         @Field("card_number") String card_number,
                                         @Field("card_number_confirm") String card_number_confirm);

    /**
     * 7.0.2 用户银行卡列表
     */
    @POST("bank/findBankCardList")
    @FormUrlEncoded
    Observable<BaseHttpBeanWithData<List<BankCard>>> findBankCardList(@Field("username") String username,
                                                                      @Field("signature") String signature,
                                                                      @Field("currencyId") Integer currencyId);

    /**
     * 7.0.3 用户银行卡删除
     */
    @POST("bank/deleteBankCard")
    @FormUrlEncoded
    Observable<BaseHttpBean> deleteBankCard(@Field("username") String username,
                                            @Field("signature") String signature,
                                            @Field("id") int id);

    /**
     * 7.0.8 币种兑换记录列表
     */
    @POST("exchange/exchangeList")
    @FormUrlEncoded
    Observable<BaseHttpBeanWithData<List<ExchangeRecord>>> exchangeList(@Field("username") String username,
                                                                        @Field("signature") String signature,
                                                                        @Field("pageIndex") int pageIndex,
                                                                        @Field("pageSize") int pageSize);

    /**
     * 7.0.9 兑换记录撤销
     */
    @POST("exchange/exchangeBackOut")
    @FormUrlEncoded
    Observable<BaseHttpBean> exchangeBackOut(@Field("username") String username,
                                             @Field("signature") String signature,
                                             @Field("id") int id);


    /***************************************************股票接口*********************************************************/
    /**
     * 1.0.1 获取券商信息
     */
//    @Headers({"type:zj_login"})
    @POST("api/broker/getList")
    @FormUrlEncoded
    Observable<BaseHttpBeanWithData<BaseHttpBeanWithData<List<TradeBean>>>> getTradeList(@Field("placeholder") int placeholder);

    /**
     * 1.0.2 尊嘉用户登录
     */
//    @Headers({"type:zj_login"})
    @POST("api/zunjia/login")
    @FormUrlEncoded
    Observable<BaseHttpBeanWithData<LoginTradeResponse>> stockLogin(@Field("trade_id") int trade_id,
                                                                    @Field("account") String account,
                                                                    @Field("app_version") String app_version,
                                                                    @Field("height") int height,
                                                                    @Field("language") String language,
                                                                    @Field("width") int width,
                                                                    @Field("os_version") String os_version,
                                                                    @Field("model") String model,
                                                                    @Field("platform") String platform,
                                                                    @Field("uuid") String uuid,
                                                                    @Field("version") int version,
                                                                    @Field("password") String password
    );

    /**
     * 1.0.3 提交意见反馈信息
     */
//    @Headers({"type:zj_login"})
    @POST("api/broker/addFeedBack")
    @FormUrlEncoded
    Observable<BaseHttpBean> addFeedBack(@Field("username") String username,
                                         @Field("signature") String signature,
                                         @Field("content") String content,
                                         @Field("mobile") String mobile);

    /**
     * 1.0.4 获取资讯列表
     */
//    @Headers({"type:zj_login"})
    @POST("api/broker/getNewsList")
    @FormUrlEncoded
    Observable<BaseHttpBeanWithData<BaseHttpBeanWithData<List<InformationRecordBean>>>> getNewsList(@Field("pageIndex") int pageIndex,
                                                                                                    @Field("pageSize") int pageSize,
                                                                                                    @Field("type") int type,
                                                                                                    @Field("title") String title);

    /**
     * 1.0.5 获取资讯详情
     */
//    @Headers({"type:zj_login"})
    @POST("api/broker/getNewsDetail")
    @FormUrlEncoded
    Observable<BaseHttpBeanWithData<BaseHttpBeanWithData<InformationDetailBean>>> getNewsDetail(@Field("id") int id,
                                                                                                @Field("signature") String signature,
                                                                                                @Field("username") String username);

    /**
     * 1.0.6 获取资讯收藏列表
     */
//    @Headers({"type:zj_login"})
    @POST("api/broker/getCollectList")
    @FormUrlEncoded
    Observable<BaseHttpBeanWithData<BaseHttpBeanWithData<List<InformationRecordBean>>>> getCollectList(@Field("pageIndex") int pageIndex,
                                                                                                       @Field("pageSize") int pageSize,
                                                                                                       @Field("signature") String signature,
                                                                                                       @Field("username") String username);

    /**
     * 1.0.7 新增/删除资讯收藏
     */
//    @Headers({"type:zj_login"})
    @POST("api/broker/collectionNews")
    @FormUrlEncoded
    Observable<BaseHttpBean> collectionNews(@Field("id") int id,
                                            @Field("signature") String signature,
                                            @Field("username") String username);

    /**
     * 1.0.8 点赞/取消点赞资讯
     */
//    @Headers({"type:zj_login"})
    @POST("api/broker/newsLike")
    @FormUrlEncoded
    Observable<BaseHttpBean> newsLike(@Field("id") int id,
                                      @Field("signature") String signature,
                                      @Field("username") String username);

    /**
     * 2.2 资金信息（包含持仓信息）
     */
    @Headers({"type:zj"})
    @POST("miningadapter/exchange/getAccountInfo")
    Observable<BaseStockBean<StockAccountInfo, Unit>> getAccountInfo(@Body RequestBody body);

    /**
     * 2.3 下单（包含持仓信息）
     */
    @Headers({"type:zj"})
    @POST("miningadapter/exchange/stockOrder")
    Observable<BaseStockBean<OrderResponse, Unit>> stockOrder(@Body RequestBody body);

    /**
     * 2.4 搜索股票
     */
    @Headers({"type:zj"})
    @POST("miningadapter/stockInfo/getStockListBySearch")
    Observable<BaseStockBean<List<SearchStockResponse>, Unit>> searchStock(@Body RequestBody body);

    /**
     * 2.5 当日/历史委托
     */
    @Headers({"type:zj"})
    @POST("miningadapter/exchange/getDepute")
    Observable<BaseStockBean<DeputeResponse, Unit>> getDepute(@Body RequestBody body);

    /**
     * 2.6 撤单
     */
    @Headers({"type:zj"})
    @POST("miningadapter/exchange/stockOrderCancel")
    Observable<BaseStockBean> stockOrderCancel(@Body RequestBody body);


    /**
     * 2.7 获取交易类型
     */
    @Headers({"type:zj"})
    @POST("miningadapter/exchange/getTradeType")
    Observable<BaseStockBean<TradeTypeResponse, Unit>> getTradeType(@Body RequestBody body);

    /**
     * 2.8 当日成交
     */
    @Headers({"type:zj"})
    @POST("miningadapter/exchange/getExchangeDetail")
    Observable<BaseStockBean<ExchangeResponse, Unit>> getExchangeDetail(@Body RequestBody body);

    /**
     * 2.9 获取股票基本面信息
     */
    @Headers({"type:zj"})
    @POST("miningadapter/stock/getStockBasicInfo")
    Observable<BaseStockBean<StockBasicInfo, Unit>> getStockBasicInfo(@Body RequestBody body);

    /**
     * 2.10 可申购新股列表接口
     */
    @Headers({"type:zj"})
    @POST("miningadapter/exchange/getHKNewStockList")
    Observable<BaseStockBean<NewStockResponse, Unit>> getHKNewStockList(@Body RequestBody body);


    /**
     * 2.14  获取新股申购列表（包含即将上市和已上市）
     */
    @Headers({"type:zj"})
    @POST("miningadapter/stock/getHKNewStockList")
    Observable<BaseStockBean<NewStockResponse, Unit>> getOnLineStockList(@Body RequestBody body);

    /**
     * 1.0.2 股票基本信息查询
     */
    @POST("api/security/getById")
    @FormUrlEncoded
    Observable<BaseHttpBeanWithData<StockInfo>> getById(@Query("id") int id);

    /**
     * 1.0.2 股票基本信息查询 - 返回字段获取
     */
    @POST("api/security/getById")
    @FormUrlEncoded
    Observable<BaseHttpBeanWithData<OptionalListBean>> getByIdOptional(@Field("username") String username,
                                                                       @Field("signature") String signature,
                                                                       @Query("id") Integer id,
                                                                       @Query("securityCode") String securityCode

    );

    /**
     * 1.0.5 添加/取消自选
     *
     * @param code 证券编码
     */
    @POST("api/security/addOptional")
    @FormUrlEncoded
    Observable<BaseHttpBean> addOptional(@Field("username") String username,
                                         @Field("signature") String signature,
                                         @Field("code") String code);

    /**
     * 1.0.6 自选列表
     */
    @POST("api/security/optionalList")
    @FormUrlEncoded
    Observable<BaseHttpBeanWithData<BaseHttpBeanWithData<List<OptionalListBean>>>> optionalList(@Field("username") String username,
                                                                                                @Field("signature") String signature,
                                                                                                @Field("pageIndex") int pageIndex,
                                                                                                @Field("pageSize") int pageSize);

    /**
     * 1.0.1 股票列表查询/窝轮牛熊/融资股票
     *
     * @param pageIndex    Y	int	分页页码
     * @param pageSize     Y	int	页面数据量
     * @param securityName N	String	证券编码/证券简体名称/证券繁体名称
     * @param type         N	int	1-窝轮牛熊 2-融资股票 3-主板 4-创业板
     * @param sort         N	1-涨跌幅降序 2-涨跌幅升序 3-实时价格降序 4-实时价格升序 5-涨跌额降序
     */
    @POST("api/security/list")
    @FormUrlEncoded
    Observable<BaseHttpBeanWithData<BaseHttpBeanWithData<List<OptionalListBean>>>> getSecurityList(@Field("securityName") String securityName,
                                                                                                   @Field("type") Integer type,
                                                                                                   @Field("pageIndex") int pageIndex,
                                                                                                   @Field("pageSize") int pageSize,
                                                                                                   @Field("sort") Integer sort);

    /**
     * 1.0.1 股票列表查询/窝轮牛熊/融资股票
     *
     * @param pageIndex Y	int	分页页码
     * @param pageSize  Y	int	页面数据量
     * @param type      N	1-涨幅 2-跌幅
     * @param sort      N	1-涨跌幅降序 2-涨跌幅升序 3-实时价格降序 4-实时价格升序 5-涨跌额降序
     */
    @POST("api/security/getPriceLimit")
    @FormUrlEncoded
    Observable<BaseHttpBeanWithData<BaseHttpBeanWithData<List<OptionalListBean>>>> getPriceLimit(@Field("type") Integer type,
                                                                                                 @Field("pageIndex") int pageIndex,
                                                                                                 @Field("pageSize") int pageSize,
                                                                                                 @Field("sort") Integer sort);

    /**
     * 1.0.13 热门行业股票列表
     *
     * @param sortDir  A 表示升序, 即ascend  D 表示降序, 即descend
     * @param induCode 分页时用于定位上下文(第一次可不传)，下一页传上一页列表最后一条记录的induCode
     * @param count    Y	int	页面数据量
     */
    @POST("api/security/industryStockList")
    @FormUrlEncoded
    Observable<BaseHttpBeanWithData<List<IndustryListBean>>> industryStockList(@Field("sortDir") String sortDir,
                                                                               @Field("induCode") String induCode,
                                                                               @Field("count") int count);


    /**
     * 1.0.14 行业成分股列表
     *
     * @param induCode  分页时用于定位上下文(第一次可不传)，下一页传上一页列表最后一条记录的induCode
     * @param count     Y	 int	页面数据量
     * @param assetId   首次请求传空，下一页请求传上次最后一条的assetId
     * @param sortField 0-按现价排 1-按涨跌幅排 2-按市值排 3-按市盈率排 4-按市净率排 （默认为1）
     * @param sortDir   A 表示升序, 即ascend  D 表示降序, 即descend
     */
    @POST("api/security/industryComponentsList")
    @FormUrlEncoded
    Observable<BaseHttpBeanWithData<List<IndustryPartBean>>> industryComponentsList(@Field("induCode") String induCode,
                                                                                    @Field("count") int count,
                                                                                    @Field("assetId") String assetId,
                                                                                    @Field("sortField") Integer sortField,
                                                                                    @Field("sortDir") String sortDir);

    /**
     * 1.0.4 交易规则
     */
    @POST("api/security/rulesList")
    Observable<BaseHttpBeanWithData<List<StockRulesBean>>> rulesList();

    /**
     * 1.0.7 股票买卖盘列表
     *
     * @param securityCode 证券编码
     */
    @POST("api/security/orderList")
    @FormUrlEncoded
    Observable<BaseHttpBeanWithData<WSBuySellDeepBean>> buySellOrderList(@Field("securityCode") String securityCode);

    /**
     * 1.0.16 获取股票成交明细列表
     *
     * @param securityCode 证券编码
     */
    @POST("api/security/transactionDetailList")
    @FormUrlEncoded
    Observable<BaseHttpBeanWithData<DealDetailBean>> transactionDetailList(@Field("securityCode") String securityCode,
                                                                           @Field("date") String date,
                                                                           @Field("pageIndex") int pageIndex,
                                                                           @Field("pageSize") int pageSize);

    /**
     * 1.0.8 股票分时
     *
     * @param securityCode 证券编码
     */
    @POST("api/security/timeShareList")
    @FormUrlEncoded
    Observable<BaseHttpBeanWithData<StockMinHour>> stockTimeShareList(@Field("securityCode") String securityCode);

    /**
     * 1.0.9 股票日K/周K/月K
     *
     * @param dateType 时间类型 1-日K 2-周K 3-月K
     */
    @POST("api/security/klineList")
    @FormUrlEncoded
    Observable<BaseHttpBeanWithData<List<StockKLineBean>>> klineList(@Field("securityCode") String securityCode,
                                                                     @Field("dateType") int dateType);

    /**
     * 1.0.10 指数列表（恒生/国企/港通）
     */
    @POST("api/index/indexDataList")
    Observable<BaseHttpBeanWithData<List<WSExponentChangeBean>>> indexDataList();

    /**
     * 1.0.11 指数分时
     *
     * @param type 1-HSI 2-HSCEI 3-港通精选100指数
     */
    @POST("api/index/timeShareList")
    @FormUrlEncoded
    Observable<BaseHttpBeanWithData<ExponentMinHour>> exponentTimeShareList(@Field("type") int type);

    /**
     * 1.0.12 指数日K/周K/月K
     *
     * @param type     1-HSI 2-HSCEI 3-港通精选100指数
     * @param dateType 时间类型 1-日K 2-周K 3-月K
     */
    @POST("api/index/klineList")
    @FormUrlEncoded
    Observable<BaseHttpBeanWithData<List<ExponentKLineBean>>> exponentKlineList(@Field("type") int type,
                                                                                @Field("dateType") int dateType);

    /**
     * 1.0.14 获取市场状态
     */
    @POST("api/market/marketStatus")
    Observable<BaseHttpBeanWithData<List<WSMarketStatus>>> marketStatus();

    /**
     * 2.2.3 微信登录
     */
    @POST("api/member/Tripartite/weChatLogin")
    @FormUrlEncoded
    Observable<WXLoginBean> weChatLogin(@Field("code") String code);

    /**
     * 2.2.4 绑定微信账号
     */
    @POST("api/member/Tripartite/weChatBind")
    @FormUrlEncoded
    Observable<WXLoginBean> weChatBind(@Field("code") String code,
                                       @Field("mobile") String mobile,
                                       @Field("random") String random);

    /**
     * 3.0.2 主要指标-市盈率列表（图表数据）
     * type  1 报告期 2 年度
     */
    @POST("api/f10/corpSumm")
    @FormUrlEncoded
    Observable<BaseHttpBeanWithData<CompanyProfile>> corpSumm(@Field("securityCode") String securityCode);

    /**
     * 3.0.2 主要指标-市盈率列表（图表数据）
     * type  1 报告期 2 年度
     */
    @POST("api/f10/peList")
    @FormUrlEncoded
    Observable<BaseHttpBeanWithData<List<PEBean>>> peList(@Field("securityCode") String securityCode,
                                                          @Field("type") int type);

    /**
     * 3.0.3 主要指标-市净率最新数据
     */
    @POST("api/f10/pe")
    @FormUrlEncoded
    Observable<BaseHttpBeanWithData<PBBean>> pe(@Field("securityCode") String securityCode);

    /**
     * 3.0.4 利润表
     */
    @POST("api/f10/plInfo")
    @FormUrlEncoded
    Observable<BaseHttpBeanWithData<ProfitBean>> plInfo(@Field("securityCode") String securityCode);

    /**
     * 3.0.5 资产负债表
     */
    @POST("api/f10/balanceSheet")
    @FormUrlEncoded
    Observable<BaseHttpBeanWithData<SheetBean>> balanceSheet(@Field("securityCode") String securityCode);

    /**
     * 3.0.6 现金流量表最新数据
     */
    @POST("api/f10/cashFlow")
    @FormUrlEncoded
    Observable<BaseHttpBeanWithData<CashFlowBean>> cashFlow(@Field("securityCode") String securityCode);

    /**
     * 3.0.7 现金流量列表（图表数据）
     */
    @POST("api/f10/cashFlowList")
    @FormUrlEncoded
    Observable<BaseHttpBeanWithData<List<CashFlowBean>>> cashFlowList(@Field("securityCode") String securityCode,
                                                                      @Field("type") int type);

    /**
     * 3.0.8 财务概要（同主要指标其他信息）
     */
    @POST("api/f10/financeSumm")
    @FormUrlEncoded
    Observable<BaseHttpBeanWithData<FinanceSummaryBean>> financeSumm(@Field("securityCode") String securityCode);

    /**
     * 3.0.9 分红送转(派息记录)
     */
    @POST("api/f10/dh")
    @FormUrlEncoded
    Observable<BaseHttpBeanWithData<FHBean>> dh(@Field("securityCode") String securityCode);

    /**
     * 3.1.0 回购
     */
    @POST("api/f10/sb")
    @FormUrlEncoded
    Observable<BaseHttpBeanWithData<SBBean>> sb(@Field("securityCode") String securityCode,
                                                @Field("pageIndex") Integer pageIndex,
                                                @Field("pageSize") Integer pageSize);

    /**
     * 3.1.1 股本信息
     */
    @POST("api/f10/equityInfo")
    @FormUrlEncoded
    Observable<BaseHttpBeanWithData<EquityBean>> equityInfo(@Field("securityCode") String securityCode);

    /**
     * 3.1.2 股东明细
     */
    @POST("api/f10/shareholderList")
    @FormUrlEncoded
    Observable<BaseHttpBeanWithData<List<HoldBean>>> shareholderList(@Field("securityCode") String securityCode);

    /**
     * 4.0.13 获取开户信息
     */
    @POST("api/account/getByUserIdAccount")
    @FormUrlEncoded
    Observable<BaseHttpBeanWithData<EstablishStatusBean>> getByUserIdAccount(@Field("username") String username,
                                                                             @Field("signature") String signature);

    /**
     * 4.0.1 身份证明
     */
    @POST("api/account/saveIdcard")
    @FormUrlEncoded
    Observable<BaseHttpBeanWithData<UploadIdCardBean>> saveIdcard(@Field("username") String username,
                                                                  @Field("signature") String signature,
                                                                  @Field("idcardPositive") String idcardPositive,
                                                                  @Field("idcardContrary") String idcardContrary);

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
    @POST("api/account/saveIdentity")
    @FormUrlEncoded
    Observable<BaseHttpBean> saveIdentity(@Field("username") String username,
                                          @Field("signature") String signature,
                                          @Field("name") String name,
                                          @Field("lastName") String lastName,
                                          @Field("firstName") String firstName,
                                          @Field("sex") int sex,
                                          @Field("birth") String birth,
                                          @Field("address") String address,
                                          @Field("idcard") String idcard);

    /**
     * 4.0.3 填写个人资料
     *
     * @param contactAddress   Y	String	通讯地址
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
    @POST("api/account/savePersonalData")
    @FormUrlEncoded
    Observable<BaseHttpBean> savePersonalData(@Field("username") String username,
                                              @Field("signature") String signature,
                                              @Field("contactAddress") String contactAddress,
                                              @Field("email") String email,
                                              @Field("proofOfAddress") String proofOfAddress,
                                              @Field("educationType") int educationType,
                                              @Field("jobType") int jobType,
                                              @Field("industryCategory") int industryCategory,
                                              @Field("companyName") String companyName,
                                              @Field("dutyType") int dutyType,
                                              @Field("taxState") String taxState,
                                              @Field("taxNumber") String taxNumber);


    /**
     * 4.0.4 银行卡验证
     *
     * @param name     Y	String	姓名
     * @param idcard   Y	String	身份证号
     * @param bankName Y	String	银行名称
     * @param cardNo   Y	String	银行卡号
     */
    @POST("api/account/saveBankCard")
    @FormUrlEncoded
    Observable<BaseHttpBean> saveBankCard(@Field("username") String username,
                                          @Field("signature") String signature,
                                          @Field("name") String name,
                                          @Field("idcard") String idcard,
                                          @Field("bankName") String bankName,
                                          @Field("cardNo") String cardNo);

    /**
     * 4.0.5 个人声明
     *
     * @param personalStatement Y	String	个人声明 格式：1,2,3
     */
    @POST("api/account/savePersonalState")
    @FormUrlEncoded
    Observable<BaseHttpBean> savePersonalState(@Field("username") String username,
                                               @Field("signature") String signature,
                                               @Field("personalStatement") String personalStatement);

    /**
     * 4.0.6 投资经验
     *
     * @param securitiesInvestmentExType Y	Int	证券投资经验类型 1：0-1年  2：1-3年  3：3-5年  4：5年以上
     * @param fundInvestmentExType       Y	Int	基金投资经验类型 1：0-1年  2：1-3年  3：3-5年  4：5年以上
     * @param foreignInvestmentExType    Y	Int	外汇投资经验类型 1：0-1年  2：1-3年  3：3-5年  4：5年以上
     * @param fixedInvestmentExType      Y	Int	固定收益产品投资经验类型 1：0-1年  2：1-3年  3：3-5年  4：5年以上
     * @param otherInvestmentExType      Y	Int	其他投资经验类型 1：0-1年  2：1-3年  3：3-5年  4：5年以上
     */
    @POST("api/account/saveInvestmentExperience")
    @FormUrlEncoded
    Observable<BaseHttpBean> saveInvestmentExperience(@Field("username") String username,
                                                      @Field("signature") String signature,
                                                      @Field("securitiesInvestmentExType") int securitiesInvestmentExType,
                                                      @Field("fundInvestmentExType") int fundInvestmentExType,
                                                      @Field("foreignInvestmentExType") int foreignInvestmentExType,
                                                      @Field("fixedInvestmentExType") int fixedInvestmentExType,
                                                      @Field("otherInvestmentExType") int otherInvestmentExType);

    /**
     * 4.0.7 财务状况
     *
     * @param householdIncomeType     Y	Int	家庭年收入 1：<20w  2：20-50w 3：50-100w  4：>100w
     * @param householdNetWorthType   Y	Int	家庭净资产 1：<50w  2：50-200w 3：200-1000w  4：>1000w
     * @param riskToleranceType       Y	Int	风险承受能力 1：高(30%以上損失)  2：中(20%-30%損失) 3：低(10%-20%損失) 4：很低(10%以下損失)
     * @param investmentObjectiveType Y	Int	投资目标 1：資本增值 2：股息回报 3：投机 4：对冲 5：其他
     */
    @POST("api/account/saveFinancialSituation")
    @FormUrlEncoded
    Observable<BaseHttpBean> saveFinancialSituation(@Field("username") String username,
                                                    @Field("signature") String signature,
                                                    @Field("householdIncomeType") int householdIncomeType,
                                                    @Field("householdNetWorthType") int householdNetWorthType,
                                                    @Field("riskToleranceType") int riskToleranceType,
                                                    @Field("investmentObjectiveType") int investmentObjectiveType);

    /**
     * 4.0.8 选择账户
     *
     * @param openAccountType                 Y	String	选择开通的账户类型 格式 1,2  1-融资账户 2-现金账户
     * @param openMarketType                  Y	String	选择开通的市场类型 格式1,2 1：港股（必选）2：中华通 3：美股
     * @param ifPromotionOfDerivatives        Y	Int	是否推行衍生品投资 0-是 1-否
     * @param derivativesInvestmentExperience Y	String	衍生品投资经历 格式：1,2  1：我曾受過有關衍生品的培訓課程 2：具有與衍生品相關的工作經驗 3：在過去三年，至少產生過5次有關衍生品交易
     */
    @POST("api/account/saveSelectAccount")
    @FormUrlEncoded
    Observable<BaseHttpBean> saveSelectAccount(@Field("username") String username,
                                               @Field("signature") String signature,
                                               @Field("openAccountType") int openAccountType,
                                               @Field("openMarketType") String openMarketType,
                                               @Field("ifPromotionOfDerivatives") int ifPromotionOfDerivatives,
                                               @Field("derivativesInvestmentExperience") String derivativesInvestmentExperience);

    /**
     * 4.0.9 密码设置
     *
     * @param password        Y	String	密码(md5加密)
     * @param confirmPassword Y	String	确认密码(md5加密)
     */
    @POST("api/account/savePassword")
    @FormUrlEncoded
    Observable<BaseHttpBean> savePassword(@Field("username") String username,
                                          @Field("signature") String signature,
                                          @Field("password") String password,
                                          @Field("confirmPassword") String confirmPassword);

    /**
     * 4.0.10 上传手持证件照
     *
     * @param idcardHord Y	String	手持证件照
     */
    @POST("api/account/saveHoldIdcard")
    @FormUrlEncoded
    Observable<BaseHttpBean> saveHoldIdcard(@Field("username") String username,
                                            @Field("signature") String signature,
                                            @Field("idcardHord") String idcardHord);

    /**
     * 4.0.11 身份检测（保存人脸识别照）
     *
     * @param faceVerification Y	String	人脸识别照片
     */
    @POST("api/account/savefaceVerification")
    @FormUrlEncoded
    Observable<BaseHttpBean> savefaceVerification(@Field("username") String username,
                                                  @Field("signature") String signature,
                                                  @Field("faceVerification") String faceVerification);

    /**
     * 4.0.12 确认签名照
     *
     * @param signaturePic Y	String	人脸识别照片
     */
    @POST("api/account/saveSignature")
    @FormUrlEncoded
    Observable<BaseHttpBean> saveSignature(@Field("username") String username,
                                           @Field("signature") String signature,
                                           @Field("signaturePic") String signaturePic);

    /**
     * 1.0.18 据正股获取涡轮牛熊股票列表
     *
     * @param signaturePic Y	String	人脸识别照片
     */
    @POST("api/security/underlyingStockList")
    @FormUrlEncoded
    Observable<BaseHttpBeanWithData<BaseHttpBeanWithData<List<OptionalListBean>>>> underlyingStockList(@Field("pageIndex") int pageIndex,
                                                                                                       @Field("pageSize") int pageSize,
                                                                                                       @Field("securityCode") String securityCode,
                                                                                                       @Field("type") int type,
                                                                                                       @Field("productType") int productType,
                                                                                                       @Field("sort") Integer sort,
                                                                                                       @Field("username") String username);

}
