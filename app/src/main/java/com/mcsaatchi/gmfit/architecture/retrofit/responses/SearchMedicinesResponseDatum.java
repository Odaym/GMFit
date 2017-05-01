package com.mcsaatchi.gmfit.architecture.retrofit.responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SearchMedicinesResponseDatum {
  @SerializedName("descr") @Expose private String descr;
  @SerializedName("MDosg") @Expose private String mDosg;
  @SerializedName("MForm") @Expose private String mForm;
  @SerializedName("MPres") @Expose private String mPres;
  @SerializedName("unitForm") @Expose private String unitForm;

  public String getDescr() {
    return descr;
  }

  public void setDescr(String descr) {
    this.descr = descr;
  }

  public String getMDosg() {
    return mDosg;
  }

  public void setMDosg(String mDosg) {
    this.mDosg = mDosg;
  }

  public String getMForm() {
    return mForm;
  }

  public void setMForm(String mForm) {
    this.mForm = mForm;
  }

  public String getMPres() {
    return mPres;
  }

  public void setMPres(String mPres) {
    this.mPres = mPres;
  }

  public String getUnitForm() {
    return unitForm;
  }

  public void setUnitForm(String unitForm) {
    this.unitForm = unitForm;
  }
}
