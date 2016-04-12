package com.mcsaatchi.gmfit.activities;

import android.graphics.Typeface;
import android.os.Bundle;

import com.andreabaccega.widget.FormEditText;
import com.mcsaatchi.gmfit.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SignUp_Activity extends Base_Activity {

    @Bind(R.id.emailET)
    FormEditText emailET;
    @Bind(R.id.passwordET)
    FormEditText passwordET;
    @Bind(R.id.confirmPasswordET)
    FormEditText confirmPasswordET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_activity);

        ButterKnife.bind(this);

        passwordET.setTypeface(Typeface.DEFAULT);
        confirmPasswordET.setTypeface(Typeface.DEFAULT);
    }
}
