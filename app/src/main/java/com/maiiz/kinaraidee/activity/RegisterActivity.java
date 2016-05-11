
package com.maiiz.kinaraidee.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
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
import com.maiiz.kinaraidee.dao.Element;
import com.maiiz.kinaraidee.dao.User;
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

public class RegisterActivity extends AppCompatActivity {

  @BindView(R.id.etEmail) EditText etEmail;
  @BindView(R.id.etPassword) EditText etPassword;
  @BindView(R.id.etPasswordConfirmation) EditText etPasswordConfirmation;
  @BindView(R.id.etFirstName) EditText etFirstName;
  @BindView(R.id.etLastName) EditText etLastName;
  @BindView(R.id.btnSignUp) Button btnSignUp;
  @BindView(R.id.tvSignIn) TextView tvSignIn;

  private ProgressDialog dialog;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_register);
    ButterKnife.bind(this);
    initInstances();
  }

  @OnClick(R.id.btnSignUp)
  public void signup() {
    // Init ProgressDialog
    dialog = ProgressDialog.show(RegisterActivity.this, null, getResources().getString(R.string.please_wait), true);
    dialog.setCancelable(true);

    final String email = etEmail.getText().toString().trim();
    final String password = etPassword.getText().toString().trim();
    String passwordConfirmation = etPasswordConfirmation.getText().toString().trim();
    String firstName = etFirstName.getText().toString().trim();
    String lastName = etLastName.getText().toString().trim();

    // Create User
    User user = new User();
    user.setEmail(email);
    user.setFirstName(firstName);
    user.setLastName(lastName);
    user.setPassword(password);
    user.setPasswordConfirmation(passwordConfirmation);

    // Put User into Element
    Element element = new Element();
    element.setUser(user);

    Call<Element> createUserCall = HttpManager
      .getInstance()
      .getService()
      .createUser(element);

    createUserCall.enqueue(new Callback<Element>() {
      @Override
      public void onResponse(Call<Element> call, Response<Element> response) {
        if (response.isSuccessful()) {
          User user = response.body().getUser();

          Map<String, String> map = new HashMap<String, String>();
          map.put("client_id", Constants.CLIENT_ID);
          map.put("client_secret", Constants.CLIENT_SECRET);
          map.put("grant_type", Constants.GRANT_TYPE);
          map.put("email", email);
          map.put("password", password);

          signInAfterSignUp(map);
        } else {
          dialog.dismiss();

          Log.d("error", response.raw().toString());
          try {
            JSONObject jsonObject = new JSONObject(response.errorBody().string());
            String error = jsonObject.getString("errors");
            AlertDialog dialog = new AlertDialog.Builder(RegisterActivity.this).create();
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

  @OnClick(R.id.tvSignIn)
  public void navigateToSignIn() {
    finish();
  }

  public void navigateToMain() {
    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
    startActivity(intent);
  }

  public void signInAfterSignUp(Map<String, String> map) {
    Call<AccessToken> accessTokenCall = HttpManager
      .getInstance()
      .getService().createAccessToken(
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

          dialog.dismiss();
          navigateToMain();
        } else {
          dialog.dismiss();

          try {
            JSONObject jsonObject = new JSONObject(response.errorBody().string());
            String error = jsonObject.getString("error");
            AlertDialog dialog = new AlertDialog.Builder(RegisterActivity.this).create();
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

  private void initInstances() {
    tvSignIn.setPaintFlags(tvSignIn.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
  }
}
