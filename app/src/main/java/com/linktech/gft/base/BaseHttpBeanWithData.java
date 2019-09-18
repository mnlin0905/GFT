package com.linktech.gft.base;

import android.support.annotation.Nullable;

import com.linktech.gft.R;
import com.linktech.gft.plugins.PlusFunsPluginsKt;

/**
 * Created on 2018/5/31  11:11
 * function : 带有data的字段
 *
 * @author mnlin
 */
public final class BaseHttpBeanWithData<DATA> extends BaseHttpBean {
    /**
     * 可能存储着数据的字段
     */
    private DATA data;
    private DATA result;
    private DATA questions;
    private DATA imgUrls;
    private DATA inviterVos;
    private DATA news;
    private DATA list;
    private DATA collectList;
    private DATA traderInfoList;

    @Nullable
    public DATA getData() {
        Object[] all = new Object[]{data, result, questions, imgUrls, inviterVos, news, list, collectList, traderInfoList};
        for (int i = 0; i < all.length; i++) {
            if (all[i] != null) {
                return (DATA) all[i];
            }
        }

        return null;
    }

    /**
     * 抛异常
     */
    public DATA getDataOrException() {
        if (data != null) {
            return data;
        }
        if (questions != null) {
            return questions;
        }
        if (imgUrls != null) {
            return imgUrls;
        }
        if (inviterVos != null) {
            return inviterVos;
        }
        throw new RuntimeException(PlusFunsPluginsKt.getString(null, R.string.base_total_null_pointer_exception));
    }

    /**
     * 夹层 对象,用于获取最内部的值
     */
    public static final class HttpBeanInterLayer<INNER> {
        private INNER addressVos;

        public INNER getInnerData() {
            if (addressVos != null) {
                return addressVos;
            }

            throw new RuntimeException(PlusFunsPluginsKt.getString(null, R.string.common_no_inner_data));
        }
    }
}
