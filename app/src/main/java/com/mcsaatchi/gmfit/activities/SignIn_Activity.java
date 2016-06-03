package com.mcsaatchi.gmfit.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
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
import com.mcsaatchi.gmfit.classes.ApiHelper;
import com.mcsaatchi.gmfit.classes.Cons;
import com.mcsaatchi.gmfit.classes.Helpers;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SignIn_Activity extends Base_Activity {

    @Bind(R.id.emailET)
    FormEditText emailET;
    @Bind(R.id.passwordET)
    FormEditText passwordET;
    @Bind(R.id.signInBTN)
    Button signInBTN;
    @Bind(R.id.forgotPasswordTV)
    TextView forgotPasswordTV;
    private ArrayList<FormEditText> allFields = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(Helpers.createActivityBundleWithProperties(R.string.sign_in_activity_title, true));

        setContentView(R.layout.activity_sign_in);

        ButterKnife.bind(this);

        passwordET.setTypeface(Typeface.DEFAULT);

        allFields.add(emailET);
        allFields.add(passwordET);

        signInBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Helpers.validateFields(allFields)) {
                    try {
                        final JSONObject jsonForRequest = new JSONObject();
                        jsonForRequest.put(Cons.REQUEST_PARAM_EMAIL, emailET.getText().toString());
                        jsonForRequest.put(Cons.REQUEST_PARAM_PASSWORD, passwordET.getText().toString());

                        ApiHelper.runApiAsyncTask(SignIn_Activity.this, Cons.API_NAME_SIGN_IN, Cons.POST_REQUEST_TYPE, jsonForRequest, R.string
                                .logging_in_dialog_title, R.string.logging_in_dialog_message, null);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        SpannableString ss = new SpannableString(getString(R.string.forgot_password_button));
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                startActivity(new Intent(SignIn_Activity.this, ForgotPassword_Activity.class));
            }
        };
        ss.setSpan(clickableSpan, 0, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        forgotPasswordTV.setText(ss);
        forgotPasswordTV.setMovementMethod(LinkMovementMethod.getInstance());
        forgotPasswordTV.setHighlightColor(Color.BLUE);
    }
}
