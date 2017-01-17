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
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.common.Constants;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
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
      InputMethodManager imm =
          (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
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

  public static String prepareDateWithTimeForAPIRequest(LocalDateTime dt) {
    String minuteFormatted = "";

    if (dt.getMinuteOfHour() < 10) {
      minuteFormatted = "0" + dt.getMinuteOfHour();
    } else {
      minuteFormatted = String.valueOf(dt.getMinuteOfHour());
    }

    return dt.getYear()
        + "-"
        + dt.getMonthOfYear()
        + "-"
        + dt.getDayOfMonth()
        + " "
        + dt.getHourOfDay()
        + ":"
        + minuteFormatted
        + ":"
        + dt.getSecondOfMinute();
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

    int currentMonth = calendar.get(Calendar.MONTH) + 1;

    DateTime timeNow =
        new DateTime(calendar.get(Calendar.YEAR), currentMonth, calendar.get(Calendar.DAY_OF_MONTH),
            calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE),
            calendar.get(Calendar.MINUTE));

    DateTime desiredAlarmTime;

    switch (mealType) {
      case "Breakfast":
        desiredAlarmTime = new DateTime(calendar.get(Calendar.YEAR), currentMonth,
            calendar.get(Calendar.DAY_OF_MONTH), 9, 0, 0);

        if (desiredAlarmTime.isAfter(timeNow)) {
          calendar.set(calendar.get(Calendar.YEAR), currentMonth,
              calendar.get(Calendar.DAY_OF_MONTH), 9, 0, 0);
        } else {
          calendar.set(calendar.get(Calendar.YEAR), currentMonth,
              calendar.get(Calendar.DAY_OF_MONTH) + 1, 9, 0, 0);
        }

        intent.putExtra("MEAL_TYPE", "Breakfast");
        pendingIntent =
            PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        prefs.edit().putString(Constants.BREAKFAST_REMINDER_ALARM_TIME, "09:00:am").apply();

        break;
      case "Lunch":
        desiredAlarmTime = new DateTime(calendar.get(Calendar.YEAR), currentMonth,
            calendar.get(Calendar.DAY_OF_MONTH), 14, 45, 0);

        if (desiredAlarmTime.isAfter(timeNow)) {
          calendar.set(calendar.get(Calendar.YEAR), currentMonth,
              calendar.get(Calendar.DAY_OF_MONTH), 14, 45, 0);
        } else {
          calendar.set(calendar.get(Calendar.YEAR), currentMonth,
              calendar.get(Calendar.DAY_OF_MONTH) + 1, 14, 45, 0);
        }

        intent.putExtra("MEAL_TYPE", "Lunch");
        pendingIntent =
            PendingIntent.getBroadcast(context, 1, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        prefs.edit().putString(Constants.LUNCH_REMINDER_ALARM_TIME, "14:45:pm").apply();

        break;
      case "Dinner":
        desiredAlarmTime = new DateTime(calendar.get(Calendar.YEAR), currentMonth,
            calendar.get(Calendar.DAY_OF_MONTH), 20, 0, 0);

        if (desiredAlarmTime.isAfter(timeNow)) {
          calendar.set(calendar.get(Calendar.YEAR), currentMonth,
              calendar.get(Calendar.DAY_OF_MONTH), 20, 0, 0);
        } else {
          calendar.set(calendar.get(Calendar.YEAR), currentMonth,
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
