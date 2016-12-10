package com.mcsaatchi.gmfit.common.classes;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import com.andreabaccega.widget.FormEditText;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.rest.AuthenticationResponseChartData;
import com.mcsaatchi.gmfit.common.Constants;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

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

  public static void hideKeyboard(View currentFocus, Context context) {
    if (currentFocus != null) {
      InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
      imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
    }
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

    return cal.get(Calendar.YEAR) + "-" + (cal.get(Calendar.MONTH) + 1) + "-" + cal.get(
        Calendar.DAY_OF_MONTH);
  }

  public static String prepareDateForAPIRequest(LocalDate dt) {
    DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");
    DateTime formattedDate = formatter.parseDateTime(dt.toString());

    return formattedDate.getYear()
        + "-"
        + formattedDate.getMonthOfYear()
        + "-"
        + formattedDate.getDayOfMonth();
  }

  public static Bundle createActivityBundleWithProperties(int activityTitleResourceId,
      boolean enableBackButton) {
    Bundle bundle = new Bundle();

    if (activityTitleResourceId != 0) {
      bundle.putInt(Constants.BUNDLE_ACTIVITY_TITLE, activityTitleResourceId);
    }

    bundle.putBoolean(Constants.BUNDLE_ACTIVITY_BACK_BUTTON_ENABLED, enableBackButton);

    return bundle;
  }

  public static void setBarChartData(BarChart chart,
      List<AuthenticationResponseChartData> chartData) {
    ArrayList<BarEntry> valsMetrics = new ArrayList<>();
    ArrayList<String> xVals = new ArrayList<>();

    int k = 0;

    for (int i = 0; i < chartData.size(); i++) {
      xVals.add("");

      BarEntry val1 = new BarEntry((int) Float.parseFloat(chartData.get(i).getValue()), k);
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

    /**
     * If you would like to show values, use this formatter
     */
    data.setValueFormatter(new MyValueFormatter());

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

    chart.getBarData().setDrawValues(false);

    chart.invalidate();
  }

  private static int findLargestNumber(List<AuthenticationResponseChartData> rawChartData) {
    int smallest = (int) Double.parseDouble(rawChartData.get(0).getValue());
    int largetst = (int) Double.parseDouble(rawChartData.get(0).getValue());

    for (int i = 1; i < rawChartData.size(); i++) {
      int currentValue = (int) Double.parseDouble(rawChartData.get(i).getValue());
      if (currentValue > largetst) {
        largetst = currentValue;
      } else if (currentValue < smallest) smallest = currentValue;
    }

    return largetst;
  }

  public static boolean isInternetAvailable(Context context) {
    ConnectivityManager cm =
        (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

    return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
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

  public static void setupAlarmForMeal(Context context, SharedPreferences prefs, String mealType) {
    Intent intent = new Intent(context, AlarmReceiver.class);

    PendingIntent pendingIntent = null;

    Calendar calendar = Calendar.getInstance();

    DateTime timeNow = new DateTime(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE), calendar.get(Calendar.MINUTE));

    DateTime desiredAlarmTime;

    switch (mealType) {
      case "Breakfast":
        desiredAlarmTime = new DateTime(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH), 9, 0, 0);

        if (desiredAlarmTime.isAfter(timeNow)) {
          calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
              calendar.get(Calendar.DAY_OF_MONTH), 9, 0, 0);
        } else {
          calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
              calendar.get(Calendar.DAY_OF_MONTH) + 1, 9, 0, 0);
        }

        intent.putExtra("MEAL_TYPE", "Breakfast");
        pendingIntent =
            PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        prefs.edit().putString(Constants.BREAKFAST_REMINDER_ALARM_TIME, "09:00:am").apply();

        break;
      case "Lunch":
        desiredAlarmTime = new DateTime(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH), 14, 45, 0);

        if (desiredAlarmTime.isAfter(timeNow)) {
          calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
              calendar.get(Calendar.DAY_OF_MONTH), 14, 45, 0);
        } else {
          calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
              calendar.get(Calendar.DAY_OF_MONTH) + 1, 14, 45, 0);
        }

        intent.putExtra("MEAL_TYPE", "Lunch");
        pendingIntent =
            PendingIntent.getBroadcast(context, 1, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        prefs.edit().putString(Constants.LUNCH_REMINDER_ALARM_TIME, "14:45:pm").apply();

        break;
      case "Dinner":
        desiredAlarmTime = new DateTime(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH), 20, 0, 0);

        if (desiredAlarmTime.isAfter(timeNow)) {
          calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
              calendar.get(Calendar.DAY_OF_MONTH), 20, 0, 0);
        } else {
          calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
              calendar.get(Calendar.DAY_OF_MONTH) + 1, 20, 0, 0);
        }

        intent.putExtra("MEAL_TYPE", "Dinner");
        pendingIntent =
            PendingIntent.getBroadcast(context, 2, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        prefs.edit().putString(Constants.DINNER_REMINDER_ALARM_TIME, "20:00:pm").apply();

        break;
    }

    AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY,
        pendingIntent);
  }

  public static class MyValueFormatter implements ValueFormatter {
    @Override public String getFormattedValue(float value, Entry entry, int dataSetIndex,
        ViewPortHandler viewPortHandler) {
      return Math.round(value) + "";
    }
  }
}