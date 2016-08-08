package com.mcsaatchi.gmfit.pedometer;

import android.content.SharedPreferences;

/**
 * Wrapper for {@link SharedPreferences}, handles preferences-related tasks.
 *
 */

public class PedometerSettings {

    public static int M_NONE = 1;
    public static int M_PACE = 2;
    public static int M_SPEED = 3;
    SharedPreferences mSettings;

    public PedometerSettings(SharedPreferences settings) {
        mSettings = settings;
    }

    public boolean isMetric() {
        return mSettings.getString("units", "imperial").equals("metric");
    }

    public float getStepLength() {
        try {
            return Float.valueOf(mSettings.getString("step_length", "7").trim());
        } catch (NumberFormatException e) {
            // TODO: reset value, & notify user somehow
            return 0f;
        }
    }

    public float getBodyWeight() {
        try {
            return Float.valueOf(mSettings.getString("body_weight", "77").trim());
        } catch (NumberFormatException e) {
            // TODO: reset value, & notify user somehow
            return 0f;
        }
    }

    public void setBodyWeight(float weight) {
        mSettings.edit().putString("body_weight", "77").apply();
    }

    public boolean wakeAggressively() {
        return mSettings.getString("operation_level", "run_in_background").equals("wake_up");
    }

    public boolean isRunning() {
        return mSettings.getString("exercise_type", "running").equals("running");
    }

    //-------------------------------------------------------------------
    // Desired pace & speed: 
    // these can not be set in the preference activity, only on the main
    // screen if "maintain" is set to "pace" or "speed" 

    public float getDesiredSpeed() {
        return mSettings.getFloat("desired_speed", 4f); // km/h or mph
    }

    public void savePaceOrSpeedSetting(int maintain, float desiredPaceOrSpeed) {
        SharedPreferences.Editor editor = mSettings.edit();
        if (maintain == M_PACE) {
            editor.putInt("desired_pace", (int) desiredPaceOrSpeed);
        } else if (maintain == M_SPEED) {
            editor.putFloat("desired_speed", desiredPaceOrSpeed);
        }
        editor.apply();
    }

    public void saveServiceRunningWithTimestamp(boolean running) {
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putBoolean("service_running", running);
        editor.putLong("last_seen", Utils.currentTimeInMillis());
        editor.apply();
    }

    public void saveServiceRunningWithNullTimestamp(boolean running) {
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putBoolean("service_running", running);
        editor.putLong("last_seen", 0);
        editor.apply();
    }

    public void clearServiceRunning() {
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putBoolean("service_running", false);
        editor.putLong("last_seen", 0);
        editor.apply();
    }

    public boolean isServiceRunning() {
        return mSettings.getBoolean("service_running", false);
    }

    public boolean isNewStart() {
        // activity last paused more than 10 minutes ago
        return mSettings.getLong("last_seen", 0) < Utils.currentTimeInMillis() - 1000 * 60 * 10;
    }
}
