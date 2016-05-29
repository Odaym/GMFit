package com.mcsaatchi.gmfit.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
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

public class SignUp_Activity extends Base_Activity {

    private static OkHttpClient client = new OkHttpClient();
    @Bind(R.id.emailET)
    FormEditText emailET;
    @Bind(R.id.emailETFloating)
    MaterialTextField emailETFloating;
    @Bind(R.id.passwordET)
    FormEditText passwordET;
    @Bind(R.id.createAccountBTN)
    Button createAccountBTN;
    @Bind(R.id.creatingAccountTOSTV)
    TextView creatingAccountTOSTV;
    private ArrayList<FormEditText> allFields = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(Helpers.createActivityBundleWithProperties(R.string.sign_up_activity_title, true));

        setContentView(R.layout.activity_sign_up);

        ButterKnife.bind(this);

        allFields.add(emailET);
        allFields.add(passwordET);

        emailETFloating.expand();

        passwordET.setTypeface(Typeface.DEFAULT);

        createAccountBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Helpers.validateFields(allFields)) {
                    if (Helpers.isInternetAvailable(SignUp_Activity.this)) {
                        try {

                            final JSONObject jsonForRequest = new JSONObject();
                            jsonForRequest.put(Cons.REQUEST_PARAM_EMAIL, emailET.getText().toString());
                            jsonForRequest.put(Cons.REQUEST_PARAM_PASSWORD, passwordET.getText().toString());

                            new AsyncTask<String, String, String>() {
                                ProgressDialog signingUpDialog;

                                protected void onPreExecute() {
                                    signingUpDialog = new ProgressDialog(SignUp_Activity.this);
                                    signingUpDialog.setTitle(getString(R.string.signing_up_dialog_title));
                                    signingUpDialog.setMessage(getString(R.string.signing_up_dialog_message));
                                    signingUpDialog.show();
                                }

                                protected String doInBackground(String... aParams) {
                                    RequestBody body = RequestBody.create(Cons.JSON_FORMAT_IDENTIFIER, jsonForRequest.toString());
                                    Request request = new Request.Builder()
                                            .url(Cons.ROOT_URL_ADDRESS + Cons.API_NAME_REGISTER)
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
                                    Log.d("ASYNCRESULT", "onPostExecute: Response was : \n" + aResult);

                                    if (aResult == null) {
                                        Helpers.showNoInternetDialog(SignUp_Activity.this);
                                    } else {

                                        int responseCode = ApiHelper.parseAPIResponseForCode(aResult);

                                        AlertDialog alertDialog = new AlertDialog.Builder(SignUp_Activity.this).create();
                                        alertDialog.setTitle(R.string.signing_up_dialog_title);
                                        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.ok),
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();
                                                    }
                                                });

                                        signingUpDialog.dismiss();

                                        switch (responseCode) {
                                            case Cons.API_RESPONSE_NOT_PARSED_CORRECTLY:
                                                alertDialog.setMessage(getString(R.string.error_response_from_server_incorrect));
                                                alertDialog.show();
                                                break;
                                            case Cons.REGISTERATION_API_EMAIL_TAKEN_CODE:
                                                alertDialog.setMessage(getString(R.string.email_already_taken_api_response));
                                                alertDialog.show();
                                                break;
                                            case Cons.API_REQUEST_SUCCEEDED_CODE:
                                                int operationStatusCode = ApiHelper.parseAndSaveRegisterationToken(SignUp_Activity.this, aResult);

                                                switch (operationStatusCode) {
                                                    case Cons.REGISTRATION_PROCESS_SUCCEEDED_TOKEN_SAVED:
                                                        EventBus_Singleton.getInstance().post(new EventBus_Poster(Cons.EVENT_SIGNNED_UP_SUCCESSFULLY_CLOSE_LOGIN_ACTIVITY));

                                                        Intent intent = new Intent(SignUp_Activity.this, GetStarted_Activity.class);
                                                        startActivity(intent);
                                                        finish();
                                                        break;
                                                    case Cons.API_RESPONSE_NOT_PARSED_CORRECTLY:
                                                        alertDialog.setMessage(getString(R.string.error_response_from_server_incorrect));
                                                        alertDialog.show();
                                                        break;
                                                }
                                                break;
                                        }
                                    }
                                }
                            }.execute();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Helpers.showNoInternetDialog(SignUp_Activity.this);
                    }
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
        ss.setSpan(clickableSpan, 41, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        creatingAccountTOSTV.setText(ss);
        creatingAccountTOSTV.setMovementMethod(LinkMovementMethod.getInstance());
        creatingAccountTOSTV.setHighlightColor(Color.BLUE);
    }
}
