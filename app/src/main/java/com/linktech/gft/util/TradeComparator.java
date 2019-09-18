package com.linktech.gft.util;

import com.linktech.gft.bean.CountryBean;
import com.linktech.gft.bean.TradeBean;

import org.jetbrains.annotations.NotNull;

import java.util.Comparator;

/**
 * 拼音排序
 *
 * @author
 */
public class TradeComparator implements Comparator<TradeBean> {
    public static TradeComparator instance = new TradeComparator();

    public static TradeComparator getInstance() {
        return instance;
    }

    @Override
    public int compare(@NotNull TradeBean o1, @NotNull TradeBean o2) {
        return o1.getLetters().compareTo(o2.getLetters());
    }
}
