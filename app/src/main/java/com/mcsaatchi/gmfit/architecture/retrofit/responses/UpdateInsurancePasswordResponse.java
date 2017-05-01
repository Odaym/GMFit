package com.mcsaatchi.gmfit.architecture.retrofit.responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UpdateInsurancePasswordResponse {
  @SerializedName("data") @Expose private UpdateInsurancePasswordResponseData data;

  public UpdateInsurancePasswordResponseData getData() {
    return data;
  }

  public void setData(UpdateInsurancePasswordResponseData data) {
    this.data = data;
  }
}
