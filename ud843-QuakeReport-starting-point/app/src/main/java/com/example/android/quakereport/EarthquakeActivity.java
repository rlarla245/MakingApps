package com.example.android.quakereport;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class EarthquakeActivity extends AppCompatActivity {

    public static final String LOG_TAG = EarthquakeActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.earthquake_activity);

        // Create a fake list of earthquake locations.
        ArrayList<Earthquake> earthquakes = new ArrayList<>();
        earthquakes.add(new Earthquake("7.2", "San Francisco", "Feb 2, 2018"));
        earthquakes.add(new Earthquake("7.2", "London", "Feb 2, 2018"));
        earthquakes.add(new Earthquake("7.2", "Tokyo", "Feb 2, 2018"));
        earthquakes.add(new Earthquake("7.2", "Mexico City", "Feb 2, 2018"));
        earthquakes.add(new Earthquake("7.2", "Moscow", "Feb 2, 2018"));
        earthquakes.add(new Earthquake("7.2", "Rio de Janeiro", "Feb 2, 2018"));
        earthquakes.add(new Earthquake("7.2", "Paris", "Feb 2, 2018"));

        // Find a reference to the {@link ListView} in the layout
        ListView earthquakeListView = (ListView) findViewById(R.id.list);

        // Create a new {@link ArrayAdapter} of earthquakes
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_list_item_1, earthquakes);

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        earthquakeListView.setAdapter(adapter);

    }
}
