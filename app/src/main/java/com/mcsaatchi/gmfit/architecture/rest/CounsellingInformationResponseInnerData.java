package com.mcsaatchi.gmfit.architecture.rest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CounsellingInformationResponseInnerData {
  @SerializedName("message") @Expose private String message;
  @SerializedName("replyCode") @Expose private String replyCode;
  @SerializedName("status") @Expose private String status;
  @SerializedName("transactionNo") @Expose private Integer transactionNo;
  @SerializedName("compatibilityCheckDesc") @Expose private String compatibilityCheckDesc;

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public String getReplyCode() {
    return replyCode;
  }

  public void setReplyCode(String replyCode) {
    this.replyCode = replyCode;
  }

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

  public String getCompatibilityCheckDesc() {
    return compatibilityCheckDesc;
  }

  public void setCompatibilityCheckDesc(String compatibilityCheckDesc) {
    this.compatibilityCheckDesc = compatibilityCheckDesc;
  }
}
