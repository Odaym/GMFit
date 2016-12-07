package com.mcsaatchi.gmfit.architecture.rest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class EmergencyProfileResponseBody {
  @SerializedName("user_pdf") @Expose private String userPdf;

  /**
   * @return The userPdf
   */
  public String getUserPdf() {
    return userPdf;
  }

  /**
   * @param userPdf The user_pdf
   */
  public void setUserPdf(String userPdf) {
    this.userPdf = userPdf;
  }
}
