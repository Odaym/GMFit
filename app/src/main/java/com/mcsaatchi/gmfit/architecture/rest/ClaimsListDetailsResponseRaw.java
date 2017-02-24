package com.mcsaatchi.gmfit.architecture.rest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ClaimsListDetailsResponseRaw {
  @SerializedName("raw") @Expose private ClaimsListDetailsRawContents raw;

  public ClaimsListDetailsRawContents getRaw() {
    return raw;
  }

  public void setRaw(ClaimsListDetailsRawContents raw) {
    this.raw = raw;
  }
}
