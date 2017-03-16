package com.mcsaatchi.gmfit.architecture.rest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CRMNotesResponse {
  @SerializedName("data") @Expose private CRMNotesResponseData data;

  public CRMNotesResponseData getData() {
    return data;
  }

  public void setData(CRMNotesResponseData data) {
    this.data = data;
  }
}
