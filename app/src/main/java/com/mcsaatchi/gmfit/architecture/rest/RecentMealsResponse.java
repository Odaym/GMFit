package com.mcsaatchi.gmfit.architecture.rest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RecentMealsResponse {
  @SerializedName("data") @Expose private RecentMealsResponseData data;

  /**
   * @return The data
   */
  public RecentMealsResponseData getData() {
    return data;
  }

  /**
   * @param data The data
   */
  public void setData(RecentMealsResponseData data) {
    this.data = data;
  }
}
