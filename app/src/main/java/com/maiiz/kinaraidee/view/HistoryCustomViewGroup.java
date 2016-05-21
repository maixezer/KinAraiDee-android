package com.maiiz.kinaraidee.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.maiiz.kinaraidee.Constants;
import com.maiiz.kinaraidee.R;
import com.maiiz.kinaraidee.dao.AccessToken;
import com.maiiz.kinaraidee.dao.Element;
import com.maiiz.kinaraidee.dao.History;
import com.maiiz.kinaraidee.manager.HttpManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by MaiiZ on 5/21/2016 AD.
 */
public class HistoryCustomViewGroup extends FrameLayout {

  @BindView(R.id.tvFoodName) TextView tvFoodName;
  @BindView(R.id.tvFoodCal) TextView tvFoodCal;
  @BindView(R.id.delBtn) ImageButton delBtn;

  private SharedPreferences sPreferences;
  private History history;
  private LinearLayout foodList;

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
    sPreferences = getContext().getSharedPreferences(Constants.APP_NAME, Context.MODE_PRIVATE);
    ButterKnife.bind(this);
  }

  public void setHistory(History history) {
    this.history = history;
    setFoodText();
  }

  public void setFoodList(LinearLayout foodList) {
    this.foodList = foodList;
  }

  private void setFoodText() {
    tvFoodName.setText(history.getFood().getName());
    tvFoodCal.setText(history.getFood().getCalories());
  }

  @OnClick(R.id.delBtn)
  public void deleteHistory() {
    Call<Element> deleteHistoryCall = HttpManager
      .getInstance()
      .getService(getAccessToken())
      .deleteHistory(history.getId());

    deleteHistoryCall.enqueue(new Callback<Element>() {
      @Override
      public void onResponse(Call<Element> call, Response<Element> response) {
        if (response.isSuccessful()) {
          Toast.makeText(getContext(), "Delete History Successfully", Toast.LENGTH_LONG)
            .show();
          foodList.removeView(HistoryCustomViewGroup.this);
          foodList.invalidate();
        } else {
          Log.e("errors", response.raw().toString());
          try {
            JSONObject jsonObject = new JSONObject(response.errorBody().string());
            String error = jsonObject.getString("error");
            AlertDialog dialog = new AlertDialog.Builder(getContext()).create();
            dialog.setMessage(error);
            dialog.show();
          } catch (IOException e) {
            e.printStackTrace();
          } catch (JSONException e) {
            e.printStackTrace();
          }
        }
      }

      @Override
      public void onFailure(Call<Element> call, Throwable t) {

      }
    });
  }

  private AccessToken getAccessToken() {
    AccessToken accessToken = new AccessToken();
    accessToken.setAccessToken(sPreferences.getString(Constants.ACCESS_TOKEN, null));
    accessToken.setTokenType(sPreferences.getString(Constants.TOKEN_TYPE, null));
    return accessToken;
  }
}