package com.mcsaatchi.gmfit.classes;

import com.j256.ormlite.android.apptools.OrmLiteConfigUtil;
import com.mcsaatchi.gmfit.models.DataChart;
import com.mcsaatchi.gmfit.models.FitnessWidget;
import com.mcsaatchi.gmfit.models.MealItem;
import com.mcsaatchi.gmfit.models.NutritionWidget;
import com.mcsaatchi.gmfit.models.User;

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