package com.github.tifezh.kchartlib.chart.beans;

import android.support.annotation.Nullable;

import com.github.tifezh.kchartlib.chart.entity.IMinuteLine;

import java.util.Date;

/**
 * 分时图实体
 * Created by tifezh on 2017/7/20.
 */

public class MinuteLineEntity implements IMinuteLine {
    /**
     * time : 09:30
     * price : 3.53
     * avg : 3.5206
     * vol : 9251
     */

    public Date time;
    public float price;
    public float avg;
    public float volume;

    public MinuteLineEntity() {
    }

    public MinuteLineEntity(Date time, float price, float avg, float volume) {
        this.time = time;
        this.price = price;
        this.avg = avg;
        this.volume = volume;
    }

    public MinuteLineEntity(Date time, double price, double avg, double volume) {
        this.time = time;
        this.price = (float) price;
        this.avg = (float) avg;
        this.volume = (float) volume;
    }

    @Override
    public float getAvgPrice() {
        return avg;
    }

    @Override
    public float getPrice() {
        return price;
    }

    @Override
    public Date getDate() {
        return time;
    }

    @Override
    public float getVolume() {
        return volume;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return obj instanceof MinuteLineEntity && ((MinuteLineEntity) obj).getDate() != null && ((MinuteLineEntity) obj).getDate().equals(time);
    }
}
