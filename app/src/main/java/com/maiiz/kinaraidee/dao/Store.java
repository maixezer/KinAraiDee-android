package com.maiiz.kinaraidee.dao;

import com.google.gson.annotations.SerializedName;

/**
 * Created by MaiiZ on 5/16/2016 AD.
 */
public class Store {

  @SerializedName("id") private String id;
  @SerializedName("name") private String name;
  @SerializedName("lat") private String lat;
  @SerializedName("lng") private String lng;
  @SerializedName("created_at") private String createdAt;
  @SerializedName("updated_at") private String updatedAt;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getLat() {
    return lat;
  }

  public void setLat(String lat) {
    this.lat = lat;
  }

  public String getLng() {
    return lng;
  }

  public void setLng(String lng) {
    this.lng = lng;
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
}
