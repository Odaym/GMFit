package com.mcsaatchi.gmfit.architecture.otto;

import com.mcsaatchi.gmfit.fitness.models.FitnessWidget;
import java.util.ArrayList;

public class FitnessWidgetsOrderChangedEvent {
  private ArrayList<FitnessWidget> widgetsMapFitness;

  public FitnessWidgetsOrderChangedEvent(ArrayList<FitnessWidget> widgetsMapFitness) {
    this.widgetsMapFitness = widgetsMapFitness;
  }

  public ArrayList<FitnessWidget> getWidgetsMapFitness() {
    return widgetsMapFitness;
  }
}
