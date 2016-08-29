package com.example.gabrielle.laundryonline;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.gabrielle.laundryonline.db.UserAddressDetails;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;

public class NewAddressMapActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnMarkerDragListener,
        GoogleMap.OnMapLongClickListener,
        View.OnClickListener, PlaceSelectionListener{

    private GoogleMap mMap;
    private double longitude;
    private double latitude;
    private Button buttonSave;
    private ImageButton buttonNext, buttonPrev;
    private EditText addressOnMap,detailAddress, labelAddress;
    private GoogleApiClient googleApiClient;
    private String email;
    private Marker marker;
    private String placeAddress,placeDetail,placeLabel;
    private double placeLatitude;
    private double placeLongitude;
   // private UserCreateNewAddress mCreateNewAddressTask = null;
    private LinearLayout progressBarLayout;
    private SessionManager sessionMgr;
    private int addressTextViewNumber;
    private DatabaseReference mDatabase;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser user;
    private Intent intentShowAllAddress, intentShowOption;
    //place.getName()+ place.getId()+place.getAddress()+ place.getPhoneNumber()+ place.getWebsiteUri()+String.valueOf(place.getLatLng().latitude)+String.valueOf(place.getLatLng().longitude)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_new_address_map);
        Intent intent = getIntent();

        sessionMgr= new SessionManager(getApplicationContext());
        mDatabase = FirebaseDatabase.getInstance().getReference();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        //google api init
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        buttonNext = (ImageButton)findViewById(R.id.next_button);
        buttonPrev = (ImageButton) findViewById(R.id.prev_button);
        intentShowOption =  new Intent(this, ShowOptionActivity.class);
        buttonPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(intentShowOption);
            }
        });
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //attemptLogin();
                createAddress();
                //back to addressoption
            }
        });
        addressOnMap = (EditText)findViewById(R.id.addressOnMap);
        detailAddress = (EditText)findViewById(R.id.addressDetail);
        labelAddress  = (EditText) findViewById(R.id.addressLabel);
        // Retrieve the PlaceAutocompleteFragment.
        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        autocompleteFragment.setOnPlaceSelectedListener(this);

        progressBarLayout = (LinearLayout)findViewById(R.id.new_address_progress_layout);
        intentShowAllAddress = new Intent(this,ShowAllAddressActivity.class);
    }

    @Override
    protected void onStart(){
        googleApiClient.connect();
        super.onStart();
    }
    protected void onStop(){
        googleApiClient.disconnect();
        super.onStop();
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

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-6.2, 106.822);
        if (marker != null) {
            marker.remove();
        }
        marker = mMap.addMarker(new MarkerOptions().position(sydney).title("Jakarta City"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        mMap.setOnMarkerDragListener(this);
        mMap.setOnMapLongClickListener(this);
    }

    private void getCurrentLocation(){
        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        try {
            Location location = LocationServices
                    .FusedLocationApi
                    .getLastLocation(googleApiClient);
            //initCamera(mCurrentLocation);
            if (location != null) {
                //Getting longitude and latitude
                longitude = location.getLongitude();
                latitude = location.getLatitude();

                //moving the map to location
                moveMap();
            }
        } catch (Exception ex) {
            //Log.e( "Error creating location service: " , ex.getMessage() );

        }

    }

    private void moveMap(){
        //String to display current latitude and longitude
        String msg = latitude + ", "+longitude;

        //Creating a LatLng Object to store Coordinates
        LatLng latLng = new LatLng(latitude, longitude);

        //Adding marker to map
        if (marker != null) {
            marker.remove();
        }
        marker = mMap.addMarker(new MarkerOptions()
                .position(latLng) //setting position
                .draggable(true) //Making the marker draggable
                .title("Current Location")); //Adding a title

        //Moving the camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.getUiSettings().setZoomControlsEnabled(true);
        //Animating the camera
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));

        //Displaying current coordinates in toast
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        getCurrentLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        //mMap.clear();
        if (marker != null) {
            marker.remove();
        }
        marker = mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .draggable(true)
                .title(getAddressFromLatLng(latLng)));

        addressOnMap.setText(getAddressFromLatLng(latLng));
    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        latitude = marker.getPosition().latitude;
        longitude = marker.getPosition().longitude;

        moveMap();
    }

    private String getAddressFromLatLng(LatLng latLng){
        Geocoder geocoder = new Geocoder(this);

        String address = "";
        try {
            address = geocoder
                    .getFromLocation(latLng.latitude, latLng.longitude, 1)
                    .get(0).getAddressLine(0);
        } catch (IOException e) {
        }

        return address;
    }
    @Override
    public void onPlaceSelected(Place place) {
        //Log.i(TAG, "Place Selected: " + place.getName());

        // Format the returned place's details and display them in the TextView.
        placeAddress = place.getAddress().toString();
        placeLatitude = place.getLatLng().latitude;
        placeLongitude = place.getLatLng().longitude;
        addressOnMap.setText(place.getAddress());
        //mMap.clear();
        //place.getName()+ place.getId()+place.getAddress()+ place.getPhoneNumber()+ place.getWebsiteUri()+String.valueOf(place.getLatLng().latitude)+String.valueOf(place.getLatLng().longitude)
        LatLng latLng = new LatLng(place.getLatLng().latitude, place.getLatLng().longitude);
        if (marker != null) {
            marker.remove();
        }
        marker = mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .draggable(true)
                .title(place.getAddress().toString()));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(place.getLatLng()));
        //mMap.getUiSettings().setZoomControlsEnabled(true);
        //Animating the camera
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        CharSequence attributions = place.getAttributions();
        if (!TextUtils.isEmpty(attributions)) {
            detailAddress.setText(Html.fromHtml(attributions.toString())+String.valueOf(place.getLatLng().latitude)+String.valueOf(place.getLatLng().longitude));

        } else {
            detailAddress.setText("");
        }
    }

    /**
     * Callback invoked when PlaceAutocompleteFragment encounters an error.
     */
    @Override
    public void onError(Status status) {
       // Log.e(TAG, "onError: Status = " + status.toString());

        Toast.makeText(this, "Place selection failed: " + status.getStatusMessage(),
                Toast.LENGTH_SHORT).show();
    }

    public void createAddress(){

        View focusView = null;
        addressOnMap.setError(null);
        detailAddress.setError(null);
        labelAddress.setError(null);
        placeAddress = addressOnMap.getText().toString();
        placeDetail = detailAddress.getText().toString();
        placeLabel = labelAddress.getText().toString();
        if(placeAddress.length()==0){
            progressBarLayout.setVisibility(View.GONE);
            addressOnMap.setError("alamat tidak boleh kosong");
            focusView = addressOnMap;
        }else if(placeDetail.length()==0){
            progressBarLayout.setVisibility(View.GONE);
            detailAddress.setError("detail tidak boleh kosong");
            focusView = detailAddress;
        }else if(labelAddress.length()==0){
            progressBarLayout.setVisibility(View.GONE);
            labelAddress.setError("label tidak boleh kosong");
            focusView = labelAddress;
        }else{
            Log.d("createaddress",sessionMgr.getUidPreferences());
            progressBarLayout.setVisibility(View.VISIBLE);
            UserAddressDetails userAddress = new UserAddressDetails();
            userAddress.setUsername_id(sessionMgr.getUidPreferences());
            userAddress.setAddressLatitude(placeLatitude);
            userAddress.setAddressLongitude(placeLongitude);
            userAddress.setCompleteAddress(placeAddress);
            userAddress.setDetailAddress(placeDetail);
            userAddress.setLabelAddress(placeLabel);


            mDatabase.child("userDetailAddress").push().setValue(userAddress);
            new java.util.Timer().schedule(
                    new java.util.TimerTask() {
                        @Override
                        public void run() {
                            // your code here
                        }
                    },
                    5000
            );

            startActivity(intentShowAllAddress);
            progressBarLayout.setVisibility(View.GONE);
        }
//        mCreateNewAddressTask = new UserCreateNewAddress();
//        mCreateNewAddressTask.execute((Void)null);
       // showProgress(true);


        //showProgress(false);
    }

//    public class UserCreateNewAddress extends AsyncTask<Void, Void, Boolean>{
//        private Context context;
//        UserCreateNewAddress(){
//
//        }
//        @Override
//        protected Boolean doInBackground(Void... params){
//
//
//            UserAddressDetails userDetail = new UserAddressDetails();
//            userDetail.setUser_email(email);
//            if(addressTextViewNumber==1){
//                userDetail.setCompleteAddress1(placeAddress);
//                userDetail.setAddress1latitude(placeLatitude);
//                userDetail.setAddress1longitude(placeLongitude);
//                userDetail.setAdditionalDetailAddress1(placeDetail);
//            }else{
//                userDetail.setCompleteAddress2(placeAddress);
//                userDetail.setAddress2latitude(placeLatitude);
//                userDetail.setAddress2longitude(placeLongitude);
//                userDetail.setAdditionalDetailAddress2(placeDetail);
//            }
//
//
//            Log.d("ampesini1-----",email+placeAddress+placeLatitude+placeLongitude+placeDetail);
//            dbHandler.addCompleteAddress(userDetail,addressTextViewNumber);
//
//            Log.d("ampesini2-----",dbHandler.getCompletePickUpAddress(userDetail.getUser_email()));
//            return true;
//        }
//        @Override
//        protected void onPreExecute() {
//            // SHOW THE SPINNER WHILE LOADING FEEDS
//            progressBarLayout.setVisibility(View.VISIBLE);
//        }
//        @Override
//        protected void onPostExecute(final Boolean success){
//            if(success){
//                //MailSender ms = new MailSender("mEmail","mPassword");
//                //ms.sendMail("test","confirmation","gabybongbong@gmail.com");
//                progressBarLayout.setVisibility(View.GONE);
//
//                Intent intenttoAddOrder = new Intent(getApplicationContext(), AddOrderActivity.class);
//                startActivity(intenttoAddOrder);
//                //finish();
//        }
//        }
//        @Override
//        protected void onCancelled(){
//            mCreateNewAddressTask = null;
//            progressBarLayout.setVisibility(View.GONE);
//
//        }
//
//    }
    /**
     * Helper method to format information about a place nicely.
     */
//    private static Spanned formatPlaceDetails(Resources res, CharSequence name, String id,
//                                              CharSequence address, CharSequence phoneNumber, Uri websiteUri) {
////        Log.e(TAG, res.getString(R.string.place_details, name, id, address, phoneNumber,
////                websiteUri));
//        return Html.fromHtml(res.getString(R.string.place_details, name, id, address, phoneNumber,
//                websiteUri));
//
//    }
}
