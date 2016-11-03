package com.mcsaatchi.gmfit.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.classes.Constants;
import com.mcsaatchi.gmfit.classes.Helpers;
import com.mcsaatchi.gmfit.data_access.DataAccessHandler;
import com.mcsaatchi.gmfit.rest.AuthenticationResponse;
import com.mcsaatchi.gmfit.rest.AuthenticationResponseChart;
import com.mcsaatchi.gmfit.rest.AuthenticationResponseInnerBody;
import com.mcsaatchi.gmfit.rest.UserProfileResponseMedicalCondition;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Splash_Activity extends AppCompatActivity {

    private SharedPreferences prefs;
    private Intent intent;

    private List<UserProfileResponseMedicalCondition> userMedicalConditions = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        prefs = getSharedPreferences(Constants.SHARED_PREFS_TITLE, Context.MODE_PRIVATE);

        int SPLASH_TIME_OUT = 1000;
        int NO_INTERNET_DIALOG_TIMEOUT = 3000;

        Log.d("TAGTAG", "onCreate outside if : " + prefs.getBoolean(prefs.getString(Constants.EXTRAS_USER_EMAIL, "") + "_" + Constants.EVENT_FINISHED_SETTING_UP_PROFILE_SUCCESSFULLY, false));

        /**
         * User is not logged in
         */
        if (!prefs.getBoolean(Constants.EXTRAS_USER_LOGGED_IN, false)) {
            Log.d("TAGTAG", "User not logged in");

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    intent = new Intent(Splash_Activity.this, Login_Activity.class);
                    startActivity(intent);
                }
            }, SPLASH_TIME_OUT);

            /**
             * User did not finish setting up profile
             */
        } else if (!prefs.getBoolean(prefs.getString(Constants.EXTRAS_USER_EMAIL, "") + "_" + Constants.EVENT_FINISHED_SETTING_UP_PROFILE_SUCCESSFULLY, false)) {

            Log.d("TAGTAG", "onCreate: user has not yet finished setup profile process");
            intent = new Intent(Splash_Activity.this, SetupProfile_Activity.class);
            startActivity(intent);

            /**
             * User is logged in and they did finish setting up their profile
             */
        } else if (prefs.getBoolean(Constants.EXTRAS_USER_LOGGED_IN, false)) {
            if (Helpers.isInternetAvailable(Splash_Activity.this)) {
                Log.d("TAGTAG", "onCreate: signing the user in silently now");

                signInUserSilently(prefs.getString(Constants.EXTRAS_USER_EMAIL, ""), prefs.getString(Constants.EXTRAS_USER_PASSWORD, ""));
            } else {
                Helpers.showNoInternetDialog(Splash_Activity.this);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                }, NO_INTERNET_DIALOG_TIMEOUT);
            }
        }
    }

    private void signInUserSilently(String email, String password) {
        DataAccessHandler.getInstance().signInUserSilently(email, password, new Callback<AuthenticationResponse>() {
            @Override
            public void onResponse(Call<AuthenticationResponse> call, Response<AuthenticationResponse> response) {
                switch (response.code()) {
                    case 200:
                        AuthenticationResponseInnerBody responseBody = response.body().getData().getBody();

                        //Refreshes access token
                        prefs.edit().putString(Constants.PREF_USER_ACCESS_TOKEN, "Bearer " + responseBody.getToken()).apply();

                        /**
                         * Don't send the widgets over to the Main Activity here
                         */
                        List<AuthenticationResponseChart> chartsMap = responseBody.getCharts();

                        Intent intent = new Intent(Splash_Activity.this, Main_Activity.class);
                        intent.putParcelableArrayListExtra(Constants.BUNDLE_FITNESS_CHARTS_MAP, (ArrayList<AuthenticationResponseChart>) chartsMap);
                        startActivity(intent);

                        break;
                }
            }

            @Override
            public void onFailure(Call<AuthenticationResponse> call, Throwable t) {
                Log.d("TAG", "onFailure: Failed to login user silently");
            }
        });
    }
}
