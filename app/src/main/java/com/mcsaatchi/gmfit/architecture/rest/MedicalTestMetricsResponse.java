package com.mcsaatchi.gmfit.architecture.rest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MedicalTestMetricsResponse {
  @SerializedName("data") @Expose private MedicalTestMetricsResponseData data;

  /**
   * @return The data
   */
  public MedicalTestMetricsResponseData getData() {
    return data;
  }

  /**
   * @param data The data
   */
  public void setData(MedicalTestMetricsResponseData data) {
    this.data = data;
  }
}
