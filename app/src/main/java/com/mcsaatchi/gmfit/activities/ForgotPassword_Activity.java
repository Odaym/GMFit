package com.mcsaatchi.gmfit.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.andreabaccega.widget.FormEditText;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.classes.Helpers;
import com.mcsaatchi.gmfit.logger.Log;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ForgotPassword_Activity extends Base_Activity {

    @Bind(R.id.emailET)
    FormEditText emailET;
    @Bind(R.id.submitForgotPasswordEmailBTN)
    Button submitForgotPasswordEmailBTN;
    @Bind(R.id.didntReceivePasswordEmailTV)
    TextView didntReceivePasswordEmailTV;

    private ArrayList<FormEditText> allFields = new ArrayList<>();
    private Helpers helpers = Helpers.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(Helpers.createActivityBundleWithProperties(R.string.forgot_password_activity_title, true));

        setContentView(R.layout.activity_forgot_password);

        ButterKnife.bind(this);

        allFields.add(emailET);

        submitForgotPasswordEmailBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (helpers.validateFields(allFields)) {
                    Log.toaster(ForgotPassword_Activity.this, "All fields check out!");
                }
            }
        });

        SpannableString ss = new SpannableString(getString(R.string.didnt_receive_forgot_password_email));
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                Log.toaster(ForgotPassword_Activity.this, "Handle forgot password logic");
            }
        };

        ss.setSpan(clickableSpan, 29, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        didntReceivePasswordEmailTV.setText(ss);
        didntReceivePasswordEmailTV.setMovementMethod(LinkMovementMethod.getInstance());
        didntReceivePasswordEmailTV.setHighlightColor(Color.BLUE);
    }
}
