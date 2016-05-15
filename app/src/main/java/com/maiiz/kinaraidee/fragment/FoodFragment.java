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
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.maiiz.kinaraidee.Constants;
import com.maiiz.kinaraidee.R;
import com.maiiz.kinaraidee.dao.AccessToken;
import com.maiiz.kinaraidee.dao.Element;
import com.maiiz.kinaraidee.dao.Food;
import com.maiiz.kinaraidee.manager.HttpManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by MaiiZ on 5/13/2016 AD.
 */
public class FoodFragment extends Fragment {

  @BindView(R.id.tvFoodName) TextView tvFoodName;
  @BindView(R.id.foodImage) ImageView foodImage;
  @BindView(R.id.tvRandomFood) TextView tvRandomFood;

  private ProgressDialog dialog;
  private SharedPreferences sPreferences;
  private Map<String, String> filters;

  public static FoodFragment newInstance() {
    FoodFragment fragment = new FoodFragment();
    return fragment;
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.fragment_food, container, false);
    ButterKnife.bind(this, rootView);
    initInstances();
    return rootView;
  }

  private void initInstances() {
    sPreferences = getActivity().getSharedPreferences(Constants.APP_NAME, Context.MODE_PRIVATE);
    filters = new HashMap<>();
    randomFood();
  }

  @Override
  public void onResume() {
    super.onResume();

    if (sPreferences.getBoolean(Constants.FILTER_FLAG, false)) {
      filters.put(Constants.MIN_CALORIES, sPreferences.getString(Constants.MIN_CALORIES, "0"));
      filters.put(Constants.MAX_CALORIES, sPreferences.getString(Constants.MAX_CALORIES, "2000"));
      filters.put(Constants.LIKE_TAGS, sPreferences.getString(Constants.LIKE_TAGS, ""));
      filters.put(Constants.DISLIKE_TAGS, sPreferences.getString(Constants.DISLIKE_TAGS, ""));

      Log.d("min_calories", filters.get(Constants.MIN_CALORIES));
      Log.d("max_calories", filters.get(Constants.MAX_CALORIES));

      randomFood();
    }
  }

  @Override
  public void onStop() {
    super.onStop();
    SharedPreferences.Editor editor = getActivity().getSharedPreferences(Constants.APP_NAME, Context.MODE_PRIVATE).edit();
    editor.putBoolean(Constants.FILTER_FLAG, false);
    editor.apply();
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    SharedPreferences.Editor editor = getActivity().getSharedPreferences(Constants.APP_NAME, Context.MODE_PRIVATE).edit();
    editor.putBoolean(Constants.FILTER_FLAG, false);
    editor.apply();
  }

  @OnClick(R.id.tvRandomFood)
  public void randomFood() {
    dialog = ProgressDialog.show(this.getContext(), null, getResources().getString(R.string.please_wait), true);
    dialog.setCancelable(true);

    // get Access Token
    AccessToken accessToken = new AccessToken();
    accessToken.setAccessToken(sPreferences.getString(Constants.ACCESS_TOKEN, null));
    accessToken.setTokenType(sPreferences.getString(Constants.TOKEN_TYPE, null));

    Call<Element> retriveFoodCall = HttpManager.getInstance()
      .getService(accessToken)
      .randomFood(filters);

    retriveFoodCall.enqueue(new Callback<Element>() {
      @Override
      public void onResponse(Call<Element> call, Response<Element> response) {
        if (response.isSuccessful()) {
          Food food = response.body().getFood();

          tvFoodName.setText(String.format("%s : %s Kcal", food.getName(), food.getCalories()));
          Glide.with(FoodFragment.this.getContext()).load(food.getImage()).into(foodImage);
        } else {
          try {
            JSONObject jsonObject = new JSONObject(response.errorBody().string());
            String error = jsonObject.getString("message");
            AlertDialog dialog = new AlertDialog.Builder(FoodFragment.this.getContext()).create();
            dialog.setMessage(error);
            dialog.show();
            tvFoodName.setText(R.string.not_found);
            foodImage.setImageResource(android.R.color.transparent);
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
        dialog.dismiss();
      }
    });
  }
}
