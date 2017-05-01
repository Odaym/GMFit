package com.mcsaatchi.gmfit.architecture.retrofit.responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class WeightHistoryResponseBody {
  @SerializedName("data") @Expose private List<WeightHistoryResponseDatum> data = null;

  public List<WeightHistoryResponseDatum> getData() {
    return data;
  }

  public void setData(List<WeightHistoryResponseDatum> data) {
    this.data = data;
  }
}
