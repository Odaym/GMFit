package com.mcsaatchi.gmfit.activities;


import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class AppLaunch_Test {

    @Rule
    public ActivityTestRule<Splash_Activity> mActivityTestRule = new ActivityTestRule<>(Splash_Activity.class);

    @Test
    public void appLaunch_Test() {
    }

}
