package com.mcsaatchi.gmfit.architecture.otto;

public class DataChartDeletedEvent {
  private String chartTitle;

  public DataChartDeletedEvent(String chartTitle) {
    this.chartTitle = chartTitle;
  }

  public String getChartTitle() {
    return chartTitle;
  }
}
