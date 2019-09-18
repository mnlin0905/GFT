package com.linktech.gft.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.linktech.gft.R;
import com.linktech.gft.base.BaseActivity;
import com.linktech.gft.interfaces.EditTextChangedListener;

import java.lang.ref.WeakReference;
import java.util.List;

import skin.support.widget.SkinCompatEditText;

/**
 * Created on 2018/12/7  17:08
 * function : 监听是否有文字来触发 事件
 *
 * @author mnlin
 */
public class WatchInputEditText extends SkinCompatEditText implements TextWatcher {
    /**
     * 是否绑定空值监听器
     */
    public boolean nullMonitorEnable;
    /**
     * 唯一监听器
     */
    private WeakReference<EditTextChangedListener> singleTextNullWatcher;

    private String a, b, c;

    public WatchInputEditText(Context context) {
        this(context, null);
    }

    public WatchInputEditText(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.editTextStyle);
    }

    public WatchInputEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        //检测是否需要监听器
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.WatchInputEditText);
        nullMonitorEnable = ta.getBoolean(R.styleable.WatchInputEditText_WatchInputEditText_monitoring, true);
        ta.recycle();

        //是否设置监听器
        if (nullMonitorEnable) {
            //添加watcher
            addTextChangedListener(this);

            //开始前主动触发一次
            post(() -> {
                if (singleTextNullWatcher != null && singleTextNullWatcher.get() != null) {
                    singleTextNullWatcher.get().onExistEditTextChanged(this, getText());
                }
            });
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        //移除
        if (nullMonitorEnable) {
            removeTextChangedListener(this);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (!isInEditMode()) {
            if (nullMonitorEnable && singleTextNullWatcher == null) {
                // 如果 context 满足,则设置为 context
                List<? extends Fragment> fragments = ((BaseActivity) getContext()).getSupportFragmentManager().getFragments();

                //指定应该谁做listener
                Object target = getContext() instanceof EditTextChangedListener ? getContext() : null;

                //查找是否应该设置为fragment
                for (Fragment fragment : fragments) {
                    if (isParentChild(fragment.getView(), this) && fragment instanceof EditTextChangedListener) {
                        target = fragment;
                        break;
                    }
                }

                //如果目标是EditTextChangedListener子类,则设置为监听器
                if (target != null) {
                    singleTextNullWatcher = new WeakReference<>((EditTextChangedListener) target);
                }
            }
        }

    }

    /**
     * 判断两个view是否构成子父节点关系
     */
    private boolean isParentChild(View parent, View child) {
        //如果是同一个对象,也算是有父子关系
        if (parent == child) {
            return true;
        } else if (parent instanceof ViewGroup) {
            //父节点
            ViewParent child_parent = child.getParent();
            while (true) {
                //找到了父类为parent
                if (child_parent == parent) {
                    return true;
                }

                //找不到目标父类
                if (child_parent == null) {
                    return false;
                }

                //重置
                child_parent = child_parent.getParent();
            }
        } else {
            return false;
        }
    }

    /**
     * 设置监听事件
     */
    public void setEditTextChangedListener(EditTextChangedListener singleTextNullWatcher) {
        this.singleTextNullWatcher = new WeakReference<>(singleTextNullWatcher);
    }

    /**
     * This method is called to notify you that, within <code>s</code>,
     * the <code>count</code> characters beginning at <code>start</code>
     * are about to be replaced by new text with length <code>after</code>.
     * It is an error to attempt to make changes to <code>s</code> from
     * this callback.
     *
     * @param s
     * @param start
     * @param count
     * @param after
     */
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    /**
     * This method is called to notify you that, within <code>s</code>,
     * the <code>count</code> characters beginning at <code>start</code>
     * have just replaced old text that had length <code>before</code>.
     * It is an error to attempt to make changes to <code>s</code> from
     * this callback.
     *
     * @param s
     * @param start
     * @param before
     * @param count
     */
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    /**
     * This method is called to notify you that, somewhere within
     * <code>s</code>, the text has been changed.
     * It is legitimate to make further changes to <code>s</code> from
     * this callback, but be careful not to get yourself into an infinite
     * loop, because any changes you make will cause this method to be
     * called again recursively.
     * (You are not told where the change took place because other
     * afterTextChanged() methods may already have made other changes
     * and invalidated the offsets.  But if you need to know here,
     * you can use {@link android.text.Spannable} in {@link #onTextChanged}
     * to mark your place and then look up from here where the span
     * ended up.
     *
     * @param s
     */
    @Override
    public void afterTextChanged(Editable s) {
        if (nullMonitorEnable && singleTextNullWatcher != null && singleTextNullWatcher.get() != null) {
            post(() -> singleTextNullWatcher.get().onExistEditTextChanged(this, s));
        }
    }
}
