package com.linktech.gft.adapter;

import android.app.Dialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.linktech.gft.R;
import com.linktech.gft.base.BaseActivity;
import com.linktech.gft.base.BaseRecyclerViewHolder;
import com.linktech.gft.bean.WalletTakeOutBean;
import com.linktech.gft.plugins.PlusFunsPluginsKt;
import com.linktech.gft.util.NumberUtil;
import com.linktech.gft.window.CancelConfirmDialogFragment;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created on 2018/1/12
 * function : 转出记录
 *
 * @author LinkTech
 */

public class TakeOutCurrencyRecordAdapter extends RecyclerView.Adapter<TakeOutCurrencyRecordAdapter.ViewHolder> {
    private BaseActivity baseActivity;
    private List<WalletTakeOutBean.Record> datas;
    private BaseRecyclerViewHolder.OnViewClickListener listener;
    private onCancelTakeOutListener cancelTakeOutListener;

    /**
     * 是否禁止处理callback
     */
    private volatile boolean forbid;

    public TakeOutCurrencyRecordAdapter(BaseActivity baseActivity, List<WalletTakeOutBean.Record> datas, BaseRecyclerViewHolder.OnViewClickListener listener, onCancelTakeOutListener cancelTakeOutListener) {
        this.baseActivity = baseActivity;
        this.datas = datas;
        this.listener = listener;
        this.cancelTakeOutListener = cancelTakeOutListener;
    }

    @Override
    public TakeOutCurrencyRecordAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new TakeOutCurrencyRecordAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_take_out_currency_record, parent, false), listener);
    }

    @Override
    public void onBindViewHolder(TakeOutCurrencyRecordAdapter.ViewHolder holder, int position) {
        WalletTakeOutBean.Record bean = datas.get(position);
        holder.mTvTime.setText(bean.getCreateTime());
        holder.mTvAmount.setText(NumberUtil.getDefaultShow(bean.getActual()));
        holder.mTvStatus.setText(bean.getStatusStr());
        holder.mTvStatus.setTextColor(bean.getStatusColor(baseActivity));
        holder.mTvOperate.setText(bean.getOperateStr());
        holder.mTvOperate.setTextColor(bean.getOperateColor(baseActivity));
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    /**
     * 撤销监听事件
     */
    public interface onCancelTakeOutListener {
        /**
         * @param position 需要撤销的item位置
         */
        void onCancelTakeOut(int position);
    }

    class ViewHolder extends BaseRecyclerViewHolder implements CancelConfirmDialogFragment.OnOperateListener {
        @BindView(R.id.tv_time)
        TextView mTvTime;
        @BindView(R.id.tv_amount)
        TextView mTvAmount;
        @BindView(R.id.tv_status)
        TextView mTvStatus;
        @BindView(R.id.tv_operate)
        TextView mTvOperate;
        @BindView(R.id.iv_navigation)
        ImageView mIvNavigation;

        ViewHolder(View itemView, OnViewClickListener listener) {
            super(itemView, listener);
        }

        @OnClick({R.id.tv_operate})
        public void onViewClicked(View view) {
            if (datas.get(getCurrentPosition()).getOperateAble()) {
                new CancelConfirmDialogFragment()
                        .setTitle(PlusFunsPluginsKt.getString(null, R.string.activity_take_out_currency_record_alert_revoked))
                        .setCancelText(PlusFunsPluginsKt.getString(null, R.string.function_cancel))
                        .setConfirmText(PlusFunsPluginsKt.getString(null, R.string.function_confirm))
                        .setOnOperateListener(this)
                        .show(baseActivity.getSupportFragmentManager(), "cancel");
            }
        }

        /**
         * 点击左侧按钮
         *
         * @param dialog dialog
         * @return 返回true则默认不会关闭dialog
         */
        @Override
        public boolean onCancel(Dialog dialog) {
            return false;
        }

        /**
         * 点击右侧按钮
         *
         * @param dialog dialog
         */
        @Override
        public boolean onConfirm(Dialog dialog) {
            cancelTakeOutListener.onCancelTakeOut(getCurrentPosition());
            return false;
        }
    }
}
