package com.updatetest.firestoretest;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.updatetest.firestoretest.Models.UserModel;

import java.util.ArrayList;
import java.util.List;

public class OrderByChildLimit extends AppCompatActivity {
    Query.Direction direction = null;
    String startAge = null;
    String endAge = null;
    String limit = null;

    List<UserModel> userModels = new ArrayList<>();

    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_by_child_limit);

        recyclerView = findViewById(R.id.orderbychile_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new OrderByChildLimitAdapter());

        // 위젯 호출
        Spinner spinner_Direction = findViewById(R.id.orderbychile_spinner_direction);
        Spinner spinner_StartAge = findViewById(R.id.orderbychile_spinner_startat);
        Spinner spinner_EndAge = findViewById(R.id.orderbychile_spinner_endat);
        Spinner spinner_Limit = findViewById(R.id.orderbychile_spinner_limit);

        spinner_Direction.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String string_SelectDirection = adapterView.getItemAtPosition(i).toString();
                if (string_SelectDirection.equals("ASC")) {
                    direction = Query.Direction.ASCENDING;
                    getListFromDB();
                }

                if (string_SelectDirection.equals("DESC")) {
                    direction = Query.Direction.DESCENDING;
                    getListFromDB();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinner_StartAge.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                startAge = adapterView.getItemAtPosition(i).toString();
                getListFromDB();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinner_EndAge.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                endAge = adapterView.getItemAtPosition(i).toString();
                getListFromDB();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinner_Limit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                limit = adapterView.getItemAtPosition(i).toString();
                getListFromDB();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    void getListFromDB() {
        if (direction != null && startAge != null && endAge != null && limit != null) {
            FirebaseFirestore.getInstance().collection("users").orderBy("age", direction)
                    .startAt(Integer.parseInt(startAge))
                    .endAt(Integer.parseInt(endAge))
                    .limit(Long.parseLong(limit))
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            userModels.clear();
                            for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()) {
                                UserModel userModel = snapshot.toObject(UserModel.class);
                                userModels.add(userModel);
                            }
                            recyclerView.getAdapter().notifyDataSetChanged();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(OrderByChildLimit.this, "데이터를 불러오지 못했습니다.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    class OrderByChildLimitAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        public OrderByChildLimitAdapter() {
            getListFromDB();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.get_readdb_item, parent, false);

            return new CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ((CustomViewHolder)holder).name.setText(userModels.get(position).name);
            ((CustomViewHolder)holder).age.setText(String.valueOf(userModels.get(position).age));
            ((CustomViewHolder)holder).major.setText(userModels.get(position).major);
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
                name = view.findViewById(R.id.getReadDBItem_textview_name);
                age = view.findViewById(R.id.getReadDBItem_textview_age);
                major = view.findViewById(R.id.getReadDBItem_textview_major);
            }
        }
    }
}
