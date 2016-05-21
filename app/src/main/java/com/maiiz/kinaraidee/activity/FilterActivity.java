package com.maiiz.kinaraidee.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.maiiz.kinaraidee.R;
import com.maiiz.kinaraidee.fragment.FilterFragment;

public class FilterActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_filter);

    initInstances();

    if (savedInstanceState == null) {
      getSupportFragmentManager().beginTransaction()
        .replace(R.id.contentContainer, FilterFragment.newInstance())
        .commit();
    }
  }

  private void initInstances() {
    getSupportActionBar().setTitle("Filters");
    getSupportActionBar().setHomeButtonEnabled(true);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();

    switch (id) {
      case android.R.id.home:
        finish();
        return true;
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
  }
}
