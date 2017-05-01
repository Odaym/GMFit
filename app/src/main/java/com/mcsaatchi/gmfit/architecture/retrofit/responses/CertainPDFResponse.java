package com.mcsaatchi.gmfit.architecture.retrofit.responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CertainPDFResponse {
  @SerializedName("data") @Expose private CertainPDFResponseData data;

  public CertainPDFResponseData getData() {
    return data;
  }

  public void setData(CertainPDFResponseData data) {
    this.data = data;
  }
}
