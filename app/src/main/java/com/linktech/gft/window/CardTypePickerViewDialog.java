package com.linktech.gft.window;

import android.view.View;

import com.bigkoo.pickerview.OptionsPickerView;
import com.bigkoo.pickerview.lib.WheelView;
import com.linktech.gft.R;
import com.linktech.gft.base.BaseActivity;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.List;

/**
 * Created on 2018/1/17
 * function : 身份证件选择
 *
 * @author LinkTech
 */

public class CardTypePickerViewDialog {
    private static CardTypePickerViewDialog singleInstance;
    private static WeakReference<BaseActivity> reference;
    onCardTypePickerViewListener listener;
    private OptionsPickerView pvLocation;
    private List<String> cardTypes;

    private CardTypePickerViewDialog() {

    }

    /**
     * 以activity为key,保存dialog数据,保证每个activity弹出同一个dialog
     */
    public synchronized static CardTypePickerViewDialog getInstance(BaseActivity activity) {
        if (activity != null) {
            reference = new WeakReference<BaseActivity>(activity);
        }
        if (singleInstance == null) {
            singleInstance = new CardTypePickerViewDialog();
        }
        return singleInstance;
    }


    /**
     * 生成pickerview
     */
    private void createPickerView(BaseActivity activity) {
        cardTypes = Arrays.asList(reference.get().getResources().getStringArray(R.array.array_person_card_type));
        pvLocation = new OptionsPickerView.Builder(activity, new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v1) {
                if (listener != null) {
                    listener.onTypePickerFinish(options1, cardTypes.get(options1));
                }
            }
        }).setLayoutRes(R.layout.layout_pickerview_options_bunkers, view -> {
            view.findViewById(R.id.tv_confirm).setOnClickListener(v1 -> {
                pvLocation.returnData();
                pvLocation.dismiss();
            });
            view.findViewById(R.id.tv_cancel).setOnClickListener(v1 -> {
                pvLocation.dismiss();
            });
        }).setContentTextSize((int) (activity.getResources().getDimensionPixelSize(R.dimen.text_size_17sp) / activity.getResources().getDisplayMetrics().density))
                .setTextColorCenter(activity.dispatchGetColor(R.color.black_text_171a3f))
                .setTextColorOut(activity.dispatchGetColor(R.color.black_text_663a4254))
                .setLineSpacingMultiplier(3F)
                .isDialog(false)
                .isCenterLabel(false)
                .setOutSideCancelable(false)
                .setDividerType(WheelView.DividerType.FILL)
                .setDividerColor(activity.dispatchGetColor(R.color.mc_divider_gray))
                .build();

        pvLocation.setPicker(cardTypes);

        pvLocation.setOnDismissListener(o -> {
            if (listener != null) {
                listener.onTypePickerDismiss();
                listener = null;
            }
        });
    }

    /**
     * 直接显示dialog
     *
     * @return true表示显示成功, false表示尚未初始化完成
     */
    public boolean show() {
        BaseActivity activity;
        if (reference != null && (activity = reference.get()) != null) {
            createPickerView(activity);
            pvLocation.show();
            return true;
        }
        return false;
    }

    /**
     * 设置监听器
     */
    public CardTypePickerViewDialog setOnPickerViewListener(onCardTypePickerViewListener listener) {
        this.listener = listener;
        return this;
    }

    /**
     * 时间选择器监听事件
     */
    public interface onCardTypePickerViewListener {
        /**
         * 当picker消失时进行回调
         */
        default  void onTypePickerDismiss(){}

        /**
         * 0 表示身份证
         * 1 表示军官证
         * 2 表示护照
         */
        void onTypePickerFinish(int position, String type);
    }
}
