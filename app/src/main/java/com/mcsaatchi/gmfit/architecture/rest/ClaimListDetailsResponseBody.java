package com.mcsaatchi.gmfit.architecture.rest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ClaimListDetailsResponseBody {
  @SerializedName("data") @Expose private List<ClaimListDetailsResponseDatum> data = null;

  public List<ClaimListDetailsResponseDatum> getData() {
    return data;
  }

  public void setData(List<ClaimListDetailsResponseDatum> data) {
    this.data = data;
  }
}
