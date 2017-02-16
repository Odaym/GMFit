package com.mcsaatchi.gmfit.architecture.rest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SubCategoriesResponse {
  @SerializedName("data") @Expose private SubCategoriesResponseData data;

  public SubCategoriesResponseData getData() {
    return data;
  }

  public void setData(SubCategoriesResponseData data) {
    this.data = data;
  }
}
