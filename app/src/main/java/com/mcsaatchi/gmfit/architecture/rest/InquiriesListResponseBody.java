package com.mcsaatchi.gmfit.architecture.rest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class InquiriesListResponseBody {
  @SerializedName("data") @Expose private InquiriesListResponseInnerData data;

  public InquiriesListResponseInnerData getData() {
    return data;
  }

  public void setData(InquiriesListResponseInnerData data) {
    this.data = data;
  }
}
