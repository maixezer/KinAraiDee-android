package com.maiiz.kinaraidee.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.PersistableBundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.maiiz.kinaraidee.Constants;
import com.maiiz.kinaraidee.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

  @BindView(R.id.drawerLayout) DrawerLayout drawerLayout;
  @BindView(R.id.toolbar) Toolbar toolbar;
  @BindView(R.id.tvSignOut) TextView tvSignOut;

  ActionBarDrawerToggle actionBarDrawerToggle;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    ButterKnife.bind(this);
    initInstances();
  }

  private void initInstances() {
    setSupportActionBar(toolbar);

    actionBarDrawerToggle = new ActionBarDrawerToggle(
      MainActivity.this,
      drawerLayout,
      R.string.open_drawer,
      R.string.close_drawer
    );

    drawerLayout.addDrawerListener(actionBarDrawerToggle);

    // Enabled Home Button on Action Bar ( Tool Bar )
    getSupportActionBar().setHomeButtonEnabled(true);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
  }

  @OnClick(R.id.tvSignOut)
  public void navigateToSignIn() {
    // Clear access token in shared preferences
    SharedPreferences.Editor sharedPreferences = getSharedPreferences(Constants.APP_NAME , MODE_PRIVATE).edit();
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
    if(actionBarDrawerToggle.onOptionsItemSelected(item)) return true;
    return super.onOptionsItemSelected(item);
  }
}
