package com.example.user.uber;

/**
 * Name of the module: OptionsListActivity.java
 *
 * Date on which this module was created: 15/3/2017
 *
 * Author's Name: Saurabh Bazari
 *
 * Modification History: Samyak Jain 17/3/17
 *                     : Shreyanshi Bharadia 18/3/17
 *
 * Synopsis of the module: This module displays options for user if he wants to check buses from campus or to campus.
 *
 */

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class OptionsListActivity extends AppCompatActivity {

    private Button fromCampus;
    private Button toCampus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options_list);

        fromCampus = findViewById(R.id.fromCampusBusList);
        fromCampus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OptionsListActivity.this, FromCampusBusList.class);
                startActivity(intent);
            }
        });

        toCampus =  findViewById(R.id.toCampusBusList);
        toCampus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OptionsListActivity.this, ToCampusBusList.class);
                startActivity(intent);
            }
        });

    }
}


