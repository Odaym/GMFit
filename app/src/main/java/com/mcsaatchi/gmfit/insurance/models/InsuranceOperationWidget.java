package com.mcsaatchi.gmfit.insurance.models;

public class InsuranceOperationWidget {
  private int widgetResourceID;
  private String widgetName;

  public InsuranceOperationWidget(int widgetResourceID, String widgetName) {
    this.widgetResourceID = widgetResourceID;
    this.widgetName = widgetName;
  }

  public int getWidgetResourceID() {
    return widgetResourceID;
  }

  public void setWidgetResourceID(int widgetResourceID) {
    this.widgetResourceID = widgetResourceID;
  }

  public String getWidgetName() {
    return widgetName;
  }

  public void setWidgetName(String widgetName) {
    this.widgetName = widgetName;
  }
}
