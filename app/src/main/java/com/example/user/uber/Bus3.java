package com.example.user.uber;


/**
 * Name of the module: Bus1.java
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

import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.LocationCallback;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Timer;
import java.util.TimerTask;

public class Bus3  extends AppCompatActivity {
    private ListView listView;
    private final LatLng latLng = new LatLng(26.192506,91.694412);
    Location busStop1= new Location("");
    Location driverLocation = new Location("");

    private LatLng driver_latLng;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bus2);

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
//                erasePolylines();
//                if(driverMarker!=null) {
//                    driverMarker.remove();
//                }
                getDistancefromStop();


            }
        },0,30);

        busStop1.setLatitude(latLng.latitude);
        busStop1.setLongitude(latLng.longitude);




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
                    Intent myIntent = new Intent(view.getContext(), BusStop1.class);
                    startActivityForResult(myIntent, 0);
                }

            }
        });
    }

    private void getDistancefromStop(){
        final DatabaseReference driverLocation = FirebaseDatabase.getInstance().getReference().child("DriverAvailable");

        final GeoFire driverGeo = new GeoFire(driverLocation);
        final String driverId = "kl12pwrC9DgjY7GDOan6yfQCQil1";


        driverGeo.getLocation(driverId, new LocationCallback() {
            @Override
            public void onLocationResult(String key, GeoLocation location) {
                driver_latLng = new LatLng(location.latitude,location.longitude);
               // getRouteToMarker(driver_latLng);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
//        driver_location = DriverMapActivity.driverCurrentLocation;
//        final LatLng driverLatlng = new LatLng(DriverMapActivity.driverCurrentLocation.getLatitude(),DriverMapActivity.driverCurrentLocation.getLongitude());


        //getRouteToMarker(driver_latLng);
    }


}


