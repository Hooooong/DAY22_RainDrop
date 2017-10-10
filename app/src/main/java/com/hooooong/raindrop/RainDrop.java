package com.hooooong.raindrop;

/**
 * Created by Android Hong on 2017-10-10.
 */

// RainDrop 이란 클래스는 스스로 행동을 해야 하기 때문에 Thread 를 상속받는다.
public class RainDrop{
    // 속성
    private float x;
    private float y;
    private float speed;
    private float size;
    private int color;
    // 생명 주기 - 바닥에 닿을때 까지
    private float limit;

    public RainDrop() {
    }

    public RainDrop(float x, float y, float speed, float size, int color, float limit) {
        this.x = x;
        this.y = y;
        this.speed = speed;
        this.size = size;
        this.color = color;
        this.limit = limit;
    }


    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public float getSize() {
        return size;
    }

    public void setSize(float size) {
        this.size = size;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public float getLimit() {
        return limit;
    }

    public void setLimit(float limit) {
        this.limit = limit;
    }
}
