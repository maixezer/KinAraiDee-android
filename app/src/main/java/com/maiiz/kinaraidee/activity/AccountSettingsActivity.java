package com.maiiz.kinaraidee.activity;

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;

import com.maiiz.kinaraidee.R;
import com.maiiz.kinaraidee.adapter.ViewPagerAdapter;
import com.maiiz.kinaraidee.fragment.HistoryFragment;
import com.maiiz.kinaraidee.fragment.SettingsFragment;
import com.maiiz.kinaraidee.manager.IFragmentActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AccountSettingsActivity extends AppCompatActivity implements IFragmentActivity {

  @BindView(R.id.viewPager)
  ViewPager viewPager;
  @BindView(R.id.tabLayout)
  TabLayout tabLayout;

  private ViewPagerAdapter viewPagerAdapter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_account_settings);
    ButterKnife.bind(this);
    initInstances();
  }

  private void initInstances() {
    getSupportActionBar().setTitle("Account Settings");
    getSupportActionBar().setHomeButtonEnabled(true);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setElevation(0);

    // set PagerAdapter to viewPager
    viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
    viewPager.setAdapter(viewPagerAdapter);
    tabLayout.setupWithViewPager(viewPager);

    // Give the TabLayout to ViewPager
    viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
    tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
      @Override
      public void onTabSelected(TabLayout.Tab tab) {
        viewPager.setCurrentItem(tab.getPosition());
        final InputMethodManager imm = (InputMethodManager)getSystemService(
          Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(viewPager.getWindowToken(), 0);
      }

      @Override
      public void onTabUnselected(TabLayout.Tab tab) {

      }

      @Override
      public void onTabReselected(TabLayout.Tab tab) {

      }
    });
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

  @Override
  public void notifyTotalCals(String totalCals, Fragment frag) {
    if (frag instanceof HistoryFragment) {
      ((SettingsFragment) viewPagerAdapter.getFragment(0)).setLeftCals(totalCals);
    }
  }
}