package com.linktech.gft.interfaces;

import android.text.Editable;

import com.linktech.gft.view.WatchInputEditText;

import io.reactivex.annotations.NonNull;

/**
 * Created on 2018/12/7  17:12
 * function : 文本变化监听
 *
 * @author mnlin
 */
public interface EditTextChangedListener {
    /**
     * 当文本发生变化
     */
    void onExistEditTextChanged(@NonNull WatchInputEditText editText, @NonNull Editable s);
}
