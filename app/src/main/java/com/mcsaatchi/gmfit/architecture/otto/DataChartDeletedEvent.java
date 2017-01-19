package com.mcsaatchi.gmfit.architecture.otto;

import com.mcsaatchi.gmfit.common.models.DataChart;

public class DataChartDeletedEvent {
  private DataChart chartObject;

  public DataChartDeletedEvent(DataChart chartObject) {
    this.chartObject = chartObject;
  }

  public DataChart getChartObject() {
    return chartObject;
  }
}
