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
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.mcsaatchi.gmfit.BuildConfig;
import com.mcsaatchi.gmfit.common.Constants;
import com.mcsaatchi.gmfit.architecture.otto.EventBusPoster;
import com.mcsaatchi.gmfit.architecture.otto.EventBusSingleton;
import com.mcsaatchi.gmfit.architecture.GMFitApplication;
import com.mcsaatchi.gmfit.architecture.data_access.DBHelper;
import com.mcsaatchi.gmfit.architecture.data_access.DataAccessHandler;
import com.mcsaatchi.gmfit.fitness.models.FitnessWidget;
import com.mcsaatchi.gmfit.architecture.rest.AuthenticationResponse;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import javax.inject.Inject;
import org.joda.time.LocalDate;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
  private final Handler handler = new Handler();
  @Inject DataAccessHandler dataAccessHandler;
  @Inject SharedPreferences prefs;
  private Timer timer = new Timer();
  private String todayDate;
  private String yesterdayDate;

  private RuntimeExceptionDao<FitnessWidget, Integer> fitnessWidgetsDAO;
  private DBHelper dbHelper = null;

  @Override public void onAccuracyChanged(final Sensor sensor, int accuracy) {
  }

  @Override public void onSensorChanged(final SensorEvent event) {
    LocalDate dt = new LocalDate();

    todayDate = dt.toString();
    yesterdayDate = dt.minusDays(1).toString();

    int stepsToday = prefs.getInt(todayDate + "_steps", 0);

    if (prefs.getBoolean(Constants.EXTRAS_FIRST_APP_LAUNCH, true)) {
      prefs.edit().putBoolean(Constants.EXTRAS_FIRST_APP_LAUNCH, false).apply();

      prefs.edit().putInt(todayDate + "_steps", 0).apply();
      prefs.edit().putFloat(todayDate + "_calories", 0).apply();
      prefs.edit().putFloat(todayDate + "_distance", 0.00f).apply();
    } else {
      float caloriesToday =
          calculateCalories(prefs.getFloat(Constants.EXTRAS_USER_PROFILE_WEIGHT, 70),
              METRIC_RUNNING_FACTOR, STEP_LENGTH);
      float distanceToday = calculateDistance(STEP_LENGTH);

      storeStepsToday(stepsToday, Math.round(event.values[0] * 1.5f), "steps");
      storeCaloriesToday(caloriesToday, prefs.getFloat(todayDate + "_calories", 0), "calories");
      storeDistanceToday(distanceToday, prefs.getFloat(todayDate + "_distance", 0), "distance");

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

  public void storeStepsToday(int stepsToday, int stepsRightNow, String metricName) {
    prefs.edit().putInt(todayDate + "_" + metricName, stepsToday + stepsRightNow).apply();
  }

  public void storeCaloriesToday(float caloriesToday, float caloriesSoFar, String metricName) {
    prefs.edit().putFloat(todayDate + "_" + metricName, caloriesToday + caloriesSoFar).apply();
  }

  public void storeDistanceToday(float distanceToday, float distanceSoFar, String metricName) {
    prefs.edit().putFloat(todayDate + "_" + metricName, distanceToday + distanceSoFar).apply();
  }

  public void findAndUpdateWidgetsInDB(List<FitnessWidget> fitnessWidgets, float calculatedCalories,
      float calculatedDistance) {
    for (int i = 0; i < fitnessWidgets.size(); i++) {
      switch (fitnessWidgets.get(i).getTitle()) {
        case "Active Calories":
          fitnessWidgets.get(i)
              .setValue(
                  (int) ((calculatedCalories + prefs.getFloat(todayDate + "_calories", 0)) * 1));
          break;
        case "Distance Traveled":
          fitnessWidgets.get(i)
              .setValue((calculatedDistance + prefs.getFloat(todayDate + "_distance", 0)));
          break;
      }

      fitnessWidgetsDAO.update(fitnessWidgets.get(i));
    }
  }

  public void sendOutEventBusEvents() {
    EventBusSingleton.getInstance()
        .post(new EventBusPoster(Constants.EVENT_STEP_COUNTER_INCREMENTED));
    EventBusSingleton.getInstance()
        .post(new EventBusPoster(Constants.EVENT_CALORIES_COUNTER_INCREMENTED));
    EventBusSingleton.getInstance()
        .post(new EventBusPoster(Constants.EVENT_DISTANCE_COUNTER_INCREMENTED));
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

  @Override public void onCreate() {
    super.onCreate();

    ((GMFitApplication) getApplication()).getAppComponent().inject(this);

    if (BuildConfig.DEBUG) Log.d("SERVICE_TAG", "SensorListener onCreate");

    reRegisterSensor();

    fitnessWidgetsDAO = getDBHelper().getFitnessWidgetsDAO();

    /**
     * Timer Task for calculating metrics as the phone is active
     */
    TimerTask doAsynchronousTask = new TimerTask() {
      @Override public void run() {

        handler.post(new Runnable() {
          @SuppressWarnings("unchecked") public void run() {
            refreshAccessToken();
          }
        });
      }
    };

    timer.schedule(doAsynchronousTask, 0, Constants.WAIT_TIME_BEFORE_CHECKING_METRICS_SERVICE);
  }

  private void refreshAccessToken() {
    dataAccessHandler.refreshAccessToken(
        prefs.getString(Constants.PREF_USER_ACCESS_TOKEN, Constants.NO_ACCESS_TOKEN_FOUND_IN_PREFS),
        new Callback<AuthenticationResponse>() {
          @Override public void onResponse(Call<AuthenticationResponse> call,
              Response<AuthenticationResponse> response) {
            switch (response.code()) {
              case 200:
                prefs.edit()
                    .putString(Constants.PREF_USER_ACCESS_TOKEN,
                        "Bearer " + response.body().getData().getBody().getToken())
                    .apply();

                String[] slugsArray = new String[] {
                    "steps-count", "active-calories", "distance-traveled"
                };

                Number[] valuesArray = new Number[] {
                    prefs.getInt(todayDate + "_steps", 0),
                    (int) prefs.getFloat(todayDate + "_calories", 0),
                    prefs.getFloat(todayDate + "_distance", 0)
                };

                synchronizeMetricsWithServer(slugsArray, valuesArray);

                break;
            }
          }

          @Override public void onFailure(Call<AuthenticationResponse> call, Throwable t) {

          }
        });
  }

  private void synchronizeMetricsWithServer(String[] slugsArray, Number[] valuesArray) {
    dataAccessHandler.synchronizeMetricsWithServer(
        prefs.getString(Constants.PREF_USER_ACCESS_TOKEN, Constants.NO_ACCESS_TOKEN_FOUND_IN_PREFS),
        slugsArray, valuesArray);

    wipeOutFitnessMetricsAtMidnight();
  }

  private void wipeOutFitnessMetricsAtMidnight() {
    /**
     * Doesn't contain today's date as a key, but DOES contain yesterday's day as a key
     */
    if (!prefs.contains(todayDate + "_steps") && prefs.contains(yesterdayDate)) {
      Log.d("TAGTAG",
          "run: Doesn't contain today's date as a key, but DOES contain yesterday's day as a key");

      prefs.edit().remove(yesterdayDate + "_steps").apply();
      prefs.edit().remove(yesterdayDate + "_distance").apply();
      prefs.edit().remove(yesterdayDate + "_calories").apply();

      List<FitnessWidget> fitnessWidgets = fitnessWidgetsDAO.queryForAll();

      for (int i = 0; i < fitnessWidgets.size(); i++) {
        fitnessWidgets.get(i).setValue(0);

        fitnessWidgetsDAO.update(fitnessWidgets.get(i));
      }
    }
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

  public DBHelper getDBHelper() {
    if (dbHelper == null) {
      dbHelper = OpenHelperManager.getHelper(getApplicationContext(), DBHelper.class);
    }
    return dbHelper;
  }
}
