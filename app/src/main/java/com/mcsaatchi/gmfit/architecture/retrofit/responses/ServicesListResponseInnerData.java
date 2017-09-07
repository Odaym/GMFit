package com.mcsaatchi.gmfit.architecture.retrofit.responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ServicesListResponseInnerData {
  @SerializedName("status") @Expose private String status;
  @SerializedName("transactionNo") @Expose private Integer transactionNo;
  @SerializedName("serviceVOArr") @Expose private List<ServicesListResponseServiceVOArr>
      serviceVOArr = null;

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

  public List<ServicesListResponseServiceVOArr> getServiceVOArr() {
    return serviceVOArr;
  }

  public void setServiceVOArr(List<ServicesListResponseServiceVOArr> serviceVOArr) {
    this.serviceVOArr = serviceVOArr;
  }
}
