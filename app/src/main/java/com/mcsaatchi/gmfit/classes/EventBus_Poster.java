package com.mcsaatchi.gmfit.classes;

import android.support.design.internal.ParcelableSparseArray;
import android.util.SparseArray;

import com.mcsaatchi.gmfit.models.DataChart;
import com.mcsaatchi.gmfit.models.MealItem;

import java.util.ArrayList;
import java.util.List;

public class EventBus_Poster {
    private String message;
    private String stringExtra;
    private String[] stringsExtra;
    private MealItem mealItem;
    private ParcelableSparseArray parcelableSparseExtra;
    private SparseArray<String> sparseArrayExtra;
    private ArrayList<Float> floatArrayExtra;
    private List<DataChart> dataChartsListExtra;

    public EventBus_Poster(String message) {
        this.message = message;
    }

    public EventBus_Poster(String message, ParcelableSparseArray parcelableSparseExtra) {
        this.message = message;
        this.parcelableSparseExtra = parcelableSparseExtra;
    }

    public EventBus_Poster(String message, SparseArray<String> sparseArrayExtra) {
        this.message = message;
        this.sparseArrayExtra = sparseArrayExtra;
    }

    public EventBus_Poster(String message, ArrayList<Float> floatArrayExtra) {
        this.message = message;
        this.floatArrayExtra = floatArrayExtra;
    }

    public EventBus_Poster(String message, MealItem mealItem) {
        this.message = message;
        this.mealItem = mealItem;
    }

    public EventBus_Poster(String message, String stringExtra) {
        this.message = message;
        this.stringExtra = stringExtra;
    }

    public EventBus_Poster(String message, String[] stringsExtra) {
        this.message = message;
        this.stringsExtra = stringsExtra;
    }

    public EventBus_Poster(String message, List<DataChart> dataChartsListExtra) {
        this.message = message;
        this.dataChartsListExtra = dataChartsListExtra;
    }

    public MealItem getMealItemExtra() {
        return mealItem;
    }

    public ArrayList<Float> getFloatArrayExtra() {
        return floatArrayExtra;
    }

    public void setDoubleArrayExtra(ArrayList<Float> floatArrayExtra) {
        this.floatArrayExtra = floatArrayExtra;
    }

    public String getStringExtra() {
        return stringExtra;
    }

    public void setStringExtra(String stringExtra) {
        this.stringExtra = stringExtra;
    }

    public String[] getStringsExtra() {
        return stringsExtra;
    }

    public String getMessage() {
        return message;
    }

    public ParcelableSparseArray getParcelableSparseExtra(){
        return parcelableSparseExtra;
    }

    public SparseArray<String> getSparseArrayExtra() {
        return sparseArrayExtra;
    }

    public List<DataChart> getDataChartsListExtra() {
        return dataChartsListExtra;
    }
}