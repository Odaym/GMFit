package com.mcsaatchi.gmfit.architecture.retrofit.responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DeleteActivityResponse {
  @SerializedName("data") @Expose private DeleteActivityResponseData data;

  public DeleteActivityResponseData getData() {
    return data;
  }

  public void setData(DeleteActivityResponseData data) {
    this.data = data;
  }
}
