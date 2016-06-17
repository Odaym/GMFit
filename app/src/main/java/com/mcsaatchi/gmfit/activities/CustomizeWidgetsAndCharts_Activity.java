package com.mcsaatchi.gmfit.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.internal.ParcelableSparseArray;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.classes.Cons;
import com.mcsaatchi.gmfit.classes.Helpers;
import com.mcsaatchi.gmfit.classes.SlidingTabLayout;
import com.mcsaatchi.gmfit.fragments.CustomizeCharts_Fragment;
import com.mcsaatchi.gmfit.fragments.CustomizeWidgets_Fragment;

import butterknife.Bind;
import butterknife.ButterKnife;

public class CustomizeWidgetsAndCharts_Activity extends Base_Activity {
    @Bind(R.id.pager)
    ViewPager pager;
    @Bind(R.id.tabs)
    SlidingTabLayout tabs;

    private String typeOfFragmentToCustomizeFor;
    private ParcelableSparseArray widgetsMapExtra;

    private String[] tabTitles = new String[]{
            "Widgets", "Charts"
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(Helpers.createActivityBundleWithProperties(R.string.customize_widgets_and_charts_activity_title, true));

        setContentView(R.layout.activity_customize_widgets_and_charts);

        ButterKnife.bind(this);

        Bundle intentExtras = getIntent().getExtras();

        //Grab the Fragment type from one of the three Fragments (Fitness, Nutrition, Health)
        if (intentExtras != null) {
            typeOfFragmentToCustomizeFor = intentExtras.getString(Cons.EXTRAS_CUSTOMIZE_WIDGETS_FRAGMENT_TYPE);
            widgetsMapExtra = (ParcelableSparseArray) intentExtras.getSparseParcelableArray(Cons.BUNDLE_FITNESS_WIDGETS_MAP);
        }

        CustomizerViewPager_Adapter fragmentsPagerAdapter = new CustomizerViewPager_Adapter(getSupportFragmentManager(), tabTitles, widgetsMapExtra);

        tabs.setDistributeEvenly(true);

        pager.setAdapter(fragmentsPagerAdapter);
        pager.setCurrentItem(0);

        tabs.setViewPager(pager);
    }

    public class CustomizerViewPager_Adapter extends FragmentStatePagerAdapter {

        private String[] tabTitles;
        private ParcelableSparseArray widgetsMapExtra;

        public CustomizerViewPager_Adapter(FragmentManager fm, String[] tabTitles, ParcelableSparseArray widgetsMapExtra) {
            super(fm);

            this.tabTitles = tabTitles;
            this.widgetsMapExtra = widgetsMapExtra;
        }

        @Override
        public Fragment getItem(int position) {
            Bundle fragmentArguments = new Bundle();
            fragmentArguments.putString(Cons.EXTRAS_CUSTOMIZE_WIDGETS_FRAGMENT_TYPE, typeOfFragmentToCustomizeFor);
            fragmentArguments.putParcelable(Cons.BUNDLE_FITNESS_WIDGETS_MAP, widgetsMapExtra);

            switch (position) {
                case 0:
                    Fragment customizeWidgetFragment = new CustomizeWidgets_Fragment();
                    customizeWidgetFragment.setArguments(fragmentArguments);
                    return customizeWidgetFragment;
                case 1:
                    Fragment customizeChartsFragment = new CustomizeCharts_Fragment();
                    customizeChartsFragment.setArguments(fragmentArguments);
                    return customizeChartsFragment;
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
