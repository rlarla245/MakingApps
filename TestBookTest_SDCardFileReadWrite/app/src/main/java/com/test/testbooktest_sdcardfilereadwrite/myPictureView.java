package com.test.testbooktest_sdcardfilereadwrite;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

// 2. 새로운 클래스를 생성하고 파라미터가 2개인 생성자를 불러옵니다.
public class myPictureView extends View {
    // 3. 이미지 경로 및 파일을 지정하는 변수를 생성합니다.
    String imagePath = null;
    public myPictureView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    // onDraw 메소드를 호출합니다. 사진을 불러오는 메소드입니다.
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 4. 이미지 경로가 존재할 때
        if (imagePath != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            canvas.drawBitmap(bitmap, 0, 0, null);
            bitmap.recycle();
        }
    }
}
