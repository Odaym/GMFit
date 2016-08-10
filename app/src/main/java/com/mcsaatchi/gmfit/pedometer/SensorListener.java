/*
 * Copyright 2013 Thomas Hoffmann
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

/**
 * Background service which keeps the step-sensor listener alive to always get
 * the number of steps since boot.
 * <p>
 * This service won't be needed any more if there is a way to read the
 * step-value without waiting for a sensor event
 */
public class SensorListener extends Service implements SensorEventListener {

    public final static String ACTION_PAUSE = "pause";
    private final static int NOTIFICATION_ID = 1;
    private final static int MICROSECONDS_IN_ONE_MINUTE = 60000000;
    private static boolean WAIT_FOR_VALID_STEPS = false;
    private static int steps;

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
            if (WAIT_FOR_VALID_STEPS && steps > 0) {
                WAIT_FOR_VALID_STEPS = false;
//                Database db = Database.getInstance(this);
//                if (db.getSteps(Util.getToday()) == Integer.MIN_VALUE) {
//                    int pauseDifference = steps -
//                            getSharedPreferences("pedometer", Context.MODE_MULTI_PROCESS)
//                                    .getInt("pauseCount", steps);
//                    db.insertNewDay(Util.getToday(), steps - pauseDifference);
//                    if (pauseDifference > 0) {
//                        // update pauseCount for the new day
//                        getSharedPreferences("pedometer", Context.MODE_MULTI_PROCESS).edit()
//                                .putInt("pauseCount", steps).commit();
//                    }
//                    reRegisterSensor();
//                }

//                db.saveCurrentSteps(steps);
//                db.close();
//                updateNotificationState();
//                startService(new Intent(this, WidgetUpdateService.class));
            }
        }
    }

    @Override
    public IBinder onBind(final Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        if (intent != null && ACTION_PAUSE.equals(intent.getStringExtra("action"))) {
            if (BuildConfig.DEBUG)
                Log.d("SERVICE_TAG", "onStartCommand action: " + intent.getStringExtra("action"));
            if (steps == 0) {
//                Database db = Database.getInstance(this);
//                steps = db.getCurrentSteps();
//                db.close();
            }
            SharedPreferences prefs = getSharedPreferences("pedometer", Context.MODE_MULTI_PROCESS);
            if (prefs.contains("pauseCount")) { // resume counting
                int difference = steps -
                        prefs.getInt("pauseCount", steps); // number of steps taken during the pause
//                Database db = Database.getInstance(this);
//                db.updateSteps(Util.getToday(), -difference);
//                db.close();
//                prefs.edit().remove("pauseCount").commit();
//                updateNotificationState();
            } else { // pause counting
                // cancel restart
                ((AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE))
                        .cancel(PendingIntent.getService(getApplicationContext(), 2,
                                new Intent(this, SensorListener.class),
                                PendingIntent.FLAG_UPDATE_CURRENT));
                prefs.edit().putInt("pauseCount", steps).commit();
                stopSelf();
                return START_NOT_STICKY;
            }
        }

        // restart service every hour to get the current step count
        ((AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE))
                .set(AlarmManager.RTC, System.currentTimeMillis() + AlarmManager.INTERVAL_HOUR,
                        PendingIntent.getService(getApplicationContext(), 2,
                                new Intent(this, SensorListener.class),
                                PendingIntent.FLAG_UPDATE_CURRENT));

        WAIT_FOR_VALID_STEPS = true;

        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) Log.d("SERVICE_TAG", "SensorListener onCreate");
        reRegisterSensor();
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
