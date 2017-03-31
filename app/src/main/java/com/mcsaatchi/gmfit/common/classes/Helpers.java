package com.mcsaatchi.gmfit.common.classes;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import com.andreabaccega.widget.FormEditText;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.common.Constants;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import okhttp3.ResponseBody;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

  public static String getFormattedString(int amount) {
    return NumberFormat.getNumberInstance(Locale.US).format(amount);
  }

  public static int getNumberFromFromattedString(String formattedString) {
    try {
      return NumberFormat.getNumberInstance(Locale.US).parse(formattedString).intValue();
    } catch (ParseException e) {
      e.printStackTrace();
    }

    return 0;
  }

  public static String getCalendarDate() {
    Calendar cal = Calendar.getInstance();
    Date now = new Date();
    cal.setTime(now);

    return cal.get(Calendar.YEAR) + "-" + (cal.get(Calendar.MONTH) + 1) + "-" + cal.get(
        Calendar.DAY_OF_MONTH);
  }

  public static String formatDateToDefault(LocalDate dt) {
    DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");
    DateTime formattedDate = formatter.parseDateTime(dt.toString());

    return formattedDate.getYear()
        + "-"
        + formattedDate.getMonthOfYear()
        + "-"
        + formattedDate.getDayOfMonth();
  }

  public static String formatRequestTime() {
    Date today = Calendar.getInstance().getTime();
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

    return formatter.format(today);
  }

  public static String formatInsuranceDate(LocalDate dt) {
    SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
    Date date = null;
    try {
      date = fmt.parse(dt.toString());
    } catch (ParseException e) {
      e.printStackTrace();
    }

    SimpleDateFormat fmtOut = new SimpleDateFormat("MMM dd, yyyy");

    return fmtOut.format(date);
  }

  public static String prepareDateWithTimeForAPIRequest(LocalDateTime dt) {
    return dt.getYear()
        + "-"
        + dt.getMonthOfYear()
        + "-"
        + dt.getDayOfMonth()
        + " "
        + dt.getHourOfDay()
        + ":"
        + new DecimalFormat("00").format(dt.getMinuteOfHour())
        + ":"
        + new DecimalFormat("00").format(dt.getSecondOfMinute());
  }

  public static int determineStatusColor(String status) {
    if (status != null) {
      switch (status) {
        case "Processing":
        case "Submited":
        case "Dispensed":
        case "Submitted":
          return R.color.status_submitted;
        case "Rejected":
        case "Pending Deletion":
          return R.color.status_rejected;
        case "Accepted":
        case "Active":
        case "Approved":
        case "Partially Approved":
          return R.color.status_approved;
      }
    }

    return 0;
  }

  public static String provideErrorStringFromJSON(ResponseBody errorResponseBody) {
    if (errorResponseBody != null) {
      try {
        JSONObject mainObject = new JSONObject(errorResponseBody.string());
        JSONObject dataObject = mainObject.getJSONObject("data");
        JSONObject bodyObject = dataObject.getJSONObject("body");
        JSONArray errorObject = bodyObject.getJSONArray("error");

        return errorObject.get(0).toString();
      } catch (JSONException | IOException e) {
        e.printStackTrace();
      }
    }

    return "";
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
        (dialog, which) -> dialog.dismiss());
    alertDialog.show();
  }

  public static void setupAlarmForMeal(Context context, SharedPreferences prefs, String mealType) {
    Intent intent = new Intent(context, AlarmReceiver.class);

    PendingIntent pendingIntent = null;

    Calendar calendar = null;

    switch (mealType) {
      case "Breakfast":
        calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 9);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        if (calendar.before(Calendar.getInstance())) {
          calendar.add(Calendar.DATE, 1);
        }

        intent.putExtra("MEAL_TYPE", "Breakfast");
        pendingIntent =
            PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        prefs.edit().putString(Constants.BREAKFAST_REMINDER_ALARM_TIME, "09:00 AM").apply();

        break;
      case "Lunch":
        calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 14);
        calendar.set(Calendar.MINUTE, 45);
        calendar.set(Calendar.SECOND, 0);

        if (calendar.before(Calendar.getInstance())) {
          calendar.add(Calendar.DATE, 1);
        }

        intent.putExtra("MEAL_TYPE", "Lunch");
        pendingIntent =
            PendingIntent.getBroadcast(context, 1, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        prefs.edit().putString(Constants.LUNCH_REMINDER_ALARM_TIME, "02:45 PM").apply();

        break;
      case "Dinner":
        calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 20);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        if (calendar.before(Calendar.getInstance())) {
          calendar.add(Calendar.DATE, 1);
        }

        intent.putExtra("MEAL_TYPE", "Dinner");
        pendingIntent =
            PendingIntent.getBroadcast(context, 2, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        prefs.edit().putString(Constants.DINNER_REMINDER_ALARM_TIME, "08:00 PM").apply();

        break;
    }

    if (calendar != null) {
      AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
      int ALARM_TYPE = AlarmManager.RTC_WAKEUP;

      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        am.setRepeating(ALARM_TYPE, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY,
            pendingIntent);
      } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        am.setExact(ALARM_TYPE, calendar.getTimeInMillis(), pendingIntent);
      } else {
        am.set(ALARM_TYPE, calendar.getTimeInMillis(), pendingIntent);
      }
    }
  }
}
