package com.mcsaatchi.gmfit.architecture.retrofit.responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class InquiriesListResponse {
  @SerializedName("data") @Expose private InquiriesListResponseData data;

  public InquiriesListResponseData getData() {
    return data;
  }

  public void setData(InquiriesListResponseData data) {
    this.data = data;
  }
}
