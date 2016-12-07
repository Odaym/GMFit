package com.mcsaatchi.gmfit.architecture.rest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserProfileResponse {
  @SerializedName("data") @Expose private UserProfileResponseData data;

  /**
   * @return The data
   */
  public UserProfileResponseData getData() {
    return data;
  }

  /**
   * @param data The data
   */
  public void setData(UserProfileResponseData data) {
    this.data = data;
  }
}
