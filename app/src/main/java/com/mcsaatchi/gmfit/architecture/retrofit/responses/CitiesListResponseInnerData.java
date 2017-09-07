package com.mcsaatchi.gmfit.architecture.retrofit.responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class CitiesListResponseInnerData {
  @SerializedName("status") @Expose private String status;
  @SerializedName("transactionNo") @Expose private Integer transactionNo;
  @SerializedName("cityVOArr") @Expose private List<CitiesListResponseCityVOArr> cityVOArr = null;

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

  public List<CitiesListResponseCityVOArr> getCityVOArr() {
    return cityVOArr;
  }

  public void setCityVOArr(List<CitiesListResponseCityVOArr> cityVOArr) {
    this.cityVOArr = cityVOArr;
  }
}
