package com.linktech.gft.activity.other;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spannable;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.blankj.utilcode.util.BarUtils;
import com.linktech.gft.R;
import com.linktech.gft.base.BaseActivity;
import com.linktech.gft.interfaces.OnFilterResultListener;
import com.linktech.gft.plugins.APTPlugins;
import com.linktech.gft.plugins.ARouterConst;
import com.linktech.gft.plugins.DisableAPTProcess;
import com.linktech.gft.plugins.InjectActivityTitle;
import com.linktech.gft.plugins.InjectLayoutRes;
import com.linktech.gft.plugins.InjectMenuRes;
import com.linktech.gft.plugins.PlusFunsPluginsKt;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

import static com.linktech.gft.util.Const.KEY_FILTER_INPUT_KEYS;
import static com.linktech.gft.util.Const.KEY_FILTER_SOURCE;
import static com.linktech.gft.util.Const.KEY_POSITION;
import static com.linktech.gft.util.Const.VALUE_POSITION_NULL;

/**
 * 搜索界面
 * <p>
 * 需传入一个列表,用于信息过滤
 * <p>
 * 在过滤之后通过setResult方式传回两种值:
 * 如果选中了某个item:则返回一个int类型的position
 * 如果非选中,而是输入关键字进行搜索,则返回string类型的字符串
 */
@Route(path = ARouterConst.Activity_SearchFilterActivity)
@DisableAPTProcess(disables = APTPlugins.DAGGER)
@InjectLayoutRes(layoutResId = R.layout.activity_search_filter)
@InjectMenuRes(menuResId = R.menu.menu_text_search)
@InjectActivityTitle(titleRes = R.string.label_search_filter)
public class SearchFilterActivity extends BaseActivity implements InputFilter, TextWatcher, AdapterView.OnItemClickListener {
    @BindView(R.id.et_filter)
    EditText mEtFilter;
    @BindView(R.id.lv_record)
    ListView mLvRecord;

    /**
     * 非必须字段,可以为null,表示使用关键字进行搜索过滤
     */
    @Autowired(name = KEY_FILTER_SOURCE, required = false)
    List<String> source;

    /**
     * 备用数据源
     * 适配器
     */
    List<String> baseSource;
    ArrayAdapter<String> arrayAdapter;

    /**
     * 处理搜索详情信息
     *
     * @param data        数据
     * @param requestDeal 什么情况下才会处理数据
     * @param listener    监听器
     */
    public static void dealSearchResult(Intent data, boolean requestDeal, OnFilterResultListener listener) {
        if (requestDeal && listener != null) {
            int position = data.getIntExtra(KEY_POSITION, VALUE_POSITION_NULL);
            String filter = data.getStringExtra(KEY_FILTER_INPUT_KEYS);
            if (position != VALUE_POSITION_NULL) {
                listener.onFilterSelectItem(position);
            }
            if (filter != null) {
                listener.onFilterInputKeys(filter);
            }
        }
    }

    /**
     * 模糊匹配的方式
     */
    public static boolean matcherFilter(@Nullable String resource, @NotNull String filter) {
        String regex = filter.replaceAll("\\S", " $0 ").replaceAll("\\s", ".*?");
        return resource != null && resource.toUpperCase().matches(regex.toUpperCase());
    }

    /**
     * 初始化数据
     *
     * @param savedInstanceState 已存储对象
     */
    @Override
    protected void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);

        //toolbar
        toolbar.post(() -> {
            ViewGroup.LayoutParams layoutParams = toolbar.getLayoutParams();
            int statusBarHeight = BarUtils.getStatusBarHeight();
            layoutParams.height = toolbar.getHeight() + statusBarHeight;
            toolbar.setPadding(toolbar.getPaddingStart(), toolbar.getPaddingTop() + statusBarHeight, toolbar.getPaddingRight(), toolbar.getPaddingBottom());
        });

        //设置背景图
        mEtFilter.post(() -> mEtFilter.setBackground(PlusFunsPluginsKt.customShapeDrawable(null, mEtFilter.getHeight(), getResources().getColor(R.color.white_background_ccf4f4f5))));

        //加载布局文件
        if (source != null) {
            baseSource = new ArrayList<>();
            baseSource.addAll(source);
            arrayAdapter = new ArrayAdapter<String>(this, R.layout.layout_simple_list_item_1, baseSource);
            mLvRecord.setAdapter(arrayAdapter);
            mLvRecord.setOnItemClickListener(this);

            //监听文本输入
            mEtFilter.setFilters(new InputFilter[]{this});
            mEtFilter.addTextChangedListener(this);
        }
    }

    /**
     * 刷新备用数据源中数据
     *
     * @param filter 过滤文本
     */
    private void refreshBaseSource(String filter) {
        //无数据源,则不进行刷新处理
        if (source == null) {
            return;
        }

        //清除旧数据
        baseSource.clear();

        //如果无过滤文本,则默认显示全部
        if (TextUtils.isEmpty(filter)) {
            baseSource.addAll(source);
            arrayAdapter.notifyDataSetChanged();
            return;
        }

        //否则进行模糊匹配
        String regex = filter.replaceAll("\\S", " $0 ").replaceAll("\\s", ".*?");
        for (String o : source) {
            if (o != null && o.toUpperCase().matches(regex.toUpperCase())) {
                baseSource.add(o);
            }
        }
        arrayAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent();
        intent.putExtra(KEY_FILTER_INPUT_KEYS, mEtFilter.getText().toString());
        setResult(RESULT_OK, intent);
        finish();
        return true;
    }

    /**
     * This method is called when the buffer is going to replace the
     * range <code>dstart &hellip; dend</code> of <code>dest</code>
     * with the new text from the range <code>start &hellip; end</code>
     * of <code>source</code>.  Return the CharSequence that you would
     * like to have placed there instead, including an empty string
     * if appropriate, or <code>null</code> to accept the original
     * replacement.  Be careful to not to reject 0-length replacements,
     * as this is what happens when you delete text.  Also beware that
     * you should not attempt to make any changes to <code>dest</code>
     * from this method; you may only examine it for context.
     * <p>
     * Note: If <var>source</var> is an instance of {@link Spanned} or
     * {@link Spannable}, the span objects in the <var>source</var> should be
     * copied into the filtered result (i.e. the non-null return value).
     * {@link TextUtils#copySpansFrom} can be used for convenience.
     *
     * @param source
     * @param start
     * @param end
     * @param dest
     * @param dstart
     * @param dend
     */
    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        return source.toString().matches(".*\\s.*") ? null : source;
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
     * you can use {@link Spannable#setSpan} in {@link #onTextChanged}
     * to mark your place and then look up from here where the span
     * ended up.
     *
     * @param s
     */
    @Override
    public void afterTextChanged(Editable s) {
        refreshBaseSource(s.toString());
    }

    /**
     * Callback method to be invoked when an item in this AdapterView has
     * been clicked.
     * <p>
     * Implementers can call getItemAtPosition(position) if they need
     * to access the data associated with the selected item.
     *
     * @param parent   The AdapterView where the click happened.
     * @param view     The view within the AdapterView that was clicked (this
     *                 will be a view provided by the adapter)
     * @param position The position of the view in the adapter.
     * @param id       The row id of the item that was clicked.
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent();
        intent.putExtra(KEY_POSITION, source.indexOf(baseSource.get(position)));
        setResult(RESULT_OK, intent);
        finish();
    }

}
