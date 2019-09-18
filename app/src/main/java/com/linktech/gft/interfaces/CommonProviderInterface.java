package com.linktech.gft.interfaces;


import com.linktech.gft.base.BasePresenter;
import com.linktech.gft.base.BaseView;
import com.linktech.gft.retrofit.HttpInterface;

/**
 * Created on 2018/5/30  16:23
 * function : 提供内容
 *
 * @author mnlin
 */
public interface CommonProviderInterface {
    /**
     * @return 返回网络请求接口
     */
    HttpInterface getHttpInterface();

    /**
     * @return 返回v层对象
     */
    BaseView getBaseView();

    /**
     * @return 返回p层对象
     */
    <E extends BaseView> BasePresenter<E> getBasePresenter();
}
