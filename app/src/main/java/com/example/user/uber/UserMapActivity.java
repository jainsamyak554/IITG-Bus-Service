package com.example.user.uber;


/**
 * Name of the module: MainActivity.java
 *
 * Date on which this module was created: 4/4/2017
 *
 * Author's Name: Shreyanshi Bharadia
 *
 * Modification History: Samyak Jain 7/3/17
 *                     : Saurabh Bazari 8/3/17
 *
 * Synopsis of the module: In this module, user gets a prompt of putting his current location and the app tells the user the route to nearest bus stop.
 */


        import android.Manifest;
        import android.content.pm.PackageManager;
        import android.location.Location;
        import android.os.Build;
        import android.support.annotation.NonNull;
        import android.support.annotation.Nullable;
        import android.support.v4.app.ActivityCompat;
        import android.support.v4.app.FragmentActivity;
        import android.os.Bundle;
        import android.support.v4.content.ContextCompat;
        import android.view.View;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.Toast;

        import com.directions.route.AbstractRouting;
        import com.directions.route.Route;
        import com.directions.route.RouteException;
        import com.directions.route.Routing;
        import com.directions.route.RoutingListener;
        import com.firebase.geofire.GeoFire;
        import com.firebase.geofire.GeoLocation;
        import com.firebase.geofire.GeoQuery;
        import com.firebase.geofire.GeoQueryEventListener;
        import com.firebase.geofire.LocationCallback;
        import com.google.android.gms.common.ConnectionResult;
        import com.google.android.gms.common.api.GoogleApiClient;
        import com.google.android.gms.location.LocationRequest;
        import com.google.android.gms.location.LocationServices;
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

public class UserMapActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, RoutingListener {

    private Button requestNearestStop;
    private GoogleMap mMap;
    private GoogleApiClient client;
    private LocationRequest locationRequest;
    private Location lastlocation;
    private Marker currentLocationmMarker;
    private double radius = 0.1;
    public static final int REQUEST_LOCATION_CODE = 99;
    private Boolean stopFound = false;
    private String stopId;
    int PROXIMITY_RADIUS = 10000;
    double latitude,longitude;
    private EditText BusStop;
    private List<Polyline> polylines;
    private static final int[] COLORS = new int[]{R.color.primary_dark_material_light, R.color.colorAccent, R.color.colorPrimary,R.color.common_google_signin_btn_text_dark_default};



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_map);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            checkLocationPermission();

        }
        polylines = new ArrayList<>();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        requestNearestStop = (Button) findViewById(R.id.request_nearest_stop);

        requestNearestStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                radius = 0.1;

                getClosestStop();
            }
        });
    }

    private void getClosestStop(){
        final DatabaseReference stopLocation = FirebaseDatabase.getInstance().getReference().child("Bus Stops");

        final GeoFire geoFire = new GeoFire(stopLocation);
        LatLng latLng = new LatLng(lastlocation.getLatitude() , lastlocation.getLongitude());


        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(latLng.latitude,latLng.longitude),radius);
        geoQuery.removeAllListeners();
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                if(!stopFound) {
                    stopFound = true;
                    stopId = key;
//                    BusStop = (EditText) findViewById(R.id.busstop);
//                    BusStop.setText(stopLocation.child("l").child("0").get);
                    geoFire.getLocation(stopId, new LocationCallback() {
                        @Override
                        public void onLocationResult(String key, GeoLocation location) {
                            if (location!= null){
                              LatLng latlngbusstop = new LatLng(location.latitude, location.longitude);
                              getRouteToMarker(latlngbusstop);
                              MarkerOptions markerOptions= new MarkerOptions();
                              markerOptions.position(latlngbusstop);
                              markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                              markerOptions.title("Nearest Bus Stop");
                              mMap.addMarker(markerOptions);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }
            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {
                if(!stopFound){
                    radius=radius+0.1;
                    getClosestStop();

                }
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });

    }

    private void getRouteToMarker(LatLng latlngbusstop) {
        Routing routing = new Routing.Builder()
                .travelMode(AbstractRouting.TravelMode.DRIVING)
                .withListener(this)
                .alternativeRoutes(true)
                .waypoints(new LatLng(lastlocation.getLatitude(), lastlocation.getLongitude()), latlngbusstop)
                .build();
        routing.execute();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode)
        {
            case REQUEST_LOCATION_CODE:
                if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) !=  PackageManager.PERMISSION_GRANTED)
                    {
                        if(client == null)
                        {
                            bulidGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }
                }
                else
                {
                    Toast.makeText(this,"Permission Denied" , Toast.LENGTH_LONG).show();
                }
        }
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

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            bulidGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }
    }


    protected synchronized void bulidGoogleApiClient() {
        client = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
        client.connect();

    }

    @Override
    public void onLocationChanged(Location location) {

//        latitude = location.getLatitude();
//        longitude = location.getLongitude();
        lastlocation = location;
        if(currentLocationmMarker != null)
        {
            currentLocationmMarker.remove();

        }
//        Log.d("lat = ",""+latitude);
        LatLng latLng = new LatLng(location.getLatitude() , location.getLongitude());

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Location");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        currentLocationmMarker = mMap.addMarker(markerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomBy(85));

        if(client != null)
        {
            LocationServices.FusedLocationApi.removeLocationUpdates(client,this);
        }
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {

        locationRequest = new LocationRequest();
        locationRequest.setInterval(100);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);


        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION ) == PackageManager.PERMISSION_GRANTED)
        {
            LocationServices.FusedLocationApi.requestLocationUpdates(client, locationRequest, this);
        }
    }


    public boolean checkLocationPermission()
    {
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)  != PackageManager.PERMISSION_GRANTED )
        {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION))
            {
                ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.ACCESS_FINE_LOCATION },REQUEST_LOCATION_CODE);
            }
            else
            {
                ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.ACCESS_FINE_LOCATION },REQUEST_LOCATION_CODE);
            }
            return false;

        }
        else
            return true;
    }


    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    @Override
    public void onRoutingFailure(RouteException e) {
        if(e != null) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(this, "Something went wrong, Try again", Toast.LENGTH_SHORT).show();
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

            Toast.makeText(getApplicationContext(),"Route "+ (i+1) +": distance - "+ route.get(i).getDistanceValue()+": duration - "+ route.get(i).getDurationValue(),Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onRoutingCancelled() {
    }

    private void erasePolylines(){
        for (Polyline line: polylines){
            line.remove();

        }
        polylines.clear();
    }

}



//
//import android.Manifest;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.location.Address;
//import android.location.Geocoder;
//import android.location.Location;
//import android.os.Build;
//import android.support.annotation.NonNull;
//import android.support.annotation.Nullable;
//import android.support.v4.app.ActivityCompat;
//import android.support.v4.app.FragmentActivity;
//import android.os.Bundle;
//import android.support.v4.content.ContextCompat;
//import android.util.Log;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.Toast;
//
//import com.firebase.geofire.GeoFire;
//import com.firebase.geofire.GeoLocation;
//import com.google.android.gms.common.ConnectionResult;
//import com.google.android.gms.common.api.GoogleApiClient;
//import com.google.android.gms.location.LocationRequest;
//import com.google.android.gms.location.LocationServices;
//import com.google.android.gms.maps.CameraUpdateFactory;
//import com.google.android.gms.maps.GoogleMap;
//import com.google.android.gms.location.LocationListener;
//import com.google.android.gms.maps.OnMapReadyCallback;
//import com.google.android.gms.maps.SupportMapFragment;
//import com.google.android.gms.maps.model.BitmapDescriptorFactory;
//import com.google.android.gms.maps.model.LatLng;
//import com.google.android.gms.maps.model.Marker;
//import com.google.android.gms.maps.model.MarkerOptions;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//
//import java.io.IOException;
//import java.util.List;
//
//public class UserMapActivity extends FragmentActivity implements OnMapReadyCallback,
//        GoogleApiClient.ConnectionCallbacks,
//        GoogleApiClient.OnConnectionFailedListener,
//        LocationListener{
//
//
//    private GoogleMap mMap;
//    private GoogleApiClient client;
//    private LocationRequest locationRequest;
//    private Location lastlocation;
//    private Marker currentLocationmMarker;
//    public static final int REQUEST_LOCATION_CODE = 99;
//    int PROXIMITY_RADIUS = 10000;
//    double latitude,longitude;
//    private Button LogOut;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_user_map);
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
//        {
//            checkLocationPermission();
//
//        }
//        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
//        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.map);
//        mapFragment.getMapAsync(this);
//
//        LogOut= (Button) findViewById(R.id.logout);
//
//        LogOut.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                FirebaseAuth.getInstance().signOut();
//                Intent intent = new Intent(DriverMapActivity.this, MainActivity.class);
//                startActivity(intent);
//                finish();
//                return;
//            }
//        });
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        switch(requestCode)
//        {
//            case REQUEST_LOCATION_CODE:
//                if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
//                {
//                    if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) !=  PackageManager.PERMISSION_GRANTED)
//                    {
//                        if(client == null)
//                        {
//                            bulidGoogleApiClient();
//                        }
//                        mMap.setMyLocationEnabled(true);
//                    }
//                }
//                else
//                {
//                    Toast.makeText(this,"Permission Denied" , Toast.LENGTH_LONG).show();
//                }
//        }
//    }
//
//    /**
//     * Manipulates the map once available.
//     * This callback is triggered when the map is ready to be used.
//     * This is where we can add markers or lines, add listeners or move the camera. In this case,
//     * we just add a marker near Sydney, Australia.
//     * If Google Play services is not installed on the device, the user will be prompted to install
//     * it inside the SupportMapFragment. This method will only be triggered once the user has
//     * installed Google Play services and returned to the app.
//     */
//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//        mMap = googleMap;
//
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//            bulidGoogleApiClient();
//            mMap.setMyLocationEnabled(true);
//        }
//    }
//
//
//    protected synchronized void bulidGoogleApiClient() {
//        client = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
//        client.connect();
//
//    }
//
//    @Override
//    public void onLocationChanged(Location location) {
//
////        latitude = location.getLatitude();
////        longitude = location.getLongitude();
//        lastlocation = location;
//        if(currentLocationmMarker != null)
//        {
//            currentLocationmMarker.remove();
//
//        }
////        Log.d("lat = ",""+latitude);
//        LatLng latLng = new LatLng(location.getLatitude() , location.getLongitude());
//        MarkerOptions markerOptions = new MarkerOptions();
//        markerOptions.position(latLng);
//        markerOptions.title("Current Location");
//        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
//        currentLocationmMarker = mMap.addMarker(markerOptions);
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
//        mMap.animateCamera(CameraUpdateFactory.zoomBy(10));
//
//        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
//        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("DriverAvailable");
//        GeoFire geoFire = new GeoFire(ref);
//        geoFire.setLocation(userId,new GeoLocation(location.getLatitude(),location.getLongitude()));
//
////        if(client != null)
////        {
////            LocationServices.FusedLocationApi.removeLocationUpdates(client,this);
////        }
//    }
//
//    public void onClick(View v)
//    {
////        Object dataTransfer[] = new Object[2];
////        GetNearbyPlacesData getNearbyPlacesData = new GetNearbyPlacesData();
////
////        switch(v.getId())
////        {
////            case R.id.B_search:
////                EditText tf_location =  findViewById(R.id.TF_location);
////                String location = tf_location.getText().toString();
////                List<Address> addressList;
////
////
////                if(!location.equals(""))
////                {
////                    Geocoder geocoder = new Geocoder(this);
////
////                    try {
////                        addressList = geocoder.getFromLocationName(location, 5);
////
////                        if(addressList != null)
////                        {
////                            for(int i = 0;i<addressList.size();i++)
////                            {
////                                LatLng latLng = new LatLng(addressList.get(i).getLatitude() , addressList.get(i).getLongitude());
////                                MarkerOptions markerOptions = new MarkerOptions();
////                                markerOptions.position(latLng);
////                                markerOptions.title(location);
////                                mMap.addMarker(markerOptions);
////                                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
////                                mMap.animateCamera(CameraUpdateFactory.zoomTo(10));
////                            }
////                        }
////                    } catch (IOException e) {
////                        e.printStackTrace();
////                    }
////                }
////                break;
////            case R.id.B_hopistals:
////                mMap.clear();
////                String hospital = "hospital";
////                String url = getUrl(latitude, longitude, hospital);
////                dataTransfer[0] = mMap;
////                dataTransfer[1] = url;
////
////                getNearbyPlacesData.execute(dataTransfer);
////                Toast.makeText(MapsActivity.this, "Showing Nearby Hospitals", Toast.LENGTH_SHORT).show();
////                break;
////
////
////            case R.id.B_schools:
////                mMap.clear();
////                String school = "school";
////                url = getUrl(latitude, longitude, school);
////                dataTransfer[0] = mMap;
////                dataTransfer[1] = url;
////
////                getNearbyPlacesData.execute(dataTransfer);
////                Toast.makeText(MapsActivity.this, "Showing Nearby Schools", Toast.LENGTH_SHORT).show();
////                break;
////            case R.id.B_restaurants:
////                mMap.clear();
////                String resturant = "restuarant";
////                url = getUrl(latitude, longitude, resturant);
////                dataTransfer[0] = mMap;
////                dataTransfer[1] = url;
////
////                getNearbyPlacesData.execute(dataTransfer);
////                Toast.makeText(MapsActivity.this, "Showing Nearby Restaurants", Toast.LENGTH_SHORT).show();
////                break;
////            case R.id.B_to:
////        }
//    }
//
//
//    private String getUrl(double latitude , double longitude , String nearbyPlace)
//    {
//
//        StringBuilder googlePlaceUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
//        googlePlaceUrl.append("location="+latitude+","+longitude);
//        googlePlaceUrl.append("&radius="+PROXIMITY_RADIUS);
//        googlePlaceUrl.append("&type="+nearbyPlace);
//        googlePlaceUrl.append("&sensor=true");
//        googlePlaceUrl.append("&key="+"AIzaSyBLEPBRfw7sMb73Mr88L91Jqh3tuE4mKsE");
//
//        Log.d("MapsActivity", "url = "+googlePlaceUrl.toString());
//
//        return googlePlaceUrl.toString();
//    }
//
//    @Override
//    public void onConnected(@Nullable Bundle bundle) {
//
//        locationRequest = new LocationRequest();
//        locationRequest.setInterval(100);
//        locationRequest.setFastestInterval(1000);
//        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
//
//
//        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION ) == PackageManager.PERMISSION_GRANTED)
//        {
//            LocationServices.FusedLocationApi.requestLocationUpdates(client, locationRequest, this);
//        }
//    }
//
//
//    public boolean checkLocationPermission()
//    {
//        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)  != PackageManager.PERMISSION_GRANTED )
//        {
//
//            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION))
//            {
//                ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.ACCESS_FINE_LOCATION },REQUEST_LOCATION_CODE);
//            }
//            else
//            {
//                ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.ACCESS_FINE_LOCATION },REQUEST_LOCATION_CODE);
//            }
//            return false;
//
//        }
//        else
//            return true;
//    }
//
//
//    @Override
//    public void onConnectionSuspended(int i) {
//    }
//
//    @Override
//    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
//    }
//    protected void onStop(){
//        super.onStop();
//
//        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
//        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("DriverAvailable");
//        GeoFire geoFire = new GeoFire(ref);
//        geoFire.removeLocation(userId);
//
//    }
//}
////
////import android.content.Context;
////import android.location.Location;
////import android.location.LocationListener;
////import android.location.LocationManager;
////import android.os.Bundle;
////import android.support.design.widget.FloatingActionButton;
////import android.support.design.widget.Snackbar;
////import android.support.v7.app.AppCompatActivity;
////import android.support.v7.widget.Toolbar;
////import android.view.Menu;
////import android.view.MenuItem;
////import android.view.View;
////
////import com.google.android.gms.common.ConnectionResult;
////import com.google.android.gms.common.GooglePlayServicesUtil;
////import com.google.android.gms.maps.CameraUpdateFactory;
////import com.google.android.gms.maps.GoogleMap;
////import com.google.android.gms.maps.MapView;
////import com.google.android.gms.maps.model.LatLng;
////import com.google.android.gms.maps.model.MarkerOptions;
////import com.orhanobut.logger.Logger;
////
////import butterknife.Bind;
////import butterknife.ButterKnife;
////
////public class MainActivity extends AppCompatActivity {
////
////    public static final int LOCATION_UPDATE_MIN_DISTANCE = 10;
////    public static final int LOCATION_UPDATE_MIN_TIME = 5000;
////
////    @Bind(R.id.mapview)
////    MapView mMapView;
////    @Bind(R.id.toolbar)
////    Toolbar mToolbar;
////    @Bind(R.id.fab)
////    FloatingActionButton mFab;
////
////    private GoogleMap mGoogleMap;
////    private LocationListener mLocationListener = new LocationListener() {
////        @Override
////        public void onLocationChanged(Location location) {
////            if (location != null) {
////                Logger.d(String.format("%f, %f", location.getLatitude(), location.getLongitude()));
////                drawMarker(location);
////                mLocationManager.removeUpdates(mLocationListener);
////            } else {
////                Logger.d("Location is null");
////            }
////        }
////
////        @Override
////        public void onStatusChanged(String s, int i, Bundle bundle) {
////
////        }
////
////        @Override
////        public void onProviderEnabled(String s) {
////
////        }
////
////        @Override
////        public void onProviderDisabled(String s) {
////
////        }
////    };
////    private LocationManager mLocationManager;
////
////    @Override
////    protected void onCreate(Bundle savedInstanceState) {
////        super.onCreate(savedInstanceState);
////        setContentView(R.layout.activity_main);
////        ButterKnife.bind(this);
////        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
////        setSupportActionBar(toolbar);
////
////        Logger.init();
////        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
////        fab.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View view) {
////                getCurrentLocation();
////            }
////        });
////        mMapView.onCreate(savedInstanceState);
////        mGoogleMap = mMapView.getMap();
////        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
////        initMap();
////        getCurrentLocation();
////    }
////
////    @Override
////    protected void onDestroy() {
////        super.onDestroy();
////        mMapView.onDestroy();
////    }
////
////    @Override
////    protected void onResume() {
////        super.onResume();
////        mMapView.onResume();
////        getCurrentLocation();
////    }
////
////    @Override
////    protected void onPause() {
////        super.onPause();
////        mMapView.onPause();
////        mLocationManager.removeUpdates(mLocationListener);
////    }
////
////    @Override
////    public boolean onCreateOptionsMenu(Menu menu) {
////        // Inflate the menu; this adds items to the action bar if it is present.
////        getMenuInflater().inflate(R.menu.menu_main, menu);
////        return true;
////    }
////
////    @Override
////    public boolean onOptionsItemSelected(MenuItem item) {
////        // Handle action bar item clicks here. The action bar will
////        // automatically handle clicks on the Home/Up button, so long
////        // as you specify a parent activity in AndroidManifest.xml.
////        int id = item.getItemId();
////
////        //noinspection SimplifiableIfStatement
////        if (id == R.id.action_settings) {
////            return true;
////        }
////
////        return super.onOptionsItemSelected(item);
////    }
////
////
////    private void initMap() {
////        int googlePlayStatus = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
////        if (googlePlayStatus != ConnectionResult.SUCCESS) {
////            GooglePlayServicesUtil.getErrorDialog(googlePlayStatus, this, -1).show();
////            finish();
////        } else {
////            if (mGoogleMap != null) {
////                mGoogleMap.setMyLocationEnabled(true);
////                mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
////                mGoogleMap.getUiSettings().setAllGesturesEnabled(true);
////            }
////        }
////    }
////
////    private void getCurrentLocation() {
////        boolean isGPSEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
////        boolean isNetworkEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
////
////        Location location = null;
////        if (!(isGPSEnabled || isNetworkEnabled))
////            Snackbar.make(mMapView, R.string.error_location_provider, Snackbar.LENGTH_INDEFINITE).show();
////        else {
////            if (isNetworkEnabled) {
////                mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
////                        LOCATION_UPDATE_MIN_TIME, LOCATION_UPDATE_MIN_DISTANCE, mLocationListener);
////                location = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
////            }
////
////            if (isGPSEnabled) {
////                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
////                        LOCATION_UPDATE_MIN_TIME, LOCATION_UPDATE_MIN_DISTANCE, mLocationListener);
////                location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
////            }
////        }
////        if (location != null) {
////            Logger.d(String.format("getCurrentLocation(%f, %f)", location.getLatitude(),
////                    location.getLongitude()));
////            drawMarker(location);
////        }
////    }
////
////    private void drawMarker(Location location) {
////        if (mGoogleMap != null) {
////            mGoogleMap.clear();
////            LatLng gps = new LatLng(location.getLatitude(), location.getLongitude());
////            mGoogleMap.addMarker(new MarkerOptions()
////                    .position(gps)
////                    .title("Current Position"));
////            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(gps, 12));
////        }
////
////    }
////}