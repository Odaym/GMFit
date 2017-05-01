package com.mcsaatchi.gmfit.architecture.retrofit.responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AddCRMNoteResponseInnerData {
  @SerializedName("status") @Expose private String status;
  @SerializedName("transactionNo") @Expose private Integer transactionNo;
  @SerializedName("crmRtnValue") @Expose private String crmRtnValue;

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

  public String getCrmRtnValue() {
    return crmRtnValue;
  }

  public void setCrmRtnValue(String crmRtnValue) {
    this.crmRtnValue = crmRtnValue;
  }
}
