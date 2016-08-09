package com.mcsaatchi.gmfit.activities;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.andreabaccega.widget.FormEditText;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.classes.Helpers;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ForgotPassword_Activity extends Base_Activity {

    @Bind(R.id.emailET)
    FormEditText emailET;
    @Bind(R.id.submitForgotPasswordEmailBTN)
    Button submitForgotPasswordEmailBTN;
    @Bind(R.id.toolbar)
    Toolbar toolbar;

    private ArrayList<FormEditText> allFields = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_forgot_password);

        ButterKnife.bind(this);

        setupToolbar(toolbar, R.string.forgot_password_activity_title, true);
        addTopPaddingToolbar(toolbar);

        allFields.add(emailET);

        submitForgotPasswordEmailBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Helpers.validateFields(allFields)) {
                }
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setupToolbar(Toolbar toolbar) {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(getDrawable(R.drawable.ic_arrow_left));
        toolbar.setTitleTextAppearance(this, R.style.Toolbar_TitleTextStyle);
    }
}
