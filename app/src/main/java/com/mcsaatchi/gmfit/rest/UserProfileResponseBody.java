package com.mcsaatchi.gmfit.rest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserProfileResponseBody {

  @SerializedName("data") @Expose private UserProfileResponseDatum data;

  /**
   * @return The data
   */
  public UserProfileResponseDatum getData() {
    return data;
  }

  /**
   * @param data The data
   */
  public void setData(UserProfileResponseDatum data) {
    this.data = data;
  }
}
