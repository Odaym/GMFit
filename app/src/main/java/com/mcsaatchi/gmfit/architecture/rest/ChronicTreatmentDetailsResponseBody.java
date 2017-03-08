package com.mcsaatchi.gmfit.architecture.rest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ChronicTreatmentDetailsResponseBody {
  @SerializedName("data") @Expose private ChronicTreatmentDetailsResponseInnerData data;

  public ChronicTreatmentDetailsResponseInnerData getData() {
    return data;
  }

  public void setData(ChronicTreatmentDetailsResponseInnerData data) {
    this.data = data;
  }
}
