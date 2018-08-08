package com.squareandcube.locationtracking.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareandcube.locationtracking.LatlongPreferences;
import com.squareandcube.locationtracking.MyService;
import com.squareandcube.locationtracking.R;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

//    private LatlongPreferences latlongPreferences = new LatlongPreferences(this);
    private MyService service;
    private String empid, taskId;
    private LatLng sydney;
    private int a=0;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    GoogleApiClient mGoogleApiClient;
    String deslatitute, deslongitude, emplatitute, emplongitude, desaddres;
    Double employeeDestinationLatitude, employeeDestinationLongitude, latitude, longitude;
    String desAddress;
    @SuppressLint("HandlerLeak")
    private final Handler toastHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

        }

    };

    // Getting Value from Shared Preference in every 10 second
    Timer timerObj = new Timer();

    private GoogleMap mMap;
    private LocationRequest mLocationRequest;
    private long UPDATE_INTERVAL = 1000;  /* 4 secs */
    private long FASTEST_INTERVAL = 0; /* 1 secs */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        service = new MyService();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        empid = sharedPreferences.getString("EmployeeId","");
        taskId = sharedPreferences.getString("EmployeeTask","");
        desAddress = sharedPreferences.getString("daddress","");
        deslatitute = sharedPreferences.getString("dLat","");
        deslongitude = sharedPreferences.getString("dLong","");

        employeeDestinationLatitude = Double.parseDouble(deslatitute.trim());
        employeeDestinationLongitude = Double.parseDouble(deslongitude.trim());

            Intent intent = new Intent(MapsActivity.this, MyService.class);
            intent.putExtra("eId", empid);
            intent.putExtra("tId", taskId);
            startService(intent);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(MapsActivity.this);

        connectClient();

        TimerTask timerTaskObj = new TimerTask() {
            public void run() {
//                SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
//                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//                emplatitute = sharedPreferences.getString("emplatitute", "");
//                emplongitude = sharedPreferences.getString("emplongitude", "");
                emplatitute = new LatlongPreferences(getApplicationContext()).getLat();
                emplongitude = new LatlongPreferences(getApplicationContext()).getLong();
                toastHandler.sendEmptyMessage(0);

                try {
                    latitude = Double.parseDouble(emplatitute.trim());
                    longitude = Double.parseDouble(emplongitude.trim());

                    Log.d("TAG", "runs: " + latitude);
                    Log.d("TAG", "runs: " + emplatitute);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        };
        timerObj.schedule(timerTaskObj, 0, 8000);
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
        sydney = new LatLng(employeeDestinationLatitude, employeeDestinationLongitude);
        mMap.addMarker(new MarkerOptions().position(sydney).title(desaddres))
                    .setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

        if(a == 0) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 20));
            a=1;
        }

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        // mMap.setMyLocationEnabled(true);

    }

    public void connectClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!checkPlayServices()) {
            Toast.makeText(getApplicationContext(), "Please install google play services", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        startLocationUpdates();

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

        if (latitude != null && longitude != null) {
            LatLng latLng = new LatLng(latitude, longitude);
            Log.d("lat", "onLocationChanged: " + latLng.toString());
            mMap.addMarker(new MarkerOptions().position(latLng).title(latLng.toString()));
            Log.d("lat", "onLocationChang: " + latLng.toString());
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

        }
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else
                finish();

            return false;
        }
        return true;
    }

    protected void startLocationUpdates() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(), "Enable Permissions", Toast.LENGTH_LONG).show();
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopService(new Intent(getBaseContext(), MyService.class));
        timerObj.cancel();
        Toast.makeText(getApplicationContext(),"services",Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopLocationUpdates();
    }

    public void stopLocationUpdates()
    {
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi
                    .removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        new LatlongPreferences(getApplicationContext()).isEmpty();
//        stopService(new Intent(getBaseContext(), MyService.class));
//        timerObj.cancel();
//        service.stopTimer();
        new LatlongPreferences(getApplicationContext()).clear();

        Intent mIntent = new Intent(MapsActivity.this, TrackEmployeeActivity.class);
        startActivity(mIntent);
        finish();
    }
}
