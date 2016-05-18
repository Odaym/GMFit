package com.mcsaatchi.gmfit.classes;

import android.util.SparseArray;

import com.mcsaatchi.gmfit.models.MealItem;

public class EventBus_Poster {
    private String message;
    private String stringExtra;
    private MealItem mealItem;
    private SparseArray<String[]> sparseArrayExtra;

    public EventBus_Poster(String message) {
        this.message = message;
    }

    public EventBus_Poster(String message, SparseArray<String[]> sparseArrayExtra) {
        this.message = message;
        this.sparseArrayExtra = sparseArrayExtra;
    }

    public EventBus_Poster(String message, MealItem mealItem) {
        this.message = message;
        this.mealItem = mealItem;
    }

    public EventBus_Poster(String message, String stringExtra) {
        this.message = message;
        this.stringExtra = stringExtra;
    }

    public MealItem getMealItemExtra(){
        return mealItem;
    }

    public String getStringExtra() {
        return stringExtra;
    }

    public void setStringExtra(String stringExtra) {
        this.stringExtra = stringExtra;
    }

    public String getMessage() {
        return message;
    }

    public SparseArray<String[]> getSparseArrayExtra() {
        return sparseArrayExtra;
    }
}