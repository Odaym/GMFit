package com.mcsaatchi.gmfit.activities;


import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.mcsaatchi.gmfit.R;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class AppRunThrough_Test {

    @Rule
    public ActivityTestRule<Splash_Activity> mActivityTestRule = new ActivityTestRule<>(Splash_Activity.class);

    @Rule
    public ActivityTestRule<Login_Activity> loginActivityRef = new ActivityTestRule<>(Login_Activity.class);

    @Rule
    public ActivityTestRule<SignIn_Activity> signInActivityRef = new ActivityTestRule<>(SignIn_Activity.class);

    @Rule
    public ActivityTestRule<Main_Activity> mainActivity = new ActivityTestRule<>(Main_Activity.class);

    @Test
    public void appRunThrough_Test() {

        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.signInBTN), withText("Sign In"),
                        withParent(withId(R.id.signInButtonsLayout))));
        appCompatButton.perform(click());

        ViewInteraction appCompatTextView = onView(
                allOf(withId(R.id.showPasswordTV), withText("Show")));
        appCompatTextView.perform(click());

        ViewInteraction appCompatTextView2 = onView(
                allOf(withId(R.id.showPasswordTV), withText("Hide")));
        appCompatTextView2.perform(click());

        ViewInteraction appCompatButton2 = onView(
                allOf(withId(R.id.signInBTN), withText("Sign In")));
        appCompatButton2.perform(click());

        ViewInteraction frameLayout = onView(
                allOf(withId(R.id.bottom_navigation_container)));
        frameLayout.perform(click());

        ViewInteraction frameLayout2 = onView(
                allOf(withId(R.id.bottom_navigation_container)));
        frameLayout2.perform(click());

        ViewInteraction frameLayout3 = onView(
                allOf(withId(R.id.bottom_navigation_container)));
        frameLayout3.perform(click());

        ViewInteraction appCompatButton3 = onView(
                allOf(withId(R.id.logoutBTN), withText("Logout"),
                        withParent(withId(R.id.userPictureAndNameLayout))));
        appCompatButton3.perform(click());
    }
}
