package com.example.gabrielle.laundryonline;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


import java.io.IOException;

/**
 * Created by gabrielle on 6/1/2016.
 */
public class MapFragment extends SupportMapFragment implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnMapLongClickListener,
        GoogleMap.OnMapClickListener,
        GoogleMap.OnMarkerClickListener {
    private GoogleApiClient mGoogleApiClient;
    private Location mCurrentLocation;
    private GoogleMap gMap;
    private final int[] MAP_TYPES = {GoogleMap.MAP_TYPE_SATELLITE,
            GoogleMap.MAP_TYPE_NORMAL,
            GoogleMap.MAP_TYPE_HYBRID,
            GoogleMap.MAP_TYPE_TERRAIN,
            GoogleMap.MAP_TYPE_NONE};
    private int curMapTypeIndex = 0;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        initListeners();
    }
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
    private void initListeners() {
        gMap.setOnMarkerClickListener(this);
        gMap.setOnMapLongClickListener(this);
        gMap.setOnMapClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        try {
            mCurrentLocation = LocationServices
                    .FusedLocationApi
                    .getLastLocation(mGoogleApiClient);
            initCamera(mCurrentLocation);
        } catch (Exception ex) {
            //Log.e( "Error creating location service: " , ex.getMessage() );

        }
    }

    private void initCamera(Location location) {
        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        try {
            CameraPosition position = CameraPosition.builder()
                    .target(new LatLng(location.getLatitude(),
                            location.getLongitude()))
                    .zoom(10f)
                    .bearing(0.0f)
                    .tilt(0.0f)
                    .build();

            gMap.animateCamera(CameraUpdateFactory
                    .newCameraPosition(position), null);

            gMap.setMapType(MAP_TYPES[curMapTypeIndex]);
            gMap.setTrafficEnabled(true);
            gMap.setMyLocationEnabled(true);
            gMap.getUiSettings().setZoomControlsEnabled(true);
        } catch (Exception ex) {
        }


    }

    @Override
    public void onMapClick(LatLng latLng) {

        MarkerOptions options = new MarkerOptions().position(latLng);
        options.title(getAddressFromLatLng(latLng));

        options.icon(BitmapDescriptorFactory.defaultMarker());
        gMap.addMarker(options);
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        MarkerOptions options = new MarkerOptions().position(latLng);
//        options.title(getAddressFromLatLng(latLng));
//
//        options.icon(BitmapDescriptorFactory.fromBitmap(
//                BitmapFactory.decodeResource(getResources(),
//                        R.mipmap.ic_launcher)));
        gMap.addMarker(new MarkerOptions()
                .position(latLng) //setting position
                .draggable(true) //Making the marker draggable
                .title(getAddressFromLatLng(latLng))); //Adding a title
        //gMap.addMarker(options);
    }

    private String getAddressFromLatLng(LatLng latLng) {
        Geocoder geocoder = new Geocoder(getActivity());

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
    public boolean onMarkerClick(Marker marker) {
        marker.showInfoWindow();
        return true;
    }
    @Override
    public void onConnectionSuspended(int i) {

    }



}
