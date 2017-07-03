package com.mcsaatchi.gmfit.architecture.retrofit.responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CounsellingInformationResponseInnerData {
  @SerializedName("status") @Expose private String status;
  @SerializedName("transactionNo") @Expose private Integer transactionNo;
  @SerializedName("conselingInfoMsg") @Expose private String conselingInfoMsg;

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

  public String getConselingInfoMsg() {
    return conselingInfoMsg;
  }

  public void setConselingInfoMsg(String conselingInfoMsg) {
    this.conselingInfoMsg = conselingInfoMsg;
  }
}
