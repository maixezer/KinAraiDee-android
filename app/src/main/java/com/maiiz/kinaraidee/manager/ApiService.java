package com.maiiz.kinaraidee.manager;

import com.maiiz.kinaraidee.dao.AccessToken;
import com.maiiz.kinaraidee.dao.Element;

import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;

/**
 * Created by MaiiZ on 5/11/2016 AD.
 */
public interface ApiService {

  @POST("oauth/token")
  Call<AccessToken> createAccessToken(@Body RequestBody body);

  @GET("api/v1/users/me")
  Call<Element> currentUser();

  @POST("api/v1/users")
  Call<Element> createUser(@Body Element user);

  @GET("api/v1/foods")
  Call<Element> randomFood(@QueryMap Map<String, String> options);
}
