package com.mcsaatchi.gmfit.architecture.retrofit.responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CurrenciesListResponse {
  @SerializedName("data") @Expose private CurrenciesListResponseData data;

  public CurrenciesListResponseData getData() {
    return data;
  }

  public void setData(CurrenciesListResponseData data) {
    this.data = data;
  }
}
