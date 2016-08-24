package com.mcsaatchi.gmfit.activities;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.widget.LinearLayout;

import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.classes.Cons;
import com.mcsaatchi.gmfit.classes.SlidingTabLayout;
import com.mcsaatchi.gmfit.fragments.SlugBreakdown_Fragment_Daily;
import com.mcsaatchi.gmfit.fragments.SlugBreakdown_Fragment_Monthly;
import com.mcsaatchi.gmfit.fragments.SlugBreakdown_Fragment_Yearly;
import com.mcsaatchi.gmfit.rest.SlugBreakdownResponseInnerData;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SlugBreakdown_Activity extends Base_Activity {
    @Bind(R.id.pager)
    ViewPager pager;
    @Bind(R.id.tabs)
    SlidingTabLayout tabs;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.parentLayoutToCustomize)
    LinearLayout parentLayoutToCustomize;

    private String typeOfFragmentToCustomizeFor;
    private SlugBreakdownResponseInnerData slugBreakdownData;

    private String[] tabTitles = new String[]{
            "Day", "Month", "Year"
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_slug_breakdown);

        ButterKnife.bind(this);

        setupToolbar(toolbar, R.string.app_name, true);

        Bundle intentExtras = getIntent().getExtras();

        //Grab the Fragment type from one of the three Fragments (Fitness, Nutrition, Health)
        if (intentExtras != null) {
            typeOfFragmentToCustomizeFor = intentExtras.getString(Cons.EXTRAS_CUSTOMIZE_WIDGETS_FRAGMENT_TYPE);
            slugBreakdownData = intentExtras.getParcelable(Cons.BUNDLE_SLUG_BREAKDOWN_DATA);

            switch(typeOfFragmentToCustomizeFor){
                case Cons.EXTRAS_FITNESS_FRAGMENT:
                    parentLayoutToCustomize.setBackground(getResources().getDrawable(R.drawable.fitness_background));
                    break;
                case Cons.EXTRAS_NUTRITION_FRAGMENT:
                    parentLayoutToCustomize.setBackground(getResources().getDrawable(R.drawable.nutrition_background));
                    break;
                case Cons.EXTRAS_HEALTH_FRAGMENT:
                    break;
            }
        }


        SlugBreakdownViewPager_Adapter fragmentsPagerAdapter = new SlugBreakdownViewPager_Adapter(getSupportFragmentManager(), tabTitles, slugBreakdownData);

        tabs.setDistributeEvenly(true);
        tabs.setSelectedIndicatorColors(getResources().getColor(android.R.color.white));

        pager.setAdapter(fragmentsPagerAdapter);
        pager.setCurrentItem(0);

        tabs.setViewPager(pager);
    }

    public class SlugBreakdownViewPager_Adapter extends FragmentStatePagerAdapter {

        private String[] tabTitles;
        private SlugBreakdownResponseInnerData slugBreakdownData;

        public SlugBreakdownViewPager_Adapter(FragmentManager fm, String[] tabTitles, SlugBreakdownResponseInnerData slugBreakdownData) {
            super(fm);

            this.tabTitles = tabTitles;
            this.slugBreakdownData = slugBreakdownData;
        }

        @Override
        public Fragment getItem(int position) {
            Bundle fragmentArguments = new Bundle();
//            fragmentArguments.putString(Cons.EXTRAS_CUSTOMIZE_WIDGETS_FRAGMENT_TYPE, typeOfFragmentToCustomizeFor);

            Fragment slugBreakdownFragment;

            switch (position) {
                case 0:
                    slugBreakdownFragment = new SlugBreakdown_Fragment_Daily();
                    fragmentArguments.putParcelableArrayList(Cons.BUNDLE_SLUG_BREAKDOWN_DATA_DAILY, (ArrayList<? extends Parcelable>) slugBreakdownData.getDaily());
                    slugBreakdownFragment.setArguments(fragmentArguments);
                    return slugBreakdownFragment;
                case 1:
                    slugBreakdownFragment = new SlugBreakdown_Fragment_Monthly();
                    fragmentArguments.putParcelableArrayList(Cons.BUNDLE_SLUG_BREAKDOWN_DATA_MONTHLY, (ArrayList<? extends Parcelable>) slugBreakdownData.getMonthly());
                    slugBreakdownFragment.setArguments(fragmentArguments);
                    return slugBreakdownFragment;
                case 2:
                    slugBreakdownFragment = new SlugBreakdown_Fragment_Yearly();
                    fragmentArguments.putParcelableArrayList(Cons.BUNDLE_SLUG_BREAKDOWN_DATA_YEARLY, (ArrayList<? extends Parcelable>) slugBreakdownData.getYearly());
                    slugBreakdownFragment.setArguments(fragmentArguments);
                    return slugBreakdownFragment;
            }

            return null;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }

        @Override
        public int getCount() {
            return tabTitles.length;
        }
    }
}
