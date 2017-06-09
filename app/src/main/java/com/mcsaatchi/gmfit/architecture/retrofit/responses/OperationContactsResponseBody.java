package com.mcsaatchi.gmfit.architecture.retrofit.responses;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;

public class OperationContactsResponseBody implements Parcelable{
  public static final Creator<OperationContactsResponseBody> CREATOR =
      new Creator<OperationContactsResponseBody>() {
        @Override public OperationContactsResponseBody createFromParcel(Parcel source) {
          return new OperationContactsResponseBody(source);
        }

        @Override public OperationContactsResponseBody[] newArray(int size) {
          return new OperationContactsResponseBody[size];
        }
      };
  @SerializedName("id") @Expose private Integer id;
  @SerializedName("name") @Expose private String name;
  @SerializedName("title") @Expose private String title;
  @SerializedName("social_media_links") @Expose private String socialMediaLinks;
  @SerializedName("created_at") @Expose private String createdAt;
  @SerializedName("updated_at") @Expose private String updatedAt;
  @SerializedName("emails") @Expose private String emails;
  @SerializedName("locations") @Expose private List<OperationContactsResponseLocation> locations = null;

  public OperationContactsResponseBody() {
  }

  protected OperationContactsResponseBody(Parcel in) {
    this.id = (Integer) in.readValue(Integer.class.getClassLoader());
    this.name = in.readString();
    this.title = in.readString();
    this.socialMediaLinks = in.readString();
    this.createdAt = in.readString();
    this.updatedAt = in.readString();
    this.emails = in.readString();
    this.locations = new ArrayList<OperationContactsResponseLocation>();
    in.readList(this.locations, OperationContactsResponseLocation.class.getClassLoader());
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getSocialMediaLinks() {
    return socialMediaLinks;
  }

  public void setSocialMediaLinks(String socialMediaLinks) {
    this.socialMediaLinks = socialMediaLinks;
  }

  public String getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(String createdAt) {
    this.createdAt = createdAt;
  }

  public String getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(String updatedAt) {
    this.updatedAt = updatedAt;
  }

  public String getEmails() {
    return emails;
  }

  public void setEmails(String emails) {
    this.emails = emails;
  }

  public List<OperationContactsResponseLocation> getLocations() {
    return locations;
  }

  public void setLocations(List<OperationContactsResponseLocation> locations) {
    this.locations = locations;
  }

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    dest.writeValue(this.id);
    dest.writeString(this.name);
    dest.writeString(this.title);
    dest.writeString(this.socialMediaLinks);
    dest.writeString(this.createdAt);
    dest.writeString(this.updatedAt);
    dest.writeString(this.emails);
    dest.writeList(this.locations);
  }
}
