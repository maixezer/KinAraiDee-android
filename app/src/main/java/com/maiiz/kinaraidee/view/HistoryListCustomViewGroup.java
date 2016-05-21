package com.maiiz.kinaraidee.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.maiiz.kinaraidee.R;

import butterknife.ButterKnife;

/**
 * Created by MaiiZ on 5/21/2016 AD.
 */
public class HistoryListCustomViewGroup extends FrameLayout {

  public HistoryListCustomViewGroup(Context context) {
    super(context);
    initInflate();
    initInstances();
  }

  public HistoryListCustomViewGroup(Context context, AttributeSet attrs) {
    super(context, attrs);
    initInflate();
    initInstances();
  }

  public HistoryListCustomViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    initInflate();
    initInstances();
  }

  @TargetApi(21)
  public HistoryListCustomViewGroup(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
    initInflate();
    initInstances();
  }

  private void initInflate() {
    inflate(getContext(), R.layout.list_item_history, this);
  }

  private void initInstances() {
    ButterKnife.bind(this);
  }
}
