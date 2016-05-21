package com.maiiz.kinaraidee.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.maiiz.kinaraidee.Constants;
import com.maiiz.kinaraidee.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnEditorAction;

/**
 * Created by MaiiZ on 5/19/2016 AD.
 */
public class SettingsFragment extends Fragment {

  @BindView(R.id.etCalPerDay) EditText etCalPerDay;
  @BindView(R.id.tvLeftCals) TextView tvLeftCals;

  private SharedPreferences sPreferences;
  private SharedPreferences.Editor editor;
  private String totalCals;
  private String calPerDay;

  public static SettingsFragment newInstance() {
    SettingsFragment fragment = new SettingsFragment();
    return fragment;
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.fragment_settings, container, false);
    ButterKnife.bind(this, rootView);
    initInstances();
    return rootView;
  }

  private void initInstances() {
    sPreferences = getActivity().getSharedPreferences(Constants.APP_NAME, Context.MODE_PRIVATE);
    editor = getActivity().getSharedPreferences(Constants.APP_NAME, Context.MODE_PRIVATE).edit();
  }

  @OnEditorAction(R.id.etCalPerDay)
  public boolean setCalsPerDay() {
    calPerDay = etCalPerDay.getText().toString().trim();
    editor.putString(Constants.CALORIES_PER_DAY, calPerDay);
    editor.apply();

    tvLeftCals.setText(calDiff());
    return false;
  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    calPerDay = sPreferences.getString(Constants.CALORIES_PER_DAY, "0");

    etCalPerDay.setText(calPerDay);
  }

  @Override
  public void onResume() {
    super.onResume();
    etCalPerDay.setText(calPerDay);
  }

  private String calDiff() {
    try {
      return (Double.parseDouble(calPerDay) - Double.parseDouble(totalCals)) + "";
    } catch (NumberFormatException e) {
      AlertDialog dialog = new AlertDialog.Builder(getContext()).create();
      dialog.setMessage("Please type number only...");
      dialog.show();
    }

    return null;
  }

  public void setLeftCals(String totalCals) {
    this.totalCals = totalCals;
    tvLeftCals.setText(calDiff());
  }
}
