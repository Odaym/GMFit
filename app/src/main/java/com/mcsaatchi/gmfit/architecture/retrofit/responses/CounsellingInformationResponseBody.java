package com.mcsaatchi.gmfit.architecture.retrofit.responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CounsellingInformationResponseBody {
  @SerializedName("data") @Expose private CounsellingInformationResponseInnerData data;

  public CounsellingInformationResponseInnerData getData() {
    return data;
  }

  public void setData(CounsellingInformationResponseInnerData data) {
    this.data = data;
  }
}
