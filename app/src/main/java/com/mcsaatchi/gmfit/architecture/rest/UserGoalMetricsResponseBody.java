package com.mcsaatchi.gmfit.architecture.rest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserGoalMetricsResponseBody {
  @SerializedName("metrics") @Expose private UserGoalMetricsResponseMetrics metrics;

  /**
   * @return The metrics
   */
  public UserGoalMetricsResponseMetrics getMetrics() {
    return metrics;
  }

  /**
   * @param metrics The metrics
   */
  public void setMetrics(UserGoalMetricsResponseMetrics metrics) {
    this.metrics = metrics;
  }
}
