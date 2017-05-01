package com.mcsaatchi.gmfit.architecture.retrofit.responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CertainPDFResponseBody {
  @SerializedName("pdf") @Expose private String pdf;

  public String getData() {
    return pdf;
  }

  public void setData(String pdf) {
    this.pdf = pdf;
  }
}
