package com.maiiz.kinaraidee.fragment;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.maiiz.kinaraidee.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by MaiiZ on 5/15/2016 AD.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback,GoogleApiClient.ConnectionCallbacks, LocationListener {

  @BindView(R.id.mapView) MapView mapView;

  private GoogleMap map;
  private GoogleApiClient client;

  public static MapFragment newInstance() {
    MapFragment fragment = new MapFragment();
    return fragment;
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.fragment_map, container, false);
    ButterKnife.bind(this, rootView);

    mapView.onCreate(savedInstanceState);
    mapView.getMapAsync(this);

    initInstances();
    return rootView;
  }

  private void initInstances() {
    client = new GoogleApiClient.Builder(MapFragment.this.getContext())
      .addConnectionCallbacks(this)
      .addApi(LocationServices.API)
      .build();
  }

  @Override
  public void onMapReady(GoogleMap googleMap) {
    map = googleMap;

    map.getUiSettings().setMyLocationButtonEnabled(true);
    if (ActivityCompat.checkSelfPermission(MapFragment.this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
      && ActivityCompat.checkSelfPermission(MapFragment.this.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

      return;
    }
    map.setMyLocationEnabled(true);

    // Needs to call MapsInitializer before doing any CameraUpdateFactory calls
    MapsInitializer.initialize(this.getActivity());

    // Updates the location and zoom of the MapView
    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(43.1, -87.9), 10);
    map.animateCamera(cameraUpdate);
  }

  @Override
  public void onConnected(Bundle bundle) {
    Location location = LocationServices.FusedLocationApi.getLastLocation(client);

    if (location != null) {
      LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());
      map.moveCamera(CameraUpdateFactory.newLatLng(loc));
      map.animateCamera(CameraUpdateFactory.zoomIn());
    }

    LocationRequest req = new LocationRequest();
    req.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    req.setInterval(2000);

    LocationServices.FusedLocationApi.requestLocationUpdates(client, req, this);
  }

  @Override
  public void onConnectionSuspended(int i) {

  }

  @Override
  public void onLocationChanged(Location location) {

  }

  @Override
  public void onStart() {
    super.onStart();
    client.connect();
  }

  @Override
  public void onStop() {
    super.onStop();
    client.connect();
  }

  @Override
  public void onResume() {
    mapView.onResume();
    super.onResume();

    if(client.isConnected()) {
      LocationRequest req = new LocationRequest();
      req.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
      req.setInterval(2000);

      LocationServices.FusedLocationApi.requestLocationUpdates(client, req, this);
    }
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    mapView.onDestroy();
  }

  @Override
  public void onLowMemory() {
    super.onLowMemory();
    mapView.onLowMemory();
  }

  @Override
  public void onPause() {
    super.onPause();

    if(client.isConnected()) {
      LocationServices.FusedLocationApi.removeLocationUpdates(client, this);
    }
  }
}
