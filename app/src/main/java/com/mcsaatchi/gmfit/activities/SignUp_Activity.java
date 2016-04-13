package com.mcsaatchi.gmfit.activities;

import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.TextView;

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
    @Bind(R.id.alreadySignedUpTV)
    TextView alreadySignedUpTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Bundle bundle = new Bundle();
        bundle.putInt("activity_title", R.string.sign_up_activity_title);

        super.onCreate(bundle);

        setContentView(R.layout.signup_activity);

        ButterKnife.bind(this);

        passwordET.setTypeface(Typeface.DEFAULT);
        confirmPasswordET.setTypeface(Typeface.DEFAULT);
    }
}
