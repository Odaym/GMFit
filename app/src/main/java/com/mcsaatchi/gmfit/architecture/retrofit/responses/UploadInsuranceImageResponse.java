package com.mcsaatchi.gmfit.architecture.retrofit.responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UploadInsuranceImageResponse {

  @SerializedName("data") @Expose private UploadInsuranceImageResponseData data;

  /**
   * @return The data
   */
  public UploadInsuranceImageResponseData getData() {
    return data;
  }

  /**
   * @param data The data
   */
  public void setData(UploadInsuranceImageResponseData data) {
    this.data = data;
  }
}
