package com.example.user.uber;


/**
 * Name of the module: Bus2.java
 *
 * Date on which this module was created: 8/3/2017
 *
 * Author's Name: Shreyanshi Bharadia
 *
 * Modification History: Saurabh Bazari 10/3/17
 *                     : Samyak Jain 14/3/17
 *
 * Synopsis of the module: This module shows the route of the bus and its arrival time at different stops.
 *
 */

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class Bus2  extends AppCompatActivity {

    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.from_campus_bus_list);

        listView = (ListView) findViewById(R.id.listv);
        String[] values = new String[]{"Subansiri","Core 2","Hospital"};

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, values);

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (position == 0) {
                    Intent myIntent = new Intent(view.getContext(), BusStop1.class);
                    startActivityForResult(myIntent, 0);
                }

                if (position == 1) {
                    Intent myIntent = new Intent(view.getContext(), BusStop2.class);
                    startActivityForResult(myIntent, 0);
                }

                if (position == 2) {
                    Intent myIntent = new Intent(view.getContext(), BusStop3.class);
                    startActivityForResult(myIntent, 0);
                }

            }
        });
    }


}


