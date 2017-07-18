package com.mcsaatchi.gmfit.architecture.retrofit.responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ArticleDetailsResponse {
  @SerializedName("data") @Expose private ArticleDetailsResponseData data;

  public ArticleDetailsResponseData getData() {
    return data;
  }

  public void setData(ArticleDetailsResponseData data) {
    this.data = data;
  }
}
