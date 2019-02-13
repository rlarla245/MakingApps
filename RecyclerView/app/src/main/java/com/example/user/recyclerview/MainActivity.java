package com.example.user.recyclerview;

import android.renderscript.ScriptGroup;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static ImageView largeImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 서포트 액션바 삭제
        getSupportActionBar().hide();

        // 리사이클러 뷰를 가져옵니다.
        RecyclerView view = (RecyclerView)findViewById(R.id.mainactivity_recyclerview);

        // 레이아웃 매니저 및 어댑터 설정
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (position == 0) {
                    // 3개의 공간을 차지합니다.
                    return 3;
                }
                return 1;
            }
        });

        view.setLayoutManager(gridLayoutManager);
        view.setAdapter(new RecyclerViewAdapter());
    }

    class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        int[] images = new int[]{R.drawable.i_1, R.drawable.i_2, R.drawable.i_3, R.drawable.i_4, R.drawable.i_1, R.drawable.i_2, R.drawable.i_3, R.drawable.i_4, R.drawable.i_1, R.drawable.i_2, R.drawable.i_3, R.drawable.i_4, R.drawable.i_1};

        // 포지션에 따라 사진 크기를 변경합시다.
        @Override
        public int getItemViewType(int position) {
            if (position == 0) {
                return 0;
            }
            return 1;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item, parent, false);
            if (viewType == 0) {
                int width = parent.getResources().getDisplayMetrics().widthPixels;
                int height = parent.getResources().getDisplayMetrics().widthPixels / 3 * 2;

                view.setLayoutParams(new LinearLayout.LayoutParams(width, height));
                return new CustomViewHolder(view);
            }
            int width = parent.getResources().getDisplayMetrics().widthPixels / 3;
            int height = parent.getResources().getDisplayMetrics().widthPixels / 3;

            view.setLayoutParams(new LinearLayout.LayoutParams(width, height));
            return new CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ((CustomViewHolder)holder).imageView_item.setImageResource(images[position]);
        }

        @Override
        public int getItemCount() {
            return images.length;
        }

        private class CustomViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView_item;
            public CustomViewHolder(View view) {
                super(view);
                imageView_item = (ImageView)view.findViewById(R.id.mainactivity_recyclerview_item_imageview);
            }
        }
    }
}
