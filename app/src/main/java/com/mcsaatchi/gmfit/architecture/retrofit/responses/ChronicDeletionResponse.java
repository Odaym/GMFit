package com.mcsaatchi.gmfit.architecture.retrofit.responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ChronicDeletionResponse {
  @SerializedName("data") @Expose private ChronicDeletionResponseBody data;

  public ChronicDeletionResponseBody getData() {
    return data;
  }

  public void setData(ChronicDeletionResponseBody data) {
    this.data = data;
  }
}
