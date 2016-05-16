package com.maiiz.kinaraidee.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.maiiz.kinaraidee.Constants;
import com.maiiz.kinaraidee.R;
import com.maiiz.kinaraidee.dao.Store;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by MaiiZ on 5/15/2016 AD.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback,
  GoogleApiClient.ConnectionCallbacks,
  GoogleApiClient.OnConnectionFailedListener,
  ResultCallback<LocationSettingsResult>,
  LocationListener {

  @BindView(R.id.mapView) MapView mapView;

  private GoogleMap map;
  private GoogleApiClient client;
  private LocationRequest req;
  private Location location;
  private SharedPreferences sPreferences;

  private Boolean reqLocationUpdates;

  private LocationSettingsRequest locSettingReq;

  protected static final int REQUEST_CHECK_SETTINGS = 0x1;
  // Keys for storing activity state in the Bundle.
  protected final static String KEY_REQUESTING_LOCATION_UPDATES = "requesting-location-updates";
  protected final static String KEY_LOCATION = "location";

  private Circle mapCircle;

  private static List<Store> stores;

  public static MapFragment newInstance(List<Store> stores) {
    MapFragment fragment = new MapFragment();
    MapFragment.stores = stores;
    return fragment;
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.fragment_map, container, false);
    ButterKnife.bind(this, rootView);

    mapView.onCreate(savedInstanceState);
    mapView.getMapAsync(this);

    initInstances(savedInstanceState);
    return rootView;
  }

  private void initInstances(Bundle savedInstanceState) {
    sPreferences = getActivity().getSharedPreferences(Constants.APP_NAME, Context.MODE_PRIVATE);
    reqLocationUpdates = false;

    updateValuesFromBundle(savedInstanceState);

    buildGoogleApiClient();
    createLocationRequest();
    buildLocationSettingsRequest();
    checkLocationSettings();
  }

  private void updateValuesFromBundle(Bundle savedInstanceState) {
    if (savedInstanceState != null) {
      // Update the value of mRequestingLocationUpdates from the Bundle, and make sure that
      // the Start Updates and Stop Updates buttons are correctly enabled or disabled.
      if (savedInstanceState.keySet().contains(KEY_REQUESTING_LOCATION_UPDATES)) {
        reqLocationUpdates = savedInstanceState.getBoolean(
          KEY_REQUESTING_LOCATION_UPDATES);
      }

      // Update the value of mCurrentLocation from the Bundle and update the UI to show the
      // correct latitude and longitude.
      if (savedInstanceState.keySet().contains(KEY_LOCATION)) {
        // Since KEY_LOCATION was found in the Bundle, we can be sure that mCurrentLocation
        // is not null.
        location = savedInstanceState.getParcelable(KEY_LOCATION);
      }
      updateLocationUI();
    }
  }

  private void buildGoogleApiClient() {
    client = new GoogleApiClient.Builder(getContext())
      .addConnectionCallbacks(this)
      .addOnConnectionFailedListener(this)
      .addApi(LocationServices.API)
      .build();
  }

  private void createLocationRequest() {
    req = new LocationRequest();
    req.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
  }

  private void buildLocationSettingsRequest() {
    // Check Location settings enabled
    LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
      .addLocationRequest(req);
    builder.addLocationRequest(req);
    locSettingReq = builder.build();
  }

  private void checkLocationSettings() {
    PendingResult<LocationSettingsResult> result =
      LocationServices.SettingsApi.checkLocationSettings(
        client,
        locSettingReq
      );

    result.setResultCallback(this);
  }

  @Override
  public void onResult(LocationSettingsResult locationSettingsResult) {
    final Status status = locationSettingsResult.getStatus();
    switch (status.getStatusCode()) {
      case LocationSettingsStatusCodes.SUCCESS:
        Log.i("check", "All location settings are satisfied.");
        startLocationUpdates();
        break;
      case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
        Log.i("MapFragment", "Location settings are not satisfied. Show the user a dialog to" +
          "upgrade location settings ");

        try {
          // Show the dialog by calling startResolutionForResult(), and check the result
          // in onActivityResult().
          status.startResolutionForResult(getActivity(), REQUEST_CHECK_SETTINGS);
        } catch (IntentSender.SendIntentException e) {
          Log.i("MapFragment", "PendingIntent unable to execute request.");
        }
        break;
      case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
        Log.i("MapFragment", "Location settings are inadequate, and cannot be fixed here. Dialog " +
          "not created.");
        break;
    }
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    switch (requestCode) {
      // Check for the integer request code originally supplied to startResolutionForResult().
      case REQUEST_CHECK_SETTINGS:
        switch (resultCode) {
          case Activity.RESULT_OK:
            Log.i("MapFragment", "User agreed to make required location settings changes.");
            startLocationUpdates();
            break;
          case Activity.RESULT_CANCELED:
            Log.i("MapFragment", "User chose not to make required location settings changes.");
            break;
        }
        break;
    }
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
    if (location == null) {
      location = LocationServices.FusedLocationApi.getLastLocation(client);
      updateLocationUI();
    }
  }

  @Override
  public void onLocationChanged(Location location) {
    this.location = location;
    updateLocationUI();
  }

  @Override
  public void onConnectionSuspended(int i) {
    Log.i("MapFragment", "Connection suspended");
  }

  @Override
  public void onConnectionFailed(ConnectionResult connectionResult) {
    Log.i("MapFragment", "Connection failed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());
  }

  private void updateLocationUI() {
    if (location != null) {
      LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());
      map.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 16f));

      double nearRadius = Double.parseDouble(sPreferences.getString(Constants.NEAR_RADIUS, "1"));

      // circle radius
      if (mapCircle != null) mapCircle.remove();

      CircleOptions circle = new CircleOptions()
        .center(loc)
        .radius(nearRadius * 1000)
        .strokeColor(R.color.map_circle_storke)
        .fillColor(R.color.map_cricle_fill);

      mapCircle = map.addCircle(circle);
      setStoresLocation(nearRadius * 1000);
    }
  }

  private void startLocationUpdates() {
    LocationServices.FusedLocationApi.requestLocationUpdates(
      client,
      req,
      this
    ).setResultCallback(new ResultCallback<Status>() {
      @Override
      public void onResult(Status status) {
        reqLocationUpdates = true;
        updateLocationUI();
      }
    });
  }

  private void stopLocationUpdates() {
    LocationServices.FusedLocationApi.removeLocationUpdates(
      client,
      this
    ).setResultCallback(new ResultCallback<Status>() {
      @Override
      public void onResult(Status status) {
        reqLocationUpdates = false;
      }
    });
  }

  @Override
  public void onStart() {
    super.onStart();
    client.connect();
  }

  @Override
  public void onStop() {
    super.onStop();
    client.disconnect();
  }

  @Override
  public void onPause() {
    super.onPause();

    if (client.isConnected()) {
      stopLocationUpdates();
    }
  }

  @Override
  public void onResume() {
    mapView.onResume();
    super.onResume();

    if (client.isConnected() && reqLocationUpdates) {
      startLocationUpdates();
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
  public void onSaveInstanceState(Bundle outState) {
    outState.putBoolean(KEY_REQUESTING_LOCATION_UPDATES, reqLocationUpdates);
    outState.putParcelable(KEY_LOCATION, location);
    mapView.onSaveInstanceState(outState);
    super.onSaveInstanceState(outState);
  }

  private void setStoresLocation(double nearRadius) {
    for (Store s : stores) {
      String name = s.getName();

      // location of store
      Location sLoc = new Location("");
      double sLat = Double.parseDouble(s.getLat());
      double sLng = Double.parseDouble(s.getLng());

      sLoc.setLatitude(sLat);
      sLoc.setLongitude(sLng);

      if (location != null) {
        if (location.distanceTo(sLoc) <= nearRadius) {
          map.addMarker(new MarkerOptions().position(new LatLng(sLat, sLng)).title(name));
        }
      }
    }
  }
}
