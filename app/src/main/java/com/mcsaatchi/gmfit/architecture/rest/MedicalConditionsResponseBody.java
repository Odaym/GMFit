package com.mcsaatchi.gmfit.architecture.rest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;

public class MedicalConditionsResponseBody {
  @SerializedName("data") @Expose private List<MedicalConditionsResponseDatum> data =
      new ArrayList<MedicalConditionsResponseDatum>();

  /**
   * @return The data
   */
  public List<MedicalConditionsResponseDatum> getData() {
    return data;
  }

  /**
   * @param data The data
   */
  public void setData(List<MedicalConditionsResponseDatum> data) {
    this.data = data;
  }
}
