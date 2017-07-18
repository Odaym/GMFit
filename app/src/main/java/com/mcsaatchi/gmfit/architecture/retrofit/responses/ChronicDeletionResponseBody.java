package com.mcsaatchi.gmfit.architecture.retrofit.responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ChronicDeletionResponseBody {
  @SerializedName("data") @Expose private ChronicDeletionResponseDatum data;

  public ChronicDeletionResponseDatum getData() {
    return data;
  }

  public void setData(ChronicDeletionResponseDatum data) {
    this.data = data;
  }
}
