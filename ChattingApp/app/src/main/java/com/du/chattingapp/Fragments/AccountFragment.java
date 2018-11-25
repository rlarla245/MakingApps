package com.du.chattingapp.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.du.chattingapp.Models.UserModel;
import com.du.chattingapp.R;
import com.du.chattingapp.SignUpActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

public class AccountFragment extends Fragment {
    private static final int PICK_IMAGE_FROM_ALBUM = 0;
    private FirebaseStorage storage;

    // 변수 호출
    ImageView imageView_profileimage;
    TextView userName;
    TextView userPhoneNumber;
    TextView textView_message;

    public Uri profileImageUri;
    public String uid;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.account_fragment, container, false);

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        storage = FirebaseStorage.getInstance();

        // 변수 호출
        imageView_profileimage = (ImageView)view.findViewById(R.id.accountfragment_imageview_userprofileimage);
        userName = (TextView)view.findViewById(R.id.accountfragment_username);
        userPhoneNumber = (TextView)view.findViewById(R.id.accountfragment_textview_userphonenumber);
        textView_message = (TextView)view.findViewById(R.id.accountfragment_textview_statusmessage);

        // 내 uid
        final String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // 사진 누를 경우 수정하게 해줍시다.
        imageView_profileimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 앨범 오픈
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                startActivityForResult(photoPickerIntent, PICK_IMAGE_FROM_ALBUM);
            }
        });

        final ImageButton imageButton = (ImageButton)view.findViewById(R.id.accountfragment_imagebutton_changestatusmessage);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(view.getContext());
            }
        });

        // 데이터베이스 접근
        FirebaseDatabase.getInstance().getReference().child("users").child(myUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserModel userModel = dataSnapshot.getValue(UserModel.class);

                Glide.with(view.getContext()).load(userModel.profileImageUri)
                        .apply(new RequestOptions().circleCrop()).into(imageView_profileimage);
                imageView_profileimage.setVisibility(View.VISIBLE);

                userName.setText(userModel.userName);
                userName.setVisibility(View.VISIBLE);
                imageButton.setVisibility(View.VISIBLE);

                // 휴대폰 번호 출력
                String a = new String(userModel.userPhoneNumber);
                StringBuffer phoneNumber = new StringBuffer(a);

                if (phoneNumber.length() == 11) {
                    phoneNumber.insert(3, '-');
                    phoneNumber.insert(8, '-');

                    userPhoneNumber.setText(phoneNumber);
                    userPhoneNumber.setVisibility(View.VISIBLE);
                }

                else{
                    userPhoneNumber.setText(phoneNumber);
                    userPhoneNumber.setVisibility(View.VISIBLE);
                }

                if (userModel.status_message == null) {
                    textView_message.setText("상태메시지를 입력해주세요:)");
                    textView_message.setVisibility(View.VISIBLE);
                }

                else {
                    textView_message.setText(userModel.status_message);
                    textView_message.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        // 권한 받고 갑시다.
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_FROM_ALBUM && resultCode == RESULT_OK) {
            StorageReference storageRef
                    = storage.getReferenceFromUrl("gs://chattingapp-aa575.appspot.com");

            // 4. 다음과 같이 정상적으로 경로를 받아옵니다.
            Uri file = Uri.fromFile(new File(getPath(data.getData())));
            StorageReference riversRef
                    = storageRef.child("images/"+file.getLastPathSegment());
            UploadTask uploadTask = riversRef.putFile(file);

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    //
                }
            }).addOnSuccessListener
                    (new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Uri downloadUrl = taskSnapshot.getDownloadUrl();

                            // 해시 맵을 생성해 해당 유저 - 다운로드 이미지 uri를 지정해줍니다.
                            Map<String, Object> imageMap = new HashMap<String, Object>();

                            // 해시 맵에 key 값으로 유저를, value 값으로 해당 이미지의 다운로드 경로를 입력합니다.
                            imageMap.put("profileImageUri", downloadUrl.toString());

                            // 업로드 할 때마다 value 값의 이미지 uri가 수정(updateChrldren)되므로 유저 아이콘을 수정할 수 있습니다.
                            // 대신 하나의 이미지만 업로드가 될 수 밖에 없습니다.
                            FirebaseDatabase.getInstance().getReference().child("users").child(uid).updateChildren(imageMap);
                        }
                    });


            /*
            // 회원가입 시 사진을 선택하면 가운데 뷰를 바꿔줍니다.
            Glide.with(getActivity()).load(data.getData())
                    .apply(new RequestOptions().circleCrop()).into(imageView_profileimage);

            // 원래 getPath() 메소드를 만드는게 편하긴 함
            String[] proj = {MediaStore.Images.Media.DATA};
            CursorLoader cursorLoader = new CursorLoader
                    (getActivity(), data.getData(), proj, null, null, null);
            Cursor cursor = cursorLoader.loadInBackground();
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();

            // 이미지 경로 == return cursor.getString(index);
            String photoPath = cursor.getString(column_index);

            // 앨범에서 고른 사진을 새로운 파일로 생성합니다.
            File f = new File(photoPath);

            FirebaseStorage
                    .getInstance()
                    .getReference()
                    // 앨범을 통해 열린 이미지가
                    // 해당 스토리지의 "userProfileImages"에 이미지들을 담게 됩니다.
                    .child("userImages")
                    // 그 이미지 데이터 테이블의 '내' uid 데이터베이스에 담깁니다.
                    .child(uid)
                    // 해당 파일의 이미지 경로를 서버에 입력합니다(Url = 경로);
                    .putFile(Uri.fromFile(f))
                    // 성공 시
                    .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            // 다운로드 uri(링크)를 지정합니다. Firebase 최신 버전에서는 사용할 수 없습니다.
                            @SuppressWarnings("VisibleForTests")
                            String url = task.getResult().getDownloadUrl().toString();

                            // 해시 맵을 생성해 해당 유저 - 다운로드 이미지 uri를 지정해줍니다.
                            Map<String, Object> imageMap = new HashMap<String, Object>();

                            // 해시 맵에 key 값으로 유저를, value 값으로 해당 이미지의 다운로드 경로를 입력합니다.
                            imageMap.put("profileImageUri", url);

                            // 업로드 할 때마다 value 값의 이미지 uri가 수정(updateChrldren)되므로 유저 아이콘을 수정할 수 있습니다.
                            // 대신 하나의 이미지만 업로드가 될 수 밖에 없습니다.
                            FirebaseDatabase.getInstance().getReference().child("users").child(uid).updateChildren(imageMap);
                        }
                    });
            */
        }
    }

    // 경로를 지정해주는 메소드입니다.
    public String getPath(Uri uri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        CursorLoader cursorLoader = new CursorLoader(getActivity(), uri, proj, null, null, null);

        Cursor cursor = cursorLoader.loadInBackground();
        int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        cursor.moveToFirst();
        return cursor.getString(index);
    }

    void showDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.dialog_comment,null);

        final EditText editText = (EditText)view.findViewById(R.id.comment_dialog_edittext);

        builder.setView(view).setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Map<String,Object> stringObjectMap = new HashMap<>();
                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                stringObjectMap.put("status_message", editText.getText().toString());
                FirebaseDatabase.getInstance().getReference().child("users").child(uid).updateChildren(stringObjectMap);

            }

        }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.show();
    }
}
