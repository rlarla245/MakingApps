package com.du.instagramprototypeproject.bottmNavigations;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.du.instagramprototypeproject.LoginActivity;
import com.du.instagramprototypeproject.MainActivity;
import com.du.instagramprototypeproject.R;
import com.du.instagramprototypeproject.databinding.ActivityMainBinding;
import com.du.instagramprototypeproject.databinding.ItemDetailviewBinding;
import com.du.instagramprototypeproject.model.AlarmDTO;
import com.du.instagramprototypeproject.model.ContentDTO;
import com.du.instagramprototypeproject.model.FollowDTO;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.media.CamcorderProfile.get;
import static com.du.instagramprototypeproject.util.StatusCode.FRAGMENT_ARG;

public class DetailViewFragment extends android.app.Fragment {
    // 유저를 지정합니다.
    String destinationUidToken;
    private FirebaseUser user;
    private String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

    // 왜 생성자를 쓰는가? onStart()가 낫지 않나?
    public DetailViewFragment() {
        // 현재 유저를 지정합니다.
        user = FirebaseAuth.getInstance().getCurrentUser();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        // 서버 문제로 인한 에러 발생 회피 코드입니다.
        if (user.getUid() == null) {
            startActivity(new Intent(getView().getContext(), LoginActivity.class));
        }

        // 해당 레이아웃을 불러옵니다.
        View view = inflater.inflate(R.layout.fragment_detailview, container, false);

        // 리사이클러 뷰를 호출합니다.
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.detailviewfragment_recyclerview);

        // 레이아웃 매니저 및 어댑터를 설정합니다.
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setAdapter(new DetailRecyclerViewAdapter());

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        // 다른 액티비티에서 다시 넘어올 경우(foucs가 다시 잡혔을 경우) 프로그레스바를 없앱니다.
        MainActivity.binding.progressBar.setVisibility(View.GONE);
    }

    // 어댑터를 설정합니다.
    private class DetailRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        // 컨텐츠 내용과 uid들을 담는 리스트를 생성합니다.
        private ArrayList<ContentDTO> contentDTOs;
        private ArrayList<String> contentUidList;
        String detailImageUid;

        DetailRecyclerViewAdapter() {
             contentDTOs = new ArrayList<>();
             contentUidList = new ArrayList<>();

             // 현재 유저의 좋아요 정보를 불러오게 됩니다.
            FirebaseDatabase.getInstance().getReference().child("users")
                    .orderByKey().equalTo(uid)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                // 해당 데이터 테이블에 맞는 값들을 불러오게 됩니다.
                                FollowDTO comparableFollowDTO = snapshot.getValue(FollowDTO.class);

                                // 유저가 실제로 존재하고, 그 유저의 팔로잉 데이터들이 있을 경우에만 데이터들을 불러오게 됩니다.
                                // 왼쪽 조건은 상단에 uid 변수 생성 시 유저가 없을 경우 어차피 에러가 나므로 필요없지 않은가?
                                if (comparableFollowDTO != null && comparableFollowDTO.followings != null) {
                                    // 해당 메소드를 통해 팔로우 정보를 불러옵니다.
                                    getContents(comparableFollowDTO.followings);
                                }
                            }
                            // 갱신
                            notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
        }

        // 좋아요 데이터 테이블의 팔로잉 데이터들을 불러오는 메소드입니다.
        public void getContents(final Map<String, Boolean> follwings) {
            FirebaseDatabase.getInstance().getReference()
                    .child("images").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    contentDTOs.clear();
                    contentUidList.clear();

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        ContentDTO items = snapshot.getValue(ContentDTO.class);

                        // null 에러를 피하기 위해 조건 생성(1)
                        // 내가 팔로우하는 사람들의 사진들만 보여야 하므로 해당 조건 생성(2)
                        // 내가 올린 사진들은 없애야 하므로 해당 조건 생성(3) -> 어차피 내가 나를 팔로우 할수는 없으니 필요없는 조건 아닌가?
                        // !items.uid.equals(uid)
                        if (items != null && follwings.containsKey(items.uid)) {
                            contentDTOs.add(items);
                            // getKey 메소드를 통해 해당 item의 uid 값을 불러옵니다.
                            contentUidList.add(snapshot.getKey());
                        }
                    }
                    notifyDataSetChanged();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // 레이아웃 아이템들을 담게 되는 레이아웃을 불러옵니다.
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_detailview, parent, false);

            return new CustomViewHolder(view);
        }

        // 데이터들을 바인딩 해줍니다.
        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
            // 변수 지정해줘야 '해당' 인덱스 값을 저장받습니다.
            final int finalPosition = position;
            final ItemDetailviewBinding binding = ((CustomViewHolder) holder).getBinding();

            // 해당 유저의 프로필 이미지를 불러옵니다.
            FirebaseDatabase.getInstance()
                    .getReference()
                    .child("profileImages").child(contentDTOs.get(position).uid)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            // 프로필 이미지가 존재할 경우
                            if (dataSnapshot.exists()) {
                                @SuppressWarnings("VisibleForTests")
                                String url = dataSnapshot.getValue().toString();

                                // 프로필 이미지를 적용합니다.
                                Glide.with(holder.itemView.getContext())
                                        .load(url)
                                        .apply(new RequestOptions().circleCrop())
                                        .into(binding.detailviewitemProfileImage);
                            }
                            // 프로필 이미지가 없을 경우에는?
                            else {
                                binding.detailviewitemProfileImage.setImageResource(R.mipmap.ic_launcher_foreground);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

            // 프로필 이미지를 누를 경우의 메소드입니다.
            binding.detailviewitemProfileImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    android.app.Fragment fragment = new UserFragment();

                    // 해당 유저의 uid와 이메일을 담아 유저 프레그먼트로 정보를 전송합니다.
                    Bundle bundle = new Bundle();
                    bundle.putString("destinationUid", contentDTOs.get(finalPosition).uid);
                    bundle.putString("userId", contentDTOs.get(finalPosition).userId);
                    bundle.putInt(FRAGMENT_ARG, 5);

                    fragment.setArguments(bundle);

                    getActivity().getFragmentManager().beginTransaction()
                            .replace(R.id.main_content, fragment)
                            .commit();
                }
            });

            // 유저 아이디
            binding.detailviewitemProfileTextview.setText(contentDTOs.get(position).userId);

            // 가운데 이미지
            Glide.with(holder.itemView.getContext())
                    .load(contentDTOs.get(position).imageUrl)
                    .into(binding.detailviewitemImageviewContent);

            // 설명 텍스트
            binding.detailviewitemExplainTextview.setText(contentDTOs.get(position).explain);

            // 좋아요 이미지를 누를 경우
            binding.detailviewitemFavoriteImageview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 해당 이미지의 uid를 의미합니다.
                    detailImageUid = contentUidList.get(finalPosition);

                    // 해당 포지션값에 해당하는 이미지에 데이터를 표시합니다.
                    favoriteEvent(finalPosition, detailImageUid);
                }
            });

            // 이미 누른 경우 채워진 하트로 이미지를 변경합니다.
            if (contentDTOs.get(position)
                    .favorites.containsKey(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                binding.detailviewitemFavoriteImageview.setImageResource(R.drawable.ic_favorite);
            }

            else {
                // 누르지 않았을 경우 또는 취소한 경우 비워진 하트로 이미지를 변경합니다.
                binding.detailviewitemFavoriteImageview.setImageResource(R.drawable.ic_favorite_border);
            }

            // 좋아요 숫자
            binding.detailviewitemFavoritecounterTextview.setText("좋아요 " + contentDTOs.get(position).favoriteCount + "개");

            // 댓글 이미지를 누를 경우(CommentActivity로 이동합니다.)
            binding.detailviewitemCommentImageview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), CommentActivity.class);
                    // 데이터 베이스의 테이블 uid를 입력합니다.
                    intent.putExtra("imageUid", contentUidList.get(finalPosition));

                    // 이미지를 업로드한 해당 유저의 uid 값을 입력합니다.
                    intent.putExtra("destinationUid", contentDTOs.get(finalPosition).uid);

                    // 해당 정보를 가지고 CommentActivity로 이동합니다.
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            // 해당 ArrayList 크기 만큼 리사이클러 뷰의 아이템 갯수를 설정합니다.
            return contentDTOs.size();
        }

        // 좋아요를 누를 경우의 메소드입니다.
        private void favoriteEvent(int position, final String imageUid) {
            // 해당 이미지 넘버입니다.
            final int finalPosition = position;

            // images 테이블의 값들을 불러와 입력합니다.
            FirebaseDatabase.getInstance().getReference("images").child(contentUidList.get(position))
                    .runTransaction(new Transaction.Handler() {
                        // 데이터가 중복으로 쌓이는 것을 막기 위해 runTransaction 메소드를 사용합니다.
                        @Override
                        public Transaction.Result doTransaction(MutableData mutableData) {
                            ContentDTO contentDTO = mutableData.getValue(ContentDTO.class);

                            // 현재 유저의 uid입니다.
                            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                            if (contentDTO == null) {
                                return Transaction.success(mutableData);
                            }

                            if (contentDTO.favorites.containsKey(uid)) {
                                // 이미 눌렀을 경우 취소 기능을 하게 됩니다.
                                contentDTO.favoriteCount = contentDTO.favoriteCount - 1;
                                contentDTO.favorites.remove(uid);
                            } else {
                                // 좋아요를 추가합니다.
                                contentDTO.favoriteCount = contentDTO.favoriteCount + 1;
                                contentDTO.favorites.put(uid, true);
                                // 해당 유저에게 알람 메시지를 전송하게 됩니다.
                                favoriteAlarm(contentDTOs.get(finalPosition).uid, imageUid);
                            }

                            // 데이터를 전송합니다.
                            mutableData.setValue(contentDTO);
                            return Transaction.success(mutableData);
                        }

                        @Override
                        public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

                        }
                    });
        }

        // setValue 메소드를 통해 데이터를 전송합니다.
        public void favoriteAlarm(final String destinationUid, String imageUid) {
            AlarmDTO alarmDTO = new AlarmDTO();

            // 해당 유저 및 관련 정보를 db에 넣습니다.
            alarmDTO.destinationUid = destinationUid;
            alarmDTO.userId = user.getEmail();
            alarmDTO.uid = user.getUid();
            alarmDTO.kind = 0;
            alarmDTO.imageUid = imageUid;

            FirebaseDatabase.getInstance().getReference().child("alarms")
                    .push().setValue(alarmDTO).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    // favorite 프레그먼트에 입력이 완료되었을 경우
                    FirebaseDatabase.getInstance().getReference().child("pushToken")
                            .child(destinationUid).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                destinationUidToken = snapshot.getValue().toString();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    String messageSendGcm = user.getEmail() + getString(R.string.alarm_favorite);
                    MainActivity.sendGcm(destinationUidToken, "알림 메시지 입니다.", messageSendGcm);
                }
            });
        }

        private class CustomViewHolder extends RecyclerView.ViewHolder {
            private ItemDetailviewBinding binding;

            CustomViewHolder(View itemView) {
                super(itemView);
                binding = DataBindingUtil.bind(itemView);
            }

            ItemDetailviewBinding getBinding() {
                return binding;
            }
        }
    }
}
