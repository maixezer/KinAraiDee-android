package com.maiiz.kinaraidee.dao;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by MaiiZ on 5/11/2016 AD.
 */
public class Element {

  @SerializedName("user") User user;
  @SerializedName("food") Food food;
  @SerializedName("history") History history;
  @SerializedName("histories") List<History> histories;

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

  public History getHistory() {
    return history;
  }

  public void setHistory(History history) {
    this.history = history;
  }

  public List<History> getHistories() {
    return histories;
  }

  public void setHistories(List<History> histories) {
    this.histories = histories;
  }
}
