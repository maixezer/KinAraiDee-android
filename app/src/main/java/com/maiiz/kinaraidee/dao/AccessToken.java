package com.maiiz.kinaraidee.dao;

import com.google.gson.annotations.SerializedName;

/**
 * Created by MaiiZ on 5/11/2016 AD.
 */
public class AccessToken {

  @SerializedName("access_token") private String accessToken;
  @SerializedName("token_type") private String tokenType;

  public String getAccessToken() {
    return accessToken;
  }

  public void setAccessToken(String accessToken) {
    this.accessToken = accessToken;
  }

  public String getTokenType() {
    // OAuth requires uppercase Authorization HTTP header value for token type
    if ( ! Character.isUpperCase(tokenType.charAt(0))) {
      tokenType = Character
        .toString(tokenType.charAt(0))
        .toUpperCase() + tokenType.substring(1);
    }

    return tokenType;
  }

  public void setTokenType(String tokenType) {
    this.tokenType = tokenType;
  }
}
