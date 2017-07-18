package com.mcsaatchi.gmfit.architecture.retrofit.responses;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ArticlesResponseBody implements Parcelable {
  public static final Creator<ArticlesResponseBody> CREATOR = new Creator<ArticlesResponseBody>() {
    @Override public ArticlesResponseBody createFromParcel(Parcel source) {
      return new ArticlesResponseBody(source);
    }

    @Override public ArticlesResponseBody[] newArray(int size) {
      return new ArticlesResponseBody[size];
    }
  };
  @SerializedName("id") @Expose private int id;
  @SerializedName("title") @Expose private String title;
  @SerializedName("content") @Expose private String content;
  @SerializedName("image") @Expose private String image;
  @SerializedName("date_publishing") @Expose private String datePublishing;

  public ArticlesResponseBody() {
  }

  protected ArticlesResponseBody(Parcel in) {
    this.id = in.readInt();
    this.title = in.readString();
    this.content = in.readString();
    this.image = in.readString();
    this.datePublishing = in.readString();
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public String getImage() {
    return image;
  }

  public void setImage(String image) {
    this.image = image;
  }

  public String getDatePublishing() {
    return datePublishing;
  }

  public void setDatePublishing(String datePublishing) {
    this.datePublishing = datePublishing;
  }

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    dest.writeInt(this.id);
    dest.writeString(this.content);
    dest.writeString(this.image);
    dest.writeString(this.datePublishing);
  }
}
