package com.mcsaatchi.gmfit.architecture.rest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class CRMCategoriesResponseBody {
  @SerializedName("data") @Expose private List<CRMCategoriesResponseDatum> data = null;

  public List<CRMCategoriesResponseDatum> getData() {
    return data;
  }

  public void setData(List<CRMCategoriesResponseDatum> data) {
    this.data = data;
  }
}
