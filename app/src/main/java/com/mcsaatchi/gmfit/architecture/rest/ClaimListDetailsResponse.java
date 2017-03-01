package com.mcsaatchi.gmfit.architecture.rest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ClaimListDetailsResponse {
  @SerializedName("data") @Expose private ClaimListDetailsResponseData data;

  public ClaimListDetailsResponseData getData() {
    return data;
  }

  public void setData(ClaimListDetailsResponseData data) {
    this.data = data;
  }
}
