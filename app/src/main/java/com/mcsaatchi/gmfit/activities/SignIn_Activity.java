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
import com.mcsaatchi.gmfit.classes.EventBus_Poster;
import com.mcsaatchi.gmfit.classes.EventBus_Singleton;
import com.mcsaatchi.gmfit.classes.Helpers;
import com.mcsaatchi.gmfit.rest.AuthenticationResponse;
import com.mcsaatchi.gmfit.rest.AuthenticationResponseChart;
import com.mcsaatchi.gmfit.rest.AuthenticationResponseInnerBody;
import com.mcsaatchi.gmfit.rest.AuthenticationResponseWidget;
import com.mcsaatchi.gmfit.rest.RestClient;

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

        prefs = getSharedPreferences(Cons.SHARED_PREFS_TITLE, Context.MODE_PRIVATE);

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

    public void signInUser(final String email, final String password) {
        final ProgressDialog waitingDialog = new ProgressDialog(this);
        waitingDialog.setTitle(getString(R.string.signing_in_dialog_title));
        waitingDialog.setMessage(getString(R.string.signing_in_dialog_message));
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

        Call<AuthenticationResponse> signInUserCall = new RestClient().getGMFitService().signInUser(new SignInRequest(email,
                password));

        signInUserCall.enqueue(new Callback<AuthenticationResponse>() {
            @Override
            public void onResponse(Call<AuthenticationResponse> call, Response<AuthenticationResponse> response) {
                switch (response.code()) {
                    case 200:
                        AuthenticationResponseInnerBody responseBody = response.body().getData().getBody();

                        //Refreshes access token
                        prefs.edit().putString(Cons.PREF_USER_ACCESS_TOKEN, "Bearer " + responseBody.getToken()).apply();
                        prefs.edit().putString(Cons.EXTRAS_USER_EMAIL, email).apply();
                        prefs.edit().putString(Cons.EXTRAS_USER_PASSWORD, password).apply();
                        prefs.edit().putBoolean(Cons.EXTRAS_USER_LOGGED_IN, true).apply();

                        List<AuthenticationResponseWidget> widgetsMap = responseBody.getWidgets();
                        List<AuthenticationResponseChart> chartsMap = responseBody.getCharts();

                        EventBus_Singleton.getInstance().post(new EventBus_Poster(Cons.EVENT_SIGNNED_UP_SUCCESSFULLY_CLOSE_LOGIN_ACTIVITY));

                        Intent intent = new Intent(SignIn_Activity.this, Main_Activity.class);
                        intent.putParcelableArrayListExtra("widgets", (ArrayList<AuthenticationResponseWidget>) widgetsMap);
                        intent.putParcelableArrayListExtra("charts", (ArrayList<AuthenticationResponseChart>) chartsMap);
                        startActivity(intent);

                        waitingDialog.dismiss();

                        finish();

                        break;
                    case 401:
                        alertDialog.setMessage(getString(R.string.login_failed_wrong_credentials));
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

    public class SignInRequest {
        final String email;
        final String password;

        public SignInRequest(String email, String password) {
            this.email = email;
            this.password = password;
        }
    }
}
