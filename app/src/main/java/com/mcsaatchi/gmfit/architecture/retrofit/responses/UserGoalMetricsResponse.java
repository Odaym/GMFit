package com.mcsaatchi.gmfit.architecture.retrofit.responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserGoalMetricsResponse {
  @SerializedName("data") @Expose private UserGoalMetricsResponseData data;

  /**
   * @return The data
   */
  public UserGoalMetricsResponseData getData() {
    return data;
  }

  /**
   * @param data The data
   */
  public void setData(UserGoalMetricsResponseData data) {
    this.data = data;
  }
}
