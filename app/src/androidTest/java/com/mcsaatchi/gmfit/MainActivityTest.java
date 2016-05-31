package com.mcsaatchi.gmfit;

import android.support.design.widget.CoordinatorLayout;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.widget.NestedScrollView;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.FrameLayout;

import com.mcsaatchi.gmfit.activities.Main_Activity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest extends ActivityInstrumentationTestCase2<Main_Activity> {

    @Rule
    public ActivityTestRule<Main_Activity> mainActivityActivityTestRule = new ActivityTestRule<Main_Activity>(Main_Activity.class);
    private Main_Activity mainActivity;

    public MainActivityTest() {
        super(Main_Activity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        mainActivity = getActivity();
    }

    @Test
    public void clickAddChartButton_opensAddChartUi() throws Exception{
    }

    @Test
    public void coordinatorLayoutNotNull() {
        CoordinatorLayout coordinatorLayout = (CoordinatorLayout) mainActivity.findViewById(R.id.myCoordinator);

        assertNotNull(coordinatorLayout);
    }

    public void nestedScrollViewNotNull() {
        NestedScrollView nestedScrollView = (NestedScrollView) mainActivity.findViewById(R.id.myScrollingContent);

        assertNotNull(nestedScrollView);
    }

    public void fragmentContainerNotNull() {
        FrameLayout fragmentContainer = (FrameLayout) mainActivity.findViewById(R.id.fragment_container);

        assertNotNull(fragmentContainer);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }
}
