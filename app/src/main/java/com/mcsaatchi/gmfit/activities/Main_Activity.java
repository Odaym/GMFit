package com.mcsaatchi.gmfit.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.LinearLayout;

import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.classes.Cons;
import com.mcsaatchi.gmfit.fragments.Fitness_Fragment;
import com.mcsaatchi.gmfit.fragments.MainProfile_Fragment;
import com.mcsaatchi.gmfit.fragments.Nutrition_Fragment;
import com.mcsaatchi.gmfit.pedometer.SensorListener;
import com.mcsaatchi.gmfit.rest.AuthenticationResponseChart;
import com.mcsaatchi.gmfit.rest.AuthenticationResponseWidget;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnMenuTabClickListener;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class Main_Activity extends Base_Activity {

    public static int USER_AUTHORISED_REQUEST_CODE = 5;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.mainContentLayout)
    LinearLayout mainContentLayout;

    private BottomBar bottomBar;
    private Fitness_Fragment fitnessFragment;
    private Nutrition_Fragment nutritionFragment;
    private MainProfile_Fragment mainProfileFragment;
    private SharedPreferences prefs;

    private ArrayList<AuthenticationResponseWidget> widgetsMap;
    private ArrayList<AuthenticationResponseChart> chartsMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        startService(new Intent(this, SensorListener.class));

        ButterKnife.bind(this);

        setupToolbar(toolbar, R.string.app_name, false);

        prefs = getSharedPreferences(Cons.SHARED_PREFS_TITLE, Context.MODE_PRIVATE);

        Log.d("USER_ACCESS_TOKEN", "onCreate: User access token is : " + prefs.getString(Cons.PREF_USER_ACCESS_TOKEN, Cons.NO_ACCESS_TOKEN_FOUND_IN_PREFS));

        if (getIntent().getExtras() != null) {
            chartsMap = getIntent().getExtras().getParcelableArrayList("charts");
        }

        bottomBar = BottomBar.attachShy((CoordinatorLayout) findViewById(R.id.myCoordinator),
                findViewById(R.id.myScrollingContent), savedInstanceState);

        fitnessFragment = new Fitness_Fragment();
        nutritionFragment = new Nutrition_Fragment();
        mainProfileFragment = new MainProfile_Fragment();

        bottomBar.setActiveTabColor(ContextCompat.getColor(this, R.color.bpDarker_blue));

        bottomBar.setItemsFromMenu(R.menu.bottom_navigation, new OnMenuTabClickListener() {
            @Override
            public void onMenuTabSelected(@IdRes int menuItemId) {

                switch (menuItemId) {
                    case R.id.item_one:
                        Bundle bundle = new Bundle();
                        bundle.putParcelableArrayList("charts", chartsMap);

                        fitnessFragment.setArguments(bundle);

                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fitnessFragment).commit();
                        mainContentLayout.setBackground(getResources().getDrawable(R.drawable.fitness_background));
                        break;
                    case R.id.item_two:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, nutritionFragment).commit();
                        mainContentLayout.setBackground(getResources().getDrawable(R.drawable.nutrition_background));
                        break;
                    case R.id.item_five:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, mainProfileFragment).commit();
                        break;
                }
            }

            @Override
            public void onMenuTabReSelected(@IdRes int menuItemId) {
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        bottomBar.onSaveInstanceState(outState);
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