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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.maiiz.kinaraidee.Constants;
import com.maiiz.kinaraidee.R;
import com.maiiz.kinaraidee.dao.AccessToken;
import com.maiiz.kinaraidee.dao.Element;
import com.maiiz.kinaraidee.dao.Food;
import com.maiiz.kinaraidee.dao.History;
import com.maiiz.kinaraidee.manager.HttpManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by MaiiZ on 5/13/2016 AD.
 */
public class FoodFragment extends Fragment {

  @BindView(R.id.tvFoodName)
  TextView tvFoodName;
  @BindView(R.id.foodImage)
  ImageView foodImage;
  @BindView(R.id.tvRandomFood)
  TextView tvRandomFood;
  @BindView(R.id.mapBtn)
  ImageButton mapBtn;
  @BindView(R.id.addHistoryBtn)
  ImageButton historyBtn;

  private ProgressDialog dialog;
  private SharedPreferences sPreferences;
  private Map<String, String> filters;
  private Food food;

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
    setButtonEnabled(true);

    // case backstack reload image
    if (food != null) {
      Glide.with(getContext())
        .load(food.getImage())
        .bitmapTransform(new CropCircleTransformation(getContext()))
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .into(foodImage);
    } else {
      setFilters();
      randomFood();
    }
  }

  @Override
  public void onResume() {
    super.onResume();
    if (sPreferences.getBoolean(Constants.FILTER_FLAG, false)) {
      setFilters();
      randomFood();
    }
  }

  @Override
  public void onPause() {
    super.onPause();
    clearFilterFlag();
  }

  @OnClick(R.id.foodImage)
  public void scaleUpImage() {
    Fragment fragment = FullImageFragment.newInstance();

    if (fragment instanceof FullImageFragment) {
      FullImageFragment fullFrag = (FullImageFragment) fragment;
      fullFrag.setImage(food.getImage());

      getActivity().getSupportFragmentManager().beginTransaction()
        .setCustomAnimations(
          R.anim.from_right, R.anim.to_left,
          R.anim.from_left, R.anim.to_right
        )
        .replace(R.id.contentContainer, fullFrag)
        .addToBackStack(null)
        .commit();
    }
  }

  @OnClick(R.id.tvRandomFood)
  public void randomFood() {
    dialog = ProgressDialog.show(this.getContext(), null, getResources().getString(R.string.please_wait), true);
    dialog.setCancelable(true);

    Call<Element> retriveFoodCall = HttpManager.getInstance()
      .getService(getAccessToken())
      .randomFood(filters);

    retriveFoodCall.enqueue(new Callback<Element>() {
      @Override
      public void onResponse(Call<Element> call, Response<Element> response) {
        if (response.isSuccessful()) {
          food = response.body().getFood();

          tvFoodName.setText(String.format("%s : %s Kcal", food.getName(), food.getCalories()));
          Glide.with(getContext())
            .load(food.getImage())
            .bitmapTransform(new CropCircleTransformation(getContext()))
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(foodImage);
          setButtonEnabled(true);
        } else {
          try {
            JSONObject jsonObject = new JSONObject(response.errorBody().string());
            String error = jsonObject.getString("message");
            AlertDialog dialog = new AlertDialog.Builder(getContext()).create();
            dialog.setMessage(error);
            dialog.show();
            tvFoodName.setText(R.string.not_found);
            foodImage.setImageResource(R.drawable.no_food);
            setButtonEnabled(false);
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

  @OnClick(R.id.mapBtn)
  public void navigateToMap() {
    Fragment fragment = MapFragment.newInstance(food.getStores());

    if (fragment instanceof MapFragment) {
      getActivity().getSupportFragmentManager().beginTransaction()
        .setCustomAnimations(
          R.anim.from_right, R.anim.to_left,
          R.anim.from_left, R.anim.to_right
        )
        .replace(R.id.contentContainer, fragment)
        .addToBackStack(null)
        .commit();
    }
  }

  @OnClick(R.id.addHistoryBtn)
  public void addHistory() {
    dialog = ProgressDialog.show(this.getContext(), null, getResources().getString(R.string.please_wait), true);
    dialog.setCancelable(true);
    // create History
    History history = new History();
    history.setFoodId(food.getId());

    Element element = new Element();
    element.setHistory(history);

    Call<Element> createHistoryCall = HttpManager
      .getInstance()
      .getService()
      .createHistory(element);

    createHistoryCall.enqueue(new Callback<Element>() {
      @Override
      public void onResponse(Call<Element> call, Response<Element> response) {
        if (response.isSuccessful()) {
          History history = response.body().getHistory();

          Toast.makeText(getContext(), String.format("Add %s to your history", history.getFood().getName()), Toast.LENGTH_LONG).show();
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

  private void clearFilterFlag() {
    SharedPreferences.Editor editor = sPreferences.edit();
    editor.putBoolean(Constants.FILTER_FLAG, false);
    editor.apply();
  }

  private void setFilters() {
    filters.put(Constants.MIN_CALORIES, sPreferences.getString(Constants.MIN_CALORIES, "0"));
    filters.put(Constants.MAX_CALORIES, sPreferences.getString(Constants.MAX_CALORIES, "2000"));
    filters.put(Constants.LIKE_TAGS, sPreferences.getString(Constants.LIKE_TAGS, ""));
    filters.put(Constants.DISLIKE_TAGS, sPreferences.getString(Constants.DISLIKE_TAGS, ""));
    filters.put(Constants.NEAR_RADIUS, sPreferences.getString(Constants.NEAR_RADIUS, "1"));
  }

  private AccessToken getAccessToken() {
    AccessToken accessToken = new AccessToken();
    accessToken.setAccessToken(sPreferences.getString(Constants.ACCESS_TOKEN, null));
    accessToken.setTokenType(sPreferences.getString(Constants.TOKEN_TYPE, null));
    return accessToken;
  }

  private void setButtonEnabled(boolean status) {
    mapBtn.setEnabled(status);
    historyBtn.setEnabled(status);
  }
}
