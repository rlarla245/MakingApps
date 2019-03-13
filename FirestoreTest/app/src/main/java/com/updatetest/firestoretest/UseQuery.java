package com.updatetest.firestoretest;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.updatetest.firestoretest.Models.UserModel;

import java.util.ArrayList;
import java.util.List;

public class UseQuery extends AppCompatActivity {
    List<UserModel> priUserModles = new ArrayList<>();
    List<UserModel> userModels = new ArrayList<>();
    List<UserModel> listForFilter = new ArrayList<>();
    String major = null;
    String age = null;

    RecyclerView recyclerView;

    EditText editText_InputString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_use_query);

        recyclerView = findViewById(R.id.usequeryactivity_recyclerview_readdata);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        UseQueryRecyclerViewAdapter useQueryRecyclerViewAdapter = new UseQueryRecyclerViewAdapter();
        recyclerView.setAdapter(useQueryRecyclerViewAdapter);

        // 텍스트 변경에 따라 지속적으로 반응합니다.
        editText_InputString = findViewById(R.id.usequeryactivity_edittext_inputstring);
        editText_InputString.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                searchList(editable.toString());
            }
        });

        Spinner spinner_major = findViewById(R.id.usequeryactivity_spinner_major);
        spinner_major.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                major = adapterView.getItemAtPosition(i).toString();
                listBySpinner();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        Spinner spinner_age = findViewById(R.id.usequeryactivity_spinner_age);
        spinner_age.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                age = adapterView.getItemAtPosition(i).toString();
                listBySpinner();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    void listBySpinner() {
        if (major != null && age != null) {
            FirebaseFirestore.getInstance().collection("users").whereEqualTo("major", major)
                    .whereGreaterThanOrEqualTo("age", Integer.parseInt(age))
                    .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
               userModels.clear();
               for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()) {
                   UserModel userModel = snapshot.toObject(UserModel.class);
                   userModels.add(userModel);
                   listForFilter.add(userModel);
               }
               recyclerView.getAdapter().notifyDataSetChanged();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(UseQuery.this, "데이터를 불러오지 못했습니다.", Toast.LENGTH_SHORT).show();
                    System.out.println(e.getMessage());
                    finish();
                }
            });
        }
    }

    void searchList(String filterString) {
        List<UserModel> fileterList = new ArrayList<>();
        fileterList.clear();

        List<UserModel> orFilterList = new ArrayList<>();
        orFilterList.clear();

        // or 쿼리를 살린 리스트
        for (UserModel userModel : listForFilter) {
            if (checkCharacter(userModel.name, filterString) && !orFilterList.contains(userModel)) {
                orFilterList.add(userModel);
            }

            /*
            if (userModel.name.contains(filterString)) {
                fileterList.add(userModel);
            }
            */
        }
        userModels.clear();
        userModels.addAll(orFilterList);

        recyclerView.getAdapter().notifyDataSetChanged();
    }

    Boolean checkCharacter(String name, String searchString) {
        String[] array = searchString.split(" ");
        for (String a : array) {
            if (name.contains(a)) {
                return true;
            }
        }
        return false;
    }

    class UseQueryRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        public UseQueryRecyclerViewAdapter() {
            FirebaseFirestore.getInstance().collection("users").get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            priUserModles.clear();
                            for (DocumentSnapshot snapshots : queryDocumentSnapshots.getDocuments()) {
                                UserModel userModel = snapshots.toObject(UserModel.class);
                                priUserModles.add(userModel);
                            }
                            notifyDataSetChanged();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(UseQuery.this, "데이터를 불러오지 못했습니다.", Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.snapshot_readdb_item, parent, false);

            return new CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ((CustomViewHolder)holder).name.setText(userModels.get(position).name);
            ((CustomViewHolder)holder).major.setText(userModels.get(position).major);
            ((CustomViewHolder)holder).age.setText(String.valueOf(userModels.get(position).age));
        }

        @Override
        public int getItemCount() {
            return userModels.size();
        }

        private class CustomViewHolder extends RecyclerView.ViewHolder {
            TextView name;
            TextView age;
            TextView major;
            public CustomViewHolder(View view) {
                super(view);
                name = view.findViewById(R.id.snapshotReadDBItem_textview_name);
                age = view.findViewById(R.id.snapshotReadDBItem_textview_age);
                major = view.findViewById(R.id.snapshotReadDBItem_textview_major);
            }
        }
    }
}
