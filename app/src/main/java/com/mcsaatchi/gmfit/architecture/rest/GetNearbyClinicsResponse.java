package com.mcsaatchi.gmfit.architecture.rest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GetNearbyClinicsResponse {
  @SerializedName("data") @Expose private GetNearbyClinicsResponseData data;

  public GetNearbyClinicsResponseData getData() {
    return data;
  }

  public void setData(GetNearbyClinicsResponseData data) {
    this.data = data;
  }
}
