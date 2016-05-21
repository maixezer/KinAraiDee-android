package com.maiiz.kinaraidee.dao;

import com.google.gson.annotations.SerializedName;

/**
 * Created by MaiiZ on 5/20/2016 AD.
 */
public class History {
  @SerializedName("id") private String id;
  @SerializedName("created_at") private String createdAt;
  @SerializedName("updated_at") private String updatedAt;
  @SerializedName("user") private User user;
  @SerializedName("food") private Food food;
  @SerializedName("food_id") private String foodId;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(String createdAt) {
    this.createdAt = createdAt;
  }

  public String getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(String updatedAt) {
    this.updatedAt = updatedAt;
  }

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

  public String getFoodId() {
    return foodId;
  }

  public void setFoodId(String foodId) {
    this.foodId = foodId;
  }
}
