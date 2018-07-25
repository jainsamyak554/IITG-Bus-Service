package com.example.user.uber;

/**
 * Name of the module: BusStop2.java
 *
 * Date on which this module was created: 10/3/2017
 *
 * Author's Name: Saurabh Bazari
 *
 * Modification History: Shreyanshi Bharadia 12/3/17
 *                     : Samyak Jain 14/3/17
 *
 * Synopsis of the module: This module shows the distance to the current and estimated arrival time of the bus to the bus stop1.
 *
 */


import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.LocationCallback;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class BusStop1 extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, RoutingListener {

    private LatLng driver_latLng;
    private Marker driverMarker;
    final long period = 30000;
    private GoogleMap mMap;
    private List<Polyline> polylines;
    private static final int[] COLORS = new int[]{R.color.colorAccent, R.color.colorAccent, R.color.colorPrimary,R.color.common_google_signin_btn_text_dark_default};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_stop1);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        polylines = new ArrayList<>();

        mapFragment.getMapAsync(this);
        new Timer().schedule(new TimerTask() {

            @Override
            public void run() {
                getDistancefromStop();
            }
        },0,period);

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

    }

    private void getDistancefromStop(){

        final DatabaseReference driverLocation = FirebaseDatabase.getInstance().getReference().child("DriverAvailable");
        final GeoFire driverGeo = new GeoFire(driverLocation);
        final String driverId = "kl12pwrC9DgjY7GDOan6yfQCQil1";
        final LatLng latLng = new LatLng(26.192506,91.694412);

        driverGeo.getLocation(driverId, new LocationCallback() {
            @Override
            public void onLocationResult(String key, GeoLocation location) {
                driver_latLng = new LatLng(location.latitude,location.longitude);

                if(driverMarker!=null) {
                    driverMarker.remove();
                }

                getRouteToMarker(driver_latLng);
                MarkerOptions markerOptions= new MarkerOptions();
                markerOptions.position(driver_latLng);
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                markerOptions.title("Bus Location");
                mMap.moveCamera(CameraUpdateFactory.newLatLng(driver_latLng));
                driverMarker=mMap.addMarker(markerOptions);
                MarkerOptions destinationMarkerOptions= new MarkerOptions();
                destinationMarkerOptions.position(latLng);
                destinationMarkerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                destinationMarkerOptions.title("Bus Stop 1");
                mMap.addMarker(destinationMarkerOptions);
                mMap.animateCamera(CameraUpdateFactory.zoomBy(10));

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onRoutingFailure(RouteException e) {
        if(e != null) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(this, "Something went wrong, Try again", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRoutingStart() {

    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {
        if(polylines.size()>0) {
            for (Polyline poly : polylines) {
                poly.remove();
            }
        }

        polylines = new ArrayList<>();
        //add route(s) to the map.
        for (int i = 0; i <route.size(); i++) {

            //In case of more than 5 alternative routes
            int colorIndex = i % COLORS.length;

            PolylineOptions polyOptions = new PolylineOptions();
            polyOptions.color(getResources().getColor(COLORS[colorIndex]));
            polyOptions.width(10 + i * 3);
            polyOptions.addAll(route.get(i).getPoints());
            Polyline polyline = mMap.addPolyline(polyOptions);
            polylines.add(polyline);

//            EditText distance= findViewById(R.id.distance);
//            distance.setText(route.get(i).getDistanceValue());
//
//            EditText time= findViewById(R.id.time);
//            time.setText(route.get(i).getDurationValue()/60);

            Toast.makeText(getApplicationContext(),"Route "+ (i+1) +": distance - "+ route.get(i).getDistanceValue()+": duration - "+ (route.get(i).getDurationValue()/60),Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onRoutingCancelled() {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    private void getRouteToMarker(LatLng latLng){
        Routing routing = new Routing.Builder()
                .travelMode(AbstractRouting.TravelMode.DRIVING)
                .withListener(this)
                .alternativeRoutes(false)
                .waypoints(driver_latLng, new LatLng(26.192506,91.694412))
                .build();
        routing.execute();
    }
    private void erasePolylines(){
        for (Polyline line: polylines){
            line.remove();

        }
        polylines.clear();

    }
}
