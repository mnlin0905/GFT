package com.linktech.gft.util;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.wangnan.library.model.Point;
import com.wangnan.library.painter.AliPayPainter;

import java.util.List;

/**
 * Created on 2019/6/4  10:13
 * function : 自定义,可控制是否绘制
 *
 * @author mnlin
 */
public class CustomAliPayPainter extends AliPayPainter {
    /**
     * 是否显示轨迹线
     */
    public boolean showGestureTravel = true;

    @Override
    public void drawLines(List<Point> points, float eventX, float eventY, int lineSize, Canvas canvas) {
        if(showGestureTravel){
            super.drawLines(points, eventX, eventY, lineSize, canvas);
        }
    }

    @Override
    public void drawNormalPoint(Point point, Canvas canvas, Paint normalPaint) {
        // 1.绘制实心点
        mNormalPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(point.x, point.y, point.radius / 3.0F, mNormalPaint);
    }

    @Override
    public void drawPressPoint(Point point, Canvas canvas, Paint pressPaint) {
        if(showGestureTravel){
            super.drawPressPoint(point, canvas, pressPaint);
        }else{
            mNormalPaint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(point.x, point.y, point.radius / 3.0F, mNormalPaint);
        }
    }

    @Override
    public void drawErrorPoint(Point point, Canvas canvas, Paint errorPaint) {
        if(showGestureTravel){
            super.drawErrorPoint(point, canvas, errorPaint);
        }else {
            mNormalPaint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(point.x, point.y, point.radius / 3.0F, mNormalPaint);
        }
    }
}
