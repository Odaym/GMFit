package com.mcsaatchi.gmfit.architecture.retrofit.responses;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;

public class ClaimListDetailsResponseDatum implements Parcelable {
  public static final Creator<ClaimListDetailsResponseDatum> CREATOR =
      new Creator<ClaimListDetailsResponseDatum>() {
        @Override public ClaimListDetailsResponseDatum createFromParcel(Parcel source) {
          return new ClaimListDetailsResponseDatum(source);
        }

        @Override public ClaimListDetailsResponseDatum[] newArray(int size) {
          return new ClaimListDetailsResponseDatum[size];
        }
      };
  @SerializedName("id") @Expose private Integer id;
  @SerializedName("claim") @Expose private String claim;
  @SerializedName("status") @Expose private String status;
  @SerializedName("category") @Expose private String category;
  @SerializedName("subcategory") @Expose private String subcategory;
  @SerializedName("date") @Expose private String date;
  @SerializedName("amount") @Expose private Integer amount;
  @SerializedName("currency") @Expose private String currency;
  @SerializedName("provider") @Expose private String provider;
  @SerializedName("reasonOfRejection") @Expose private String reasonOfRejection;
  @SerializedName("medicationinfo") @Expose private List<String> medicationinfo = null;
  @SerializedName("images") @Expose private List<ClaimListDetailsResponseImage> images = null;
  @SerializedName("conversation") @Expose private List<String> conversation = null;

  public ClaimListDetailsResponseDatum() {
  }

  protected ClaimListDetailsResponseDatum(Parcel in) {
    this.id = (Integer) in.readValue(Integer.class.getClassLoader());
    this.claim = in.readString();
    this.status = in.readString();
    this.category = in.readString();
    this.subcategory = in.readString();
    this.date = in.readString();
    this.amount = (Integer) in.readValue(Integer.class.getClassLoader());
    this.currency = in.readString();
    this.provider = in.readString();
    this.reasonOfRejection = in.readString();
    this.medicationinfo = in.createStringArrayList();
    this.images = new ArrayList<ClaimListDetailsResponseImage>();
    in.readList(this.images, ClaimListDetailsResponseImage.class.getClassLoader());
    this.conversation = in.createStringArrayList();
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getClaim() {
    return claim;
  }

  public void setClaim(String claim) {
    this.claim = claim;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }

  public String getSubcategory() {
    return subcategory;
  }

  public void setSubcategory(String subcategory) {
    this.subcategory = subcategory;
  }

  public String getDate() {
    return date;
  }

  public void setDate(String date) {
    this.date = date;
  }

  public Integer getAmount() {
    return amount;
  }

  public void setAmount(Integer amount) {
    this.amount = amount;
  }

  public String getCurrency() {
    return currency;
  }

  public void setCurrency(String currency) {
    this.currency = currency;
  }

  public String getProvider() {
    return provider;
  }

  public void setProvider(String provider) {
    this.provider = provider;
  }

  public String getReasonOfRejection() {
    return reasonOfRejection;
  }

  public void setReasonOfRejection(String reasonOfRejection) {
    this.reasonOfRejection = reasonOfRejection;
  }

  public List<String> getMedicationinfo() {
    return medicationinfo;
  }

  public void setMedicationinfo(List<String> medicationinfo) {
    this.medicationinfo = medicationinfo;
  }

  public List<ClaimListDetailsResponseImage> getImages() {
    return images;
  }

  public void setImages(List<ClaimListDetailsResponseImage> images) {
    this.images = images;
  }

  public List<String> getConversation() {
    return conversation;
  }

  public void setConversation(List<String> conversation) {
    this.conversation = conversation;
  }

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    dest.writeValue(this.id);
    dest.writeString(this.claim);
    dest.writeString(this.status);
    dest.writeString(this.category);
    dest.writeString(this.subcategory);
    dest.writeString(this.date);
    dest.writeValue(this.amount);
    dest.writeString(this.currency);
    dest.writeString(this.provider);
    dest.writeString(this.reasonOfRejection);
    dest.writeStringList(this.medicationinfo);
    dest.writeList(this.images);
    dest.writeStringList(this.conversation);
  }
}
