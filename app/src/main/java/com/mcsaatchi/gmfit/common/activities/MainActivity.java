package com.mcsaatchi.gmfit.common.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.rest.AuthenticationResponseChart;
import com.mcsaatchi.gmfit.architecture.rest.AuthenticationResponseChartData;
import com.mcsaatchi.gmfit.common.Constants;
import com.mcsaatchi.gmfit.common.models.DataChart;
import com.mcsaatchi.gmfit.fitness.fragments.FitnessFragment;
import com.mcsaatchi.gmfit.fitness.pedometer.SensorListener;
import com.mcsaatchi.gmfit.health.fragments.HealthFragment;
import com.mcsaatchi.gmfit.insurance.fragments.InsuranceFragment;
import com.mcsaatchi.gmfit.nutrition.fragments.NutritionFragment;
import com.mcsaatchi.gmfit.profile.fragments.MainProfileFragment;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;
import java.util.ArrayList;
import timber.log.Timber;

public class MainActivity extends BaseActivity {

  public static int USER_AUTHORISED_REQUEST_CODE = 5;
  @Bind(R.id.toolbar) Toolbar toolbar;
  @Bind(R.id.mainContentLayout) LinearLayout mainContentLayout;
  @Bind(R.id.myScrollingContent) NestedScrollView myScrollingContent;
  @Bind(R.id.bottomBar) BottomBar bottomBar;

  private FitnessFragment fitnessFragment;
  private NutritionFragment nutritionFragment;
  private HealthFragment healthFragment;
  private MainProfileFragment mainProfileFragment;

  private ArrayList<AuthenticationResponseChart> chartsMap;
  private ArrayList<DataChart> finalChartsMap = new ArrayList<>();

  @Override protected void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_main);

    startService(new Intent(this, SensorListener.class));

    ButterKnife.bind(this);

    setupToolbar(getClass().getSimpleName(), toolbar, getResources().getString(R.string.app_name),
        false);

    Timber.d("onCreate: User access token is : %s ",
        prefs.getString(Constants.PREF_USER_ACCESS_TOKEN,
            Constants.NO_ACCESS_TOKEN_FOUND_IN_PREFS));

    if (getIntent().getExtras() != null) {
      chartsMap =
          getIntent().getExtras().getParcelableArrayList(Constants.BUNDLE_FITNESS_CHARTS_MAP);

      if (chartsMap != null) {
        for (int i = 0; i < chartsMap.size(); i++) {
          DataChart dataChart = new DataChart();
          dataChart.setChart_id(chartsMap.get(i).getChartId());
          dataChart.setName(chartsMap.get(i).getName());
          dataChart.setType(chartsMap.get(i).getSlug());
          dataChart.setPosition(Integer.parseInt(chartsMap.get(i).getPosition()));
          dataChart.setChartData(
              (ArrayList<AuthenticationResponseChartData>) chartsMap.get(i).getData());
          dataChart.setWhichFragment(Constants.EXTRAS_FITNESS_FRAGMENT);

          finalChartsMap.add(dataChart);
        }
      }
    }

    fitnessFragment = new FitnessFragment();
    nutritionFragment = new NutritionFragment();
    healthFragment = new HealthFragment();
    mainProfileFragment = new MainProfileFragment();

    bottomBar.getTabWithId(R.id.item_one).findViewById(R.id.bb_bottom_bar_icon).setScaleX(1.1f);
    bottomBar.getTabWithId(R.id.item_one).findViewById(R.id.bb_bottom_bar_icon).setScaleY(1.1f);

    bottomBar.getTabWithId(R.id.item_two).findViewById(R.id.bb_bottom_bar_icon).setScaleX(1.1f);
    bottomBar.getTabWithId(R.id.item_two).findViewById(R.id.bb_bottom_bar_icon).setScaleY(1.1f);

    bottomBar.getTabWithId(R.id.item_three).findViewById(R.id.bb_bottom_bar_icon).setScaleX(1.1f);
    bottomBar.getTabWithId(R.id.item_three).findViewById(R.id.bb_bottom_bar_icon).setScaleY(1.1f);

    bottomBar.getTabWithId(R.id.item_four).findViewById(R.id.bb_bottom_bar_icon).setScaleX(1.1f);
    bottomBar.getTabWithId(R.id.item_four).findViewById(R.id.bb_bottom_bar_icon).setScaleY(1.1f);

    bottomBar.getTabWithId(R.id.item_five).findViewById(R.id.bb_bottom_bar_icon).setScaleX(1.1f);
    bottomBar.getTabWithId(R.id.item_five).findViewById(R.id.bb_bottom_bar_icon).setScaleY(1.1f);

    bottomBar.findViewById(R.id.bb_bottom_bar_item_container)
        .setPadding(0, 0, 0,
            getResources().getDimensionPixelSize(R.dimen.bottom_bar_navigation_bottom_padding));

    bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
      @Override public void onTabSelected(@IdRes int tabId) {

        myScrollingContent.fullScroll(View.FOCUS_UP);

        switch (tabId) {
          case R.id.item_one:
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList(Constants.BUNDLE_FITNESS_CHARTS_MAP, finalChartsMap);

            try {
              fitnessFragment.setArguments(bundle);
            } catch (IllegalStateException e) {
              Log.d("TAG", "onMenuTabSelected: Fragment already active!");
            }

            getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fitnessFragment)
                .commit();
            mainContentLayout.setBackground(
                getResources().getDrawable(R.drawable.fitness_background));
            break;
          case R.id.item_two:
            getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, nutritionFragment)
                .commit();
            mainContentLayout.setBackground(
                getResources().getDrawable(R.drawable.nutrition_background));
            break;
          case R.id.item_three:
            getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, healthFragment)
                .commit();
            mainContentLayout.setBackground(
                getResources().getDrawable(R.drawable.health_background));
            break;
          case R.id.item_four:
            myScrollingContent.setClickable(false);
            getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new InsuranceFragment())
                .commit();
            mainContentLayout.setBackground(
                getResources().getDrawable(R.drawable.insurance_background));
            break;
          case R.id.item_five:
            getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, mainProfileFragment)
                .commit();
            mainContentLayout.setBackground(
                getResources().getDrawable(R.drawable.general_background));
            break;
        }
      }
    });
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    fitnessFragment.onActivityResult(USER_AUTHORISED_REQUEST_CODE, resultCode, data);
  }
}