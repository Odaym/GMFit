package com.mcsaatchi.gmfit.architecture.retrofit.responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class SearchMedicinesResponseInnerData {
  @SerializedName("itemLst") @Expose private List<SearchMedicinesResponseDatum> itemLst = null;
  @SerializedName("status") @Expose private String status;
  @SerializedName("transactionNo") @Expose private Integer transactionNo;

  public List<SearchMedicinesResponseDatum> getItemLst() {
    return itemLst;
  }

  public void setItemLst(List<SearchMedicinesResponseDatum> itemLst) {
    this.itemLst = itemLst;
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
}
