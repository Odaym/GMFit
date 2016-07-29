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


/**
 * Calculates and displays pace (steps / minute), handles input of desired pace,
 * notifies user if he/she has to go faster or slower.
 * <p/>
 * Uses {@link PaceNotifier}, calculates speed as product of pace and step length.
 *
 */

public class SpeedNotifier implements PaceNotifier.Listener {

    public interface Listener {
        public void valueChanged(float value);

        public void passValue();
    }

    private Listener mListener;

    int mCounter = 0;
    float mSpeed = 0;

    boolean mIsMetric;
    float mStepLength;

    PedometerSettings mSettings;
    Utils mUtils;

    /**
     * Desired speed, adjusted by the user
     */
    float mDesiredSpeed;

    public SpeedNotifier(Listener listener, PedometerSettings settings, Utils utils) {
        mListener = listener;
        mUtils = utils;
        mSettings = settings;
        mDesiredSpeed = mSettings.getDesiredSpeed();
        reloadSettings();
    }

    public void setSpeed(float speed) {
        mSpeed = speed;
        notifyListener();
    }

    public void reloadSettings() {
        mIsMetric = mSettings.isMetric();
        mStepLength = mSettings.getStepLength();
        notifyListener();
    }

    public void setDesiredSpeed(float desiredSpeed) {
        mDesiredSpeed = desiredSpeed;
    }

    private void notifyListener() {
        mListener.valueChanged(mSpeed);
    }

    public void paceChanged(int value) {
        mSpeed = // kilometers / hour
                value * mStepLength // centimeters / minute
                        / 100000f * 60f; // centimeters/kilometer

        notifyListener();
    }


    public void passValue() {
        // Not used
    }
}

