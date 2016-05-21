package com.maiiz.kinaraidee.adapter;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.maiiz.kinaraidee.R;
import com.maiiz.kinaraidee.dao.History;
import com.maiiz.kinaraidee.util.UtilsDate;
import com.maiiz.kinaraidee.view.HistoryCustomViewGroup;
import com.maiiz.kinaraidee.view.HistoryListCustomViewGroup;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by MaiiZ on 5/21/2016 AD.
 */
public class HistoryListAdapter extends BaseAdapter {

  private TextView tvDate;
  private LinearLayout foodList;

  private Map<String, List<History>> mHistories;
  private List<String> dateHistories;
  private int historyCount;

  public HistoryListAdapter(List<History> histories) {
    groupHistories(histories);
    this.historyCount = mHistories.size();
  }

  @Override
  public int getCount() {
    return historyCount;
  }

  @Override
  public Object getItem(int position) {
    return null;
  }

  @Override
  public long getItemId(int position) {
    return 0;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    HistoryListCustomViewGroup item;

    if (convertView != null) {
      item = (HistoryListCustomViewGroup) convertView;
    } else {
      item = new HistoryListCustomViewGroup(parent.getContext());
      initInstances(position, item);
    }
    return item;
  }

  private void initInstances(int position, HistoryListCustomViewGroup item) {
    String date = dateHistories.get(position);
    tvDate = (TextView) item.findViewById(R.id.tvDate);
    foodList = (LinearLayout) item.findViewById(R.id.foodList);

    tvDate.setText(String.format("Date : %s", date));
    initFoodList(date);
  }

  private void initFoodList(String date) {
    HistoryCustomViewGroup child = null;
    for (History h : mHistories.get(date)) {
      child = new HistoryCustomViewGroup(foodList.getContext());
      child.setFoodText(h.getFood().getName(), h.getFood().getCalories());
      foodList.addView(child);
    }
  }

  private void groupHistories(List<History> histories) {
    mHistories = new HashMap<>();
    dateHistories = new ArrayList<>();

    for (int i = 0; i < histories.size(); i++) {
      String date = UtilsDate.convertDate(histories.get(i).getCreatedAt());

      if (!mHistories.containsKey(date)) {
        mHistories.put(date, new ArrayList<History>());
        mHistories.get(date).add(histories.get(i));
        dateHistories.add(date);
      } else {
        mHistories.get(date).add(histories.get(i));
      }
    }
  }
}
