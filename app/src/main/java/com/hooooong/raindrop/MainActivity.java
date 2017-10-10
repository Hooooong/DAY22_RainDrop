package com.hooooong.raindrop;

import android.graphics.Color;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    ConstraintLayout stage;
    CustomView customView;
    // Thread 를 제어할 때 MainActivity 에 flag 값을 static 으로 선언해주는게 좋다.
    public static boolean runFlag = true;

    int width = 0;
    int height = 0;
    Random random = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        stage = (ConstraintLayout) findViewById(R.id.stage);
        customView = new CustomView(this);
        stage.addView(customView);
        // 화면을 지속적으로 다시 그려준다.
        customView.runStage();

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        width = metrics.widthPixels;
        height = metrics.heightPixels;
    }

    public void addRainDrop(View view){
        int x = random.nextInt(width);
        int speed = random.nextInt(10)+2;
        int size = random.nextInt(100);
        int y = size*-1;

        RainDrop rainDrop = new RainDrop(x, y, speed, size, Color.RED, height);
        customView.addRainDrop(rainDrop);
    }

    public void makeRainDrop(View view){
        new Thread(){
            @Override
            public void run() {
                while(runFlag){
                    int x = random.nextInt(width);
                    int speed = random.nextInt(10)+2;
                    int size = random.nextInt(100);
                    int y = size*-1;

                    RainDrop rainDrop = new RainDrop(x, y, speed, size, Color.RED, height);
                    customView.addRainDrop(rainDrop);

                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    @Override
    protected void onDestroy() {
        runFlag = false;
        super.onDestroy();
    }
}
