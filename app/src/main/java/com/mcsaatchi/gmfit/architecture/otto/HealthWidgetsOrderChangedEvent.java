package com.mcsaatchi.gmfit.architecture.otto;

import com.mcsaatchi.gmfit.health.models.HealthWidget;
import java.util.ArrayList;

public class HealthWidgetsOrderChangedEvent {
  private ArrayList<HealthWidget> widgetsMapHealth;

  public HealthWidgetsOrderChangedEvent(ArrayList<HealthWidget> widgetsMapHealth) {
    this.widgetsMapHealth = widgetsMapHealth;
  }

  public ArrayList<HealthWidget> getWidgetsMapHealth() {
    return widgetsMapHealth;
  }
}
