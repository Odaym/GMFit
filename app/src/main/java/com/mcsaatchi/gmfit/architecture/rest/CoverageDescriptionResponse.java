package com.mcsaatchi.gmfit.architecture.rest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CoverageDescriptionResponse {
  @SerializedName("data") @Expose private CoverageDescriptionResponseData data;

  public CoverageDescriptionResponseData getData() {
    return data;
  }

  public void setData(CoverageDescriptionResponseData data) {
    this.data = data;
  }
}
