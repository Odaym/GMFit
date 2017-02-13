package com.mcsaatchi.gmfit.architecture.rest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CardDetailsResponse {
  @SerializedName("data") @Expose private CardDetailsResponseData data;

  public CardDetailsResponseData getData() {
    return data;
  }

  public void setData(CardDetailsResponseData data) {
    this.data = data;
  }
}
