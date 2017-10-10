package com.hooooong.raindrop;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Android Hong on 2017-10-10.
 */

public class CustomView extends View {
    private Paint paint;
    private List<RainDrop> rainDropList;

    public CustomView(Context context) {
        super(context);
        // 색 지정
        paint = new Paint();
        rainDropList = new ArrayList<>();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // RainDrop 객체를 그려준다.
        if (rainDropList.size() > 0) {
            for (int i = 0; i < rainDropList.size(); i++) {
                RainDrop rainDrop = rainDropList.get(i);
                paint.setColor(rainDrop.getColor());
                canvas.drawCircle(rainDrop.getX(), rainDrop.getY(), rainDrop.getSize(), paint);
            }
        }
    }

    public synchronized void addRainDrop(RainDrop rainDrop) {
        this.rainDropList.add(rainDrop);
    }

    public void runStage() {
        new Thread() {
            @Override
            public void run() {
                while (MainActivity.runFlag) {
                    for (int i = 0; i < rainDropList.size(); i++) {
                        RainDrop rainDrop = rainDropList.get(i);
                        // rainDrop 객체가 화면 밖으로 나갔으면 지워준다.
                        if (rainDrop.getY() > rainDrop.getLimit()) {
                            rainDropList.remove(rainDrop);
                            i--;
                        } else {
                            // y값 수정
                            rainDrop.setY(rainDrop.getY() + rainDrop.getSpeed());
                        }
                    }

                    postInvalidate();

                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }
}
