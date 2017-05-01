package com.mcsaatchi.gmfit.architecture.retrofit.responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class CRMCategoriesResponseDatum {
  @SerializedName("id") @Expose private String id;
  @SerializedName("name") @Expose private String name;
  @SerializedName("subs") @Expose private List<CRMCategoriesResponseSingleSub> subs = null;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<CRMCategoriesResponseSingleSub> getSubs() {
    return subs;
  }

  public void setSubs(List<CRMCategoriesResponseSingleSub> subs) {
    this.subs = subs;
  }
}
