package com.mcsaatchi.gmfit.architecture.retrofit.responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ArticlesResponseBody {
  @SerializedName("title") @Expose private String title;
  @SerializedName("content") @Expose private String content;
  @SerializedName("image") @Expose private String image;
  @SerializedName("date_publishing") @Expose private String datePublishing;

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
}
