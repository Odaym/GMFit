package com.mcsaatchi.gmfit.architecture.rest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ChronicTreatmentListResponse {
  @SerializedName("data") @Expose private ChronicTreatmentListResponseData data;

  public ChronicTreatmentListResponseData getData() {
    return data;
  }

  public void setData(ChronicTreatmentListResponseData data) {
    this.data = data;
  }
}
