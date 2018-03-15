package com.sawa.aqarat.utilities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sawa.aqarat.R;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MapActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener, OnMapReadyCallback,
        ResultCallback<LocationSettingsResult>, GoogleMap.OnMapClickListener {

//    private static final int RESULT_GET_LOCATION = 2;
//    private static final int RESULT_DIDNT_PICK_LOCATION_ = 3;
    public static final String KEY_LATITUDE = "latitude";
    public static final String KEY_LONGITUDE = "longitude";
    public static final String KEY_Address = "Address";
    private GoogleMap mMap;


    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
//    protected static final int REQUEST_CHECK_SETTINGS = 0x1;

    long interval = 10 * 1000;   // 10 seconds, in milliseconds
    long fastestInterval = 1000;  // 1 second, in milliseconds
    float minDisplacement = 0;


    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingsRequest;


    private LatLng mSelectedLatlng = null;
    double mCurrentLatitude;
    double mCurrentLongitude;


    String saved_lat = null;
    String saved_lng = null;
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_map);


        Intent saved_LatLng = getIntent();
        if (saved_LatLng !=null)
        {
             saved_lat = saved_LatLng.getStringExtra("saved_lat");
             saved_lng = saved_LatLng.getStringExtra("saved_lng");

        }


        if (mMap == null) {
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        }


        mGoogleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();


        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create().setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
                .setInterval(interval).setFastestInterval(fastestInterval).setSmallestDisplacement(minDisplacement);

        // Check if has GPS by using Google play service
        buildLocationSettingsRequest();
        checkLocationSettings();


        //for searching map
        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i("sdg", "Place: " + place.getName());

                getLocationFromAddress(getApplicationContext(), place.getAddress().toString());

            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i("dsg", "An error occurred: " + status);
            }
        });


        Button mSend = findViewById(R.id.btn_done);
        mSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mSelectedLatlng != null) {

                String address="";
                try {
                    Geocoder geocoder;
                    List<Address> addresses;
                    geocoder = new Geocoder(MapActivity.this, Locale.getDefault());

                    addresses = geocoder.getFromLocation(mSelectedLatlng.latitude, mSelectedLatlng.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

                    address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
//                    String city = addresses.get(0).getLocality();
//                    String state = addresses.get(0).getAdminArea();
//                    String country = addresses.get(0).getCountryName();
//                    String postalCode = addresses.get(0).getPostalCode();
//                    String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL

                    Log.d("safrsaf",address);

                } catch (IOException e) {
                    e.printStackTrace();
                }


                Intent data = new Intent();


                    data.putExtra(KEY_LATITUDE, mSelectedLatlng.latitude + "");
                    data.putExtra(KEY_LONGITUDE, mSelectedLatlng.longitude + "");
                    data.putExtra(KEY_Address, address + "");
                    setResult(Activity.RESULT_OK, data);
                    finish();

                } else {
                    Toast.makeText(MapActivity.this, R.string.select_coord_map, Toast.LENGTH_LONG).show();
//            setResult(RESULT_DIDNT_PICK_LOCATION_, data);
                }
            }

        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mMap == null) {
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, MapActivity.this);
            mGoogleApiClient.disconnect();
        }
    }


    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.open_gps_msg)
                .setCancelable(false)
                .setPositiveButton(R.string.yes_gps_btn, new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton(R.string.no_gps_btn, new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }



    protected void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }

    protected void checkLocationSettings() {
        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(
                mGoogleApiClient, mLocationSettingsRequest );

        result.setResultCallback(this);
    }

    public boolean checkLocationPermission() {
        String permission = "android.permission.ACCESS_FINE_LOCATION";
        int res = this.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }










    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.mMap = googleMap;

        mMap = googleMap;


        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setIndoorLevelPickerEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(true);


        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getApplicationContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted

                mMap.setMyLocationEnabled(true);

            } else {
                //Request Location Permission
                checkLocationPermission();
            }
        } else {

            mMap.setMyLocationEnabled(true);

        }

        mMap.setOnMapClickListener(this);




//        mMap.clear();
//
//
//        if (mSelectedLatlng != null)
//        {
//
//            LatLng mCurrentLocation = new LatLng(mCurrentLatitude, mCurrentLongitude);
////        mMap.addMarker(new MarkerOptions().position(mCurrentLocation).title(getString(R.string.currentlocaion)));
//
//            CameraPosition cameraPosition = new CameraPosition.Builder()
//                    .target(mCurrentLocation) // Sets the center
//                    // of the map to
//                    // location user
//                    .zoom(17) // Sets the zoom
//                    .build(); // Creates a CameraPosition from the builder
//            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
//
//
//            BitmapDescriptor map_client = BitmapDescriptorFactory.fromResource(R.drawable.marker);
//            mMap.addMarker(new MarkerOptions()
//                    .snippet("Lat: " + mCurrentLocation.latitude + ", Lng: " + mCurrentLocation.longitude)
//                    .position(mCurrentLocation)
//                    .title(getResources().getString(R.string.app_name))
//                    .icon(map_client)
//                    .flat(true)
//                    .draggable(true));
//
//            mSelectedLatlng = mCurrentLocation;
//        }else
//            {
//                Log.d("null","nuuullll");
//            }

    }




    public LatLng getLocationFromAddress(Context context, String strAddress) {

        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;

        try {
            // May throw an IOException
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }
            Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();

            Log.d("dssdgsdg", location.getLatitude() + "");
            LatLng mCurrentLocation = new LatLng(location.getLatitude(), location.getLongitude());



            mMap.clear();

            mSelectedLatlng = mCurrentLocation;

            BitmapDescriptor map_client = BitmapDescriptorFactory.fromResource(R.drawable.map_pin_icon);

            mMap.addMarker(new MarkerOptions()
                    .snippet("ad here")
                    .position(mCurrentLocation)
                    .icon(map_client)
                    .title(getString(R.string.app_name))
                    .flat(true)
                    .draggable(true));
            //   "Lat: " + mCurrentLocation.latitude + ", Lng: " + mCurrentLocation.longitude

            CameraPosition cameraPosition = new CameraPosition.Builder().target(mCurrentLocation) // Sets the center
                    // of the map to
                    // location user
                    .zoom(17) // Sets the zoom
                    .build(); // Creates a CameraPosition from the builder
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));



            p1 = new LatLng(location.getLatitude(), location.getLongitude());

        } catch (IOException ex) {

            ex.printStackTrace();
        }

        return p1;
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (location == null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        } else {
            // this to get
            if (mSelectedLatlng == null)
            {
                handleNewLocation(location);
            }
        }

    }

    private void handleNewLocation(Location location) {
        Log.e("dfdsf", location.toString());

        if (saved_lat !=null && saved_lng !=null )
        {
            mCurrentLatitude = Double.parseDouble(saved_lat);
            mCurrentLongitude = Double.parseDouble(saved_lng);

        }else {

            mCurrentLatitude = location.getLatitude();
            mCurrentLongitude = location.getLongitude();

        }


// assign the Latitude and Longitude
        mSelectedLatlng = new LatLng(mCurrentLatitude, mCurrentLongitude);
// Make marker on the current location
        BitmapDescriptor map_client = BitmapDescriptorFactory.fromResource(R.drawable.map_pin_icon);

        mMap.clear();

        MarkerOptions options = new MarkerOptions()
                .position(mSelectedLatlng)
                .icon(map_client)
                .draggable(true);

        mMap.addMarker(options);
// move the camera zoom to the location
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mSelectedLatlng, 15));

        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {

            }

            @Override
            public void onMarkerDrag(Marker marker) {


            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                mCurrentLatitude = marker.getPosition().latitude;
                mCurrentLongitude = marker.getPosition().longitude;


                mSelectedLatlng = new LatLng(marker.getPosition().latitude,marker.getPosition().longitude);
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mSelectedLatlng, 12));

                Log.e("Marker", mSelectedLatlng.toString());
            }
        });


    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

         /*
         * Google Play services can resolve some errors it detects.
         * If the error has a resolution, try sending an Intent to
         * start a Google Play services activity that can resolve
         * error.
         */
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
                /*
                 * Thrown if Google Play services canceled the original
                 * PendingIntent
                 */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
            /*
             * If no resolution is available, display a dialog to the
             * user with the error.
             */
            Log.i("sfsf", "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    @Override
    public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {

    }

    @Override
    public void onLocationChanged(Location location) {


    }















    @Override
    public void onMapClick(LatLng latLng) {

        mMap.clear();
        BitmapDescriptor map_client = BitmapDescriptorFactory.fromResource(R.drawable.map_pin_icon);

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));


        mMap.addMarker(new MarkerOptions()
                .snippet("ad here")
                .position(latLng)
                .title(getString(R.string.app_name))
                .icon(map_client)
                .flat(true)
                .draggable(true));


        mSelectedLatlng = latLng;





    }



}
