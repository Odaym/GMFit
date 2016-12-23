package com.mcsaatchi.gmfit.architecture.otto;

import com.mcsaatchi.gmfit.common.models.DataChart;
import java.util.List;

public class DataChartsOrderChangedEvent {
  private List<DataChart> dataChartsListExtra;

  public DataChartsOrderChangedEvent(List<DataChart> dataChartsListExtra) {
    this.dataChartsListExtra = dataChartsListExtra;
  }

  public List<DataChart> getDataChartsListExtra() {
    return dataChartsListExtra;
  }
}
