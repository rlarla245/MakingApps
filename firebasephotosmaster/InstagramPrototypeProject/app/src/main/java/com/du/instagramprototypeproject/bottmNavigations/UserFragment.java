package com.du.instagramprototypeproject.bottmNavigations;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.du.instagramprototypeproject.LoginActivity;
import com.du.instagramprototypeproject.MainActivity;
import com.du.instagramprototypeproject.R;
import com.du.instagramprototypeproject.databinding.FragmentUserBinding;
import com.du.instagramprototypeproject.model.AlarmDTO;
import com.du.instagramprototypeproject.model.ContentDTO;
import com.du.instagramprototypeproject.model.FollowDTO;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.du.instagramprototypeproject.util.StatusCode.FRAGMENT_ARG;
import static com.du.instagramprototypeproject.util.StatusCode.PICK_PROFILE_FROM_ALBUM;

public class UserFragment extends Fragment {
    String destinationUidToken;

    FirebaseAuth.AuthStateListener authListener;

    // Data Binding
    private FragmentUserBinding binding;

    // Firebase
    private FirebaseAuth auth;
    private DatabaseReference dbRef;

    // private String destinationUid;
    private String uid;
    private String currentUserUid;

    // 팔로워, 팔로잉 레이아웃
    private LinearLayout followerList;
    private LinearLayout folloingList;

    // Activity - onActivityResult 메소드 끌어오기 위함
    private MainActivity activity;
    private int compareIndex = -1;

    // 프레그먼트가 실행될 경우 가장 첫 화면
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof Activity) {
            activity = (MainActivity) context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {

        // 해당 레이아웃 불러오기
        View view = inflater.inflate(R.layout.fragment_user, container, false);

        // Firebase
        auth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance().getReference();

        // 내 계정
        currentUserUid = auth.getCurrentUser().getUid();

        // 보고 싶은 사람의 계정
        uid = getArguments().getString("destinationUid");

        followerList = view.findViewById(R.id.userfragment_linearlayout_followerlist);
        folloingList = view.findViewById(R.id.userfragment_linearlayout_folloinglist);

        // 팔로워, 팔로우 리스트 생성
        followerList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment followerFragment = new FollowerList();
                Bundle followerBundle = new Bundle();
                // 보고 싶은 사람의 uid - 결국 내 uid를 의미
                followerBundle.putString("uid", uid);

                followerFragment.setArguments(followerBundle);

                getFragmentManager().beginTransaction().replace(R.id.main_content, followerFragment).commit();

                // getFragmentManager().beginTransaction().replace(R.id.main_content, followerFragment)
                //        .addToBackStack(FollowerList.class.getName()).commit();
            }
        });

        folloingList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment folloingFragment = new FollowingList();
                Bundle followerBundle = new Bundle();
                // 마찬가지
                followerBundle.putString("uid", uid);

                folloingFragment.setArguments(followerBundle);

                getFragmentManager().beginTransaction().replace(R.id.main_content, folloingFragment).commit();

                //getFragmentManager().beginTransaction().replace(R.id.main_content, folloingFragment)
                  //      .addToBackStack(FollowerList.class.getName()).commit();
            }
        });

        // Auth State Listener - 로그아웃 버튼이 있는 프레그먼트이기 때문에 auth 리스너를 통해
        // 로그아웃 되었을 경우 로그인 액티비티로 넘겨줘야 합니다.
        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                // 현재 유저를 지정합니다.
                FirebaseUser user = firebaseAuth.getCurrentUser();
                // User is signed out
                if (user == null) {
                    Toast.makeText(activity, getString(R.string.signout_success), Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(activity, LoginActivity.class);
                    activity.startActivity(intent);
                    activity.finish();
                }
            }
        };
        return view;
    }

    // UI 변경 작업
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        binding = FragmentUserBinding.bind(getView());

        // 이전 프레그먼트로부터 넘어온 값이 null 값이 아닐 경우
        if (getArguments() != null) {
            // uid를 상대방 계정으로 지정합니다.
            uid = getArguments().getString("destinationUid");

            // 본인 계정인 경우 -> 로그아웃, Toolbar 기본으로 설정
            if (uid != null && uid.equals(currentUserUid)) {
                binding.accountBtnFollowSignout.setText(getString(R.string.signout));
                binding.accountBtnFollowSignout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // 로그아웃 합니다.
                        signOut();
                    }
                });
                activity.setToolbarDefault();
            }

            // 본인 계정이 아닌 경우 -> 팔로우, Toolbar 설정 변경(뒤로 버튼, UserId 표시)
            else {
                binding.accountBtnFollowSignout.setText(getString(R.string.follow));
                binding.accountBtnFollowSignout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        requestFollow();
                    }
                });
                MainActivity.binding.toolbarTitleImage.setVisibility(View.GONE);
                // 백 키 생성
                MainActivity.binding.toolbarBtnBack.setVisibility(View.VISIBLE);
                MainActivity.binding.toolbarUsername.setVisibility(View.VISIBLE);

                // 해당 유저 이메일을 불러옵니다.
                MainActivity.binding.toolbarUsername.setText(getArguments().getString("userId"));

                MainActivity.binding.toolbarBtnBack.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 백 키를 누를 경우 홈 화면으로 이동합니다.
                        MainActivity.binding.bottomNavigation.setSelectedItemId(R.id.action_home);
                    }
                });
            }
        }

        // 프로필 이미지를 누를 경우 + 본인 계정일 시에만
        if (uid.equals(currentUserUid)) {
            binding.accountIvProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // 권한 요청 하는 부분
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        ActivityCompat.requestPermissions(activity, new String[]
                                {Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                    }

                    // 앨범 오픈
                    Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                    photoPickerIntent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                    activity.startActivityForResult(photoPickerIntent, PICK_PROFILE_FROM_ALBUM);
                }
            });
        }

        getProfileImage();
        getFollower();
        getFollowing();

        // 리사이클러 뷰 레이아웃 매니저 및 어댑터 설정하기
        binding.accountRecyclerview.setLayoutManager(new GridLayoutManager(activity, 3));
        UserFragmentRecyclerViewAdapter userFragmentRecyclerViewAdapter = new UserFragmentRecyclerViewAdapter();
        binding.accountRecyclerview.setAdapter(userFragmentRecyclerViewAdapter);
    }

    // 프로필 이미지 불러오는 메소드
    void getProfileImage() {
        // 여기서 uid는 상대방 uid, 데이터 베이스 내 프로필 이미지들을 불러온 뒤 상대방 uid에 맞는 데이터만 불러옵니다.
        dbRef.child("profileImages").child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            // 사진을 넣고 downloadUri로 설정한 값을 불러옵니다.
                            @SuppressWarnings("VisibleForTests")
                            String url = dataSnapshot.getValue().toString();

                            Glide.with(activity)
                                    .load(url)
                                    // 동그란 이미지로 불러옵니다.
                                    .apply(new RequestOptions().circleCrop()).into(binding.accountIvProfile);
                        }
                        else {
                            binding.accountIvProfile.setImageResource(R.mipmap.ic_launcher_foreground);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    // 팔로워 목록을 불러옵니다.
    void getFollower() {
        // 상대방 uid의 값들을 불러옵니다.
        dbRef.child("users").child(uid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // 팔로우 데이터 설정
                        FollowDTO followDTO = dataSnapshot.getValue(FollowDTO.class);

                        try {
                            binding.accountTvFollowerCount.setText(String.valueOf(followDTO.followerCount));

                            // 내가 팔로워 되어 있다면 팔로우를 취소할 수 있습니다.
                            if (followDTO.followers.containsKey(currentUserUid)) {
                                binding.accountBtnFollowSignout.setText(getString(R.string.follow_cancel));
                                binding.accountBtnFollowSignout
                                        .getBackground()
                                        .setColorFilter(ContextCompat.getColor(activity, R.color.colorLightGray), PorterDuff.Mode.MULTIPLY);
                            } else {
                                // 없을 경우 나를 팔로우합니다.
                                if (!uid.equals(currentUserUid)) {
                                    binding.accountBtnFollowSignout.setText(getString(R.string.follow));
                                    binding.accountBtnFollowSignout
                                            .getBackground().setColorFilter(null);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    // 팔로잉 리스트를 불러옵니다.
    void getFollowing() {
        // "users"에서 상대방 uid에 맞는 데이터들만 불러옵니다.
        dbRef.child("users").child(uid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        FollowDTO followDTO = dataSnapshot.getValue(FollowDTO.class);
                        try {
                            // 데이터 베이스 내 팔로우 숫자를 불러옵니다.
                            binding.accountTvFollowingCount.setText(String.valueOf(followDTO.followingCount));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    // 팔로우 하기 - 좋아요 코드와 매우 흡사함
    public void requestFollow() {
        // 내 계정과 상대방 계정 데이터를 둘 다 조정해줘야 합니다.
        dbRef.child("users").child(currentUserUid)
                // 중복 기입 방지를 위해 runTransaction 메소드를 활용합니다.
                .runTransaction(new Transaction.Handler() {
                    @Override
                    public Transaction.Result doTransaction(MutableData mutableData) {
                        FollowDTO followDTO = mutableData.getValue(FollowDTO.class);

                        // 나를 팔로우한 계정이 없을 경우 새로 생성합니다.
                        if (followDTO == null) {
                            // 새로 생성합니다.
                            followDTO = new FollowDTO();
                            followDTO.followingCount = 1;
                            followDTO.followings.put(uid, true);
                            followDTO.userId = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                            mutableData.setValue(followDTO);

                            return Transaction.success(mutableData);
                        }

                        // 상대가 나를 팔로우 하고 있을 경우 팔로우를 취소해야 합니다.
                        if (followDTO.followings.containsKey(uid)) {
                            followDTO.followingCount = followDTO.followingCount - 1;
                            followDTO.followings.remove(uid);
                        }

                        // 상대가 나를 팔로우 하고 있지 않을 경우 팔로우 합니다.
                        else {
                            followDTO.followingCount = followDTO.followingCount + 1;
                            followDTO.followings.put(uid, true);

                            // 팔로우 했다는 알람을 보내줍니다.
                            followerAlarm(uid);
                        }

                        // setValue이기 때문에 데이터를 '덮어 씌웁니다.'
                        mutableData.setValue(followDTO);

                        return Transaction.success(mutableData);
                    }

                    @Override
                    public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

                    }
                });

        // 상대방의 데이터를 뒤집니다.
        // 마찬가지로 중복 기입을 막기 위해 runTransaction() 메소드를 활용합니다.
        dbRef.child("users").child(uid)
                .runTransaction(new Transaction.Handler() {
                    @Override
                    public Transaction.Result doTransaction(MutableData mutableData) {
                        FollowDTO followDTO = mutableData.getValue(FollowDTO.class);

                        // 상대방의 팔로우가 없을 경우 데이터를 생성합니다.
                        if (followDTO == null) {
                            followDTO = new FollowDTO();
                            followDTO.userId = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                            followDTO.followerCount = 1;
                            followDTO.followers.put(currentUserUid, true);
                            mutableData.setValue(followDTO);

                            return Transaction.success(mutableData);
                        }

                        // 내가 상대방을 팔로우하고 있을 경우 취소해줍니다.
                        if (followDTO.followers.containsKey(currentUserUid)) {
                            followDTO.followerCount = followDTO.followerCount - 1;
                            followDTO.followers.remove(currentUserUid);
                        }

                        // 내가 상대방을 팔로우하지 않는다면 팔로우해줍니다.
                        else {
                            followDTO.followerCount = followDTO.followerCount + 1;
                            followDTO.followers.put(currentUserUid, true);
                        }

                        // 데이터를 뒤집어 씌웁니다.
                        mutableData.setValue(followDTO);

                        return Transaction.success(mutableData);
                    }

                    @Override
                    public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

                    }
                });
    }

    // 푸시 알람 보내기
    private void followerAlarm(final String destinationUid) {
        AlarmDTO alarmDTO = new AlarmDTO();

        // 상대방 uid
        alarmDTO.destinationUid = destinationUid;

        // 팔로우를 누른 내 이메일, id
        alarmDTO.userId = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        alarmDTO.uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // int 숫자에 따라 목적이 변경됩니다.
        alarmDTO.kind = 2;

        // 데이터베이스에 입력됩니다.
        dbRef.child("alarms").push().setValue(alarmDTO).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                // 팔로우 했을 경우 푸시 메시지를 제공합니다.
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

                String message = FirebaseAuth.getInstance().getCurrentUser().getEmail() + getString(R.string.alarm_follow);
                MainActivity.sendGcm(destinationUidToken, "알림 메시지입니다.", message);
            }
        });
    }

    /**
     * Sign Out
     */

    // 로그아웃 메소드 - 확인 요망
    @SuppressLint("RestrictedApi")
    private void signOut() {
        // get Auth Provider
        if (auth.getCurrentUser().getProviders() != null &&
                auth.getCurrentUser().getProviders().get(0).equals("google.com")) {
            googleSignOut();
        } else {
            auth.signOut();
        }
    }

    // 구글 로그아웃
    private void googleSignOut() {
        // GoogleSignInOptions 개체 구성
        // 토큰, 이메일 값 불러오기
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // api 클라이언트를 통해 데이터 불러오기
        final GoogleApiClient googleApiClient = new GoogleApiClient.Builder(activity)
                .enableAutoManage((FragmentActivity) activity, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        // hideProgressDialog();
                        Toast.makeText(activity, getString(R.string.signout_fail), Toast.LENGTH_SHORT).show();
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        googleApiClient.connect();
        googleApiClient.registerConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(@Nullable Bundle bundle) {
                auth.signOut();
                if (googleApiClient.isConnected()) {
                    Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(@NonNull Status status) {
                            if (!status.isSuccess()) {
                                // hideProgressDialog();
                                Toast.makeText(activity, getString(R.string.signout_fail), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }

            @Override
            public void onConnectionSuspended(int i) {
                // hideProgressDialog();
                Toast.makeText(activity, getString(R.string.signout_fail), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);
    }

    /* ------------------------------------------------------------------------------------------ */

    @Override
    public void onStop() {
        super.onStop();
        auth.removeAuthStateListener(authListener);
    }

    private class UserFragmentRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private ArrayList<ContentDTO> contentDTOs;
        private ArrayList<ContentDTO> wholeContentsDTOs;
        private ArrayList<String> contentsUids;
        private ArrayList<String> wholeContentsUids;

        UserFragmentRecyclerViewAdapter() {
            contentDTOs = new ArrayList<>();
            contentsUids = new ArrayList<>();

            // 나의 사진만 찾기, uid 기준으로 정렬 시키고 "uid"와 일치하는 데이터들만 불러옵니다.
            dbRef.child("images").orderByChild("uid").equalTo(uid)
                    .addValueEventListener(new ValueEventListener() {

                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            contentDTOs.clear();
                            contentsUids.clear();

                            // 반복문을 활용하여 데이터를 불러옵니다.
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                contentDTOs.add(snapshot.getValue(ContentDTO.class));
                                contentsUids.add(snapshot.getKey());
                            }

                            // 전체 '게시물' 카운터를 세기 위해 size() 메소드 활용
                            binding.accountTvPostCount.setText(String.valueOf(contentDTOs.size()));
                            notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // 디바이스 화면을 3분할 해서 폭 값으로 설정합니다.
            int width = getResources().getDisplayMetrics().widthPixels / 3;

            // 다른 표현 방식 존재
            ImageView imageView = new ImageView(parent.getContext());
            imageView.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, width));

            return new CustomViewHolder(imageView);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            // 격자 무늬 내 아이템들을 사진으로 변환합니다.
            Glide.with(holder.itemView.getContext())
                    .load(contentDTOs.get(position).imageUrl)
                    .apply(new RequestOptions().centerCrop())
                    .into(((CustomViewHolder) holder).imageView);

            // 사진을 누를 경우 댓글 액티비티로 이동합니다.
            ((CustomViewHolder) holder).imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent intent = new Intent(v.getContext(), CommentActivity.class);
                        // 데이터 베이스의 테이블 uid를 입력합니다.
                        intent.putExtra("imageUid", contentsUids.get(position));

                        // 해당 유저의 uid 값을 입력합니다.
                        intent.putExtra("destinationUid", contentDTOs.get(position).uid);
                        startActivity(intent);
                    } catch (Exception e) {
                        return;
                    }
                }
            });

            // 길게 누를 경우 삭제합니다.
            ((CustomViewHolder) holder).imageView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (uid.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                        builder.setTitle("삭제")
                                .setMessage("삭제하시겠습니까?")
                                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        wholeContentsDTOs = new ArrayList<>();
                                        wholeContentsUids = new ArrayList<>();

                                        dbRef.child("images").addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                wholeContentsDTOs.clear();
                                                wholeContentsUids.clear();

                                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                    // 총 콘텐츠의 인덱스 값
                                                    compareIndex++;
                                                    wholeContentsDTOs.add(snapshot.getValue(ContentDTO.class));
                                                    wholeContentsUids.add(snapshot.getKey());

                                                    // 에러 발생해서 이런 식으로 처리.
                                                    try {
                                                        if (uid.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                                && contentDTOs.get(position).imageUrl.equals(wholeContentsDTOs.get(compareIndex).imageUrl)) {
                                                            dbRef.child("images").child(wholeContentsUids.get(compareIndex)).removeValue()
                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void aVoid) {
                                                                            Toast.makeText(activity, "삭제되었습니다.", Toast.LENGTH_SHORT).show();
                                                                            // 갱신이 되지 않아 재접속 역할을 하게 됩니다.
                                                                            Fragment fragment = new UserFragment();

                                                                            // 해당 유저의 id와 이메일을 담아 유저 프레그먼트로 정보를 전송합니다.
                                                                            Bundle bundle = new Bundle();
                                                                            bundle.putString("destinationUid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                                                                            bundle.putString("userId", FirebaseAuth.getInstance().getCurrentUser().getEmail());
                                                                            bundle.putInt(FRAGMENT_ARG, 5);

                                                                            fragment.setArguments(bundle);

                                                                            getActivity().getFragmentManager().beginTransaction()
                                                                                    .replace(R.id.main_content, fragment)
                                                                                    .commit();
                                                                        }
                                                                    }).addOnFailureListener(new OnFailureListener() {

                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    Toast.makeText(activity, "삭제에 실패했습니다.", Toast.LENGTH_SHORT).show();
                                                                }
                                                            });
                                                        }

                                                    } catch (IndexOutOfBoundsException e) {
                                                        return;
                                                    }
                                                }
                                                notifyDataSetChanged();
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
                                        dialog.cancel();
                                    }
                                });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    }
                    return false;
                }
            });
        }

        @Override
        public int getItemCount() {
            return contentDTOs.size();
        }

        // RecyclerView Adapter - View Holder
        private class CustomViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;

            CustomViewHolder(ImageView imageView) {
                super(imageView);
                this.imageView = imageView;
            }
        }
    }
}
