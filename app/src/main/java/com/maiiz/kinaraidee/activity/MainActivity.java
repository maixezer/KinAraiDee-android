package com.maiiz.kinaraidee.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.maiiz.kinaraidee.Constants;
import com.maiiz.kinaraidee.R;
import com.maiiz.kinaraidee.dao.AccessToken;
import com.maiiz.kinaraidee.dao.Element;
import com.maiiz.kinaraidee.dao.User;
import com.maiiz.kinaraidee.fragment.FoodFragment;
import com.maiiz.kinaraidee.manager.HttpManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

  @BindView(R.id.drawerLayout) DrawerLayout drawerLayout;
  @BindView(R.id.toolbar) Toolbar toolbar;
  @BindView(R.id.nvView) NavigationView nvDrawer;

  private TextView tvUsername;
  private TextView tvEmail;
  private ActionBarDrawerToggle actionBarDrawerToggle;
  private SharedPreferences sPreferences;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    ButterKnife.bind(this);
    initInstances();
    fetchUser();

    if (savedInstanceState == null) {
      getSupportFragmentManager().beginTransaction()
        .replace(R.id.contentContainer, FoodFragment.newInstance())
        .commit();
    }
  }

  private void initInstances() {
    sPreferences = getSharedPreferences(Constants.APP_NAME, Context.MODE_PRIVATE);
    setSupportActionBar(toolbar);

    actionBarDrawerToggle = new ActionBarDrawerToggle(
      MainActivity.this,
      drawerLayout,
      R.string.open_drawer,
      R.string.close_drawer
    );

    drawerLayout.addDrawerListener(actionBarDrawerToggle);

    // Add nav_header to nvDrawer
    View headerView = LayoutInflater.from(this).inflate(R.layout.nav_header, nvDrawer, false);
    nvDrawer.addHeaderView(headerView);
    tvUsername = (TextView) headerView.findViewById(R.id.tvUsername);
    tvEmail = (TextView) headerView.findViewById(R.id.tvEmail);

    nvDrawer.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
      @Override
      public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
          case R.id.navFilters:
            Intent intent = new Intent(MainActivity.this, FilterActivity.class);
            startActivity(intent);
            break;
          case R.id.navAccountSettings:
            break;
          case R.id.navSignout:
            navigateToSignIn();
            break;
        }
        drawerLayout.closeDrawer(nvDrawer);
        return false;
      }
    });

    // Enabled Home Button on Action Bar ( Tool Bar )
    getSupportActionBar().setHomeButtonEnabled(true);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
  }

  public void navigateToSignIn() {
    // Clear access token in shared preferences
    SharedPreferences.Editor sharedPreferences = sPreferences.edit();
    sharedPreferences.clear();
    sharedPreferences.apply();

    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
    startActivity(intent);
    finish();
  }

  @Override
  protected void onPostCreate(Bundle savedInstanceState) {
    super.onPostCreate(savedInstanceState);
    actionBarDrawerToggle.syncState();
  }

  @Override
  public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
    actionBarDrawerToggle.onConfigurationChanged(newConfig);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (actionBarDrawerToggle.onOptionsItemSelected(item)) return true;
    return super.onOptionsItemSelected(item);
  }

  @Override
  protected void onResume() {
    super.onResume();
    fetchUser();
  }

  private void fetchUser() {
    // create access token
    AccessToken accessToken = new AccessToken();
    accessToken.setAccessToken(sPreferences.getString(Constants.ACCESS_TOKEN, null));
    accessToken.setTokenType(sPreferences.getString(Constants.TOKEN_TYPE, null));

    if (accessToken.getAccessToken() == null) {
      navigateToSignIn();
    } else {

      Call<Element> element = HttpManager.getInstance().getService(accessToken).currentUser();

      element.enqueue(new Callback<Element>() {
        @Override
        public void onResponse(Call<Element> call, Response<Element> response) {
          if (response.isSuccessful()) {
            User user = response.body().getUser();

            tvUsername.setText(String.format("%s %s", user.getFirstName(), user.getLastName()));
            tvEmail.setText(user.getEmail());
          } else {
            Log.e("errors", response.raw().toString());
            try {
              JSONObject jsonObject = new JSONObject(response.errorBody().string());
              String error = jsonObject.getString("error");
              AlertDialog dialog = new AlertDialog.Builder(MainActivity.this).create();
              dialog.setMessage(error);
              dialog.show();
            } catch (IOException e) {
              e.printStackTrace();
            } catch (JSONException e) {
              e.printStackTrace();
            }
            navigateToSignIn();
          }
        }

        @Override
        public void onFailure(Call<Element> call, Throwable t) {

        }
      });
    }
  }
}
