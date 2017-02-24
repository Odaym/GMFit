package com.mcsaatchi.gmfit.architecture.rest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ClaimsListDetailsRawContents {
  @SerializedName("status") @Expose private String status;
  @SerializedName("transactionNo") @Expose private Integer transactionNo;
  @SerializedName("claimDocsList") @Expose private ClaimsListDetailsDocsList claimDocsList;

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public Integer getTransactionNo() {
    return transactionNo;
  }

  public void setTransactionNo(Integer transactionNo) {
    this.transactionNo = transactionNo;
  }

  public ClaimsListDetailsDocsList getClaimDocsList() {
    return claimDocsList;
  }

  public void setClaimDocsList(ClaimsListDetailsDocsList claimDocsList) {
    this.claimDocsList = claimDocsList;
  }
}
