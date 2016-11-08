package com.mcsaatchi.gmfit.rest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class EmergencyProfileResponse {

  @SerializedName("data") @Expose private EmergencyProfileResponseData data;

  /**
   * @return The data
   */
  public EmergencyProfileResponseData getData() {
    return data;
  }

  /**
   * @param data The data
   */
  public void setData(EmergencyProfileResponseData data) {
    this.data = data;
  }
}
