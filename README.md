# RainDrop

### 설명
____________________________________________________

![RainDrop]()

- Thread 를 이용한 물방울 내리기

### KeyPoint
____________________________________________________

- Thread 란?

  - 참조 : [Thread](https://github.com/Hooooong/DAY19_Thread)

- Thread 사용

  1. Button 을 누를 때 `RainDrop` 객체 생성

      - while 문을 통해 `onDestroy()` 를 호출하기 전까지 `RainDrop` 객체를 생성한다.

      - 생성한 후 `CustomView` 의 List에 담아 사용

      ```Java
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
      ```

  2. `CustomView` 새로고침

      - 넘겨받은 `RainDrop`의 y축을 변경한 다음 `CustomView` 를 주기적으로 새로고침 해준다.

      - Android 는 UI Thread 외부에서 UI 작업을 변경하면 Exception 이 발생한다.

      - `invalidate()` 는 UI Thread(main Thread) 에서만 사용할 수 있으므로 외부 Thread를 사용하여 View 를 변경할 때는 `postInvalidate()`를 사용하여 `CustomView` 를 새로고침한다.

      - `invalidate()` 와 `postInvalidate()` 를 호출하게 되면 `CustomView` 의 `onDraw()` 메소드 호출을 한다.

      ```java
      public void runStage() {
          new Thread() {
              @Override
              public void run() {
                  while (MainActivity.runFlag) {

                      for (int i = 0; i < rainDropList.size(); i++) {
                          RainDrop rainDrop = rainDropList.get(i);
                          if (rainDrop.getY() > rainDrop.getLimit()) {
                              rainDropList.remove(rainDrop);
                              i--;
                          } else {
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
      ```

### Code Review
____________________________________________________

- MainActivity.java

  - Main Thread 이외에 `RainDrop` 객체를 반복적으로 생성하는 Thread 를 작성

  - `CustomView` 를 addView 하고 `runStage()`를 호출하여 View 를 새로고침하는 효과 발생

  ```java
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
  ```

- CustomView.java

  - `RainDrop` 객체를 통해 View 를 그려주는 클래스

  - `stage()` 메소드에 Thread 를 구현하여 `RainDrop` 객체의 y 값을 수정하고, `postInvalidate()` 를 호출하여 View 를 새로고침한다.

  ```java
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
  ```

- RainDrop.java

  - 물방울의 정보를 가지고 있는 클래스

  ```java
  // RainDrop 이란 클래스는 스스로 행동을 해야 하기 때문에 Thread 를 상속받는다.
  // 하지만 꼭 Thread 를 상속받아 run 메소드를 재정의하는 것보다 이 객체를 사용하는 곳에서 y 값을 변경하여 그려주면 된다.(CustomView.stage 메소드)
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
  ```

- activity_main.xml

  ```xml
  <?xml version="1.0" encoding="utf-8"?>
  <android.support.constraint.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
      xmlns:app="http://schemas.android.com/apk/res-auto"
      xmlns:tools="http://schemas.android.com/tools"
      android:layout_width="match_parent"
      android:layout_height="match_parent"
      tools:context="com.hooooong.raindrop.MainActivity"
      android:id="@+id/stage">

     <Button
          android:layout_width="wrap_content"
          android:layout_height="wrap_content"
          android:onClick="makeRainDrop"
          app:layout_constraintBottom_toBottomOf="parent"
          app:layout_constraintRight_toRightOf="parent"
          android:text="Make RAINDROP"/>

  </android.support.constraint.ConstraintLayout>
  ```
