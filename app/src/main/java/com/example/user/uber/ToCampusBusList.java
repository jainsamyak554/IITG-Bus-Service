package com.example.user.uber;


/**
 * Name of the module: ToCampusBusList.java
 *
 * Date on which this module was created: 14/3/2017
 *
 * Author's Name: Samyak Jain 25/3/17
 *
 * Modification History: Shreyanshi Bharadia 27/3/17
 *                     : Saurabh Bazari 29/3/17
 *
 * Synopsis of the module: Displays all the buses going from campus to city.
 *
 */


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ToCampusBusList  extends AppCompatActivity {
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.to_campus_bus_list);

        listView = (ListView) findViewById(R.id.listv);
        String[] values = new String[]{"1. 6:30am-7:30am","2. 7:45am-8:45am","3. 8:30am-9:30am","4. 10:30am-11:30am","5. 11:15am-12:15pm","6. 1:30pm-2:30pm","7. 3:30pm-4:30pm","8. 4:30pm-5:30pm","9. 5:00pm-6:00pm","10.6:30pm-7:30pm","11.7:30pm-8:30pm","12.8:45pm-9:45pm"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, values);

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (position == 0) {
                    Intent myIntent = new Intent(view.getContext(), Bus1.class);
                    startActivityForResult(myIntent, 0);
                }

                if (position == 1) {
                    Intent myIntent = new Intent(view.getContext(), Bus2.class);
                    startActivityForResult(myIntent, 0);
                }

                if (position == 2) {
                    Intent myIntent = new Intent(view.getContext(), Bus1.class);
                    startActivityForResult(myIntent, 0);
                }

            }
        });
    }
}

