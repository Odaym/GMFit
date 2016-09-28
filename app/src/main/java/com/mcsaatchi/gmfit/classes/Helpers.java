package com.mcsaatchi.gmfit.classes;

import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;

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

    public static String getTodayDate() {
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

    public static void setBarChartData(BarChart chart, List<AuthenticationResponseChartData> chartData) {
        ArrayList<BarEntry> valsMetrics = new ArrayList<>();
        ArrayList<String> xVals = new ArrayList<>();

        int k = 0;

        for (int i = 0; i < chartData.size(); i++) {
            xVals.add("");

            Log.d("TAG", "setBarChartData: " + chartData.get(i).getValue());


            BarEntry val1 = new BarEntry(Float.parseFloat(chartData.get(i).getValue()), k);
            valsMetrics.add(val1);

            k++;
        }

        BarDataSet set1;
        set1 = new BarDataSet(valsMetrics, null);
        set1.setColor(R.color.fitness_pink);
        set1.setHighLightAlpha(1);
        set1.setValueTextSize(7f);

        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);

        BarData data = new BarData(xVals, dataSets);

        chart.setScaleEnabled(false);
        chart.setDescription(null);
        chart.setDrawGridBackground(false);
        chart.getLegend().setEnabled(false);

        chart.getAxisRight().setEnabled(false);
        chart.getAxisLeft().setEnabled(false);

        chart.getXAxis().setDrawGridLines(false);

        chart.getXAxis().setEnabled(false);

        chart.getAxisLeft().setAxisMaxValue(findLargestNumber(chartData));
        chart.getAxisLeft().setShowOnlyMinMax(true);
        chart.getAxisRight().setShowOnlyMinMax(true);

        chart.getAxisLeft().setDrawLabels(false);
        chart.getAxisRight().setDrawLabels(false);

        chart.getAxisRight().setAxisMinValue(0);
        chart.getAxisLeft().setAxisMinValue(0);

        chart.setData(data);
        chart.invalidate();
    }

    private static int findLargestNumber(List<AuthenticationResponseChartData> rawChartData) {
        int smallest = (int) Double.parseDouble(rawChartData.get(0).getValue());
        int largetst = (int) Double.parseDouble(rawChartData.get(0).getValue());

        for (int i = 1; i < rawChartData.size(); i++) {
            int currentValue = (int) Double.parseDouble(rawChartData.get(i).getValue());
            if (currentValue > largetst)
                largetst = currentValue;
            else if (currentValue < smallest)
                smallest = currentValue;
        }

        return largetst;
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
