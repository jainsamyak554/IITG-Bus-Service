package com.example.user.uber;

/**
 * Name of the module: MainActivity.java
 *
 * Date on which this module was created: 14/3/2017
 *
 * Author's Name: Saurabh Bazari
 *
 * Modification History: Samyak Jain 17/3/17
 *                     : Shreyanshi Bharadia 18/3/17
 *
 * Synopsis of the module: Main file which is executed when the app is started
 *
 */


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private Button DriverLogin;
    private Button SeeSchedule;
    private Button NearestStop;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DriverLogin = findViewById(R.id.driver);

        DriverLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, DriverLoginActivity.class);
                startActivity(intent);
//                finish();
//                return;
            }
        });

        SeeSchedule = findViewById(R.id.see_schedule);

        SeeSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, OptionsListActivity.class);
                startActivity(intent);
//                finish();
//                return;
            }
        });

        NearestStop = findViewById(R.id.nearest_stop);

        NearestStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, UserMapActivity.class);
                startActivity(intent);
//                finish();
//                return;
            }
        });

    }
}
