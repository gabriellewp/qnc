package com.example.gabrielle.laundryonline;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gabrielle.laundryonline.db.DropOffLocation;
import com.example.gabrielle.laundryonline.db.LaundryOrder;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by gabrielle on 5/28/2016.
 */
import java.io.IOException;
import java.util.ArrayList;

public class DropOffActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnMarkerDragListener,
        GoogleMap.OnMapLongClickListener,
        View.OnClickListener, PlaceSelectionListener{

    private GoogleMap mMap;
    private double longitude;
    private double latitude;
    private Button buttonSave;
    private GoogleApiClient googleApiClient;
    private String email;
    private Marker markerUtara,markerBarat,markerTimur,markerSelatan,markerPusat;
    private int cityOpt;
    private DatabaseReference mDatabase;
    private ArrayList<LatLng> dropOffLocation  = new ArrayList<LatLng>();
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        setContentView(R.layout.activity_dropoff_map);
        Intent intent = getIntent();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //google api init
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        Spinner packageSpinner = (Spinner) findViewById(R.id.filterByCity);


        final ArrayAdapter<String> cityAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item){
            @Override
            public View getView(int position, View convertView, ViewGroup parent){
                View v  = super.getView(position, convertView, parent);
                if (position == getCount()) {
                    ((TextView)v.findViewById(android.R.id.text1)).setText("");
                    ((TextView)v.findViewById(android.R.id.text1)).setHint(getItem(getCount())); //"Hint to be displayed"
                }
                return v;

            }
            @Override
            public int getCount() {
                return super.getCount()-1;
            }
        };
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cityAdapter.add("Jakarta Utara");
        cityAdapter.add("Jakarta Barat");
        cityAdapter.add("Jakarta Pusat");
        cityAdapter.add("Jakarta Timur");
        cityAdapter.add("Jakarta Selatan");
        cityAdapter.add("Filter by City");
        packageSpinner.setAdapter(cityAdapter);
        packageSpinner.setSelection(cityAdapter.getCount());
        packageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // On selecting a spinner item
                int item = position;
                cityOpt = parent.getSelectedItemPosition();
                if(cityOpt!=cityAdapter.getCount()){
                    showMarker(item);
                }

                // Showing selected spinner item
                //Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
            }
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });

        //progressBarLayout = (LinearLayout)findViewById(R.id.new_address_progress_layout);
        //latitudelongitude list
//        DropOffLocation dl = new DropOffLocation();
//        dl.setDropoffaddress("utara1");
//        dl.setDropoffowner("gaby1");
//        dl.setEmployeenumber(2);
//        dl.setPointlatitude(-6.130747);
//        dl.setPointlongitude(106.865572);
//        mDatabase.child("dropOffLocations").push().setValue(dl);
//        //jakarta utara
//        dl.setDropoffaddress("utara2");
//        dl.setEmployeenumber(2);
//        dl.setPointlatitude(-6.110607);
//        dl.setPointlongitude(106.764979);
//        mDatabase.child("dropOffLocations").push().setValue(dl);
//
//        dl.setDropoffaddress("barat1");
//        dl.setEmployeenumber(2);
//        dl.setPointlatitude(-6.157433);
//        dl.setPointlongitude(106.705070);
//        mDatabase.child("dropOffLocations").push().setValue(dl);
//
//        dl.setDropoffaddress("barat2");
//        dl.setEmployeenumber(2);
//        dl.setPointlatitude(-6.155727);
//        dl.setPointlongitude(106.783691);
//        mDatabase.child("dropOffLocations").push().setValue(dl);
//
//        dl.setDropoffaddress("timur1");
//        dl.setEmployeenumber(2);
//        dl.setPointlatitude(-6.186044);
//        dl.setPointlongitude(106.929722);
//        mDatabase.child("dropOffLocations").push().setValue(dl);
//
//        dl.setDropoffaddress("timur2");
//        dl.setEmployeenumber(2);
//        dl.setPointlatitude(-6.293208);
//        dl.setPointlongitude(106.871357);
//        mDatabase.child("dropOffLocations").push().setValue(dl);
//
//        dl.setDropoffaddress("pusat1");
//        dl.setEmployeenumber(2);
//        dl.setPointlatitude(-6.198009);
//        dl.setPointlongitude(106.819981);
//        mDatabase.child("dropOffLocations").push().setValue(dl);
//
//        dl.setDropoffaddress("pusat2");
//        dl.setEmployeenumber(2);
//        dl.setPointlatitude(-6.162170);
//        dl.setPointlongitude(106.862210);
//        mDatabase.child("dropOffLocations").push().setValue(dl);
//
//        dl.setDropoffaddress("selatan1");
//        dl.setEmployeenumber(2);
//        dl.setPointlatitude(-6.246719);
//        dl.setPointlongitude(106.797771);
//        mDatabase.child("dropOffLocations").push().setValue(dl);
//
//        dl.setDropoffaddress("selatan2");
//        dl.setEmployeenumber(2);
//        dl.setPointlatitude(-6.297056);
//        dl.setPointlongitude(106.815108);
//        mDatabase.child("dropOffLocations").push().setValue(dl);
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
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
//        mDatabase.child("dropOffLocations").addValueEventListener((new ValueEventListener() {
//
//            @Override
//            public void onDataChange(DataSnapshot snapshot) {
//                Log.d("gotomdatabasevaluelist","");
//                //Log.d("orderhistoryobject",snapshot.child("laundryOrders").getValue().toString());
//                Log.d("orderhistorystrig",snapshot.getValue().toString());
//                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
//                    Log.d("orderhistorystrigds",postSnapshot.getValue().toString());
//                    DropOffLocation dl = postSnapshot.getValue(DropOffLocation.class);
//                    LatLng x = new LatLng(dl.getPointlatitude(),dl.getPointlongitude());
//                    markerUtara = mMap.addMarker(new MarkerOptions().position(x).title(dl.getDropoffaddress()));
//                    //LaundryOrder lo = postSnapshot.getValue(LaundryOrder.class);
//                    //orderList.add(lo);
//                    //Log.d("laundryordertakendate",lo.getTakenDate());
//                }
//
//            }
//            @Override
//            public void onCancelled(DatabaseError firebaseError) {
//                System.out.println("The read failed: " + firebaseError.getMessage());
//            }
//        }));
        LatLng utara1 = new LatLng(-6.130747, 106.865572);
        LatLng utara2 = new LatLng(-6.110607, 106.764979);
        //jakarta barat
        LatLng barat1 = new LatLng(-6.157433, 106.705070);
        LatLng barat2 = new LatLng(-6.155727, 106.783691);
        //jakarta timur
        LatLng timur1 = new LatLng(-6.186044, 106.929722);
        LatLng timur2 = new LatLng(-6.293208, 106.871357);
        //jakarta pusat
        LatLng pusat1 = new LatLng(-6.198009, 106.819981);
        LatLng pusat2 = new LatLng(-6.162170, 106.862210);
        //jakarta selatan
        LatLng selatan1 = new LatLng(-6.246719, 106.797771);
        LatLng selatan2 = new LatLng(-6.297056, 106.815108);
        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-6.2, 106.822);
        if (markerUtara != null && markerBarat!=null && markerTimur!=null && markerSelatan!=null && markerPusat!=null) {
            markerUtara.remove();
            markerBarat.remove();
            markerTimur.remove();
            markerSelatan.remove();
            markerPusat.remove();
        }
        markerUtara = mMap.addMarker(new MarkerOptions().position(utara1).title("Jakarta Utara1"));
        markerBarat = mMap.addMarker(new MarkerOptions().position(barat1).title("Jakarta Barat1"));
        markerPusat = mMap.addMarker(new MarkerOptions().position(pusat1).title("Jakarta Pusat1"));
        markerSelatan = mMap.addMarker(new MarkerOptions().position(selatan1).title("Jakarta Selatan1"));
        markerTimur = mMap.addMarker(new MarkerOptions().position(barat1).title("Jakarta Timur1"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(pusat1));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(10));
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
        mMap.addMarker(new MarkerOptions()
                .position(latLng) //setting position
                .draggable(true) //Making the marker draggable
                .title("Current Location")); //Adding a title

        //Moving the camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.getUiSettings().setZoomControlsEnabled(true);
        //Animating the camera
        mMap.animateCamera(CameraUpdateFactory.zoomTo(7));

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

//        mMap.addMarker(new MarkerOptions()
//                .position(latLng)
//                .draggable(true)
//                .title(getAddressFromLatLng(latLng)));

        //address1.setText(getAddressFromLatLng(latLng));
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
//        address1.setText(place.getName()+ place.getId()+
//                place.getAddress()+ place.getPhoneNumber()+ place.getWebsiteUri()+String.valueOf(place.getLatLng().latitude)+String.valueOf(place.getLatLng().longitude));

        CharSequence attributions = place.getAttributions();
        if (!TextUtils.isEmpty(attributions)) {
           // address2.setText(Html.fromHtml(attributions.toString())+String.valueOf(place.getLatLng().latitude)+String.valueOf(place.getLatLng().longitude));
        } else {
            //address2.setText("");
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
    public void showMarker(int item){
        if(item==0){
            Log.d("cityselected","jakut");
            markerUtara.setVisible(true);
            markerPusat.setVisible(false);
            markerSelatan.setVisible(false);
            markerBarat.setVisible(false);
            markerTimur.setVisible(false);
        }else if(item==1){
            Log.d("cityselected","jakbar");
            markerSelatan.setVisible(false);
            markerBarat.setVisible(true);
            markerUtara.setVisible(false);
            markerTimur.setVisible(false);
            markerPusat.setVisible(false);
        }else if(item==2){//pusat
            markerUtara.setVisible(false);
            markerPusat.setVisible(true);
            markerSelatan.setVisible(false);
            markerBarat.setVisible(false);
            markerTimur.setVisible(false);
        }else if(item==3){//timur
            markerUtara.setVisible(false);
            markerPusat.setVisible(false);
            markerSelatan.setVisible(false);
            markerBarat.setVisible(false);
            markerTimur.setVisible(true);
        }else if(item==4){//selatan
            markerUtara.setVisible(false);
            markerPusat.setVisible(false);
            markerSelatan.setVisible(true);
            markerBarat.setVisible(false);
            markerTimur.setVisible(false);
        }
    }
}
