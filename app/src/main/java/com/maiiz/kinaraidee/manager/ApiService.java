package com.maiiz.kinaraidee.manager;

import com.maiiz.kinaraidee.dao.AccessToken;
import com.maiiz.kinaraidee.dao.Element;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Created by MaiiZ on 5/11/2016 AD.
 */
public interface ApiService {

  @POST("oauth/token")
  Call<AccessToken> createAccessToken(@Body RequestBody body);

  @GET("api/v1/users/me")
  Call<Element> currentUser();
}
