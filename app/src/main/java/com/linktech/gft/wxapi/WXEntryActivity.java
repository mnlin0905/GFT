package com.linktech.gft.wxapi;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;

import com.alibaba.android.arouter.launcher.ARouter;
import com.linktech.gft.R;
import com.linktech.gft.base.BaseActivity;
import com.linktech.gft.base.BaseApplication;
import com.linktech.gft.base.BasePresenter;
import com.linktech.gft.bean.LoginBean;
import com.linktech.gft.bean.WXLoginBean;
import com.linktech.gft.plugins.ARouterConst;
import com.linktech.gft.plugins.InjectLayoutRes;
import com.linktech.gft.plugins.PlusFunsPluginsKt;
import com.linktech.gft.util.Const;
import com.linktech.gft.util.DefaultPreferenceUtil;
import com.orhanobut.logger.Logger;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;

import java.util.Objects;

/**
 * Created on 2019/7/5  14:40
 * function : 微信回调类
 *
 * @author mnlin
 */
@InjectLayoutRes(layoutResId = R.layout.activity_wx_entry)
public class WXEntryActivity extends BaseActivity<BasePresenter<WXEntryActivity>> implements IWXAPIEventHandler {
    /**
     * 登录
     * 分享
     */
    private static final int RETURN_MSG_TYPE_LOGIN = 1;
    private static final int RETURN_MSG_TYPE_SHARE = 2;

    @Override
    protected void initData(@Nullable Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        getWindow().setGravity(Gravity.BOTTOM);
        Objects.requireNonNull(BaseApplication.app.mWxApi).handleIntent(getIntent(), this);
    }

    @Override
    public void onReq(BaseReq baseReq) {

    }

    @Override
    public void onResp(BaseResp baseResp) {
        Logger.d("错误码 : " + baseResp.errCode);
        switch (baseResp.errCode) {
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                PlusFunsPluginsKt.toast(null, "授权失败");
                finish();
                break;
            case BaseResp.ErrCode.ERR_OK:
                switch (baseResp.getType()) {
                    case RETURN_MSG_TYPE_LOGIN:
                        //拿到了微信返回的code,立马再去请求access_token
                        String code = ((SendAuth.Resp) baseResp).code;

                        // 微信登录
                        mPresenter.weChatLogin(code, tag -> {
                            if (tag == null) {
                                showToast(R.string.common_unknown_error + ";result == null");
                            } else if (tag.stateCode == 0) {
                                // 成功
                                loginFinish(tag.getData());
                            } else if (tag.stateCode == 1026) {
                                ARouter.getInstance()
                                        .build(ARouterConst.Activity_BindWXRegisterActivity)
                                        .withString(Const.KEY_COMMON_VALUE_ONE, code)
                                        .navigation();
                                finish();
                            } else {
                                showToast(R.string.common_unknown_error + ";stateCode == " + tag.stateCode);
                            }
                        }, err -> finish());

                        break;

                    case RETURN_MSG_TYPE_SHARE:
                        PlusFunsPluginsKt.toast(null, "微信分享成功");
                        finish();
                        break;
                }
                break;
        }
    }

    /**
     * 登录成功
     */
    private void loginFinish(LoginBean it) {
        //设置登录状态
        DefaultPreferenceUtil.getInstance().setSignature(it.getSignature());
        DefaultPreferenceUtil.getInstance().setUsername(it.getUsername());
        DefaultPreferenceUtil.getInstance().setLogin(true);

        //登录成功,保存帐号信息
        mPresenter.getMemberInfo(get_signature(), get_username(), tag -> {
            if (tag != null) {
                DefaultPreferenceUtil.getInstance().saveUserInfoBean(tag, cb -> finish());
            }
        }, err -> finish());
    }
}
