package com.mcsaatchi.gmfit.architecture.rest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TakenMedicalTestsResponse {
  @SerializedName("data") @Expose private TakenMedicalTestsResponseData data;

  /**
   * @return The data
   */
  public TakenMedicalTestsResponseData getData() {
    return data;
  }

  /**
   * @param data The data
   */
  public void setData(TakenMedicalTestsResponseData data) {
    this.data = data;
  }
}
