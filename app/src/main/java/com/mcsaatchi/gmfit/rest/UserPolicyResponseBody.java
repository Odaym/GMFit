package com.mcsaatchi.gmfit.rest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserPolicyResponseBody {
  @SerializedName("user_policy") @Expose private String userPolicy;

  /**
   * @return The userPolicy
   */
  public String getUserPolicy() {
    return userPolicy;
  }

  /**
   * @param userPolicy The user_policy
   */
  public void setUserPolicy(String userPolicy) {
    this.userPolicy = userPolicy;
  }
}
