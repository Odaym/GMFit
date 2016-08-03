package com.mcsaatchi.gmfit.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.andreabaccega.widget.FormEditText;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.classes.Cons;
import com.mcsaatchi.gmfit.classes.Helpers;
import com.mcsaatchi.gmfit.rest.AuthenticationResponse;
import com.mcsaatchi.gmfit.rest.RestClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Verification_Activity extends Base_Activity {
    @Bind(R.id.verifyCodeET)
    FormEditText verifyCodeET;
    @Bind(R.id.submitVerificationCodeBTN)
    Button submitVerificationCodeBTN;

    private SharedPreferences prefs;

    private ArrayList<FormEditText> allFields = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_verify);

        ButterKnife.bind(this);

        prefs = getSharedPreferences(Cons.SHARED_PREFS_TITLE, Context.MODE_PRIVATE);

        submitVerificationCodeBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Helpers.validateFields(allFields)) {
                    if (Helpers.isInternetAvailable(Verification_Activity.this)) {
                        Toast.makeText(Verification_Activity.this, "Verify", Toast.LENGTH_SHORT).show();
                        verifyRegistrationCode(verifyCodeET.getText().toString());
                    }
                    else
                        Helpers.showNoInternetDialog(Verification_Activity.this);
                }
            }
        });
    }

    private void verifyRegistrationCode(String s) {
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
                if (response.body() != null) {
                    switch (response.code()) {
                        case Cons.API_REQUEST_SUCCEEDED_CODE:
                            waitingDialog.dismiss();

                            prefs.edit().putString(Cons.PREF_USER_ACCESS_TOKEN, "Bearer " + response.body().getData().getBody().getToken()).apply();

                            Intent intent = new Intent(SignUp_Activity.this, Verification_Activity.class);
                            startActivity(intent);
                            finish();

                            break;
                    }
                } else {
                    waitingDialog.dismiss();

                    //Handle the error
                    try {
                        JSONObject errorBody = new JSONObject(response.errorBody().string());
                        JSONObject errorData = errorBody.getJSONObject("data");
                        int errorCodeInData = errorData.getInt("code");

                        if (errorCodeInData == Cons.API_RESPONSE_INVALID_PARAMETERS) {
                            alertDialog.setMessage(getString(R.string.email_already_taken_api_response));
                            alertDialog.show();
                        }
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<AuthenticationResponse> call, Throwable t) {
                alertDialog.setMessage(getString(R.string.error_response_from_server_incorrect));
                alertDialog.show();
            }
        });
    }

    public class VerificationRequest {
        final String code;

        public VerificationRequest(String code) {
            this.code = code;
        }
    }
}
