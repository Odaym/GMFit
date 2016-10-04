package com.mcsaatchi.gmfit.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.LinearLayout;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.classes.Constants;
import com.mcsaatchi.gmfit.fragments.Fitness_Fragment;
import com.mcsaatchi.gmfit.fragments.Health_Fragment;
import com.mcsaatchi.gmfit.fragments.MainProfile_Fragment;
import com.mcsaatchi.gmfit.fragments.Nutrition_Fragment;
import com.mcsaatchi.gmfit.pedometer.SensorListener;
import com.mcsaatchi.gmfit.rest.AuthenticationResponseChart;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class Main_Activity extends Base_Activity {

    public static int USER_AUTHORISED_REQUEST_CODE = 5;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.mainContentLayout)
    LinearLayout mainContentLayout;
    @Bind(R.id.bottom_navigation)
    AHBottomNavigation bottom_navigation;

    private Fitness_Fragment fitnessFragment;
    private Nutrition_Fragment nutritionFragment;
    private Health_Fragment healthFragment;
    private MainProfile_Fragment mainProfileFragment;

    private ArrayList<AuthenticationResponseChart> chartsMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        startService(new Intent(this, SensorListener.class));

        ButterKnife.bind(this);

        setupToolbar(toolbar, R.string.app_name, false);

        SharedPreferences prefs = getSharedPreferences(Constants.SHARED_PREFS_TITLE, Context.MODE_PRIVATE);

        Log.d("USER_ACCESS_TOKEN", "onCreate: User access token is : " + prefs.getString(Constants.PREF_USER_ACCESS_TOKEN, Constants.NO_ACCESS_TOKEN_FOUND_IN_PREFS));

        if (getIntent().getExtras() != null) {
            chartsMap = getIntent().getExtras().getParcelableArrayList("charts");
        }

        fitnessFragment = new Fitness_Fragment();
        nutritionFragment = new Nutrition_Fragment();
        healthFragment = new Health_Fragment();
        mainProfileFragment = new MainProfile_Fragment();

        AHBottomNavigationItem item1 = new AHBottomNavigationItem(R.string.fitness_tab_title, R.drawable.ic_bottom_bar_fitness_inactive, R.color.fitness_pink);
        AHBottomNavigationItem item2 = new AHBottomNavigationItem(R.string.nutrition_tab_title, R.drawable.ic_bottom_bar_nutrition_inactive, R.color.nutrition_red);
        AHBottomNavigationItem item3 = new AHBottomNavigationItem(R.string.health_tab_title, R.drawable.ic_bottom_bar_health_inactive, R.color.health_green);
        AHBottomNavigationItem item4 = new AHBottomNavigationItem(R.string.profile_tab_title, R.drawable.ic_bottom_bar_profile, R.color.profile_blue);

        bottom_navigation.addItem(item1);
        bottom_navigation.addItem(item2);
        bottom_navigation.addItem(item3);
        bottom_navigation.addItem(item4);

        bottom_navigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {
                switch (position) {
                    case 0:
                        Bundle bundle = new Bundle();
                        bundle.putParcelableArrayList("charts", chartsMap);

                        try {
                            fitnessFragment.setArguments(bundle);
                        } catch (IllegalStateException e) {
                            Log.d("TAG", "onMenuTabSelected: Fragment already active!");
                        }

                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fitnessFragment).commit();
                        mainContentLayout.setBackground(getResources().getDrawable(R.drawable.fitness_background));
                        break;
                    case 1:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, nutritionFragment).commit();
                        mainContentLayout.setBackground(getResources().getDrawable(R.drawable.nutrition_background));
                        break;
                    case 2:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, healthFragment).commit();
                        mainContentLayout.setBackground(getResources().getDrawable(R.drawable.health_background));
                        break;
                    case 3:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, mainProfileFragment).commit();
                        break;
                }

                return true;
            }
        });

        bottom_navigation.setColored(true);
        bottom_navigation.setCurrentItem(0);
        bottom_navigation.setForceTitlesDisplay(true);
        bottom_navigation.setBehaviorTranslationEnabled(true);
        bottom_navigation.setDefaultBackgroundColor(getResources().getColor(R.color.white));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        fitnessFragment.onActivityResult(USER_AUTHORISED_REQUEST_CODE, resultCode, data);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }
}