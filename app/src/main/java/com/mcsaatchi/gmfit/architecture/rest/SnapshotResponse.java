package com.mcsaatchi.gmfit.architecture.rest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SnapshotResponse {
  @SerializedName("data") @Expose private SnapshotResponseData data;

  public SnapshotResponseData getData() {
    return data;
  }

  public void setData(SnapshotResponseData data) {
    this.data = data;
  }
}
