package com.mcsaatchi.gmfit.architecture.retrofit.responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CitiesListResponseBody {
  @SerializedName("data") @Expose private CitiesListResponseInnerData data;

  public CitiesListResponseInnerData getData() {
    return data;
  }

  public void setData(CitiesListResponseInnerData data) {
    this.data = data;
  }
}
