package com.mcsaatchi.gmfit.architecture.retrofit.responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class WeightHistoryResponse {
  @SerializedName("data") @Expose private WeightHistoryResponseData data;

  public WeightHistoryResponseData getData() {
    return data;
  }

  public void setData(WeightHistoryResponseData data) {
    this.data = data;
  }
}
