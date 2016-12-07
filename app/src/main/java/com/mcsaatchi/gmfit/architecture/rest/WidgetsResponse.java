package com.mcsaatchi.gmfit.architecture.rest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class WidgetsResponse {
  @SerializedName("data") @Expose private WidgetsResponseData data;

  /**
   * @return The data
   */
  public WidgetsResponseData getData() {
    return data;
  }

  /**
   * @param data The data
   */
  public void setData(WidgetsResponseData data) {
    this.data = data;
  }
}
