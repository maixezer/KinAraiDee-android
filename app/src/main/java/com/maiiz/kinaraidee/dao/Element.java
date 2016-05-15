package com.maiiz.kinaraidee.dao;

import com.google.gson.annotations.SerializedName;

/**
 * Created by MaiiZ on 5/11/2016 AD.
 */
public class Element {

  @SerializedName("user") User user;
  @SerializedName("food") Food food;

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public Food getFood() {
    return food;
  }

  public void setFood(Food food) {
    this.food = food;
  }
}
