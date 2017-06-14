package com.mcsaatchi.gmfit.fitness.pedometer;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.mcsaatchi.gmfit.BuildConfig;
import com.mcsaatchi.gmfit.architecture.classes.GMFitApplication;
import com.mcsaatchi.gmfit.architecture.database.DBHelper;
import com.mcsaatchi.gmfit.architecture.otto.CaloriesCounterIncrementedEvent;
import com.mcsaatchi.gmfit.architecture.otto.DistanceCounterIncrementedEvent;
import com.mcsaatchi.gmfit.architecture.otto.EventBusSingleton;
import com.mcsaatchi.gmfit.architecture.otto.StepCounterIncrementedEvent;
import com.mcsaatchi.gmfit.architecture.retrofit.architecture.DataAccessHandlerImpl;
import com.mcsaatchi.gmfit.common.Constants;
import com.mcsaatchi.gmfit.fitness.models.FitnessWidget;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import javax.inject.Inject;

/**
 * Background service which keeps the step-sensor listener alive to always get
 * the number of steps since boot.
 * <p/>
 * This service won't be needed any more if there is a way to read the
 * step-value without waiting for a sensor event
 */
public class SensorListener extends Service implements SensorEventListener {

  private static final float STEP_LENGTH = 20;
  private static float METRIC_RUNNING_FACTOR = 1.02784823f;
  @Inject DataAccessHandlerImpl dataAccessHandler;
  @Inject SharedPreferences prefs;
  @Inject DBHelper dbHelper;

  private RuntimeExceptionDao<FitnessWidget, Integer> fitnessWidgetsDAO;

  @Override public void onCreate() {
    super.onCreate();

    ((GMFitApplication) getApplication()).getAppComponent().inject(this);

    if (BuildConfig.DEBUG) Log.d("SERVICE_TAG", "SensorListener onCreate");

    reRegisterSensor();

    fitnessWidgetsDAO = dbHelper.getFitnessWidgetsDAO();
  }

  @Override public void onAccuracyChanged(final Sensor sensor, int accuracy) {
  }

  @Override public void onSensorChanged(final SensorEvent event) {
    if (prefs.getBoolean(Constants.EXTRAS_FIRST_APP_LAUNCH, true)) {
      prefs.edit().putBoolean(Constants.EXTRAS_FIRST_APP_LAUNCH, false).apply();

      clearMetricsInPrefs();
    } else {
      float caloriesToday =
          calculateCalories(prefs.getFloat(Constants.EXTRAS_USER_PROFILE_WEIGHT, 70),
              METRIC_RUNNING_FACTOR, STEP_LENGTH);
      float distanceToday = calculateDistance(STEP_LENGTH);

      int stepsToday = prefs.getInt("steps_taken", 0);

      if (new Random().nextBoolean()) {
        storeStepsToday(stepsToday, 3);
      } else {
        storeStepsToday(stepsToday, 1);
      }

      storeCaloriesToday(caloriesToday, prefs.getFloat("calories_spent", 0));
      storeDistanceToday(distanceToday, prefs.getFloat("distance_traveled", 0));

      List<FitnessWidget> fitnessWidgets = fitnessWidgetsDAO.queryForAll();

      findAndUpdateWidgetsInDB(fitnessWidgets, caloriesToday, distanceToday);

      sendOutEventBusEvents();
    }
  }

  public float calculateCalories(float weight, float metricRunningFactor, float stepLength) {
    return weight * metricRunningFactor * stepLength / 100000.0f;
  }

  public float calculateDistance(float stepLength) {
    return stepLength / 100000.0f;
  }

  public void storeStepsToday(int stepsToday, int stepsRightNow) {
    prefs.edit().putInt("steps_taken", stepsToday + stepsRightNow).apply();
  }

  public void storeCaloriesToday(float caloriesToday, float caloriesRightNow) {
    prefs.edit().putFloat("calories_spent", caloriesToday + caloriesRightNow).apply();
  }

  public void storeDistanceToday(float distanceToday, float distanceRightNow) {
    prefs.edit().putFloat("distance_traveled", distanceToday + distanceRightNow).apply();
  }

  public void findAndUpdateWidgetsInDB(List<FitnessWidget> fitnessWidgets, float calculatedCalories,
      float calculatedDistance) {
    for (int i = 0; i < fitnessWidgets.size(); i++) {
      switch (fitnessWidgets.get(i).getTitle()) {
        case "Active Calories":
          fitnessWidgets.get(i)
              .setValue((int) ((calculatedCalories + prefs.getFloat("calories_spent", 0)) * 1));
          break;
        case "Distance Traveled":
          fitnessWidgets.get(i)
              .setValue((calculatedDistance + prefs.getFloat("distance_traveled", 0)));
          break;
      }

      fitnessWidgetsDAO.update(fitnessWidgets.get(i));
    }
  }

  public void sendOutEventBusEvents() {
    EventBusSingleton.getInstance().post(new StepCounterIncrementedEvent());
    EventBusSingleton.getInstance().post(new CaloriesCounterIncrementedEvent());
    EventBusSingleton.getInstance().post(new DistanceCounterIncrementedEvent());
  }

  @Override public IBinder onBind(final Intent intent) {
    return null;
  }

  @Override public int onStartCommand(final Intent intent, int flags, int startId) {
    ((AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE)).set(
        AlarmManager.RTC, System.currentTimeMillis() + AlarmManager.INTERVAL_HOUR,
        PendingIntent.getService(getApplicationContext(), 2, new Intent(this, SensorListener.class),
            PendingIntent.FLAG_UPDATE_CURRENT));

    return START_STICKY;
  }

  private void clearMetricsInPrefs() {
    prefs.edit().putInt("steps_taken", 0).apply();
    prefs.edit().putFloat("calories_spent", 0).apply();
    prefs.edit().putFloat("distance_traveled", 0.00f).apply();
  }

  @Override public void onTaskRemoved(final Intent rootIntent) {
    super.onTaskRemoved(rootIntent);
    if (BuildConfig.DEBUG) Log.d("SERVICE_TAG", "sensor service task removed");
    // Restart service in 500 ms
    ((AlarmManager) getSystemService(Context.ALARM_SERVICE)).set(AlarmManager.RTC,
        System.currentTimeMillis() + 500,
        PendingIntent.getService(this, 3, new Intent(this, SensorListener.class), 0));
  }

  @Override public void onDestroy() {
    super.onDestroy();
    if (BuildConfig.DEBUG) Log.d("SERVICE_TAG", "SensorListener onDestroy");
    try {
      SensorManager sm = (SensorManager) getSystemService(SENSOR_SERVICE);
      sm.unregisterListener(this);
    } catch (Exception e) {
      if (BuildConfig.DEBUG) Log.d("SERVICE_TAG", e.getMessage());
      e.printStackTrace();
    }
  }

  @TargetApi(Build.VERSION_CODES.KITKAT) private void reRegisterSensor() {
    if (BuildConfig.DEBUG) Log.d("SERVICE_TAG", "re-register sensor listener");
    SensorManager sm = (SensorManager) getSystemService(SENSOR_SERVICE);
    try {
      sm.unregisterListener(this);
    } catch (Exception e) {
      if (BuildConfig.DEBUG) Log.d("SERVICE_TAG", e.getMessage());
      e.printStackTrace();
    }

    if (BuildConfig.DEBUG) {
      Log.d("SERVICE_TAG", "step sensors: " + sm.getSensorList(Sensor.TYPE_STEP_DETECTOR).size());
      if (sm.getSensorList(Sensor.TYPE_STEP_DETECTOR).size() < 1) return;
      Log.d("SERVICE_TAG", "default: " + sm.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR).getName());
    }

    // enable batching with delay of max 5 min
    sm.registerListener(this, sm.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR), 0, 0);
  }
}
