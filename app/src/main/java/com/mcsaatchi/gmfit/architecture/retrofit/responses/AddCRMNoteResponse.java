package com.mcsaatchi.gmfit.architecture.retrofit.responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AddCRMNoteResponse {
  @SerializedName("data") @Expose private AddCRMNoteResponseData data;

  public AddCRMNoteResponseData getData() {
    return data;
  }

  public void setData(AddCRMNoteResponseData data) {
    this.data = data;
  }
}
