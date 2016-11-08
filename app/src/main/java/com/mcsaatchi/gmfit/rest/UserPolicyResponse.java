package com.mcsaatchi.gmfit.rest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserPolicyResponse {
  @SerializedName("data") @Expose private UserPolicyResponseData data;

  /**
   * @return The data
   */
  public UserPolicyResponseData getData() {
    return data;
  }

  /**
   * @param data The data
   */
  public void setData(UserPolicyResponseData data) {
    this.data = data;
  }
}
