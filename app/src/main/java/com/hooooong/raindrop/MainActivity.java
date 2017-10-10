package com.hooooong.raindrop;

import android.graphics.Color;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    // Thread 를 제어할 때 MainActivity 에 flag 값을 static 으로 선언해주는게 좋다.
    public static boolean runFlag = false;

    private ConstraintLayout stage;
    private CustomView customView;
    private int width = 0;
    private int height = 0;

    Random random = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        stage = (ConstraintLayout) findViewById(R.id.stage);

        // CustomView 를 생성하고, layout 에 addView 한다.
        customView = new CustomView(this);
        stage.addView(customView);

        // 스마트폰의 width, height 의 값을 가져온다.
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        width = metrics.widthPixels;
        height = metrics.heightPixels;
    }

    public void makeRainDrop(View view){
        runFlag = true;
        // 화면을 지속적으로 다시 그려준다.(customView 의 Thread 실행)
        customView.runStage();
        new Thread(){
            @Override
            public void run() {
                while(runFlag){
                    // x, y, speed, size 설정
                    int x = random.nextInt(width);
                    int speed = random.nextInt(10)+2;
                    int size = random.nextInt(100);
                    int y = size*-1;

                    // 반복적으로 RainDrop 객체를 생성한다.(물방울)
                    RainDrop rainDrop = new RainDrop(x, y, speed, size, Color.RED, height);
                    customView.addRainDrop(rainDrop);

                    try {
                        // 0.1 초 쉬었다가 다시 실행
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    // flag 값을 통해 Thread 를 중지시키는 효과를 줄 수 있다.
    // App 이 종료되는 시점에 onDestroy 가 호출되기 때문에 이때 flag 값을 변경한다.
    @Override
    protected void onDestroy() {
        runFlag = false;
        super.onDestroy();
    }
}
