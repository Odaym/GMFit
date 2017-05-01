package com.mcsaatchi.gmfit.architecture.retrofit.responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SearchMedicinesResponse {
  @SerializedName("data") @Expose private SearchMedicinesResponseData data;

  public SearchMedicinesResponseData getData() {
    return data;
  }

  public void setData(SearchMedicinesResponseData data) {
    this.data = data;
  }
}
