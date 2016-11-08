package com.mcsaatchi.gmfit.rest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UiResponse {
  @SerializedName("data") @Expose private UiResponseData data;

  /**
   * @return The data
   */
  public UiResponseData getData() {
    return data;
  }

  /**
   * @param data The data
   */
  public void setData(UiResponseData data) {
    this.data = data;
  }
}
