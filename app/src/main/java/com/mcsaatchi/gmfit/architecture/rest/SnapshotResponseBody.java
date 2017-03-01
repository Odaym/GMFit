package com.mcsaatchi.gmfit.architecture.rest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SnapshotResponseBody {

  @SerializedName("snapshot_pdf") @Expose private String snapshotPdf;

  public String getSnapshotPdf() {
    return snapshotPdf;
  }

  public void setSnapshotPdf(String snapshotPdf) {
    this.snapshotPdf = snapshotPdf;
  }
}
