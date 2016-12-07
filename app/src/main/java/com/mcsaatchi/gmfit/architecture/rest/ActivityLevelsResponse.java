package com.mcsaatchi.gmfit.architecture.rest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ActivityLevelsResponse {

  @SerializedName("data") @Expose private ActivityLevelsResponseData data;

  /**
   * @return The data
   */
  public ActivityLevelsResponseData getData() {
    return data;
  }

  /**
   * @param data The data
   */
  public void setData(ActivityLevelsResponseData data) {
    this.data = data;
  }
}
