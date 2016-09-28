package com.mcsaatchi.gmfit.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.classes.Cons;
import com.mcsaatchi.gmfit.classes.EventBus_Poster;
import com.mcsaatchi.gmfit.classes.EventBus_Singleton;
import com.mcsaatchi.gmfit.classes.SlidingTabLayout;
import com.mcsaatchi.gmfit.fragments.SlugBreakdown_Fragment_Daily;
import com.mcsaatchi.gmfit.fragments.SlugBreakdown_Fragment_Monthly;
import com.mcsaatchi.gmfit.fragments.SlugBreakdown_Fragment_Yearly;
import com.mcsaatchi.gmfit.rest.SlugBreakdownResponseInnerData;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SlugBreakdown_Activity extends Base_Activity {
    private static final int DAY_BREAKDOWN = 0;
    private static final int MONTH_BREAKDOWN = 1;
    private static final int YEAR_BREAKDOWN = 2;
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
    private String chartTitle;
    private String chartType;

    private String[] tabTitles = new String[]{
            "Day", "Month", "Year"
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_slug_breakdown);

        ButterKnife.bind(this);

        Bundle intentExtras = getIntent().getExtras();

        //Grab the Fragment type from one of the three Fragments (Fitness, Nutrition, Health)
        if (intentExtras != null) {
            chartTitle = intentExtras.getString(Cons.EXTRAS_CHART_FULL_NAME);

            setupToolbar(toolbar, chartTitle, true);

            typeOfFragmentToCustomizeFor = intentExtras.getString(Cons.EXTRAS_CUSTOMIZE_WIDGETS_CHARTS_FRAGMENT_TYPE);
            slugBreakdownData = intentExtras.getParcelable(Cons.BUNDLE_SLUG_BREAKDOWN_DATA);
            chartType = intentExtras.getString(Cons.EXTRAS_CHART_TYPE_SELECTED, "");
            chartTitle = intentExtras.getString(Cons.EXTRAS_CHART_FULL_NAME, "");

            switch (typeOfFragmentToCustomizeFor) {
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

        SlugBreakdownViewPager_Adapter fragmentsPagerAdapter = new SlugBreakdownViewPager_Adapter(getSupportFragmentManager(), tabTitles, slugBreakdownData,
                chartType);

        tabs.setDistributeEvenly(true);
        tabs.setSelectedIndicatorColors(getResources().getColor(android.R.color.white));

        pager.setAdapter(fragmentsPagerAdapter);
        pager.setCurrentItem(DAY_BREAKDOWN);

        tabs.setViewPager(pager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chart_slug_breakdown, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.deleteChartBTN:
                new AlertDialog.Builder(this)
                        .setTitle(R.string.delete_chart_dialog_title)
                        .setMessage(R.string.delete_chart_dialog_message)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                switch (typeOfFragmentToCustomizeFor) {
                                    case Cons.EXTRAS_FITNESS_FRAGMENT:
                                        EventBus_Singleton.getInstance().post(new EventBus_Poster(Cons.EXTRAS_FITNESS_CHART_DELETED));
                                        break;
                                    case Cons.EXTRAS_NUTRITION_FRAGMENT:
                                        EventBus_Singleton.getInstance().post(new EventBus_Poster(Cons.EXTRAS_NUTRITION_CHART_DELETED));
                                        break;
                                }

                                finish();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .show();

                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public class SlugBreakdownViewPager_Adapter extends FragmentStatePagerAdapter {

        private String[] tabTitles;
        private SlugBreakdownResponseInnerData slugBreakdownData;
        private String chartType;
        private String measurementUnit;

        SlugBreakdownViewPager_Adapter(FragmentManager fm, String[] tabTitles, SlugBreakdownResponseInnerData slugBreakdownData, String chartType) {
            super(fm);

            this.tabTitles = tabTitles;
            this.slugBreakdownData = slugBreakdownData;

            this.chartType = chartType;

            switch (this.chartType) {
                case "steps-count":
                    measurementUnit = "steps";
                    break;
                case "distance-traveled":
                    measurementUnit = "km";
                    break;
                case "active-calories":
                    measurementUnit = "kcal";
                    break;
                default:
                    measurementUnit = "units";
            }
        }

        @Override
        public Fragment getItem(int position) {
            Bundle fragmentArguments = new Bundle();
//            fragmentArguments.putString(Cons.EXTRAS_CUSTOMIZE_WIDGETS_CHARTS_FRAGMENT_TYPE, typeOfFragmentToCustomizeFor);

            Fragment slugBreakdownFragment;

            float slugBreakdownYearlyTotal = 0;

            for (int i = 0; i < slugBreakdownData.getYearly().size(); i++)
                slugBreakdownYearlyTotal += Float.parseFloat(slugBreakdownData.getYearly().get(i).getTotal());

            switch (position) {
                case DAY_BREAKDOWN:
                    slugBreakdownFragment = new SlugBreakdown_Fragment_Daily();
                    fragmentArguments.putFloat(Cons.BUNDLE_SLUG_BREAKDOWN_YEARLY_TOTAL, slugBreakdownYearlyTotal);
                    fragmentArguments.putParcelableArrayList(Cons.BUNDLE_SLUG_BREAKDOWN_DATA_DAILY, (ArrayList<? extends Parcelable>) slugBreakdownData.getDaily());
                    fragmentArguments.putString(Cons.BUNDLE_SLUG_BREAKDOWN_MEASUREMENT_UNIT, measurementUnit);
                    slugBreakdownFragment.setArguments(fragmentArguments);
                    return slugBreakdownFragment;
                case MONTH_BREAKDOWN:
                    slugBreakdownFragment = new SlugBreakdown_Fragment_Monthly();
                    fragmentArguments.putFloat(Cons.BUNDLE_SLUG_BREAKDOWN_YEARLY_TOTAL, slugBreakdownYearlyTotal);
                    fragmentArguments.putParcelableArrayList(Cons.BUNDLE_SLUG_BREAKDOWN_DATA_MONTHLY, (ArrayList<? extends Parcelable>) slugBreakdownData.getMonthly());
                    fragmentArguments.putString(Cons.BUNDLE_SLUG_BREAKDOWN_MEASUREMENT_UNIT, measurementUnit);
                    slugBreakdownFragment.setArguments(fragmentArguments);
                    return slugBreakdownFragment;
                case YEAR_BREAKDOWN:
                    slugBreakdownFragment = new SlugBreakdown_Fragment_Yearly();
                    fragmentArguments.putFloat(Cons.BUNDLE_SLUG_BREAKDOWN_YEARLY_TOTAL, slugBreakdownYearlyTotal);
                    fragmentArguments.putParcelableArrayList(Cons.BUNDLE_SLUG_BREAKDOWN_DATA_YEARLY, (ArrayList<? extends Parcelable>) slugBreakdownData.getYearly());
                    fragmentArguments.putString(Cons.BUNDLE_SLUG_BREAKDOWN_MEASUREMENT_UNIT, measurementUnit);
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
