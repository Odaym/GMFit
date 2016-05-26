package com.mcsaatchi.gmfit.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.andreabaccega.widget.FormEditText;
import com.github.florent37.materialtextfield.MaterialTextField;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.classes.ApiHelper;
import com.mcsaatchi.gmfit.classes.Cons;
import com.mcsaatchi.gmfit.classes.EventBus_Poster;
import com.mcsaatchi.gmfit.classes.EventBus_Singleton;
import com.mcsaatchi.gmfit.classes.Helpers;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SignIn_Activity extends Base_Activity {

    private static OkHttpClient client = new OkHttpClient();
    @Bind(R.id.emailET)
    FormEditText emailET;
    @Bind(R.id.emailETFloating)
    MaterialTextField emailETFloating;
    @Bind(R.id.passwordET)
    FormEditText passwordET;
    @Bind(R.id.signInBTN)
    Button signInBTN;
    @Bind(R.id.forgotPasswordTV)
    TextView forgotPasswordTV;
    private ArrayList<FormEditText> allFields = new ArrayList<>();
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(Helpers.createActivityBundleWithProperties(R.string.sign_in_activity_title, true));

        setContentView(R.layout.activity_sign_in);

        ButterKnife.bind(this);

        prefs = getSharedPreferences(Cons.SHARED_PREFS_TITLE, Context.MODE_PRIVATE);

        passwordET.setTypeface(Typeface.DEFAULT);

        allFields.add(emailET);
        allFields.add(passwordET);

        emailETFloating.expand();

        signInBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Helpers.validateFields(allFields)) {
                    if (Helpers.isInternetAvailable(SignIn_Activity.this)) {
                        try {

                            final JSONObject jsonForRequest = new JSONObject();
                            jsonForRequest.put(Cons.REQUEST_PARAM_EMAIL, emailET.getText().toString());
                            jsonForRequest.put(Cons.REQUEST_PARAM_PASSWORD, passwordET.getText().toString());

                            new AsyncTask<String, String, String>() {
                                ProgressDialog loggingInDialog;

                                protected void onPreExecute() {
                                    loggingInDialog = new ProgressDialog(SignIn_Activity.this);
                                    loggingInDialog.setTitle(getString(R.string.logging_in_dialog_title));
                                    loggingInDialog.setMessage(getString(R.string.logging_in_dialog_message));
                                    loggingInDialog.show();
                                }

                                protected String doInBackground(String... aParams) {
                                    String userAccessToken = prefs.getString(Cons.PREF_USER_ACCESS_TOKEN, Cons.NO_ACCESS_TOKEN_FOUND_IN_PREFS);

                                    Log.d("ACCESS", "doInBackground: ACCESS TOKEN " + userAccessToken);

                                    RequestBody body = RequestBody.create(Cons.JSON_FORMAT_IDENTIFIER, jsonForRequest.toString());
                                    Request request = new Request.Builder()
                                            .url(Cons.ROOT_URL_ADDRESS + Cons.API_NAME_LOGIN)
                                            .addHeader(Cons.USER_ACCESS_TOKEN_HEADER_PARAMETER, userAccessToken)
                                            .post(body)
                                            .build();

                                    try {
                                        Response response = client.newCall(request).execute();
                                        return response.body().string();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                    return null;
                                }

                                protected void onPostExecute(String aResult) {

                                    int responseCode = ApiHelper.parseAPIResponseForCode(aResult);

                                    AlertDialog alertDialog = new AlertDialog.Builder(SignIn_Activity.this).create();
                                    alertDialog.setTitle(R.string.login_failed_alert_dialog_title);
                                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.ok),
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                }
                                            });

                                    loggingInDialog.dismiss();

                                    switch (responseCode) {
                                        case Cons.REGISTERATION_API_RESPONSE_NOT_PARSED_CORRECTLY:
                                            alertDialog.setMessage(getString(R.string.error_response_from_server_incorrect));
                                            alertDialog.show();
                                            break;
                                        case Cons.LOGIN_API_WRONG_CREDENTIALS:
                                            alertDialog.setMessage(getString(R.string.login_failed_wrong_credentials));
                                            alertDialog.show();

                                            emailET.requestFocus();

                                            break;
                                        case Cons.REGISTERATION_API_REQUEST_SUCCEEDED_CODE:
                                            EventBus_Singleton.getInstance().post(new EventBus_Poster(Cons.EVENT_SIGNNED_UP_SUCCESSFULLY_CLOSE_LOGIN_ACTIVITY));

                                            Intent intent = new Intent(SignIn_Activity.this, Main_Activity.class);
                                            startActivity(intent);
                                            finish();
                                            break;
                                    }
                                }
                            }.execute();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        AlertDialog alertDialog = new AlertDialog.Builder(SignIn_Activity.this).create();
                        alertDialog.setTitle(R.string.login_failed_alert_dialog_title);
                        alertDialog.setMessage(getString(R.string.no_internet_connection));
                        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.ok),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        alertDialog.show();
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
