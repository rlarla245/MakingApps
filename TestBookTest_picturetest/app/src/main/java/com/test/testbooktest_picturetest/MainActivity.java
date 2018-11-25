package com.test.testbooktest_picturetest;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    // `1. 메뉴에서 선택한 것이 선을 그리는 것인지, 원을 그리는 것인지 구분하기 위해 변수를 나눕니다.
    final static int LINE = 1, CIRCLE = 2;
    // 2. 현재 선택된 도형이 선인지 원인지 저장한다. 디폴트 값은 선이다.
    static int curShapte = LINE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 3. 레이아웃을 처음부터 생성하는 것이므로 변경이 필요합니다.
        setContentView(new MyGraphicView(this));
        setTitle("간단 그림판");
    }

    // 5. 메뉴 선택 가능 메소드를 생성합니다.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        // 6. 각각의 경우에 맞는 메뉴를 생성합니다. 파라미터는 순서대로 그룹id, 항목id, 순번 제목 순 입니다.
        // 항목 id는 하단 switch문의 파라미터 값들과 동일해야 합니다.
        menu.add(0, 1, 0, "선 그리기");
        menu.add(0, 2, 0, "원 그리기");
        return true;
    }

    // 7. 메뉴를 선택했을 때의 경우를 설정해줍니다.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 1:
                curShapte = LINE;
                return true;

            case 2:
                curShapte = CIRCLE;
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // 4. 그림판을 생성하는 새로운 클래스를 생성합니다.
    private static class MyGraphicView extends View {
        // 8. 시작점과 끝점 좌표를 저장 가능한 멤버변수 4개 생성
        int startX = -1, startY = -1, stopX = -1, stopY = -1;

        public MyGraphicView(Context context) {
            super(context);
        }

        // 9. 화면을 터치했을 경우입니다.
        @Override
        public boolean onTouchEvent(MotionEvent event) {
            switch (event.getAction()) {
                // 화면을 클릭했을 때
                case MotionEvent.ACTION_DOWN:
                    startX = (int) event.getX();
                    startY = (int) event.getY();
                    break;

                    // 순서대로 이동했을 때, 손을 뗐을 때
                case MotionEvent.ACTION_MOVE:
                case MotionEvent.ACTION_UP:
                    stopX = (int) event.getX();
                    stopY = (int) event.getY();
                    this.invalidate();
                    break;
            }
            return true;
        }

        // 10. 실제로 화면을 그리는 메소드
        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            // 페인트 인스턴스를 생성합니다.
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setStrokeWidth(5);
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(Color.RED);

            switch (curShapte) {
                case LINE:
                    canvas.drawLine(startX, startY, stopX, stopY, paint);
                    break;

                case CIRCLE:
                    int radius = (int) Math.sqrt(Math.pow(stopX = startX, 2));
                    canvas.drawCircle(startX, startY, radius, paint);
                    break;
            }
        }
    }
}
