package com.mcsaatchi.gmfit.architecture.otto;

import com.mcsaatchi.gmfit.nutrition.models.NutritionWidget;
import java.util.ArrayList;

public class NutritionWidgetsOrderChangedEvent {
  private ArrayList<NutritionWidget> widgetsMapNutrition;

  public NutritionWidgetsOrderChangedEvent(ArrayList<NutritionWidget> widgetsMapNutrition) {
    this.widgetsMapNutrition = widgetsMapNutrition;
  }

  public ArrayList<NutritionWidget> getWidgetsMapNutrition() {
    return widgetsMapNutrition;
  }
}
