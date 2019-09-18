package com.linktech.gft.util;

import com.linktech.gft.bean.CountryBean;
import org.jetbrains.annotations.NotNull;
import java.util.Comparator;

/**
 * 拼音排序
 *
 * @author
 */
public class KeyComparator implements Comparator<CountryBean> {
    public static KeyComparator instance = new KeyComparator();

    public static KeyComparator getInstance() {
        return instance;
    }

    @Override
    public int compare(@NotNull CountryBean o1, @NotNull CountryBean o2) {
        return o1.getLetters().compareTo(o2.getLetters());
    }
}
