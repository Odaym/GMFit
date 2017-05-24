package com.mcsaatchi.gmfit.architecture.retrofit.responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ArticlesResponse {
  @SerializedName("data") @Expose private ArticlesResponseData data;

  public ArticlesResponseData getData() {
    return data;
  }

  public void setData(ArticlesResponseData data) {
    this.data = data;
  }
}
