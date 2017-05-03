package com.mcsaatchi.gmfit;

import com.mcsaatchi.gmfit.architecture.classes.GMFitApplication;

public class GMFitTestApplication extends GMFitApplication {
  private String baseUrl;

  @Override public String getBaseURL() {
    return baseUrl;
  }

  public void setBaseURL(String url) {
    baseUrl = url;
  }
}