package com.mcsaatchi.gmfit.architecture.rest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CRMCategoriesResponse {
  @SerializedName("data") @Expose private CRMCategoriesResponseData data;

  public CRMCategoriesResponseData getData() {
    return data;
  }

  public void setData(CRMCategoriesResponseData data) {
    this.data = data;
  }
}
