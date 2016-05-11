package com.maiiz.kinaraidee.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.maiiz.kinaraidee.Constants;
import com.maiiz.kinaraidee.R;
import com.maiiz.kinaraidee.dao.AccessToken;
import com.maiiz.kinaraidee.manager.HttpManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

  @BindView(R.id.etEmail) EditText etEmail;
  @BindView(R.id.etPassword) EditText etPassword;
  @BindView(R.id.btnSignIn) Button btnSignIn;
  @BindView(R.id.tvRegister) TextView tvRegister;

  private ProgressDialog dialog;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);
    ButterKnife.bind(this);
  }

  @OnClick(R.id.tvRegister)
  public void navigateToRegister() {
    Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
    startActivity(intent);
  }

  @OnClick(R.id.btnSignIn)
  public void signin() {
    // Init ProgressDialog
    dialog = ProgressDialog.show(LoginActivity.this, null, getResources().getString(R.string.please_wait), true);
    dialog.setCancelable(true);

    String email = etEmail.getText().toString().trim();
    String password = etPassword.getText().toString().trim();

    Map<String, String> map = new HashMap<>();
    map.put("client_id", Constants.CLIENT_ID);
    map.put("client_secret", Constants.CLIENT_SECRET);
    map.put("grant_type", Constants.GRANT_TYPE);
    map.put("email", email);
    map.put("password", password);

    Call<AccessToken> accessTokenCall =
      HttpManager
        .getInstance()
        .getService()
        .createAccessToken(
          RequestBody.create(MediaType.parse("application/json"), (new JSONObject(map)).toString())
        );

    accessTokenCall.enqueue(new Callback<AccessToken>() {
      @Override
      public void onResponse(Call<AccessToken> call, Response<AccessToken> response) {
        if (response.isSuccessful()) {
          AccessToken accessToken = response.body();

          SharedPreferences.Editor editor = getSharedPreferences(Constants.APP_NAME, Context.MODE_PRIVATE).edit();
          editor.putString(Constants.ACCESS_TOKEN, accessToken.getAccessToken());
          editor.putString(Constants.TOKEN_TYPE, accessToken.getTokenType());
          editor.apply();

          navigateToMain();
          dialog.dismiss();
        } else {
          dialog.dismiss();

          try {
            JSONObject jsonObject = new JSONObject(response.errorBody().string());
            String error = jsonObject.getString("error");
            AlertDialog dialog = new AlertDialog.Builder(LoginActivity.this).create();
            dialog.setMessage(error);

            dialog.show();
          } catch (IOException e) {
            e.printStackTrace();
          } catch (JSONException e) {
            e.printStackTrace();
          }
          Log.d("error", response.raw().toString());
        }
      }

      @Override
      public void onFailure(Call<AccessToken> call, Throwable t) {

      }
    });
  }

  public void navigateToMain() {
    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
    startActivity(intent);
    finish();
  }
}
