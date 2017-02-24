package com.mcsaatchi.gmfit.architecture.rest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ClaimsListDetailsResponseBody {
  @SerializedName("data") @Expose private List<ClaimsListDetailsResponseRaw> data = null;

  public List<ClaimsListDetailsResponseRaw> getData() {
    return data;
  }

  public void setData(List<ClaimsListDetailsResponseRaw> data) {
    this.data = data;
  }
}
