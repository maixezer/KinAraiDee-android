package com.maiiz.kinaraidee.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.maiiz.kinaraidee.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by MaiiZ on 5/21/2016 AD.
 */
public class HistoryCustomViewGroup extends FrameLayout {

  @BindView(R.id.tvFoodName) TextView tvFoodName;
  @BindView(R.id.tvFoodCal) TextView tvFoodCal;

  public HistoryCustomViewGroup(Context context) {
    super(context);
    initInflate();
    initInstances();
  }

  public HistoryCustomViewGroup(Context context, AttributeSet attrs) {
    super(context, attrs);
    initInflate();
    initInstances();
  }

  public HistoryCustomViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    initInflate();
    initInstances();
  }

  @TargetApi(21)
  public HistoryCustomViewGroup(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
    initInflate();
    initInstances();
  }

  private void initInflate() {
    inflate(getContext(), R.layout.item_history, this);
  }

  private void initInstances() {
    ButterKnife.bind(this);
  }

  public void setFoodText(String foodName, String foodCals) {
    tvFoodName.setText(foodName);
    tvFoodCal.setText(foodCals);
  }
}