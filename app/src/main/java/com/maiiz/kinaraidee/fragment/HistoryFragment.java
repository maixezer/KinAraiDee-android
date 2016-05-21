package com.maiiz.kinaraidee.fragment;

import android.app.ProgressDialog;
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
import android.widget.ListView;

import com.maiiz.kinaraidee.Constants;
import com.maiiz.kinaraidee.R;
import com.maiiz.kinaraidee.activity.AccountSettingsActivity;
import com.maiiz.kinaraidee.adapter.HistoryListAdapter;
import com.maiiz.kinaraidee.dao.AccessToken;
import com.maiiz.kinaraidee.dao.Element;
import com.maiiz.kinaraidee.dao.History;
import com.maiiz.kinaraidee.manager.HttpManager;
import com.maiiz.kinaraidee.manager.IFragmentActivity;
import com.maiiz.kinaraidee.util.UtilsDate;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by MaiiZ on 5/19/2016 AD.
 */
public class HistoryFragment extends Fragment {

  @BindView(R.id.listView) ListView listView;

  private HistoryListAdapter historyListAdapter;
  private IFragmentActivity aCallback;
  private SharedPreferences sPreferences;
  private ProgressDialog dialog;
  private List<History> histories;

  public static HistoryFragment newInstance() {
    HistoryFragment fragment = new HistoryFragment();
    return fragment;
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.fragment_history, container, false);
    ButterKnife.bind(this, rootView);
    initInstances();
    return rootView;
  }

  private void initInstances() {
    sPreferences = getActivity().getSharedPreferences(Constants.APP_NAME, Context.MODE_PRIVATE);

    if (histories == null) {
      retriveHistory();
    }
  }

  @Override
  public void onPause() {
    super.onPause();
    histories = null;
  }

  @Override
  public void onStop() {
    super.onStop();
    histories = null;
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    try {
      aCallback = (IFragmentActivity) context;
    } catch (ClassCastException e) {
      throw new ClassCastException(context.toString()
        + " must implement IFragmentToActivity");
    }
  }

  @Override
  public void onDetach() {
    aCallback = null;
    super.onDetach();
  }

  private void retriveHistory() {
    dialog = ProgressDialog.show(this.getContext(), null, getResources().getString(R.string.please_wait), true);
    dialog.setCancelable(true);

    // get all history
    Call<Element> retriveHistoryCall = HttpManager
      .getInstance()
      .getService(getAccessToken())
      .getHistory();

    retriveHistoryCall.enqueue(new Callback<Element>() {
      @Override
      public void onResponse(Call<Element> call, Response<Element> response) {
        if (response.isSuccessful()) {
          histories = response.body().getHistories();

          aCallback.notifyTotalCals(calculateCals(), HistoryFragment.this);

          historyListAdapter = new HistoryListAdapter(histories);
          listView.setAdapter(historyListAdapter);
        } else {
          try {
            JSONObject jsonObject = new JSONObject(response.errorBody().string());
            String error = jsonObject.getString("errors");
            AlertDialog dialog = new AlertDialog.Builder(getContext()).create();
            dialog.setMessage(error);
            dialog.show();
          } catch (IOException e) {
            e.printStackTrace();
          } catch (JSONException e) {
            e.printStackTrace();
          }
          Log.e("errors", response.raw().toString());
        }
        dialog.dismiss();
      }

      @Override
      public void onFailure(Call<Element> call, Throwable t) {

      }
    });
  }

  private String calculateCals() {
    double sum = 0;
    for (History h : histories) {
      if(UtilsDate.getCurrentDate().equals(UtilsDate.convertDate(h.getCreatedAt()))) {
        sum += Double.parseDouble(h.getFood().getCalories());
      }
    }
    return sum + "";
  }

  private AccessToken getAccessToken() {
    AccessToken accessToken = new AccessToken();
    accessToken.setAccessToken(sPreferences.getString(Constants.ACCESS_TOKEN, null));
    accessToken.setTokenType(sPreferences.getString(Constants.TOKEN_TYPE, null));
    return accessToken;
  }
}
