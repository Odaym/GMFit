package com.mcsaatchi.gmfit.architecture.retrofit.responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ServicesListResponseBody {
  @SerializedName("data") @Expose private ServicesListResponseInnerData data;

  public ServicesListResponseInnerData getData() {
    return data;
  }

  public void setData(ServicesListResponseInnerData data) {
    this.data = data;
  }
}
