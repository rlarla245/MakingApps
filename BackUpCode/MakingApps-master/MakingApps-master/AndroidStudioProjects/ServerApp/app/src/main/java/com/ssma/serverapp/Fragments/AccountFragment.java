package com.ssma.serverapp.Fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ssma.serverapp.Model.UserModel;
import com.ssma.serverapp.R;

import java.util.HashMap;
import java.util.Map;

public class AccountFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.accountfragment, container, false);

        // 변수 호출
        final ImageView imageView_profileimage = (ImageView)view.findViewById(R.id.accountfragment_imageview_userprofileimage);
        final TextView userName = (TextView)view.findViewById(R.id.accountfragment_username);
        final TextView userPhoneNumber = (TextView)view.findViewById(R.id.accountfragment_textview_userphonenumber);
        final TextView textView_message = (TextView)view.findViewById(R.id.accountfragment_textview_statusmessage);

        // 내 uid
        final String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // 데이터베이스 접근
        FirebaseDatabase.getInstance().getReference().child("users").child(myUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserModel userModel = dataSnapshot.getValue(UserModel.class);

                Glide.with(view.getContext()).load(userModel.profileImageUri)
                        .apply(new RequestOptions().circleCrop()).into(imageView_profileimage);

                userName.setText(userModel.userName);

                // 휴대폰 번호 출력
                String a = new String(userModel.userPhoneNumber);
                StringBuffer phoneNumber = new StringBuffer(a);

                if (phoneNumber.length() == 11) {
                    phoneNumber.insert(3, '-');
                    phoneNumber.insert(8, '-');

                    userPhoneNumber.setText(phoneNumber);
                }

                else{
                    userPhoneNumber.setText(phoneNumber);
                }

                if (userModel.status_message == null) {
                    textView_message.setText("상태메시지를 입력해주세요:)");
                }

                else {
                    textView_message.setText(userModel.status_message);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        ImageButton imageButton = (ImageButton)view.findViewById(R.id.accountfragment_imagebutton_changestatusmessage);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(view.getContext());
            }
        });

        return view;
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
