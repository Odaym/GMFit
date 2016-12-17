package com.mcsaatchi.gmfit.insurance.models;

import java.util.List;

public class ReimbursementModel {
  private String id;
  private String category;
  private String subCategory;
  private String serviceDate;
  private String amount;
  private String status;
  private String type;
  private List<MedicalInformationModel> medicines;

  public ReimbursementModel(String id, String category, String subCategory, String serviceDate,
      String amount, String status, String type, List<MedicalInformationModel> medicines) {
    this.medicines = medicines;
    this.id = id;
    this.category = category;
    this.subCategory = subCategory;
    this.serviceDate = serviceDate;
    this.amount = amount;
    this.status = status;
    this.type = type;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }

  public String getSubCategory() {
    return subCategory;
  }

  public void setSubCategory(String subCategory) {
    this.subCategory = subCategory;
  }

  public String getServiceDate() {
    return serviceDate;
  }

  public void setServiceDate(String serviceDate) {
    this.serviceDate = serviceDate;
  }

  public String getAmount() {
    return amount;
  }

  public void setAmount(String amount) {
    this.amount = amount;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public List<MedicalInformationModel> getMedicines() {
    return medicines;
  }

  public void setMedicines(List<MedicalInformationModel> medicines) {
    this.medicines = medicines;
  }
}