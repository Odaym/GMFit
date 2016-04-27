package com.mcsaatchi.gmfit.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.classes.DefaultIndicator_Controller;
import com.mcsaatchi.gmfit.classes.Helpers;
import com.mcsaatchi.gmfit.classes.Indicator_Controller;
import com.mcsaatchi.gmfit.fragments.IntroSlider_Fragment;
import com.mcsaatchi.gmfit.fragments.Setup_Profile_1_Fragment;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SetupProfile_Activity extends Base_Activity {
    @Bind(R.id.viewpager)
    ViewPager viewPager;
    @Bind(R.id.nextPageBTN)
    Button nextPageBTN;
    @Bind(R.id.previousPageBTN)
    Button previousPageBTN;
    @Bind(R.id.previousPageControllerLayout)
    LinearLayout previousPageControllerLayout;

    private Indicator_Controller indicatorController;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(Helpers.createActivityBundleWithProperties(R.string.setup_profile_activity_title, true));

        setContentView(R.layout.activity_setup_profile);

        ButterKnife.bind(this);

        setupViewPager();
    }

    private void setupViewPager() {
        viewPager.setAdapter(new SetupProfile_Adapter(getSupportFragmentManager()));

        //Prevent this viewpager from swiping
        viewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                indicatorController.selectPosition(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        initController();

        nextPageBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewPager.getCurrentItem() + 1 > 0)
                    previousPageControllerLayout.setVisibility(View.VISIBLE);

                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
            }
        });

        previousPageBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewPager.getCurrentItem() - 1 == 0)
                    previousPageControllerLayout.setVisibility(View.INVISIBLE);

                viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
            }
        });
    }

    private void initController() {
        if (indicatorController == null)
            indicatorController = new DefaultIndicator_Controller();

        FrameLayout indicatorContainer = (FrameLayout) findViewById(R.id.indicator_container);
        indicatorContainer.addView(indicatorController.newInstance(this));

        indicatorController.initialize(5);
    }

    public class SetupProfile_Adapter extends FragmentPagerAdapter {

        public SetupProfile_Adapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new Setup_Profile_1_Fragment();
                case 1:
                    return IntroSlider_Fragment.newInstance(R.layout.fragment_intro_slide_2);
                case 2:
                    return IntroSlider_Fragment.newInstance(R.layout.fragment_intro_slide_3);
                case 3:
                    return IntroSlider_Fragment.newInstance(R.layout.fragment_intro_slide_4);
                case 4:
                    return IntroSlider_Fragment.newInstance(R.layout.fragment_intro_slide_4);
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 5;
        }
    }
}