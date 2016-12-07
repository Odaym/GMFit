package com.mcsaatchi.gmfit.architecture.rest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserGoalMetricsResponseMetrics {
  @SerializedName("steps-count") @Expose private UserGoalMetricsResponseStepsCount stepsCount;
  @SerializedName("active-calories") @Expose private UserGoalMetricsResponseActiveCalories
      activeCalories;
  @SerializedName("calories") @Expose private UserGoalMetricsResponseActiveCalories calories;
  @SerializedName("distance-traveled") @Expose private UserGoalMetricsResponseDistanceTraveled
      distanceTraveled;

  /**
   * @return The stepsCount
   */
  public UserGoalMetricsResponseStepsCount getStepsCount() {
    return stepsCount;
  }

  /**
   * @param stepsCount The steps-count
   */
  public void setStepsCount(UserGoalMetricsResponseStepsCount stepsCount) {
    this.stepsCount = stepsCount;
  }

  /**
   * @return The activeCalories
   */
  public UserGoalMetricsResponseActiveCalories getActiveCalories() {
    return activeCalories;
  }

  /**
   * @param activeCalories The active-calories
   */
  public void setActiveCalories(UserGoalMetricsResponseActiveCalories activeCalories) {
    this.activeCalories = activeCalories;
  }

  /**
   * @return The distanceTraveled
   */
  public UserGoalMetricsResponseDistanceTraveled getDistanceTraveled() {
    return distanceTraveled;
  }

  /**
   * @param distanceTraveled The distance-traveled
   */
  public void setDistanceTraveled(UserGoalMetricsResponseDistanceTraveled distanceTraveled) {
    this.distanceTraveled = distanceTraveled;
  }

  public UserGoalMetricsResponseActiveCalories getCalories() {
    return calories;
  }

  public void setCalories(UserGoalMetricsResponseActiveCalories calories) {
    this.calories = calories;
  }
}
