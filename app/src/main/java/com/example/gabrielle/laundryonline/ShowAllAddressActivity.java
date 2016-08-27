package com.example.gabrielle.laundryonline;

/**
 * Created by gabrielle on 7/20/2016.
 */

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gabrielle.laundryonline.db.DropOffLocation;
import com.example.gabrielle.laundryonline.db.LaundryOrder;
import com.example.gabrielle.laundryonline.db.UserAddressDetails;
import com.google.android.gms.ads.formats.NativeAd;
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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ShowAllAddressActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
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
    private Marker[] arrayofMarker;
    private int cityOpt;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private List<UserAddressDetails> addressList = new ArrayList<>();
    private SessionManager sessionMgr;
    private Intent intentNewAddress, intentDateConfig;
    private ImageButton addAddressButton,dateConfigButton;
    private RecyclerView rv;
    private List<LatLng> arrOfLatLng= new ArrayList<>();
    private List<Marker> arrOfMarker= new ArrayList<>();

    private AddressAdapter mAdapter;
    private View mProgressView, mUIView;
    private HashMap<String, String> hashMap = new HashMap<String, String>();
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_address);
        final Intent intent = getIntent();
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        sessionMgr= new SessionManager(getApplicationContext());
        mProgressView = findViewById(R.id.login_progress);
        mUIView = findViewById(R.id.allAddressLayout);
        MyAsyncTask myAsyncTask = new MyAsyncTask();
        myAsyncTask.execute();
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        rv = (RecyclerView)findViewById(R.id.rv1);





        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        intentNewAddress = new Intent(this, NewAddressMapActivity.class);
        intentDateConfig = new Intent(this,DateConfigurationActivity2.class);
        addAddressButton = (ImageButton) findViewById(R.id.add_button);
        dateConfigButton = (ImageButton) findViewById(R.id.next_button);
        addAddressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(intentNewAddress);
            }
        });
        dateConfigButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(intentDateConfig);
            }
        });


    }
    public void setAdapter(){
        mAdapter = new AddressAdapter(addressList);
        final LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rv.setLayoutManager(llm);
        rv.setItemAnimator(new DefaultItemAnimator());
        rv.setAdapter(mAdapter);
        rv.addOnItemTouchListener(new RecyclerTouchListener(this, rv, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                UserAddressDetails uad = addressList.get(position);
                hashMap.put("label",uad.getLabelAddress());
                hashMap.put("address",uad.getCompleteAddress());
                hashMap.put("detail",uad.getDetailAddress());

                intentDateConfig.putExtra("map",hashMap);
                // view.setBackgroundColor(getResources(R.color.placeSelectedBg));
                //Toast.makeText(getApplicationContext(),lo.getTakenDate(),Toast.LENGTH_SHORT).show();
                //showDialog();
                //  view.setBackgroundColor(getResources()(R.color.placeUnselectedBg));
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));


    }
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {

            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mUIView.setVisibility(show ? View.GONE : View.VISIBLE);

            mUIView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mUIView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);

            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });

        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mUIView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
    public int getAllAddress(){
        int returnValue;
        returnValue = 0;
        int i=0;
        mDatabase.child("userDetailAddress").orderByChild("username_id").equalTo(sessionMgr.getUidPreferences()).addValueEventListener((new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Log.d("gotomdatabasevaluelist","");
                //Log.d("orderhistoryobject",snapshot.child("laundryOrders").getValue().toString());
                //Log.d("showalladdrsnap",snapshot.getValue().toString());
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Log.d("showalladdrsnap",postSnapshot.getValue().toString());
                    UserAddressDetails uad = postSnapshot.getValue(UserAddressDetails.class);
                    if(!uad.getLabelAddress().equals("")){
                        addressList.add(uad);
                    }

                    Log.d("addresslistsize2:",addressList.size()+"");
                    //Log.d("laundryordertakendate",lo.getTakenDate());

                }
                for(int i=0;i<addressList.size();i++){
                    arrOfLatLng.add(new LatLng(addressList.get(i).getAddressLatitude(),addressList.get(i).getAddressLongitude()));

                }

            }
            @Override
            public void onCancelled(DatabaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }

        }));
        returnValue = 1;
        return returnValue;



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

    }
    public void drawMap(){
        //set latitude and longitude
        Log.d("addresslistsize1:",addressList.size()+"");
        LatLng latlng ;

        for(int i=0;i<addressList.size();i++){
            latlng = new LatLng(addressList.get(i).getAddressLatitude(),addressList.get(i).getAddressLongitude());
            arrOfLatLng.add(latlng);
            Marker m = mMap.addMarker(new MarkerOptions().position(latlng).title(addressList.get(i).getLabelAddress()));
            arrOfMarker.add(m);
        }
        Log.d("arroflatlngsize1:",arrOfLatLng.size()+"");
        mMap.moveCamera(CameraUpdateFactory.newLatLng(arrOfLatLng.get(0)));
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
    class MyAsyncTask extends AsyncTask<Void, Void, Integer> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgress(true);
        }

        @Override
        protected Integer doInBackground(Void... arg0) {
            // TODO Auto-generated method stub
            int returnvalue=0;
            try{
                returnvalue = getAllAddress();
                Log.d("returnvalue",returnvalue+"");
                Thread.sleep(5000);
            }catch(InterruptedException e){
                return 0;
            }
            return 1;
        }

        @Override
        protected void onPostExecute(Integer result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            Log.d("addresslistsize3:",addressList.size()+"");

            Log.d("arroflatlngsize3:",arrOfLatLng.size()+"");
            if(addressList.size()==0){
                showProgress(false);
                startActivity(intentNewAddress);
            }else{
                setAdapter();
                drawMap();
                showProgress(false);
            }


        }
    }
    public void showDialog(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Are you sure want to confirm order?")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
    public interface ClickListener{
        void onClick (View view, int position);
        void onLongClick(View view, int position);
    }
    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener{
        private GestureDetector gestureDetector;
        private ShowAllAddressActivity.ClickListener clickListener;
        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ShowAllAddressActivity.ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }
                }
            });
        }
        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }
}
