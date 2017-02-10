package com.mcsaatchi.gmfit.architecture.rest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UpdateInsurancePasswordResponseBody {
  @SerializedName("data") @Expose private UpdateInsurancePasswordResponseInnerData data;

  public UpdateInsurancePasswordResponseInnerData getData() {
    return data;
  }

  public void setData(UpdateInsurancePasswordResponseInnerData data) {
    this.data = data;
  }
}
