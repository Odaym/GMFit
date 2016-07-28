/*
 *  Pedometer - Android App
 *  Copyright (C) 2009 Levente Bagi
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.mcsaatchi.gmfit.pedometer;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.util.ArrayList;

/**
 * Detects steps and notifies all listeners (that implement StepListener).
 */

public class StepDetector implements SensorEventListener {
    private final static String TAG = "StepDetector";

    private float mLimit = 36.75f; // 1.97  2.96  4.44  6.66  10.00  15.00  22.50  33.75  50.62

    private static final int arrays_size = 6;

    private float lastValue;

    private float lastDirection;

    private float mScale[] = new float[2];

    private float mYOffset;


    private float mLastExtremes[][] = {new float[arrays_size], new float[arrays_size]};

    private float lastDiff[] = new float[arrays_size];

    private int lastMatch = -1;

    private ArrayList<StepListener> mStepListeners = new ArrayList<StepListener>();

    public StepDetector() {
        mYOffset = 480;
//        mScale[0] = -(h * 0.5f * (1.0f / (SensorManager.STANDARD_GRAVITY * 2)));
        mScale[1] = -(mYOffset * 0.5f * (1.0f / (SensorManager.MAGNETIC_FIELD_EARTH_MAX)));
    }

    public void setSensitivity(float sensitivity) {
        mLimit = sensitivity; // 1.97  2.96  4.44  6.66  10.00  15.00  22.50  33.75  50.62
    }

    public void addStepListener(StepListener sl) {
        mStepListeners.add(sl);
    }

    public void onSensorChanged(SensorEvent event) {
        Sensor sensor = event.sensor;
        synchronized (this) {
            int sensorType = (sensor.getType() == Sensor.TYPE_ACCELEROMETER) ? 1 : 0;

            if (sensorType == 1) {
                float sensorValuesSum = 0;

                for (int i = 0; i < 3; i++) {
                    final float sensorValue = mYOffset + event.values[i] * mScale[sensorType];
                    sensorValuesSum += sensorValue;
                }

                int k = 0;

                float sensorValuesAverage = sensorValuesSum / 3;

                float direction = (sensorValuesAverage > lastValue ? 1 : (sensorValuesAverage < lastValue ? -1 : 0));

                if (direction == -lastDirection) {

                    int extType = (direction > 0 ? 0 : 1);

                    mLastExtremes[extType][k] = lastValue;

                    float diff = Math.abs(mLastExtremes[extType][k] - mLastExtremes[1 - extType][k]);

                    if (diff > mLimit) {

                        boolean isAlmostAsLargeAsPrevious = diff > (lastDiff[k] * 2 / 3);
                        boolean isPreviousLargeEnough = lastDiff[k] > (diff / 3);
                        boolean isNotContra = (lastMatch != 1 - extType);

                        if (isAlmostAsLargeAsPrevious && isPreviousLargeEnough && isNotContra) {

                            for (StepListener stepListener : mStepListeners) {
                                stepListener.onStep();
                            }

                            lastMatch = extType;
                        } else {
                            lastMatch = -1;
                        }
                    }

                    lastDiff[k] = diff;
                }

                lastDirection = direction;

                lastValue = sensorValuesAverage;
            }
        }
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}