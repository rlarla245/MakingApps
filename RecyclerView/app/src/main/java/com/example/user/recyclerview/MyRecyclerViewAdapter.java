package com.example.user.recyclerview;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * Created by user on 2018-02-08.
 */

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    // alt + insert가 컨스트럭터를 생성합니다.
    public MyRecyclerViewAdapter() {
    }

    // 이미지들을 담고 있는 이미지 배열입니다.
    public static int[] images = {R.drawable.i_1, R.drawable.i_2, R.drawable.i_3, R.drawable.i_4};

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int width = parent.getResources().getDisplayMetrics().widthPixels / 3;
        // viewHolder를 만든다는 의미와 같게 레이아웃에서 생성한 뷰를 적용해줍니다.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item, parent, false);

        view.setLayoutParams(new LinearLayout.LayoutParams(width, width));
        // gravity를 줄 수 있는 레이아웃 메소드가 linear가 대표적이라 그런듯함. 폭 - 높이 관계임 현재는
        return new RowCell(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        // 실제 이미지들을 그리드 뷰에 넣어봅시다.
        ((RowCell)holder).imageView.setImageResource(images[position]);

        holder.itemView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                MainActivity.largeImage.setImageResource(images[position]);
                return false;
            }
        });


    }

    @Override
    public int getItemCount() {
        return images.length;
    }

    private static class RowCell extends RecyclerView.ViewHolder {
        // 그 뷰안에 이미지를 넣어줘야 하므로 여기서 설정해줍니다.
        public ImageView imageView;
        public RowCell(View view) {
            super(view);
            imageView = (ImageView)view.findViewById(R.id.mainactivity_recyclerview_item_imageview);
        }
    }
}
