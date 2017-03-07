package com.mcsaatchi.gmfit.insurance.models;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;
import java.util.List;

public class ReimbursementModel implements Parcelable {
  public static final Creator<ReimbursementModel> CREATOR = new Creator<ReimbursementModel>() {
    @Override public ReimbursementModel createFromParcel(Parcel source) {
      return new ReimbursementModel(source);
    }

    @Override public ReimbursementModel[] newArray(int size) {
      return new ReimbursementModel[size];
    }
  };
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

  protected ReimbursementModel(Parcel in) {
    this.id = in.readString();
    this.category = in.readString();
    this.subCategory = in.readString();
    this.serviceDate = in.readString();
    this.amount = in.readString();
    this.status = in.readString();
    this.type = in.readString();
    this.medicines = new ArrayList<MedicalInformationModel>();
    in.readList(this.medicines, MedicalInformationModel.class.getClassLoader());
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

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(this.id);
    dest.writeString(this.category);
    dest.writeString(this.subCategory);
    dest.writeString(this.serviceDate);
    dest.writeString(this.amount);
    dest.writeString(this.status);
    dest.writeString(this.type);
    dest.writeList(this.medicines);
  }
}