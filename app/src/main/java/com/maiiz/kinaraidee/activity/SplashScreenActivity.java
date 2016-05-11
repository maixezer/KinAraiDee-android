package com.maiiz.kinaraidee.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.maiiz.kinaraidee.Constants;
import com.maiiz.kinaraidee.R;
import com.maiiz.kinaraidee.dao.AccessToken;
import com.maiiz.kinaraidee.dao.Element;
import com.maiiz.kinaraidee.manager.HttpManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashScreenActivity extends AppCompatActivity implements Runnable {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_splash_screen);
    new Handler().postDelayed(this, 3000);
  }

  @Override
  public void run() {
    SharedPreferences sPreferences = getSharedPreferences(Constants.APP_NAME, Context.MODE_PRIVATE);
    AccessToken accessToken = new AccessToken();
    accessToken.setAccessToken(sPreferences.getString(Constants.ACCESS_TOKEN, null));
    accessToken.setTokenType(sPreferences.getString(Constants.TOKEN_TYPE, null));

    if (accessToken.getAccessToken() == null) {
      navigateToLogin();
    } else {
      Call<Element> element = HttpManager.getInstance().getService(accessToken).currentUser();

      element.enqueue(new Callback<Element>() {
        @Override
        public void onResponse(Call<Element> call, Response<Element> response) {
          if (response.isSuccessful()) {
            navigateToMain();
          } else {
            Log.e("errors", response.raw().toString());
            try {
              JSONObject jsonObject = new JSONObject(response.errorBody().string());
              String error = jsonObject.getString("error");
              AlertDialog dialog = new AlertDialog.Builder(SplashScreenActivity.this).create();
              dialog.setMessage(error);
              dialog.show();
            } catch (IOException e) {
              e.printStackTrace();
            } catch (JSONException e) {
              e.printStackTrace();
            }
            navigateToLogin();
          }
        }

        @Override
        public void onFailure(Call<Element> call, Throwable t) {
          navigateToLogin();
        }
      });
    }
  }

  public void navigateToLogin() {
    Intent intent = new Intent(this, LoginActivity.class);
    startActivity(intent);
    finish();
  }

  public void navigateToMain() {
    Intent intent = new Intent(this, MainActivity.class);
    startActivity(intent);
    finish();
  }
}
