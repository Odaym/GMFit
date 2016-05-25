package com.mcsaatchi.gmfit;

import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.NestedScrollView;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.FrameLayout;

import com.mcsaatchi.gmfit.activities.Main_Activity;

public class MainActivityTest extends ActivityInstrumentationTestCase2<Main_Activity> {

    private Main_Activity mainActivity;

    public MainActivityTest() {
        super(Main_Activity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        mainActivity = getActivity();
    }

    public void testCoordinatorLayoutNotNull(){
        CoordinatorLayout coordinatorLayout = (CoordinatorLayout) mainActivity.findViewById(R.id.myCoordinator);

        assertNotNull(coordinatorLayout);
    }

    public void testNestedScrollViewNotNull(){
        NestedScrollView nestedScrollView = (NestedScrollView) mainActivity.findViewById(R.id.myScrollingContent);

        assertNotNull(nestedScrollView);
    }

    public void testFragmentContainerNotNull(){
        FrameLayout fragmentContainer = (FrameLayout) mainActivity.findViewById(R.id.fragment_container);

        assertNotNull(fragmentContainer);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }
}
