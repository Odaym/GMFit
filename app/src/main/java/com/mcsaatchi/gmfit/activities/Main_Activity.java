package com.mcsaatchi.gmfit.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.classes.Cons;
import com.mcsaatchi.gmfit.classes.Helpers;
import com.mcsaatchi.gmfit.fragments.Fitness_Fragment;
import com.mcsaatchi.gmfit.fragments.MainProfile_Fragment;
import com.mcsaatchi.gmfit.fragments.Nutrition_Fragment;
import com.mcsaatchi.gmfit.pedometer.SensorListener;
import com.mcsaatchi.gmfit.rest.AuthenticationResponseChart;
import com.mcsaatchi.gmfit.rest.AuthenticationResponseWidget;
import com.mcsaatchi.gmfit.rest.DefaultGetResponse;
import com.mcsaatchi.gmfit.rest.RestClient;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnMenuTabClickListener;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.signOut:

                if (Helpers.isInternetAvailable(this)) {
                    signOutUser();
                } else {
                    Helpers.showNoInternetDialog(this);
                }

                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void signOutUser() {
        final ProgressDialog waitingDialog = new ProgressDialog(this);
        waitingDialog.setTitle(getString(R.string.signing_out_dialog_title));
        waitingDialog.setMessage(getString(R.string.signing_out_dialog_message));
        waitingDialog.show();

        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle(R.string.signing_out_dialog_title);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        if (waitingDialog.isShowing())
                            waitingDialog.dismiss();
                    }
                });

        Call<DefaultGetResponse> signOutUserCall = new RestClient().getGMFitService().signOutUser(prefs.getString(Cons.PREF_USER_ACCESS_TOKEN, Cons
                .NO_ACCESS_TOKEN_FOUND_IN_PREFS));

        signOutUserCall.enqueue(new Callback<DefaultGetResponse>() {
            @Override
            public void onResponse(Call<DefaultGetResponse> call, Response<DefaultGetResponse> response) {
                switch (response.code()) {
                    case 200:
                        waitingDialog.dismiss();

                        prefs.edit().putString(Cons.PREF_USER_ACCESS_TOKEN, Cons.NO_ACCESS_TOKEN_FOUND_IN_PREFS).apply();
                        prefs.edit().putBoolean(Cons.EXTRAS_USER_LOGGED_IN, false).apply();

                        Intent intent = new Intent(Main_Activity.this, Login_Activity.class);
                        startActivity(intent);
                        finish();
                        break;
                }
            }

            @Override
            public void onFailure(Call<DefaultGetResponse> call, Throwable t) {
                alertDialog.setMessage(getString(R.string.error_response_from_server_incorrect));
                alertDialog.show();
            }
        });
    }
}