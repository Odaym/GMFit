package com.mcsaatchi.gmfit.architecture.rest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AddCRMNoteResponseBody {
  @SerializedName("data") @Expose private AddCRMNoteResponseInnerData data;

  public AddCRMNoteResponseInnerData getData() {
    return data;
  }

  public void setData(AddCRMNoteResponseInnerData data) {
    this.data = data;
  }
}
