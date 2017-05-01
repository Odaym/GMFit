package com.mcsaatchi.gmfit.architecture.retrofit.responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ChronicTreatmentDetailsResponse {
  @SerializedName("data") @Expose private ChronicTreatmentDetailsResponseData data;

  public ChronicTreatmentDetailsResponseData getData() {
    return data;
  }

  public void setData(ChronicTreatmentDetailsResponseData data) {
    this.data = data;
  }
}
