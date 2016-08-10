package com.mcsaatchi.gmfit.pedometer;

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
import android.os.IBinder;
import android.util.Log;

import com.mcsaatchi.gmfit.BuildConfig;
import com.mcsaatchi.gmfit.classes.Cons;

/**
 * Background service which keeps the step-sensor listener alive to always get
 * the number of steps since boot.
 * <p/>
 * This service won't be needed any more if there is a way to read the
 * step-value without waiting for a sensor event
 */
public class SensorListener extends Service implements SensorEventListener {

    private final static int MICROSECONDS_IN_ONE_MINUTE = 60000000;

    private static int steps;
    private SharedPreferences prefs;

    @Override
    public void onAccuracyChanged(final Sensor sensor, int accuracy) {
        // nobody knows what happens here: step value might magically decrease
        // when this method is called...
        if (BuildConfig.DEBUG) Log.d("SERVICE_TAG", sensor.getName() + " accuracy changed: " + accuracy);
    }

    @Override
    public void onSensorChanged(final SensorEvent event) {
        Log.d("TAG", "onSensorChanged: sensor changed");
        if (event.values[0] > Integer.MAX_VALUE) {
            if (BuildConfig.DEBUG) Log.d("SERVICE_TAG", "probably not a real value: " + event.values[0]);
            return;
        } else {
            steps = (int) event.values[0];

            prefs.edit().putInt(Cons.EXTRAS_USER_STEPS_COUNT, steps).apply();
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
            if (sm.getSensorList(Sensor.TYPE_STEP_COUNTER).size() < 1) return; // emulator
            Log.d("SERVICE_TAG", "default: " + sm.getDefaultSensor(Sensor.TYPE_STEP_COUNTER).getName());
        }

        // enable batching with delay of max 5 min
        sm.registerListener(this, sm.getDefaultSensor(Sensor.TYPE_STEP_COUNTER),
                SensorManager.SENSOR_DELAY_NORMAL, 5 * MICROSECONDS_IN_ONE_MINUTE);
    }
}
