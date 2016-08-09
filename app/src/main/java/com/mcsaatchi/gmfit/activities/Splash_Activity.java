package com.mcsaatchi.gmfit.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.classes.Cons;

public class Splash_Activity extends AppCompatActivity {

    private static final String TAG = "Splash_Activity";
    private SharedPreferences prefs;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        prefs = getSharedPreferences(Cons.SHARED_PREFS_TITLE, Context.MODE_PRIVATE);

        int SPLASH_TIME_OUT = 1000;

        if (prefs.getBoolean(Cons.EXTRAS_USER_LOGGED_IN, true)) {
            Log.d(TAG, "onCreate: User is logged in");
            intent = new Intent(Splash_Activity.this, Main_Activity.class);
            startActivity(intent);
        } else {
            Log.d(TAG, "onCreate: User is NOT logged in");
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    intent = new Intent(Splash_Activity.this, Login_Activity.class);
                    startActivity(intent);
                }
            }, SPLASH_TIME_OUT);
        }
    }
}
