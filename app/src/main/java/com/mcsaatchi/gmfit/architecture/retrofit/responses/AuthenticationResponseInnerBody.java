package com.mcsaatchi.gmfit.architecture.retrofit.responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;

public class AuthenticationResponseInnerBody {

  @SerializedName("token") @Expose private String token;
  @SerializedName("widgets") @Expose private List<AuthenticationResponseWidget> widgets =
      new ArrayList<>();
  @SerializedName("charts") @Expose private List<AuthenticationResponseChart> charts =
      new ArrayList<>();

  /**
   * @return The token
   */
  public String getToken() {
    return token;
  }

  /**
   * @param token The token
   */
  public void setToken(String token) {
    this.token = token;
  }

  /**
   * @return The widgets
   */
  public List<AuthenticationResponseWidget> getWidgets() {
    return widgets;
  }

  /**
   * @param widgets The widgets
   */
  public void setWidgets(List<AuthenticationResponseWidget> widgets) {
    this.widgets = widgets;
  }

  /**
   * @return The charts
   */
  public List<AuthenticationResponseChart> getCharts() {
    return charts;
  }

  /**
   * @param charts The charts
   */
  public void setCharts(List<AuthenticationResponseChart> charts) {
    this.charts = charts;
  }
}