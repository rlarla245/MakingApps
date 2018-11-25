package io.github.stack07142.firebase_photos.tabbar;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
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

import io.github.stack07142.firebase_photos.LoginActivity;
import io.github.stack07142.firebase_photos.MainActivity;
import io.github.stack07142.firebase_photos.R;
import io.github.stack07142.firebase_photos.databinding.FragmentUserBinding;
import io.github.stack07142.firebase_photos.model.AlarmDTO;
import io.github.stack07142.firebase_photos.model.ContentDTO;
import io.github.stack07142.firebase_photos.model.FollowDTO;

import static io.github.stack07142.firebase_photos.util.StatusCode.PICK_PROFILE_FROM_ALBUM;

public class UserFragment extends Fragment {

    // Data Binding
    private FragmentUserBinding binding;

    // Firebase
    private FirebaseAuth auth;
    private DatabaseReference dbRef;
    FirebaseAuth.AuthStateListener authListener;

    //private String destinationUid;
    private String uid;
    private String currentUserUid;

    // Activity
    private MainActivity activity;

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

        View view = inflater.inflate(R.layout.fragment_user, container, false);

        // Firebase
        auth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance().getReference();

        // 내 계정
        currentUserUid = auth.getCurrentUser().getUid();

        // 보고 싶은 사람의 계정
        uid = getArguments().getString("destinationUid");

        // Auth State Listener
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

        /*
         * UI Setting
         */

        // 넘어온 값이 null 값이 아닐 경우
        if (getArguments() != null) {
            // uid를 상대방 계정으로 지정합니다.
            uid = getArguments().getString("destinationUid");

            // 본인 계정인 경우 -> 로그아웃, Toolbar 기본으로 설정
            if (uid != null && uid.equals(currentUserUid)) {
                binding.accountBtnFollowSignout.setText(getString(R.string.signout));
                binding.accountBtnFollowSignout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
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
                activity.getBinding().toolbarTitleImage.setVisibility(View.GONE);
                // 백 키 생성
                activity.getBinding().toolbarBtnBack.setVisibility(View.VISIBLE);
                activity.getBinding().toolbarUsername.setVisibility(View.VISIBLE);

                // 해당 유저 이메일을 불러옵니다.
                activity.getBinding().toolbarUsername.setText(getArguments().getString("userId"));

                activity.getBinding().toolbarBtnBack.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        activity.getBinding().bottomNavigation.setSelectedItemId(R.id.action_home);
                    }
                });
            }
        }

        // Profile Image Click Listener
        binding.accountIvProfile.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                //권한 요청 하는 부분
                ActivityCompat.requestPermissions(activity, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

                //앨범 오픈
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                activity.startActivityForResult(photoPickerIntent, PICK_PROFILE_FROM_ALBUM);
            }
        });

        /*
         * Get Data
         */
        getProfileImage();
        getFollower();
        getFollowing();

        // Recycler View
        binding.accountRecyclerview.setLayoutManager(new GridLayoutManager(activity, 3));
        binding.accountRecyclerview.setAdapter(new UserFragmentRecyclerViewAdapter());
    }

    /* ------------------------------------------------------------------------------------------ */

    /**
     * RecyclerView Adapter
     */

    private class UserFragmentRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private ArrayList<ContentDTO> contentDTOs;

        UserFragmentRecyclerViewAdapter() {

            contentDTOs = new ArrayList<>();

            // 나의 사진만 찾기
            dbRef.child("images").orderByChild("uid").equalTo(uid).addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    contentDTOs.clear();

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                        ContentDTO content = snapshot.getValue(ContentDTO.class);

                        contentDTOs.add(snapshot.getValue(ContentDTO.class));
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

            ImageView imageView = new ImageView(parent.getContext());
            imageView.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, width));

            return new CustomViewHolder(imageView);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            // 격자 무늬 내 아이템들을 사진으로 변환합니다.
            Glide.with(holder.itemView.getContext())
                    .load(contentDTOs.get(position).imageUrl)
                    .apply(new RequestOptions().centerCrop())
                    .into(((CustomViewHolder) holder).imageView);
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

    /* ------------------------------------------------------------------------------------------ */

    /**
     * Get - Profile Image, Follwer Count, Following Count, (Post Count <- ContentsDTO's Size)
     */

    void getProfileImage() {

        // 여기서 uid는 상대방 uid
        dbRef.child("profileImages").child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (dataSnapshot.exists()) {

                            @SuppressWarnings("VisibleForTests")
                            String url = dataSnapshot.getValue().toString();
                            Glide.with(activity)
                                    .load(url)
                                    // 동그란 이미지로 불러옵니다.
                                    .apply(new RequestOptions().circleCrop()).into(binding.accountIvProfile);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }

    void getFollower() {

        // 상대방 uid의 값들을 불러옵니다.
        dbRef.child("users").child(uid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
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
                                // 없을 경우 팔로우합니다.
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

    void getFollowing() {

        dbRef.child("users").child(uid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        FollowDTO followDTO = dataSnapshot.getValue(FollowDTO.class);
                        try {
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

    /* ------------------------------------------------------------------------------------------ */

    /**
     * Request Follower, Follow Alarm
     */

    // 팔로우 하기
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

                        // Set value and report transaction success
                        mutableData.setValue(followDTO);

                        return Transaction.success(mutableData);
                    }

                    @Override
                    public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                    }
                });
    }

    private void followerAlarm(String destinationUid) {

        AlarmDTO alarmDTO = new AlarmDTO();

        // 상대방 uid
        alarmDTO.destinationUid = destinationUid;
        // 팔로우를 누른 내 이메일, id
        alarmDTO.userId = FirebaseAuth.getInstance().getCurrentUser()f.getEmail();
        alarmDTO.uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        // int 숫자에 따라 목적이 변경됩니다.
        alarmDTO.kind = 2;

        // 데이터베이스에 입력됩니다.
        dbRef.child("alarms").push().setValue(alarmDTO);
    }


    /* ------------------------------------------------------------------------------------------ */

    /**
     * Sign Out
     */

    private void signOut() {

        // get Auth Provider
        if (auth.getCurrentUser().getProviders() != null && auth.getCurrentUser().getProviders().get(0).equals("google.com")) {

            googleSignOut();
        } else {

            auth.signOut();
        }
    }

    private void googleSignOut() {

        // GoogleSignInOptions 개체 구성
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Build a GoogleApiClient with access to the Google Sign-In API and the options specified by gso.
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

    /* ------------------------------------------------------------------------------------------ */

    /**
     * Auth Status Listener
     */

    @Override
    public void onStart() {
        super.onStart();

        auth.addAuthStateListener(authListener);
    }

    @Override
    public void onStop() {
        super.onStop();

        auth.removeAuthStateListener(authListener);
    }
}
