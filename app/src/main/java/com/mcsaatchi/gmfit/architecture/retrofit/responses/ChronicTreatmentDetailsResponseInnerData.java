package com.mcsaatchi.gmfit.architecture.retrofit.responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ChronicTreatmentDetailsResponseInnerData {
  @SerializedName("endDate") @Expose private String endDate;
  @SerializedName("lastDispenssedDate") @Expose private String lastDispenssedDate;
  @SerializedName("name") @Expose private String name;
  @SerializedName("requestNbr") @Expose private String requestNbr;
  @SerializedName("startDate") @Expose private String startDate;
  @SerializedName("status") @Expose private String status;
  @SerializedName("items_list") @Expose private List<ChronicTreatmentDetailsResponseMedicationItem>
      itemsList = null;

  public String getEndDate() {
    return endDate;
  }

  public void setEndDate(String endDate) {
    this.endDate = endDate;
  }

  public String getLastDispenssedDate() {
    return lastDispenssedDate;
  }

  public void setLastDispenssedDate(String lastDispenssedDate) {
    this.lastDispenssedDate = lastDispenssedDate;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getRequestNbr() {
    return requestNbr;
  }

  public void setRequestNbr(String requestNbr) {
    this.requestNbr = requestNbr;
  }

  public String getStartDate() {
    return startDate;
  }

  public void setStartDate(String startDate) {
    this.startDate = startDate;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public List<ChronicTreatmentDetailsResponseMedicationItem> getItemsList() {
    return itemsList;
  }

  public void setItemsList(List<ChronicTreatmentDetailsResponseMedicationItem> itemsList) {
    this.itemsList = itemsList;
  }
}
