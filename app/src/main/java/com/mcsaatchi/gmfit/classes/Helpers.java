package com.mcsaatchi.gmfit.classes;

import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

import com.andreabaccega.widget.FormEditText;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.mcsaatchi.gmfit.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Helpers {

    public static Helpers helpers = null;

    private Helpers() {
    }

    public static Helpers getInstance() {
        if (helpers == null) {
            helpers = new Helpers();
        }

        return helpers;
    }

    public static boolean validateFields(ArrayList<FormEditText> allFields) {
        boolean allValid = true;

        for (FormEditText field : allFields) {
            allValid = field.testValidity() && allValid;
        }

        return allValid;
    }

    public static String getCalendarDate() {
        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);

        return cal.get(Calendar.YEAR) + "-" + (cal.get(Calendar.MONTH) + 1) + "-" + cal.get(Calendar.DAY_OF_MONTH);
    }

    public static Bundle createActivityBundleWithProperties(int activityTitleResourceId, boolean enableBackButton) {
        Bundle bundle = new Bundle();

        if (activityTitleResourceId != 0) {
            bundle.putInt(Cons.BUNDLE_ACTIVITY_TITLE, activityTitleResourceId);
        }

        bundle.putBoolean(Cons.BUNDLE_ACTIVITY_BACK_BUTTON_ENABLED, enableBackButton);

        return bundle;
    }

    public static Bundle createActivityBundleWithProperties(String activityTitle, boolean enableBackButton) {
        Bundle bundle = new Bundle();

        if (activityTitle != null) {
            bundle.putString(Cons.BUNDLE_ACTIVITY_TITLE, activityTitle);
        }

        bundle.putBoolean(Cons.BUNDLE_ACTIVITY_BACK_BUTTON_ENABLED, enableBackButton);

        return bundle;
    }

    public static void temporarySetHorizontalChartData(BarChart chart, int xLimits, int yLimits) {
        ArrayList<BarEntry> yVals1 = new ArrayList<>();

        for (int i = 0; i < yLimits + 1; i++) {
            float mult = (yLimits + 1);
            float val1 = (float) (Math.random() * mult) + mult / 3;
            yVals1.add(new BarEntry((int) val1, i));
        }

        ArrayList<String> xVals = new ArrayList<>();
        for (int i = 0; i < xLimits + 1; i++) {
            xVals.add((int) yVals1.get(i).getVal() + "");
        }

        BarDataSet set1;
        set1 = new BarDataSet(yVals1, "Legend");
        set1.setColors(new int[]{R.color.fitness_teal});
        set1.setHighLightAlpha(1);
        set1.setDrawValues(false);

        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);

        BarData data = new BarData(xVals, dataSets);

        chart.setDescription(null);
        chart.setDrawBarShadow(false);
        chart.setData(data);
        chart.invalidate();
    }

    public static void setBarChartData(BarChart chart, ArrayList<Float> metricsArray) {
        ArrayList<BarEntry> valsMetrics = new ArrayList<>();

        for (int i = 0; i < metricsArray.size(); i++) {
            BarEntry val1 = new BarEntry(metricsArray.get(i), i);
            valsMetrics.add(val1);
        }

        BarDataSet set1;
        set1 = new BarDataSet(valsMetrics, "Legend");
        set1.setColors(new int[]{R.color.fitness_dark_blue});
        set1.setHighLightAlpha(1);
        set1.setBarShadowColor(0);
        set1.setDrawValues(false);

        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);

        ArrayList<String> xVals = new ArrayList<>();
        xVals.add("S");
        xVals.add("S");
        xVals.add("M");
        xVals.add("T");
        xVals.add("W");
        xVals.add("W");
        xVals.add("Th");
        xVals.add("F");
        xVals.add("M");
        xVals.add("T");
        xVals.add("W");
        xVals.add("Th");
        xVals.add("F");

        BarData data = new BarData(xVals, dataSets);

        chart.setDescription(null);
        chart.setDrawBarShadow(false);
        chart.setData(data);
        chart.invalidate();
    }

    public static boolean isInternetAvailable(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null &&
                cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    public static void showNoInternetDialog(Context context) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(R.string.no_internet_conection_dialog_title);
        alertDialog.setMessage(context.getString(R.string.no_internet_connection_dialog_message));
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, context.getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }
}
