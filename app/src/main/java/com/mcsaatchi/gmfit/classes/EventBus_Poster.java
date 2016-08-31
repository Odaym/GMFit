package com.mcsaatchi.gmfit.classes;

import com.mcsaatchi.gmfit.models.DataChart;
import com.mcsaatchi.gmfit.models.FitnessWidget;
import com.mcsaatchi.gmfit.models.MealItem;
import com.mcsaatchi.gmfit.models.NutritionWidget;

import java.util.ArrayList;
import java.util.List;

public class EventBus_Poster {
    private String message;
    private String[] stringsExtra;
    private MealItem mealItem;
    private List<DataChart> dataChartsListExtra;
    private ArrayList<NutritionWidget> widgetsMapNutrition;
    private ArrayList<FitnessWidget> widgetsMapFitness;

    public EventBus_Poster(String message) {
        this.message = message;
    }

    public EventBus_Poster(String message, MealItem mealItem) {
        this.message = message;
        this.mealItem = mealItem;
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

    public String[] getStringsExtra() {
        return stringsExtra;
    }

    public String getMessage() {
        return message;
    }

    public List<DataChart> getDataChartsListExtra() {
        return dataChartsListExtra;
    }
}