package com.kkm.testofrecyclerview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {
    protected RecyclerView recyclerView;
    protected int[] testImage;
    // 해당 코드부터 줌 인/아웃
    protected Drawable image;
    protected ScaleGestureDetector gestureDetector;
    protected float scale = 1.0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView) findViewById(R.id.mainactivty_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true));

        RecyclerviewAdapter recyclerviewAdapter = new RecyclerviewAdapter();
        recyclerView.setAdapter(recyclerviewAdapter);

        testImage = new int[]{R.drawable.worldmap};
    }

    public class RecyclerviewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item, parent, false);

            return new CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ((CustomViewHolder) holder).imageView.setImageResource(R.drawable.worldmap);
        }

        @Override
        public int getItemCount() {
            return testImage.length;
        }
    }

    private class CustomViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;

        public CustomViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.mainactivty_recyclerview_item_imageView);
        }
    }

    private class MyImageView extends View {
        public MyImageView(Context context) {
            super(context);

            image = context.getResources().getDrawable(R.drawable.worldmap);

            setFocusable(true);
            image.setBounds(0, 0, image.getIntrinsicWidth(), image.getIntrinsicHeight());
            gestureDetector = new ScaleGestureDetector(context, new ScaleListener());
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            canvas.save();
            canvas.scale(scale, scale);
            image.draw(canvas);
            canvas.restore();
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            gestureDetector.onTouchEvent(event);
            invalidate();
            return true;
        }

        private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                scale += detector.getScaleFactor();
                if (scale < 0.5f) scale = 0.5f;
                if (scale >10.0f) scale = 10.0f;
                invalidate();
                return true;
            }
        }
    }
}
