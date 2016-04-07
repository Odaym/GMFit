package com.mcsaatchi.gmfit.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.mcsaatchi.gmfit.R;

public class Main_Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if (findViewById(R.id.fragment_container) != null) {
            if (savedInstanceState != null) {
                return;
            }

            GoogleFit_Fragment firstFragment = new GoogleFit_Fragment();
//            firstFragment.setArguments(getIntent().getExtras());

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, firstFragment).commit();
        }
    }
}
