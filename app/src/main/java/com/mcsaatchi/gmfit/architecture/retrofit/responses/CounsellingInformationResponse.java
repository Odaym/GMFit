package com.mcsaatchi.gmfit.architecture.retrofit.responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CounsellingInformationResponse {
  @SerializedName("data") @Expose private CounsellingInformationResponseData data;

  public CounsellingInformationResponseData getData() {
    return data;
  }

  public void setData(CounsellingInformationResponseData data) {
    this.data = data;
  }
}
