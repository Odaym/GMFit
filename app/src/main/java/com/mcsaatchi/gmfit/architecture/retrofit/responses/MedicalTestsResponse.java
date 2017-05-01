package com.mcsaatchi.gmfit.architecture.retrofit.responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MedicalTestsResponse {
  @SerializedName("data") @Expose private MedicalTestsResponseData data;

  /**
   * @return The data
   */
  public MedicalTestsResponseData getData() {
    return data;
  }

  /**
   * @param data The data
   */
  public void setData(MedicalTestsResponseData data) {
    this.data = data;
  }
}
