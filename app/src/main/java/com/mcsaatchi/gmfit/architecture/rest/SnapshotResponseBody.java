package com.mcsaatchi.gmfit.architecture.rest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class SnapshotResponseBody {

  @SerializedName("error") @Expose private List<String> error = null;

  public List<String> getError() {
    return error;
  }

  public void setError(List<String> error) {
    this.error = error;
  }
}
