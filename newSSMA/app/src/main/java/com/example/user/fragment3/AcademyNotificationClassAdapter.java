package com.example.user.fragment3;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class AcademyNotificationClassAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<AcademyNotificationClassDTOs> academyNotificationClassDTOs = new ArrayList<>();

    public AcademyNotificationClassAdapter() {
        academyNotificationClassDTOs.add(new AcademyNotificationClassDTOs("서비스 전략경영학회 공지사항입니다.", "금일 회의록 안건입니다.\n각 부서 담당자분들은 안건 확인 부탁드립니다.", "학회장"));
        academyNotificationClassDTOs.add(new AcademyNotificationClassDTOs("금일 지각 및 결석자들 명단입니다.", "금일 지각 및 결석자 명단입니다.\n각 부서 담당자분들은 확인바랍니다.", "HR팀장"));
        academyNotificationClassDTOs.add(new AcademyNotificationClassDTOs("서비스 전략경영학회 공지사항입니다.", "금일 회의록 안건입니다.\n각 부서 담당자분들은 안건 확인 부탁드립니다.", "학회장"));
        academyNotificationClassDTOs.add(new AcademyNotificationClassDTOs("Case Study 관련 공지합니다.", "금일 Case Study는 교수님이 직접 참관하실 예정이니 모든 학회원들의 참석 바랍니다.", "부학회장"));
        academyNotificationClassDTOs.add(new AcademyNotificationClassDTOs("금일 지각 및 결석자들 명단입니다.", "금일 지각 및 결석자 명단입니다.\n각 부서 담당자분들은 확인바랍니다.", "HR팀장"));
        academyNotificationClassDTOs.add(new AcademyNotificationClassDTOs("Case Study 관련 공지합니다.", "금일 Case Study는 교수님이 직접 참관하실 예정이니 모든 학회원들의 참석 바랍니다.", "부학회장"));
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.academy_notification_class_cardview, parent, false);
        return new RowCell(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((RowCell)holder).title.setText(academyNotificationClassDTOs.get(position).title);
        ((RowCell)holder).contents.setText(academyNotificationClassDTOs.get(position).contents);
        ((RowCell)holder).who.setText(academyNotificationClassDTOs.get(position).who);


        ((RowCell)holder).title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent noti1_intent = new Intent(view.getContext(), AcademyNotificationResult.class);
                view.getContext().startActivity(noti1_intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return academyNotificationClassDTOs.size();
    }

    private static class RowCell extends RecyclerView.ViewHolder {
        public TextView title, contents, who;
        public RowCell(View view) {
            super(view);
            title = view.findViewById(R.id.title_what);
            contents = view.findViewById(R.id.contents_what);
            who = view.findViewById(R.id.people_who);
        }
    }
}
