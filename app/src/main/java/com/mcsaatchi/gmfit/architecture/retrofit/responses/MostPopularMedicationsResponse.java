package com.mcsaatchi.gmfit.architecture.retrofit.responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MostPopularMedicationsResponse {
  @SerializedName("data") @Expose private MostPopularMedicationsResponseData data;

  public MostPopularMedicationsResponseData getData() {
    return data;
  }

  public void setData(MostPopularMedicationsResponseData data) {
    this.data = data;
  }
}
