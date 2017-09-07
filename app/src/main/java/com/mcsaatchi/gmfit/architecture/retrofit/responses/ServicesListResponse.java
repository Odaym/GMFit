package com.mcsaatchi.gmfit.architecture.retrofit.responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ServicesListResponse {
  @SerializedName("data") @Expose private ServicesListResponseData data;

  public ServicesListResponseData getData() {
    return data;
  }

  public void setData(ServicesListResponseData data) {
    this.data = data;
  }
}
