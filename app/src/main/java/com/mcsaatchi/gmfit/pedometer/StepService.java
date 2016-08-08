package com.mcsaatchi.gmfit.pedometer;

import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.mcsaatchi.gmfit.classes.Cons;
import com.mcsaatchi.gmfit.classes.Helpers;
import com.mcsaatchi.gmfit.rest.DefaultGetResponse;
import com.mcsaatchi.gmfit.rest.RestClient;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * This is an example of implementing an application service that runs locally
 * in the same process as the application.  The {@link StepServiceController}
 * and {@link StepServiceBinding} classes show how to interact with the
 * service.
 * <p/>
 * <p>Notice the use of the {@link NotificationManager} when interesting things
 * happen in the service.  This is generally how background services should
 * interact with the user, rather than doing something more disruptive such as
 * calling startActivity().
 */

public class StepService extends Service {
    private static final String TAG = "com.mcsaatchi.me.StepService";
    private final Handler handler = new Handler();
    /**
     * Receives messages from activity.
     */
    private final IBinder mBinder = new StepBinder();
    private SharedPreferences mSettings;
    private PedometerSettings mPedometerSettings;
    private SharedPreferences mState;
    private SharedPreferences.Editor mStateEditor;
    private Utils mUtils;
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private StepDetector mStepDetector;
    private StepDisplayer mStepDisplayer;
    private PaceNotifier mPaceNotifier;
    private DistanceNotifier mDistanceNotifier;
    private SpeedNotifier mSpeedNotifier;
    private CaloriesNotifier mCaloriesNotifier;
    private PowerManager.WakeLock wakeLock;
    private NotificationManager mNM;
    private int mSteps;
    private int mPace;
    private float mDistance;
    private float mSpeed;
    private float mCalories;
    private Timer timer = new Timer();
    private SharedPreferences prefs;
    private ICallback mCallback;

    /**
     * Forwards pace values from PaceNotifier to the activity.
     */
    private StepDisplayer.Listener mStepListener = new StepDisplayer.Listener() {
        public void stepsChanged(int value) {
            mSteps = value;
            passValue();
        }

        public void passValue() {
            if (mCallback != null) {
                mCallback.stepsChanged(mSteps);
            }
        }
    };

    /**
     * Forwards pace values from PaceNotifier to the activity.
     */
    private PaceNotifier.Listener mPaceListener = new PaceNotifier.Listener() {
        public void paceChanged(int value) {
            mPace = value;
            passValue();
        }

        public void passValue() {
            if (mCallback != null) {
                mCallback.paceChanged(mPace);
            }
        }
    };

    /**
     * Forwards distance values from DistanceNotifier to the activity.
     */
    private DistanceNotifier.Listener mDistanceListener = new DistanceNotifier.Listener() {
        public void valueChanged(float value) {
            mDistance = value;
            passValue();
        }

        public void passValue() {
            if (mCallback != null) {
                mCallback.distanceChanged(mDistance);
                Log.d(TAG, "passValue: Distance value changed!");
            }
        }
    };

    /**
     * Forwards speed values from SpeedNotifier to the activity.
     */
    private SpeedNotifier.Listener mSpeedListener = new SpeedNotifier.Listener() {
        public void valueChanged(float value) {
            mSpeed = value;
            passValue();
        }

        public void passValue() {
            if (mCallback != null) {
                mCallback.speedChanged(mSpeed);
            }
        }
    };

    /**
     * Forwards calories values from CaloriesNotifier to the activity.
     */
    private CaloriesNotifier.Listener mCaloriesListener = new CaloriesNotifier.Listener() {
        public void valueChanged(float value) {
            mCalories = value;
            passValue();
        }

        public void passValue() {
            if (mCallback != null) {
                mCallback.caloriesChanged(mCalories);
            }
        }
    };

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Check action just to be on the safe side.
            if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                // Unregisters the listener and registers it again.
                StepService.this.unregisterDetector();
                StepService.this.registerDetector();
//                if (mPedometerSettings.wakeAggressively()) {
                wakeLock.release();
                acquireWakeLock();
//                }
            }
        }
    };

    @Override
    public void onCreate() {
        Log.i(TAG, "[SERVICE] onCreate");
        super.onCreate();

        prefs = getSharedPreferences(Cons.SHARED_PREFS_TITLE, Context.MODE_PRIVATE);

        // Load settings
        mSettings = PreferenceManager.getDefaultSharedPreferences(this);
        mPedometerSettings = new PedometerSettings(mSettings);
        mState = getSharedPreferences("state", 0);

        mUtils = Utils.getInstance();
        mUtils.setService(this);

        // Start detecting
        mStepDetector = new StepDetector();
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        registerDetector();

        // Register our receiver for the ACTION_SCREEN_OFF action. This will make our receiver
        // code be called whenever the phone enters standby mode.
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        registerReceiver(mReceiver, filter);

        mStepDisplayer = new StepDisplayer(mPedometerSettings, mUtils);
        mStepDisplayer.setSteps(mSteps = mState.getInt("steps", 0));
        mStepDisplayer.addListener(mStepListener);
        mStepDetector.addStepListener(mStepDisplayer);

        mPaceNotifier = new PaceNotifier(mPedometerSettings, mUtils);
        mPaceNotifier.setPace(mPace = mState.getInt("pace", 0));
        mPaceNotifier.addListener(mPaceListener);
        mStepDetector.addStepListener(mPaceNotifier);

        mDistanceNotifier = new DistanceNotifier(mDistanceListener, mPedometerSettings, mUtils);
        mDistanceNotifier.setDistance(mDistance = mState.getFloat("distance", 0));
        mStepDetector.addStepListener(mDistanceNotifier);

        mSpeedNotifier = new SpeedNotifier(mSpeedListener, mPedometerSettings, mUtils);
        mSpeedNotifier.setSpeed(mSpeed = mState.getFloat("speed", 0));
        mPaceNotifier.addListener(mSpeedNotifier);

        mCaloriesNotifier = new CaloriesNotifier(mCaloriesListener, mPedometerSettings, mUtils);
        mCaloriesNotifier.setCalories(mCalories = mState.getFloat("calories", 0));
        mStepDetector.addStepListener(mCaloriesNotifier);

        mSettings = PreferenceManager.getDefaultSharedPreferences(this);

        // Tell the user we started.
        Toast.makeText(this, "Service Started", Toast.LENGTH_SHORT).show();

        /**
         * Timer Task for calculating metrics as the phone is active
         */
        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {

                handler.post(new Runnable() {
                    @SuppressWarnings("unchecked")
                    public void run() {

                        Log.d(TAG, "run: current values from if statement are -- Calories : " + prefs.getInt(Cons.EXTRAS_USER_ACTIVE_CALORIES, 0) + " -- Steps Count : " +
                                prefs.getInt(Cons.EXTRAS_USER_STEPS_COUNT, 0) + " -- Distance Traveled : " + prefs.getInt(Cons.EXTRAS_USER_DISTANCE_TRAVELED, 0));


                        LocalDate dt = new LocalDate();

                        String todayDate = dt.toString();
                        String yesterdayDate = dt.minusDays(1).toString();

                        Log.d("TAGTAG", "run: Today's date : " + todayDate);
                        Log.d("TAGTAG", "run: Yesterday's date (TODAY MINUS 1): " + yesterdayDate);

                        /**
                         * Doesn't contain today's date as a key, but DOES contain yesterday's day as a key
                         */
                        if (!prefs.contains(todayDate) && prefs.contains(yesterdayDate)) {
                            Log.d(TAG, "run: Doesn't contain today's date as a key, but DOES contain yesterday's day as a key");
                            prefs.edit().remove(yesterdayDate).apply();
                            resetValues();

                        }

                        prefs.edit().putString(todayDate, "").apply();

                        /**
                         * Steps Calculation
                         */
                        int accumulatingSteps = prefs.getInt(Cons.EXTRAS_ACCUMULATING_STEPS, 0);

                        int finalStepsValue = mSteps;

                        if (accumulatingSteps != mSteps) {
                            int stepsFromPrefs = prefs.getInt(Cons.EXTRAS_USER_STEPS_COUNT, 0);
                            prefs.edit().putInt(Cons.EXTRAS_USER_STEPS_COUNT, stepsFromPrefs + 1).apply();
                        }

                        prefs.edit().putInt(Cons.EXTRAS_ACCUMULATING_STEPS, finalStepsValue).apply();
                        /****/

                        /**
                         * Distance calculation
                         */
                        int accumulatingDistance = prefs.getInt(Cons.EXTRAS_ACCUMULATING_DISTANCE, 0);

                        int finalDistanceValue = (int) (mDistance * 1000);

                        if (accumulatingDistance != finalDistanceValue) {
                            int distanceFromPrefs = prefs.getInt(Cons.EXTRAS_USER_DISTANCE_TRAVELED, 0);
                            prefs.edit().putInt(Cons.EXTRAS_USER_DISTANCE_TRAVELED, distanceFromPrefs + 1).apply();
                        }

                        prefs.edit().putInt(Cons.EXTRAS_ACCUMULATING_DISTANCE, finalDistanceValue).apply();
                        /****/

                        /**
                         * Calories calculation
                         */
                        int accumulatingCalories = prefs.getInt(Cons.EXTRAS_ACCUMULATING_CALORIES, 0);

                        int finalCaloriesValue = (int) mCalories;

                        //If the new and old values differ, this means there was an increase
                        if (accumulatingCalories != finalCaloriesValue) {
                            //Increment the current value for Calories in prefs by 1!
                            prefs.edit().putInt(Cons.EXTRAS_USER_ACTIVE_CALORIES, accumulatingCalories + 1).apply();
                        }

                        prefs.edit().putInt(Cons.EXTRAS_ACCUMULATING_CALORIES, finalCaloriesValue).apply();
                        /****/
                    }
                });
            }
        };

        timer.schedule(doAsynchronousTask, 0, Cons.WAIT_TIME_BEFORE_CHECKING_METRICS_SERVICE);

        /**
         * Timer Task for synchronising metrics with the server
         */
        TimerTask syncMetricsWithServerTask = new TimerTask() {
            @Override
            public void run() {

                handler.post(new Runnable() {
                    @SuppressWarnings("unchecked")
                    public void run() {

                        String[] slugsArray = new String[]{"steps-count", "active-calories",
                                "distance-traveled"};

                        int[] valuesArray = new int[]{prefs.getInt(Cons.EXTRAS_USER_STEPS_COUNT, 0), prefs.getInt(Cons.EXTRAS_USER_ACTIVE_CALORIES, 0), prefs
                                .getInt(Cons.EXTRAS_USER_DISTANCE_TRAVELED, 0)};

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
                    }
                });
            }
        };

        timer.schedule(syncMetricsWithServerTask, 0, Cons.WAIT_TIME_BEFORE_SERVER_SYNC);

    }

    @Override
    public void onStart(Intent intent, int startId) {
        Log.i(TAG, "[SERVICE] onStart");
        super.onStart(intent, startId);
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "[SERVICE] onDestroy");

        // Unregister our receiver.
        unregisterReceiver(mReceiver);
        unregisterDetector();

        mStateEditor = mState.edit();
        mStateEditor.putInt("steps", mSteps);
        mStateEditor.putInt("pace", mPace);
        mStateEditor.putFloat("distance", mDistance);
        mStateEditor.putFloat("speed", mSpeed);
        mStateEditor.putFloat("calories", mCalories);
        mStateEditor.apply();

        wakeLock.release();

        super.onDestroy();

        // Stop detecting
        mSensorManager.unregisterListener(mStepDetector);

        // Tell the user we stopped.
        Toast.makeText(this, "Service Stopped", Toast.LENGTH_SHORT).show();
    }

    private void registerDetector() {
        mSensor = mSensorManager.getDefaultSensor(
                Sensor.TYPE_ACCELEROMETER /*|
            Sensor.TYPE_MAGNETIC_FIELD |
            Sensor.TYPE_ORIENTATION*/);
        mSensorManager.registerListener(mStepDetector,
                mSensor,
                SensorManager.SENSOR_DELAY_FASTEST);
    }

    private void unregisterDetector() {
        mSensorManager.unregisterListener(mStepDetector);
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "[SERVICE] onBind");
        return mBinder;
    }

    public void registerCallback(ICallback cb) {
        mCallback = cb;
        mStepDisplayer.passValue();
        mPaceListener.passValue();
    }

    public void reloadSettings() {
        if (mStepDisplayer != null) mStepDisplayer.reloadSettings();
        if (mPaceNotifier != null) mPaceNotifier.reloadSettings();
        if (mDistanceNotifier != null) mDistanceNotifier.reloadSettings();
        if (mSpeedNotifier != null) mSpeedNotifier.reloadSettings();
        if (mCaloriesNotifier != null) mCaloriesNotifier.reloadSettings();
    }

    public void resetValues() {
        prefs.edit().putInt(Cons.EXTRAS_USER_DISTANCE_TRAVELED, 0).apply();
        prefs.edit().putInt(Cons.EXTRAS_USER_ACTIVE_CALORIES, 0).apply();
        prefs.edit().putInt(Cons.EXTRAS_USER_STEPS_COUNT, 0).apply();

        mStepDisplayer.setSteps(0);
        mPaceNotifier.setPace(0);
        mDistanceNotifier.setDistance(0);
        mSpeedNotifier.setSpeed(0);
        mCaloriesNotifier.setCalories(0);
    }

    private void acquireWakeLock() {
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        int wakeFlags;
//        if (mPedometerSettings.wakeAggressively()) {
//            wakeFlags = PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP;
//        } else if (mPedometerSettings.keepScreenOn()) {
        wakeFlags = PowerManager.SCREEN_DIM_WAKE_LOCK;
//        } else {
//            wakeFlags = PowerManager.PARTIAL_WAKE_LOCK;
//        }

        wakeLock = pm.newWakeLock(wakeFlags, TAG);
        wakeLock.acquire();
    }


    public interface ICallback {
        public void stepsChanged(int value);

        public void paceChanged(int value);

        public void distanceChanged(float value);

        public void speedChanged(float value);

        public void caloriesChanged(float value);
    }

    /**
     * Class for clients to access.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with
     * IPC.
     */
    public class StepBinder extends Binder {
        public StepService getService() {
            return StepService.this;
        }
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

