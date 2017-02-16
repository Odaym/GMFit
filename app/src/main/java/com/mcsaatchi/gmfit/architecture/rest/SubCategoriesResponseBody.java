package com.mcsaatchi.gmfit.architecture.rest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class SubCategoriesResponseBody {
  @SerializedName("data") @Expose private List<SubCategoriesResponseDatum> data = null;

  public List<SubCategoriesResponseDatum> getData() {
    return data;
  }

  public void setData(List<SubCategoriesResponseDatum> data) {
    this.data = data;
  }
}
