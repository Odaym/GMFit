package com.mcsaatchi.gmfit.classes;

import android.util.SparseArray;

public class EventBus_Poster {
    private String message;
    private SparseArray<String[]> sparseArrayExtra;

    public EventBus_Poster(String message) {
        this.message = message;
    }

    public EventBus_Poster(String message, SparseArray<String[]> sparseArrayExtra) {
        this.message = message;
        this.sparseArrayExtra = sparseArrayExtra;
    }

    public String getMessage() {
        return message;
    }

    public SparseArray<String[]> getSparseArrayExtra() {
        return sparseArrayExtra;
    }
}