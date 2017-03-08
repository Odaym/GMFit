package com.mcsaatchi.gmfit.architecture.rest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ChronicTreatmentDetailsResponseMedicationItem {
  @SerializedName("itemDesc") @Expose private String itemDesc;
  @SerializedName("itemDosage") @Expose private String itemDosage;
  @SerializedName("posology") @Expose private String posology;
  @SerializedName("status") @Expose private String status;

  public String getItemDesc() {
    return itemDesc;
  }

  public void setItemDesc(String itemDesc) {
    this.itemDesc = itemDesc;
  }

  public String getItemDosage() {
    return itemDosage;
  }

  public void setItemDosage(String itemDosage) {
    this.itemDosage = itemDosage;
  }

  public String getPosology() {
    return posology;
  }

  public void setPosology(String posology) {
    this.posology = posology;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }
}
