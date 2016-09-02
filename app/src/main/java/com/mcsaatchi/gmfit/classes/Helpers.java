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
import com.mcsaatchi.gmfit.rest.AuthenticationResponseChartData;

import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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

    public static String getTodayDate(){
        LocalDate dt = new LocalDate();
        return dt.toString();
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

    public static void setBarChartData(BarChart chart, List<AuthenticationResponseChartData> chartData) {
        ArrayList<BarEntry> valsMetrics = new ArrayList<>();
        ArrayList<String> xVals = new ArrayList<>();

        int k = 0;

        for (int i = 0; i < chartData.size(); i++) {
            LocalDate date = LocalDate.parse(chartData.get(i).getDate());
            int dow = date.getDayOfWeek();

            BarEntry val1 = new BarEntry(Float.parseFloat(chartData.get(i).getValue()), k);
            valsMetrics.add(val1);

            k++;

            xVals.add(getDayofWeekString(dow));
        }

        BarDataSet set1;
        set1 = new BarDataSet(valsMetrics, "Legend");
        set1.setColor(R.color.fitness_dark_blue);
        set1.setHighLightAlpha(1);

        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);

        BarData data = new BarData(xVals, dataSets);

        chart.setDescription(null);
        chart.setDrawBarShadow(false);
        chart.setData(data);
        chart.invalidate();
    }

    public static String getDayofWeekString(int dayOfWeek) {
        switch (dayOfWeek) {
            case 1:
                return "Mon";
            case 2:
                return "Tue";
            case 3:
                return "Wed";
            case 4:
                return "Thu";
            case 5:
                return "Fri";
            case 6:
                return "Sat";
            case 7:
                return "Sun";
        }

        return "";
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
