package com.mcsaatchi.gmfit.architecture.otto;

import com.mcsaatchi.gmfit.common.models.DataChart;
import com.mcsaatchi.gmfit.fitness.models.FitnessWidget;
import com.mcsaatchi.gmfit.health.models.HealthWidget;
import com.mcsaatchi.gmfit.nutrition.models.MealItem;
import com.mcsaatchi.gmfit.nutrition.models.NutritionWidget;

import java.util.ArrayList;
import java.util.List;

public class EventBusPoster {
  private String message;
  private String chart_title;
  private String[] stringsExtra;
  private boolean booleanExtra;
  private MealItem mealItem;
  private boolean createNewMealItem;
  private List<DataChart> dataChartsListExtra;

  private ArrayList<NutritionWidget> widgetsMapNutrition;
  private ArrayList<FitnessWidget> widgetsMapFitness;
  private ArrayList<HealthWidget> widgetsMapHealth;

  public EventBusPoster(String message) {
    this.message = message;
  }

  public EventBusPoster(String message, String chart_title) {
    this.message = message;
    this.chart_title = chart_title;
  }

  public EventBusPoster(String message, MealItem mealItem, boolean createNewMealItem) {
    this.message = message;
    this.mealItem = mealItem;
    this.createNewMealItem = createNewMealItem;
  }

  public EventBusPoster(String message, String[] stringsExtra) {
    this.message = message;
    this.stringsExtra = stringsExtra;
  }

  public EventBusPoster(String message, boolean booleanExtra) {
    this.message = message;
    this.booleanExtra = booleanExtra;
  }

  public EventBusPoster(String message, List<DataChart> dataChartsListExtra) {
    this.message = message;
    this.dataChartsListExtra = dataChartsListExtra;
  }

  public void setWidgetsMapNutrition(ArrayList<NutritionWidget> widgetsMapNutrition) {
    this.widgetsMapNutrition = widgetsMapNutrition;
  }

  public void setWidgetsMapFitness(ArrayList<FitnessWidget> widgetsMapFitness) {
    this.widgetsMapFitness = widgetsMapFitness;
  }

  public void setWidgetsMapHealth(ArrayList<HealthWidget> widgetsMapHealth) {
    this.widgetsMapHealth = widgetsMapHealth;
  }

  public ArrayList<NutritionWidget> getNutritionWidgetsMap() {
    return widgetsMapNutrition;
  }

  public ArrayList<FitnessWidget> getFitnessWidgetsMap() {
    return widgetsMapFitness;
  }

  public ArrayList<HealthWidget> getHealthWidgetsMap() {
    return widgetsMapHealth;
  }

  public MealItem getMealItemExtra() {
    return mealItem;
  }

  public boolean isCreateNewMealItem() {
    return createNewMealItem;
  }

  public String[] getStringsExtra() {
    return stringsExtra;
  }

  public String getMessage() {
    return message;
  }

  public String getChart_title() {
    return chart_title;
  }

  public boolean isBooleanExtra() {
    return booleanExtra;
  }

  public void setBooleanExtra(boolean booleanExtra) {
    this.booleanExtra = booleanExtra;
  }

  public List<DataChart> getDataChartsListExtra() {
    return dataChartsListExtra;
  }
}