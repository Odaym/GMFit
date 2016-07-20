package com.mcsaatchi.gmfit.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.andreabaccega.widget.FormEditText;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.classes.Cons;
import com.mcsaatchi.gmfit.classes.Helpers;
import com.mcsaatchi.gmfit.rest.AuthenticationResponse;
import com.mcsaatchi.gmfit.rest.RestClient;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUp_Activity extends Base_Activity {

    @Bind(R.id.emailET)
    FormEditText emailET;
    @Bind(R.id.passwordET)
    FormEditText passwordET;
    @Bind(R.id.showPasswordTV)
    TextView showPasswordTV;
    @Bind(R.id.createAccountBTN)
    Button createAccountBTN;
    @Bind(R.id.creatingAccountTOSTV)
    TextView creatingAccountTOSTV;
    @Bind(R.id.toolbar)
    Toolbar toolbar;

    private boolean passwordShowing = false;

    private ArrayList<FormEditText> allFields = new ArrayList<>();

    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sign_up);

        ButterKnife.bind(this);

        setupToolbar(toolbar, R.string.sign_up_activity_title, true);
        addTopPaddingToolbar(toolbar);

        prefs = getSharedPreferences(Cons.SHARED_PREFS_TITLE, Context.MODE_PRIVATE);

        allFields.add(emailET);
        allFields.add(passwordET);

        passwordET.setTypeface(Typeface.DEFAULT);

        createAccountBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Helpers.validateFields(allFields)) {
                    registerUser(emailET.getText().toString(), passwordET.getText().toString());
                }
            }
        });

        SpannableString ss = new SpannableString(getString(R.string.creating_account_TOS));
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                startActivity(new Intent(SignUp_Activity.this, TOS_Activity.class));
            }
        };
        ss.setSpan(clickableSpan, 41, ss.length() - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        creatingAccountTOSTV.setText(ss);
        creatingAccountTOSTV.setMovementMethod(LinkMovementMethod.getInstance());
        creatingAccountTOSTV.setTextColor(getResources().getColor(R.color.offwhite_transparent));
        creatingAccountTOSTV.setHighlightColor(Color.BLUE);

        showPasswordTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (passwordShowing) {
                    passwordET.setTransformationMethod(new PasswordTransformationMethod());
                    showPasswordTV.setText(R.string.show_password);
                } else {
                    passwordET.setTransformationMethod(null);
                    showPasswordTV.setText(R.string.hide_password);
                }

                passwordET.setSelection(passwordET.getText().length());

                passwordShowing = !passwordShowing;
            }
        });
    }

    private void registerUser(String email, String password) {
        final ProgressDialog waitingDialog = new ProgressDialog(this);
        waitingDialog.setTitle(getString(R.string.signing_up_dialog_title));
        waitingDialog.setMessage(getString(R.string.signing_up_dialog_message));
        waitingDialog.show();

        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle(R.string.signing_up_dialog_title);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        if (waitingDialog.isShowing())
                            waitingDialog.dismiss();
                    }
                });

        Call<AuthenticationResponse> registerUserCall = new RestClient().getGMFitService().registerUser(new RegisterRequest(email, password));

        registerUserCall.enqueue(new Callback<AuthenticationResponse>() {
            @Override
            public void onResponse(Call<AuthenticationResponse> call, Response<AuthenticationResponse> response) {
                    switch (response.code()) {
                        case 200:
                            waitingDialog.dismiss();

                            prefs.edit().putString(Cons.PREF_USER_ACCESS_TOKEN, "Bearer " + response.body().getData().getBody().getToken()).apply();

                            Intent intent = new Intent(SignUp_Activity.this, GetStarted_Activity.class);
                            startActivity(intent);
                            finish();
                            break;
                        case 449:
                            alertDialog.setMessage(getString(R.string.email_already_taken_api_response));
                            alertDialog.show();
                            break;
                    }
            }

            @Override
            public void onFailure(Call<AuthenticationResponse> call, Throwable t) {
                alertDialog.setMessage(getString(R.string.error_response_from_server_incorrect));
                alertDialog.show();
            }
        });
    }

    public class RegisterRequest {
        final String email;
        final String password;

        public RegisterRequest(String email, String password) {
            this.email = email;
            this.password = password;
        }
    }
}
