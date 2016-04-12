package com.mcsaatchi.gmfit.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.widget.FrameLayout;

import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.classes.DefaultIndicator_Controller;

import butterknife.Bind;
import butterknife.ButterKnife;

public class Login_Activity extends Base_Activity {

    private DefaultIndicator_Controller indicatorController;

    @Bind(R.id.viewpager)
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.login_activity);

        ButterKnife.bind(this);

        viewPager.setAdapter(new IntroAdapter(getSupportFragmentManager()));

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
    }

    private void initController() {
        if (indicatorController == null)
            indicatorController = new DefaultIndicator_Controller();

        FrameLayout indicatorContainer = (FrameLayout) findViewById(R.id.indicator_container);
        indicatorContainer.addView(indicatorController.newInstance(this));

        indicatorController.initialize(4);
    }

    public class IntroAdapter extends FragmentPagerAdapter {

        public IntroAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return IntroSlider_Fragment.newInstance(R.layout.fragment_intro_slide_1);
                case 1:
                    return IntroSlider_Fragment.newInstance(R.layout.fragment_intro_slide_2);
                case 2:
                    return IntroSlider_Fragment.newInstance(R.layout.fragment_intro_slide_3);
                case 3:
                    return IntroSlider_Fragment.newInstance(R.layout.fragment_intro_slide_4);
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 4;
        }
    }
}