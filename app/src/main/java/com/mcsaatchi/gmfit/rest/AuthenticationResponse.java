package com.mcsaatchi.gmfit.rest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AuthenticationResponse {

  @SerializedName("data") @Expose private AuthenticationResponseData data;

  /**
   * @return The data
   */
  public AuthenticationResponseData getData() {
    return data;
  }

  /**
   * @param data The data
   */
  public void setData(AuthenticationResponseData data) {
    this.data = data;
  }
}
