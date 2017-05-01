package com.mcsaatchi.gmfit.architecture.retrofit.responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ClaimsListResponse {
  @SerializedName("data") @Expose private ClaimsListResponseData data;

  public ClaimsListResponseData getData() {
    return data;
  }

  public void setData(ClaimsListResponseData data) {
    this.data = data;
  }
}
