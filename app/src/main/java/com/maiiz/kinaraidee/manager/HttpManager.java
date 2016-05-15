package com.maiiz.kinaraidee.manager;

import android.content.Context;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.maiiz.kinaraidee.Constants;
import com.maiiz.kinaraidee.dao.AccessToken;

import java.io.IOException;

import io.realm.RealmObject;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by MaiiZ on 5/11/2016 AD.
 */
public class HttpManager {

  private static HttpManager instance;
  private static OkHttpClient.Builder httpClient;

  public static HttpManager getInstance() {
    if (instance == null) {
      instance = new HttpManager();
      httpClient = new OkHttpClient.Builder();
    }
    return instance;
  }

  private Context mContext;
  private ApiService service;

  private HttpManager() {
    mContext = Contextor.getInstance().getContext();
  }

  public ApiService getService() {
    return getService(null);
  }

  public ApiService getService(final AccessToken token) {
    if (token != null) {
      httpClient.addInterceptor(new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
          Request original = chain.request();

          Request.Builder requestBuilder = original.newBuilder()
            .header("Accept", "application/json")
            .header("Authorization", String.format("%s %s",
              token.getTokenType(), token.getAccessToken()))
            .method(original.method(), original.body());

          return chain.proceed(requestBuilder.build());
        }
      });
    }

    Gson gson = new GsonBuilder()
      .setExclusionStrategies(new ExclusionStrategy() {
        // This is required to make Gson work with RealmObjects
        @Override public boolean shouldSkipField(FieldAttributes f) {
          return f.getDeclaringClass().equals(RealmObject.class);
        }

        @Override public boolean shouldSkipClass(Class<?> clazz) {
          return false;
        }
      })
      .setDateFormat("yyyy-MM-dd")
      .create();

    OkHttpClient client = httpClient.build();

    Retrofit retrofit = new Retrofit.Builder()
      .baseUrl(Constants.BASE_URL)
      .client(client)
      .addConverterFactory(GsonConverterFactory.create(gson))
      .build();

    return retrofit.create(ApiService.class);
  }
}
