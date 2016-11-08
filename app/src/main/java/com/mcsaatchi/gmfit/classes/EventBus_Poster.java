package com.mcsaatchi.gmfit.classes;

import com.mcsaatchi.gmfit.models.DataChart;
import com.mcsaatchi.gmfit.models.FitnessWidget;
import com.mcsaatchi.gmfit.models.MealItem;
import com.mcsaatchi.gmfit.models.NutritionWidget;

import java.util.ArrayList;
import java.util.List;

public class EventBus_Poster {
  private String message;
  private String chart_title;
  private String[] stringsExtra;
  private MealItem mealItem;
  private boolean createNewMealItem;
  private List<DataChart> dataChartsListExtra;
  private ArrayList<NutritionWidget> widgetsMapNutrition;
  private ArrayList<FitnessWidget> widgetsMapFitness;

  public EventBus_Poster(String message) {
    this.message = message;
  }

  public EventBus_Poster(String message, String chart_title) {
    this.message = message;
    this.chart_title = chart_title;
  }

  public EventBus_Poster(String message, MealItem mealItem, boolean createNewMealItem) {
    this.message = message;
    this.mealItem = mealItem;
    this.createNewMealItem = createNewMealItem;
  }

  public EventBus_Poster(String message, String[] stringsExtra) {
    this.message = message;
    this.stringsExtra = stringsExtra;
  }

  public EventBus_Poster(String message, List<DataChart> dataChartsListExtra) {
    this.message = message;
    this.dataChartsListExtra = dataChartsListExtra;
  }

  public void setWidgetsMapNutrition(ArrayList<NutritionWidget> widgetsMapNutrition) {
    this.widgetsMapNutrition = widgetsMapNutrition;
  }

  public void setWidgetsMapFitness(ArrayList<FitnessWidget> widgetsMapFitness) {
    this.widgetsMapFitness = widgetsMapFitness;
  }

  public ArrayList<NutritionWidget> getNutritionWidgetsMap() {
    return widgetsMapNutrition;
  }

  public ArrayList<FitnessWidget> getFitnessWidgetsMap() {
    return widgetsMapFitness;
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

  public List<DataChart> getDataChartsListExtra() {
    return dataChartsListExtra;
  }
}