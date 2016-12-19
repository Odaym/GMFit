package com.mcsaatchi.gmfit.common.activities;

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
import butterknife.Bind;
import butterknife.ButterKnife;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.otto.EventBusPoster;
import com.mcsaatchi.gmfit.architecture.otto.EventBusSingleton;
import com.mcsaatchi.gmfit.architecture.rest.SlugBreakdownResponseInnerData;
import com.mcsaatchi.gmfit.common.Constants;
import com.mcsaatchi.gmfit.common.classes.SlidingTabLayout;
import com.mcsaatchi.gmfit.common.fragments.SlugBreakdownFragmentDaily;
import com.mcsaatchi.gmfit.common.fragments.SlugBreakdownFragmentMonthly;
import com.mcsaatchi.gmfit.common.fragments.SlugBreakdownFragmentYearly;
import java.util.ArrayList;

public class SlugBreakdownActivity extends BaseActivity {
  private static final int DAY_BREAKDOWN = 0;
  private static final int MONTH_BREAKDOWN = 1;
  private static final int YEAR_BREAKDOWN = 2;
  @Bind(R.id.pager) ViewPager pager;
  @Bind(R.id.tabs) SlidingTabLayout tabs;
  @Bind(R.id.toolbar) Toolbar toolbar;
  @Bind(R.id.parentLayoutToCustomize) LinearLayout parentLayoutToCustomize;
  private String typeOfFragmentToCustomizeFor;
  private SlugBreakdownResponseInnerData slugBreakdownData;
  private String chartTitle;
  private String chartType;

  private String[] tabTitles = new String[] {
      "Day", "Month", "Year"
  };

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_slug_breakdown);

    ButterKnife.bind(this);

    Bundle intentExtras = getIntent().getExtras();

    //Grab the Fragment type from one of the three Fragments (Fitness, Nutrition, Health)
    if (intentExtras != null) {
      chartTitle = intentExtras.getString(Constants.EXTRAS_CHART_FULL_NAME);

      setupToolbar(toolbar, chartTitle, true);

      typeOfFragmentToCustomizeFor = intentExtras.getString(Constants.EXTRAS_FRAGMENT_TYPE);
      slugBreakdownData = intentExtras.getParcelable(Constants.BUNDLE_SLUG_BREAKDOWN_DATA);
      chartType = intentExtras.getString(Constants.EXTRAS_CHART_TYPE_SELECTED, "");
      chartTitle = intentExtras.getString(Constants.EXTRAS_CHART_FULL_NAME, "");

      switch (typeOfFragmentToCustomizeFor) {
        case Constants.EXTRAS_FITNESS_FRAGMENT:
          parentLayoutToCustomize.setBackground(
              getResources().getDrawable(R.drawable.fitness_background));
          break;
        case Constants.EXTRAS_NUTRITION_FRAGMENT:
          parentLayoutToCustomize.setBackground(
              getResources().getDrawable(R.drawable.nutrition_background));
          break;
        case Constants.EXTRAS_HEALTH_FRAGMENT:
          break;
      }
    }

    SlugBreakdownViewPager_Adapter fragmentsPagerAdapter =
        new SlugBreakdownViewPager_Adapter(getSupportFragmentManager(), tabTitles,
            slugBreakdownData, chartType, typeOfFragmentToCustomizeFor);

    tabs.setDistributeEvenly(true);
    tabs.setSelectedIndicatorColors(getResources().getColor(android.R.color.white));

    pager.setAdapter(fragmentsPagerAdapter);
    pager.setCurrentItem(DAY_BREAKDOWN);

    tabs.setViewPager(pager);
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.chart_slug_breakdown, menu);

    return super.onCreateOptionsMenu(menu);
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.deleteChartBTN:
        new AlertDialog.Builder(this).setTitle(R.string.delete_chart_dialog_title)
            .setMessage(R.string.delete_chart_dialog_message)
            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int which) {

                switch (typeOfFragmentToCustomizeFor) {
                  case Constants.EXTRAS_FITNESS_FRAGMENT:
                    EventBusSingleton.getInstance()
                        .post(
                            new EventBusPoster(Constants.EXTRAS_FITNESS_CHART_DELETED, chartTitle));
                    break;
                  case Constants.EXTRAS_NUTRITION_FRAGMENT:
                    EventBusSingleton.getInstance()
                        .post(new EventBusPoster(Constants.EXTRAS_NUTRITION_CHART_DELETED,
                            chartTitle));
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
    private String typeOfFragmentToCustomizeFor;

    SlugBreakdownViewPager_Adapter(FragmentManager fm, String[] tabTitles,
        SlugBreakdownResponseInnerData slugBreakdownData, String chartType,
        String typeOfFragmentToCustomizeFor) {
      super(fm);

      this.tabTitles = tabTitles;
      this.slugBreakdownData = slugBreakdownData;
      this.typeOfFragmentToCustomizeFor = typeOfFragmentToCustomizeFor;
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

    @Override public Fragment getItem(int position) {
      Bundle fragmentArguments = new Bundle();
      fragmentArguments.putString(Constants.EXTRAS_FRAGMENT_TYPE, typeOfFragmentToCustomizeFor);

      Fragment slugBreakdownFragment;

      float slugBreakdownYearlyTotal = 0;

      for (int i = 0; i < slugBreakdownData.getYearly().size(); i++)
        slugBreakdownYearlyTotal +=
            Float.parseFloat(slugBreakdownData.getYearly().get(i).getTotal());

      switch (position) {
        case DAY_BREAKDOWN:
          slugBreakdownFragment = new SlugBreakdownFragmentDaily();
          fragmentArguments.putFloat(Constants.BUNDLE_SLUG_BREAKDOWN_YEARLY_TOTAL,
              slugBreakdownYearlyTotal);
          fragmentArguments.putParcelableArrayList(Constants.BUNDLE_SLUG_BREAKDOWN_DATA_DAILY,
              (ArrayList<? extends Parcelable>) slugBreakdownData.getDaily());
          fragmentArguments.putString(Constants.BUNDLE_SLUG_BREAKDOWN_MEASUREMENT_UNIT,
              measurementUnit);
          slugBreakdownFragment.setArguments(fragmentArguments);
          return slugBreakdownFragment;
        case MONTH_BREAKDOWN:
          slugBreakdownFragment = new SlugBreakdownFragmentMonthly();
          fragmentArguments.putFloat(Constants.BUNDLE_SLUG_BREAKDOWN_YEARLY_TOTAL,
              slugBreakdownYearlyTotal);
          fragmentArguments.putParcelableArrayList(Constants.BUNDLE_SLUG_BREAKDOWN_DATA_MONTHLY,
              (ArrayList<? extends Parcelable>) slugBreakdownData.getMonthly());
          fragmentArguments.putString(Constants.BUNDLE_SLUG_BREAKDOWN_MEASUREMENT_UNIT,
              measurementUnit);
          slugBreakdownFragment.setArguments(fragmentArguments);
          return slugBreakdownFragment;
        case YEAR_BREAKDOWN:
          slugBreakdownFragment = new SlugBreakdownFragmentYearly();
          fragmentArguments.putFloat(Constants.BUNDLE_SLUG_BREAKDOWN_YEARLY_TOTAL,
              slugBreakdownYearlyTotal);
          fragmentArguments.putParcelableArrayList(Constants.BUNDLE_SLUG_BREAKDOWN_DATA_YEARLY,
              (ArrayList<? extends Parcelable>) slugBreakdownData.getYearly());
          fragmentArguments.putString(Constants.BUNDLE_SLUG_BREAKDOWN_MEASUREMENT_UNIT,
              measurementUnit);
          slugBreakdownFragment.setArguments(fragmentArguments);
          return slugBreakdownFragment;
      }

      return null;
    }

    @Override public CharSequence getPageTitle(int position) {
      return tabTitles[position];
    }

    @Override public int getCount() {
      return tabTitles.length;
    }
  }
}
