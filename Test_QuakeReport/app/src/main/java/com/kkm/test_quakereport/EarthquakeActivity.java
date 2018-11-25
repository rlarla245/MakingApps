package com.kkm.test_quakereport;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class EarthquakeActivity extends AppCompatActivity {
    public RecyclerView recyclerView;
    public ArrayList<Earthquake> earthquakes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_earthquake);

        recyclerView = findViewById(R.id.earthquake_activity_recyclerview);

        // Create a fake list of earthquake locations.
        earthquakes.add(new Earthquake("7.2", "San Francisco", "Feb 2, 2018"));
        earthquakes.add(new Earthquake("7.2", "London", "Feb 2, 2018"));
        earthquakes.add(new Earthquake("7.2", "Tokyo", "Feb 2, 2018"));
        earthquakes.add(new Earthquake("7.2", "Mexico City", "Feb 2, 2018"));
        earthquakes.add(new Earthquake("7.2", "Moscow", "Feb 2, 2018"));
        earthquakes.add(new Earthquake("7.2", "Rio de Janeiro", "Feb 2, 2018"));
        earthquakes.add(new Earthquake("7.2", "Paris", "Feb 2, 2018"));

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new RecyclerviewAdapter());
    }

    public class RecyclerviewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_earthquake_item, parent, false);

            return new CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ((CustomViewHolder)holder).magnitude.setText(earthquakes.get(position).getMagnitude());
            ((CustomViewHolder)holder).location.setText(earthquakes.get(position).getLocation());
            ((CustomViewHolder)holder).occurDate.setText(earthquakes.get(position).getmDate());
        }

        @Override
        public int getItemCount() {
            return earthquakes.size();
        }

        private class CustomViewHolder extends RecyclerView.ViewHolder {
            public TextView magnitude, location, occurDate;
            public CustomViewHolder(View view) {
                super(view);
                magnitude = view.findViewById(R.id.earthquake_list_item_magnitude);
                location = view.findViewById(R.id.earthquake_list_item_location);
                occurDate = view.findViewById(R.id.earthquake_list_item_date);
            }
        }
    }
}
