package com.maiiz.kinaraidee.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.maiiz.kinaraidee.Constants;
import com.maiiz.kinaraidee.R;

import org.florescu.android.rangeseekbar.RangeSeekBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnEditorAction;

/**
 * Created by MaiiZ on 5/14/2016 AD.
 */
public class FilterFragment extends Fragment {

  private String minCals;
  private String maxCals;
  private SharedPreferences.Editor editor;

  @BindView(R.id.rsbCalories) RangeSeekBar<Integer> rsbCalories;
  @BindView(R.id.tvMinrsb) TextView tvMinrsb;
  @BindView(R.id.tvMaxrsb) TextView tvMaxrsb;
  @BindView(R.id.etLikeTag) EditText etLikeTag;
  @BindView(R.id.etNotLikeTag) EditText etNotLikeTag;
  @BindView(R.id.etNearRadius) EditText etNearRadius;

  public static FilterFragment newInstance() {
    FilterFragment fragment = new FilterFragment();
    return fragment;
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.fragment_filter, container, false);
    ButterKnife.bind(this, rootView);
    initInstances();
    return rootView;
  }

  private void initInstances() {
    editor = getActivity().getSharedPreferences(Constants.APP_NAME, Context.MODE_PRIVATE).edit();

    // clear flag
    editor.putBoolean(Constants.FILTER_FLAG, false);
    editor.apply();

    rsbCalories.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener<Integer>() {
      @Override
      public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Integer minValue, Integer maxValue) {
        minCals = bar.getSelectedMinValue().toString();
        maxCals = bar.getSelectedMaxValue().toString();

        tvMinrsb.setText(minCals);
        tvMaxrsb.setText(maxCals);

        // put min and max cal to sPreferences
        editor.putString(Constants.MIN_CALORIES, minCals);
        editor.putString(Constants.MAX_CALORIES, maxCals);
        editor.putBoolean(Constants.FILTER_FLAG, true);
        editor.apply();
      }
    });

    rsbCalories.setNotifyWhileDragging(true);
  }

  @OnEditorAction({R.id.etLikeTag, R.id.etNotLikeTag, R.id.etNearRadius})
  public boolean setFilters() {
    editor.putBoolean(Constants.FILTER_FLAG, true);
    editor.putString(Constants.LIKE_TAGS, etLikeTag.getText().toString().trim());
    editor.putString(Constants.DISLIKE_TAGS, etNotLikeTag.getText().toString().trim());
    editor.putString(Constants.NEAR_RADIUS, etNearRadius.getText().toString().trim());
    editor.apply();
    return false;
  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    // set Filter Options ( From sPreferences )
    SharedPreferences sPreferences = getActivity().getSharedPreferences(Constants.APP_NAME, Context.MODE_PRIVATE);

    minCals = sPreferences.getString(Constants.MIN_CALORIES, "0");
    maxCals = sPreferences.getString(Constants.MAX_CALORIES, "2000");

    tvMinrsb.setText(minCals);
    tvMaxrsb.setText(maxCals);

    rsbCalories.setSelectedMinValue(minCals != null ? Integer.parseInt(minCals) : 0);
    rsbCalories.setSelectedMaxValue(maxCals != null ? Integer.parseInt(maxCals) : 2000);

    etLikeTag.setText(sPreferences.getString(Constants.LIKE_TAGS, ""));
    etNotLikeTag.setText(sPreferences.getString(Constants.DISLIKE_TAGS, ""));
    etNearRadius.setText(sPreferences.getString(Constants.NEAR_RADIUS, ""));
  }
}
