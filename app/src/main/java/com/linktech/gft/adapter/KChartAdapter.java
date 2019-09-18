package com.linktech.gft.adapter;

import com.github.tifezh.kchartlib.chart.BaseKChartAdapter;
import com.linktech.gft.bean.KLineEntity;
import com.linktech.gft.plugins.PlusFunsPluginsKt;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 数据适配器
 * Created by tifezh on 2016/6/18.
 */

public class KChartAdapter extends BaseKChartAdapter {

    private List<KLineEntity> datas = new ArrayList<>();

    public KChartAdapter() {

    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int position) {
        if (position >= datas.size()) {
            return datas.get(datas.size() - 1);
        } else {
            return datas.get(position);
        }
    }

    @Override
    public Date getDate(int position) {
        if (position >= datas.size()) {
            Date last = PlusFunsPluginsKt.stockToDate(datas.get(datas.size() - 1).Date, "yyyy-MM-dd HH:mm:ss");
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(last.getTime());
            calendar.add(Calendar.DAY_OF_MONTH, position - datas.size() + 1);
            return calendar.getTime();
        } else {
            return PlusFunsPluginsKt.stockToDate(datas.get(position).Date, "yyyy-MM-dd HH:mm:ss");
        }
    }

    /**
     * 向头部添加数据
     */
    public void addHeaderData(List<KLineEntity> data) {
        if (data != null && !data.isEmpty()) {
            datas.addAll(data);
            notifyDataSetChanged();
        }
    }

    /**
     * 向尾部添加数据
     */
    public void addFooterData(List<KLineEntity> data) {
        if (data != null && !data.isEmpty()) {
            datas.addAll(0, data);
            notifyDataSetChanged();
        }
    }

    /**
     * 改变某个点的值
     *
     * @param position 索引值
     */
    public void changeItem(int position, KLineEntity data) {
        datas.set(position, data);
        notifyDataSetChanged();
    }

    public List<KLineEntity> getDatas() {
        return datas;
    }
}
