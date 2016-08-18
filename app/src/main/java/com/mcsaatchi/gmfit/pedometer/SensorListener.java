package com.mcsaatchi.gmfit.pedometer;

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

import com.mcsaatchi.gmfit.BuildConfig;
import com.mcsaatchi.gmfit.classes.Cons;
import com.mcsaatchi.gmfit.classes.EventBus_Poster;
import com.mcsaatchi.gmfit.classes.EventBus_Singleton;
import com.mcsaatchi.gmfit.classes.Helpers;
import com.mcsaatchi.gmfit.rest.DefaultGetResponse;
import com.mcsaatchi.gmfit.rest.RestClient;

import org.joda.time.LocalDate;

import java.util.Timer;
import java.util.TimerTask;

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

    private final static int MICROSECONDS_IN_ONE_MINUTE = 60000000;
    private static final String TAG = "SensorListener";
    private static final float STEP_LENGTH = 20;
    private static double METRIC_RUNNING_FACTOR = 1.02784823;
    private static int steps;
    private final Handler handler = new Handler();
    private Timer timer = new Timer();
    private SharedPreferences prefs;
    private LocalDate dt = new LocalDate();
    private String todayDate = dt.toString();
    private String yesterdayDate = dt.minusDays(1).toString();

    @Override
    public void onAccuracyChanged(final Sensor sensor, int accuracy) {
        // nobody knows what happens here: step value might magically decrease
        // when this method is called...
        if (BuildConfig.DEBUG) Log.d("SERVICE_TAG", sensor.getName() + " accuracy changed: " + accuracy);
    }

    @Override
    public void onSensorChanged(final SensorEvent event) {
        if (event.values[0] > Integer.MAX_VALUE) {
            if (BuildConfig.DEBUG) Log.d("SERVICE_TAG", "probably not a real value: " + event.values[0]);
            return;
        } else {
            steps = (int) event.values[0];

            int sensorStepsFromPrefs = prefs.getInt(Cons.EXTRAS_USER_STEPS_COUNT_FROM_SENSOR, 0);
            int todayStepsFromPrefs = prefs.getInt(todayDate + "_steps", 0);

            if (prefs.getBoolean(Cons.EXTRAS_FIRST_APP_LAUNCH, true)) {
                prefs.edit().putBoolean(Cons.EXTRAS_FIRST_APP_LAUNCH, false).apply();

                prefs.edit().putInt(todayDate + "_steps", 0).apply();
                prefs.edit().putFloat(todayDate + "_calories", 0).apply();
                prefs.edit().putFloat(todayDate + "_distance", 0).apply();
            } else if (sensorStepsFromPrefs != steps) {
                float calculatedCalories = (float) (prefs.getFloat(Cons.EXTRAS_USER_PROFILE_WEIGHT, 70) * METRIC_RUNNING_FACTOR * STEP_LENGTH / 100000.0);
                float calculatedDistance = (float) (STEP_LENGTH / 100000.0);

                prefs.edit().putInt(todayDate + "_steps", todayStepsFromPrefs + 1).apply();
                prefs.edit().putFloat(todayDate + "_calories", calculatedCalories + prefs.getFloat(todayDate + "_calories", 0)).apply();
                prefs.edit().putFloat(todayDate + "_distance", calculatedDistance + prefs.getFloat(todayDate + "_distance", 0)).apply();

                prefs.edit().putInt(Cons.EXTRAS_USER_STEPS_COUNT_FROM_SENSOR, steps).apply();
            }

            EventBus_Singleton.getInstance().post(new EventBus_Poster(Cons.EVENT_STEP_COUNTER_INCREMENTED));
            EventBus_Singleton.getInstance().post(new EventBus_Poster(Cons.EVENT_CALORIES_COUNTER_INCREMENTED));
            EventBus_Singleton.getInstance().post(new EventBus_Poster(Cons.EVENT_DISTANCE_COUNTER_INCREMENTED));
        }
    }

    @Override
    public IBinder onBind(final Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        ((AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE))
                .set(AlarmManager.RTC, System.currentTimeMillis() + AlarmManager.INTERVAL_HOUR,
                        PendingIntent.getService(getApplicationContext(), 2,
                                new Intent(this, SensorListener.class),
                                PendingIntent.FLAG_UPDATE_CURRENT));

        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) Log.d("SERVICE_TAG", "SensorListener onCreate");

        reRegisterSensor();

        prefs = getApplicationContext().getSharedPreferences(Cons.SHARED_PREFS_TITLE, Context.MODE_PRIVATE);

        /**
         * Timer Task for calculating metrics as the phone is active
         */
        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {

                handler.post(new Runnable() {
                    @SuppressWarnings("unchecked")
                    public void run() {

                        String[] slugsArray = new String[]{"steps-count", "active-calories",
                                "distance-traveled"};

                        int[] valuesArray = new int[]{prefs.getInt(todayDate + "_steps", 0), (int) prefs.getFloat(todayDate + "_calories", 0),
                                (int) prefs.getFloat(todayDate + "_distance", 0)};

                        Call<DefaultGetResponse> updateMetricsCall = new RestClient().getGMFitService().updateMetrics(prefs.getString(Cons
                                .PREF_USER_ACCESS_TOKEN, Cons.NO_ACCESS_TOKEN_FOUND_IN_PREFS), new UpdateMetricsRequest(slugsArray, valuesArray, Helpers.getCalendarDate()));

                        updateMetricsCall.enqueue(new Callback<DefaultGetResponse>() {
                            @Override
                            public void onResponse(Call<DefaultGetResponse> call, Response<DefaultGetResponse> response) {
                                switch (response.code()) {
                                    case 200:
                                        Log.d(TAG, "onResponse: SYNCED Metrics successfully");

                                        break;
                                }
                            }

                            @Override
                            public void onFailure(Call<DefaultGetResponse> call, Throwable t) {
                            }
                        });

                        /**
                         * Doesn't contain today's date as a key, but DOES contain yesterday's day as a key
                         */
                        if (!prefs.contains(todayDate + "_steps") && prefs.contains(yesterdayDate)) {
                            Log.d("TAGTAG", "run: Doesn't contain today's date as a key, but DOES contain yesterday's day as a key");

                            prefs.edit().remove(yesterdayDate + "_steps").apply();
                            prefs.edit().remove(yesterdayDate + "_distance").apply();
                            prefs.edit().remove(yesterdayDate + "_calories").apply();

                            prefs.edit().putInt(todayDate + "_steps", 0).apply();
                            prefs.edit().putFloat(todayDate + "_calories", 0).apply();
                            prefs.edit().putFloat(todayDate + "_distance", 0).apply();

                            EventBus_Singleton.getInstance().post(new EventBus_Poster(Cons.EVENT_STEP_COUNTER_INCREMENTED));
                            EventBus_Singleton.getInstance().post(new EventBus_Poster(Cons.EVENT_CALORIES_COUNTER_INCREMENTED));
                            EventBus_Singleton.getInstance().post(new EventBus_Poster(Cons.EVENT_DISTANCE_COUNTER_INCREMENTED));
                        }
                    }
                });
            }
        };

        timer.schedule(doAsynchronousTask, 0, Cons.WAIT_TIME_BEFORE_CHECKING_METRICS_SERVICE);
    }

    @Override
    public void onTaskRemoved(final Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        if (BuildConfig.DEBUG) Log.d("SERVICE_TAG", "sensor service task removed");
        // Restart service in 500 ms
        ((AlarmManager) getSystemService(Context.ALARM_SERVICE))
                .set(AlarmManager.RTC, System.currentTimeMillis() + 500, PendingIntent
                        .getService(this, 3, new Intent(this, SensorListener.class), 0));
    }

    @Override
    public void onDestroy() {
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

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void reRegisterSensor() {
        if (BuildConfig.DEBUG) Log.d("SERVICE_TAG", "re-register sensor listener");
        SensorManager sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        try {
            sm.unregisterListener(this);
        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.d("SERVICE_TAG", e.getMessage());
            e.printStackTrace();
        }

        if (BuildConfig.DEBUG) {
            Log.d("SERVICE_TAG", "step sensors: " + sm.getSensorList(Sensor.TYPE_STEP_COUNTER).size());
            if (sm.getSensorList(Sensor.TYPE_STEP_COUNTER).size() < 1)
                return; // emulator
            Log.d("SERVICE_TAG", "default: " + sm.getDefaultSensor(Sensor.TYPE_STEP_COUNTER).getName());
        }

        // enable batching with delay of max 5 min
        sm.registerListener(this, sm.getDefaultSensor(Sensor.TYPE_STEP_COUNTER),
                SensorManager.SENSOR_DELAY_NORMAL, 5 * MICROSECONDS_IN_ONE_MINUTE);
    }

    public class UpdateMetricsRequest {
        final String[] slug;
        final int[] value;
        final String date;

        public UpdateMetricsRequest(String[] slug, int[] value, String date) {
            this.slug = slug;
            this.value = value;
            this.date = date;
        }
    }
}
