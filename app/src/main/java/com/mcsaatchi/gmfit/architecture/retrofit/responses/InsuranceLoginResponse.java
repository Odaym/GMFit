package com.mcsaatchi.gmfit.architecture.retrofit.responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class InsuranceLoginResponse {
  @SerializedName("data") @Expose private InsuranceLoginResponseData data;

  public InsuranceLoginResponseData getData() {
    return data;
  }

  public void setData(InsuranceLoginResponseData data) {
    this.data = data;
  }
}
