package com.mcsaatchi.gmfit.architecture.rest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class InsuranceLoginResponseBody {
  @SerializedName("data") @Expose private InsuranceLoginResponseInnerData data;

  public InsuranceLoginResponseInnerData getData() {
    return data;
  }

  public void setData(InsuranceLoginResponseInnerData data) {
    this.data = data;
  }
}
