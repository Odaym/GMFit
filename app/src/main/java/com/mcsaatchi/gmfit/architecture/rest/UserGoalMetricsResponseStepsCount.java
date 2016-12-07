package com.mcsaatchi.gmfit.architecture.rest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserGoalMetricsResponseStepsCount {
  @SerializedName("slug") @Expose private String slug;
  @SerializedName("min_value") @Expose private String minValue;
  @SerializedName("max_value") @Expose private String maxValue;
  @SerializedName("value") @Expose private String value;

  /**
   * @return The slug
   */
  public String getSlug() {
    return slug;
  }

  /**
   * @param slug The slug
   */
  public void setSlug(String slug) {
    this.slug = slug;
  }

  /**
   * @return The minValue
   */
  public String getMinValue() {
    return minValue;
  }

  /**
   * @param minValue The min_value
   */
  public void setMinValue(String minValue) {
    this.minValue = minValue;
  }

  /**
   * @return The maxValue
   */
  public String getMaxValue() {
    return maxValue;
  }

  /**
   * @param maxValue The max_value
   */
  public void setMaxValue(String maxValue) {
    this.maxValue = maxValue;
  }

  /**
   * @return The value
   */
  public String getValue() {
    return value;
  }

  /**
   * @param value The value
   */
  public void setValue(String value) {
    this.value = value;
  }
}
