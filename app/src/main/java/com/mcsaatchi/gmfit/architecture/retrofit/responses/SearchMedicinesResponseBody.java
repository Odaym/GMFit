package com.mcsaatchi.gmfit.architecture.retrofit.responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SearchMedicinesResponseBody {
  @SerializedName("data") @Expose private SearchMedicinesResponseInnerData data;

  public SearchMedicinesResponseInnerData getData() {
    return data;
  }

  public void setData(SearchMedicinesResponseInnerData data) {
    this.data = data;
  }
}
