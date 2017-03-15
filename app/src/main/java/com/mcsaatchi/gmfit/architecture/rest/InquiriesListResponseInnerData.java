package com.mcsaatchi.gmfit.architecture.rest;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class InquiriesListResponseInnerData implements Parcelable {
  public static final Creator<InquiriesListResponseInnerData> CREATOR =
      new Creator<InquiriesListResponseInnerData>() {
        @Override public InquiriesListResponseInnerData createFromParcel(Parcel source) {
          return new InquiriesListResponseInnerData(source);
        }

        @Override public InquiriesListResponseInnerData[] newArray(int size) {
          return new InquiriesListResponseInnerData[size];
        }
      };
  @SerializedName("IncidentId") @Expose private String incidentId;
  @SerializedName("TicketNumber") @Expose private String ticketNumber;
  @SerializedName("CategoryName") @Expose private String categoryName;
  @SerializedName("SubCategoryName") @Expose private String subCategoryName;
  @SerializedName("Title") @Expose private String title;
  @SerializedName("Description") @Expose private String description;
  @SerializedName("Area") @Expose private String area;
  @SerializedName("CreatedOn") @Expose private String createdOn;
  @SerializedName("Status") @Expose private String status;

  public InquiriesListResponseInnerData() {
  }

  protected InquiriesListResponseInnerData(Parcel in) {
    this.incidentId = in.readString();
    this.ticketNumber = in.readString();
    this.categoryName = in.readString();
    this.subCategoryName = in.readString();
    this.title = in.readString();
    this.description = in.readString();
    this.area = in.readString();
    this.createdOn = in.readString();
    this.status = in.readString();
  }

  public String getIncidentId() {
    return incidentId;
  }

  public void setIncidentId(String incidentId) {
    this.incidentId = incidentId;
  }

  public String getTicketNumber() {
    return ticketNumber;
  }

  public void setTicketNumber(String ticketNumber) {
    this.ticketNumber = ticketNumber;
  }

  public String getCategoryName() {
    return categoryName;
  }

  public void setCategoryName(String categoryName) {
    this.categoryName = categoryName;
  }

  public String getSubCategoryName() {
    return subCategoryName;
  }

  public void setSubCategoryName(String subCategoryName) {
    this.subCategoryName = subCategoryName;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getArea() {
    return area;
  }

  public void setArea(String area) {
    this.area = area;
  }

  public String getCreatedOn() {
    return createdOn;
  }

  public void setCreatedOn(String createdOn) {
    this.createdOn = createdOn;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(this.incidentId);
    dest.writeString(this.ticketNumber);
    dest.writeString(this.categoryName);
    dest.writeString(this.subCategoryName);
    dest.writeString(this.title);
    dest.writeString(this.description);
    dest.writeString(this.area);
    dest.writeString(this.createdOn);
    dest.writeString(this.status);
  }
}
