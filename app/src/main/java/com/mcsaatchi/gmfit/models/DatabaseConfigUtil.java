package com.mcsaatchi.gmfit.models;

import com.j256.ormlite.android.apptools.OrmLiteConfigUtil;

public class DatabaseConfigUtil extends OrmLiteConfigUtil {
    private static final Class<?>[] classes = new Class[] {
            MealItem.class,
            DataChart.class,
            NutritionWidget.class,
            User.class,
            FitnessWidget.class
    };

    public static void main(String[] args) throws Exception {
        writeConfigFile("ormlite_config.txt", classes);
    }
}