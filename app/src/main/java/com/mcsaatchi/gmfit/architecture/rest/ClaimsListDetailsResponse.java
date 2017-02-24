package com.mcsaatchi.gmfit.architecture.rest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ClaimsListDetailsResponse {
  @SerializedName("data") @Expose private ClaimsListDetailsResponseData data;

  public ClaimsListDetailsResponseData getData() {
    return data;
  }

  public void setData(ClaimsListDetailsResponseData data) {
    this.data = data;
  }
}
