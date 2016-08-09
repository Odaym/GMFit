package com.mcsaatchi.gmfit.pedometer;


/**
 * Calculates and displays the approximate calories.
 */

public class CaloriesNotifier implements StepListener {

    private static double METRIC_RUNNING_FACTOR = 1.02784823;
    private static double IMPERIAL_RUNNING_FACTOR = 0.75031498;
    private static double METRIC_WALKING_FACTOR = 0.708;
    private static double IMPERIAL_WALKING_FACTOR = 0.517;
    PedometerSettings mSettings;
    Utils mUtils;
    boolean mIsMetric;
    boolean mIsRunning;
    float mStepLength;
    float mBodyWeight;
    private Listener mListener;
    private double mCalories = 0;
    public CaloriesNotifier(Listener listener, PedometerSettings settings, Utils utils) {
        mListener = listener;
        mUtils = utils;
        mSettings = settings;
        reloadSettings();
    }

    public void setCalories(float calories) {
        mCalories = calories;
        notifyListener();
    }

    public void reloadSettings() {
        mIsMetric = mSettings.isMetric();
        mIsRunning = mSettings.isRunning();
        mStepLength = mSettings.getStepLength();
        mBodyWeight = mSettings.getBodyWeight();
        notifyListener();
    }

    public void resetValues() {
        mCalories = 0;
    }

    public void isMetric(boolean isMetric) {
        mIsMetric = isMetric;
    }

    public void setStepLength(float stepLength) {
        mStepLength = stepLength;
    }

    public void onStep() {

        mCalories +=
                (mBodyWeight * (mIsRunning ? METRIC_RUNNING_FACTOR : METRIC_WALKING_FACTOR))
                        // Distance:
                        * mStepLength // centimeters
                        / 100000.0; // centimeters/kilometer

        notifyListener();
    }

    private void notifyListener() {
        mListener.valueChanged((float) mCalories);
    }

    public void passValue() {

    }

    public interface Listener {
        public void valueChanged(float value);

        public void passValue();
    }
}

