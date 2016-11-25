package com.mcsaatchi.gmfit.classes;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import com.crashlytics.android.Crashlytics;
import com.mcsaatchi.gmfit.BuildConfig;
import io.fabric.sdk.android.Fabric;
import java.util.Calendar;
import timber.log.Timber;

public class GMFit_Application extends Application {

  private static GMFit_Application instance;

  private SharedPreferences prefs;

  public static boolean hasNetwork() {
    return instance.checkIfHasNetwork();
  }

  public static GMFit_Application getInstance() {
    return instance;
  }

  @Override public void onCreate() {
    super.onCreate();

    prefs = getSharedPreferences(Constants.SHARED_PREFS_TITLE, Context.MODE_PRIVATE);

    instance = this;

    if (BuildConfig.DEBUG) {
      Timber.plant(new Timber.DebugTree() {
        @Override protected String createStackElementTag(StackTraceElement element) {
          return super.createStackElementTag(element) + " - " + element.getLineNumber();
        }
      });
    } else {
      Timber.plant(new TimberReleaseTree());
      Fabric.with(this, new Crashlytics());
    }

    addMealAlarms();
  }

  public boolean checkIfHasNetwork() {
    ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo networkInfo = cm.getActiveNetworkInfo();
    return networkInfo != null && networkInfo.isConnected();
  }

  private void addMealAlarms() {
    prefs = getSharedPreferences(Constants.SHARED_PREFS_TITLE, Context.MODE_PRIVATE);

    if (prefs.getBoolean(Constants.APP_FIRST_RUN, true)) {
      prefs.edit().putBoolean(Constants.APP_FIRST_RUN, false).apply();
      setupAlarmForMeal("Breakfast");
      setupAlarmForMeal("Lunch");
      setupAlarmForMeal("Dinner");
    } else {
    }
  }

  private void setupAlarmForMeal(String mealType) {
    Intent intent = new Intent(this, AlarmReceiver.class);

    PendingIntent pendingIntent =
        PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

    Calendar calendar = Calendar.getInstance();

    switch (mealType) {
      case "Breakfast":
        calendar.set(Calendar.HOUR_OF_DAY, 9);
        calendar.set(Calendar.MINUTE, 0);

        intent.putExtra("MEAL_TYPE", "Breakfast");
        pendingIntent =
            PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        prefs.edit().putString(Constants.BREAKFAST_REMINDER_ALARM_TIME, "09:00:am").apply();

        break;
      case "Lunch":
        calendar.set(Calendar.HOUR_OF_DAY, 14);
        calendar.set(Calendar.MINUTE, 45);

        intent.putExtra("MEAL_TYPE", "Lunch");
        pendingIntent =
            PendingIntent.getBroadcast(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        prefs.edit().putString(Constants.BREAKFAST_REMINDER_ALARM_TIME, "02:45:pm").apply();

        break;
      case "Dinner":
        calendar.set(Calendar.HOUR_OF_DAY, 20);
        calendar.set(Calendar.MINUTE, 0);

        intent.putExtra("MEAL_TYPE", "Dinner");
        pendingIntent =
            PendingIntent.getBroadcast(this, 2, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        prefs.edit().putString(Constants.BREAKFAST_REMINDER_ALARM_TIME, "08:00:pm").apply();

        break;
    }

    AlarmManager am = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
    am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY,
        pendingIntent);
  }
}
