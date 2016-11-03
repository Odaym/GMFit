package com.mcsaatchi.gmfit.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.andreabaccega.widget.FormEditText;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.classes.Constants;
import com.mcsaatchi.gmfit.classes.Helpers;
import com.mcsaatchi.gmfit.data_access.DataAccessHandler;
import com.mcsaatchi.gmfit.rest.AuthenticationResponse;
import com.mcsaatchi.gmfit.rest.AuthenticationResponseChart;
import com.mcsaatchi.gmfit.rest.AuthenticationResponseInnerBody;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignIn_Activity extends Base_Activity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.emailET)
    FormEditText emailET;
    @Bind(R.id.passwordET)
    FormEditText passwordET;
    @Bind(R.id.signInBTN)
    Button signInBTN;
    @Bind(R.id.forgotPasswordTV)
    TextView forgotPasswordTV;
    @Bind(R.id.showPasswordTV)
    TextView showPasswordTV;

    private SharedPreferences prefs;
    private boolean passwordShowing = false;

    private ArrayList<FormEditText> allFields = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sign_in);

        ButterKnife.bind(this);

        setupToolbar(toolbar, R.string.sign_in_activity_title, true);
        addTopPaddingToolbar(toolbar);

        prefs = getSharedPreferences(Constants.SHARED_PREFS_TITLE, Context.MODE_PRIVATE);

        allFields.add(emailET);
        allFields.add(passwordET);

        passwordET.setTypeface(Typeface.DEFAULT);
        emailET.setSelection(emailET.getText().length());

        signInBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Helpers.validateFields(allFields)) {
                    if (Helpers.isInternetAvailable(SignIn_Activity.this))
                        signInUser(emailET.getText().toString(), passwordET.getText().toString());
                    else
                        Helpers.showNoInternetDialog(SignIn_Activity.this);
                } else {
                    showPasswordTV.setVisibility(View.GONE);
                }
            }
        });

        forgotPasswordTV.setText(Html.fromHtml(getString(R.string.forgot_password_button)));
        forgotPasswordTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignIn_Activity.this, ForgotPassword_Activity.class));
            }
        });

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

        passwordET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                showPasswordTV.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    public void signInUser(final String email, final String password) {
        final ProgressDialog waitingDialog = new ProgressDialog(this);
        waitingDialog.setTitle(getString(R.string.signing_in_dialog_title));
        waitingDialog.setMessage(getString(R.string.please_wait_dialog_message));
        waitingDialog.show();

        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle(R.string.signing_in_dialog_title);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        if (waitingDialog.isShowing())
                            waitingDialog.dismiss();
                    }
                });

        DataAccessHandler.getInstance().signInUser(email, password, new Callback<AuthenticationResponse>() {
            @Override
            public void onResponse(Call<AuthenticationResponse> call, Response<AuthenticationResponse> response) {
                switch (response.code()) {
                    case 200:
                        AuthenticationResponseInnerBody responseBody = response.body().getData().getBody();

                        //Refreshes access token
                        prefs.edit().putString(Constants.PREF_USER_ACCESS_TOKEN, "Bearer " + responseBody.getToken()).apply();
                        prefs.edit().putString(Constants.EXTRAS_USER_EMAIL, email).apply();
                        prefs.edit().putString(Constants.EXTRAS_USER_PASSWORD, password).apply();
                        prefs.edit().putBoolean(Constants.EXTRAS_USER_LOGGED_IN, true).apply();

                        /**
                         * Case where the user already has an account and they just logged in with it on a new installation
                         * Cannot happen otherwise
                         */
                        prefs.edit().putBoolean(prefs.getString(Constants.EXTRAS_USER_EMAIL, "") + "_" + Constants.EVENT_FINISHED_SETTING_UP_PROFILE_SUCCESSFULLY, true).apply();

                        List<AuthenticationResponseChart> chartsMap = responseBody.getCharts();

                        Intent intent = new Intent(SignIn_Activity.this, Main_Activity.class);
                        intent.putParcelableArrayListExtra(Constants.BUNDLE_FITNESS_CHARTS_MAP, (ArrayList<AuthenticationResponseChart>) chartsMap);
                        startActivity(intent);

                        finish();

                        break;
                    case 401:
                        waitingDialog.dismiss();

                        alertDialog.setMessage(getString(R.string.login_failed_wrong_credentials));
                        alertDialog.show();
                        break;
                    case 403:
                        waitingDialog.dismiss();

                        Intent userNotVerifiedIntent = new Intent(SignIn_Activity.this, GetStarted_Activity.class);
                        startActivity(userNotVerifiedIntent);

                        finish();
                        break;
                    case 500:
                        alertDialog.setMessage(getString(R.string.error_response_from_server_incorrect));
                        alertDialog.show();
                        break;
                }
            }

            @Override
            public void onFailure(Call<AuthenticationResponse> call, Throwable t) {
                Log.d("TAG", "onFailure: Failed to log user in");
            }
        });
    }
}
