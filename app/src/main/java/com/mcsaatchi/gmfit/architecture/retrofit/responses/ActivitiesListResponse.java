package com.mcsaatchi.gmfit.architecture.retrofit.responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ActivitiesListResponse {

  @SerializedName("data") @Expose private ActivitiesListResponseData data;

  public ActivitiesListResponseData getData() {
    return data;
  }

  public void setData(ActivitiesListResponseData data) {
    this.data = data;
  }
}
