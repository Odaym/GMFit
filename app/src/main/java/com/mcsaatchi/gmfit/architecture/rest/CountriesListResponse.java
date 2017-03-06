package com.mcsaatchi.gmfit.architecture.rest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CountriesListResponse {
  @SerializedName("data") @Expose private CountriesListResponseData data;

  public CountriesListResponseData getData() {
    return data;
  }

  public void setData(CountriesListResponseData data) {
    this.data = data;
  }
}
