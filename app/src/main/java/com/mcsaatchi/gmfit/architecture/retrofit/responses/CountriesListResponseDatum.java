package com.mcsaatchi.gmfit.architecture.retrofit.responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CountriesListResponseDatum {
  @SerializedName("crmCode") @Expose private String crmCode;
  @SerializedName("isoCode") @Expose private String isoCode;
  @SerializedName("label") @Expose private String label;

  public String getCrmCode() {
    return crmCode;
  }

  public void setCrmCode(String crmCode) {
    this.crmCode = crmCode;
  }

  public String getIsoCode() {
    return isoCode;
  }

  public void setIsoCode(String isoCode) {
    this.isoCode = isoCode;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }
}
