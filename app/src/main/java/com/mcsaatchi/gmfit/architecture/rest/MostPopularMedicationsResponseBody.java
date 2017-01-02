package com.mcsaatchi.gmfit.architecture.rest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MostPopularMedicationsResponseBody {
  @SerializedName("data") @Expose private MostPopularMedicationsResponseInnerData data;

  public MostPopularMedicationsResponseInnerData getData() {
    return data;
  }

  public void setData(MostPopularMedicationsResponseInnerData data) {
    this.data = data;
  }
}
