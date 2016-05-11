package com.maiiz.kinaraidee.dao;

import com.google.gson.annotations.SerializedName;

/**
 * Created by MaiiZ on 5/11/2016 AD.
 */
public class Element {

  @SerializedName("user") User user;

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }
}
