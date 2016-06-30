package com.mcsaatchi.gmfit.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.classes.Cons;
import com.mcsaatchi.gmfit.classes.EventBus_Poster;
import com.mcsaatchi.gmfit.classes.EventBus_Singleton;
import com.mcsaatchi.gmfit.classes.Helpers;
import com.mcsaatchi.gmfit.classes.NonSwipeableViewPager;
import com.mcsaatchi.gmfit.fragments.Setup_Profile_1_Fragment;
import com.mcsaatchi.gmfit.fragments.Setup_Profile_2_Fragment;
import com.mcsaatchi.gmfit.fragments.Setup_Profile_3_Fragment;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SetupProfile_Activity extends Base_Activity {
    @Bind(R.id.viewpager)
    NonSwipeableViewPager viewPager;
    @Bind(R.id.nextPageBTN)
    Button nextPageBTN;
    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(Helpers.createActivityBundleWithProperties(R.string.setup_profile_activity_title, true));

        setContentView(R.layout.activity_setup_profile);

        ButterKnife.bind(this);

        toolbar.setTitle(R.string.setup_profile_step_1_title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setupViewPager();
    }

    private void setupViewPager() {
        viewPager.setAdapter(new SetupProfile_Adapter(getSupportFragmentManager()));

        viewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        toolbar.setTitle(getString(R.string.setup_profile_step_1_title));
                        nextPageBTN.setText(getString(R.string.next_step));
                        break;
                    case 1:
                        toolbar.setTitle(getString(R.string.setup_profile_step_2_title));
                        nextPageBTN.setText(getString(R.string.next_step));
                        break;
                    case 2:
                        toolbar.setTitle(getString(R.string.setup_profile_step_3_title));
                        nextPageBTN.setText(getString(R.string.finish_setup));
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        nextPageBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);

                // Time for submission
                if (nextPageBTN.getText().toString().equals(getString(R.string.finish_setup))) {
                    EventBus_Singleton.getInstance().post(new EventBus_Poster(Cons.EVENT_USER_FINALIZE_SETUP_PROFILE));
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:

                viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);

                break;
        }

        return false;
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
                    return new Setup_Profile_2_Fragment();
                case 2:
                    return new Setup_Profile_3_Fragment();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 3;
        }
    }
}