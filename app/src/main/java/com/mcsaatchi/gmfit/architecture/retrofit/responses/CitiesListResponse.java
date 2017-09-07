package com.mcsaatchi.gmfit.architecture.retrofit.responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CitiesListResponse {
  @SerializedName("data") @Expose private CitiesListResponseData data;

  public CitiesListResponseData getData() {
    return data;
  }

  public void setData(CitiesListResponseData data) {
    this.data = data;
  }
}
