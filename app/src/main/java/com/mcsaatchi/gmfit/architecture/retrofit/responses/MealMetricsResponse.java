package com.mcsaatchi.gmfit.architecture.retrofit.responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MealMetricsResponse {
  @SerializedName("data") @Expose private MealMetricsResponseData data;

  /**
   * @return The data
   */
  public MealMetricsResponseData getData() {
    return data;
  }

  /**
   * @param data The data
   */
  public void setData(MealMetricsResponseData data) {
    this.data = data;
  }
}
