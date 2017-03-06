package com.mcsaatchi.gmfit.architecture.rest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CountriesListResponseBody {
  @SerializedName("data") @Expose private CountriesListResponseInnerData data;

  public CountriesListResponseInnerData getData() {
    return data;
  }

  public void setData(CountriesListResponseInnerData data) {
    this.data = data;
  }
}
