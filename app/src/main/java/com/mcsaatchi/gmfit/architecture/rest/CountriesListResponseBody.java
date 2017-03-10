package com.mcsaatchi.gmfit.architecture.rest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class CountriesListResponseBody {
  @SerializedName("data") @Expose private List<CountriesListResponseDatum> data = null;

  public List<CountriesListResponseDatum> getData() {
    return data;
  }

  public void setData(List<CountriesListResponseDatum> data) {
    this.data = data;
  }
}
