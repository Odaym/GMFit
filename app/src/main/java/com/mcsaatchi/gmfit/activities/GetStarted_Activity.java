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
import android.widget.ImageView;

import com.andreabaccega.widget.FormEditText;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.classes.CircleTransform;
import com.mcsaatchi.gmfit.classes.Cons;
import com.mcsaatchi.gmfit.classes.Helpers;
import com.mcsaatchi.gmfit.data_access.DataAccessHandler;
import com.mcsaatchi.gmfit.rest.DefaultGetResponse;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GetStarted_Activity extends Base_Activity {

    @Bind(R.id.getStartedIMG)
    ImageView getStartedIMG;
    @Bind(R.id.setup_profile_button)
    Button setupProfileBTN;
    @Bind(R.id.verifyCodeET)
    FormEditText verifyCodeET;

    private SharedPreferences prefs;

    private ArrayList<FormEditText> allFields = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_get_started);

        ButterKnife.bind(this);

        prefs = getSharedPreferences(Cons.SHARED_PREFS_TITLE, Context.MODE_PRIVATE);

        allFields.add(verifyCodeET);

        Picasso.with(this).load(R.drawable.fragment_intro_picture).transform(new CircleTransform()).into
                (getStartedIMG);

        setupProfileBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Helpers.validateFields(allFields)) {
                    if (Helpers.isInternetAvailable(GetStarted_Activity.this)) {
                        verifyRegistrationCode(verifyCodeET.getText().toString());
                    } else
                        Helpers.showNoInternetDialog(GetStarted_Activity.this);
                }
            }
        });
    }

    private void verifyRegistrationCode(String verificationCode) {
        final ProgressDialog waitingDialog = new ProgressDialog(this);
        waitingDialog.setTitle(getString(R.string.verifying_email_dialog_title));
        waitingDialog.setMessage(getString(R.string.verifying_email_dialog_message));
        waitingDialog.show();

        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle(R.string.verifying_email_dialog_title);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        if (waitingDialog.isShowing())
                            waitingDialog.dismiss();
                    }
                });

        DataAccessHandler.getInstance().verifyUser(prefs.getString(Cons
                .PREF_USER_ACCESS_TOKEN, ""), verificationCode, new Callback<DefaultGetResponse>() {
            @Override
            public void onResponse(Call<DefaultGetResponse> call, Response<DefaultGetResponse> response) {
                switch (response.code()) {
                    case 200:
                        waitingDialog.dismiss();

                        prefs.edit().putBoolean(Cons.EXTRAS_USER_LOGGED_IN, true).apply();

                        Intent intent = new Intent(GetStarted_Activity.this, SetupProfile_Activity.class);
                        startActivity(intent);
                        finish();
                        break;
                    case 401:
                        alertDialog.setMessage(getString(R.string.wrong_verification_code));
                        alertDialog.show();
                        break;
                }
            }

            @Override
            public void onFailure(Call<DefaultGetResponse> call, Throwable t) {
                alertDialog.setMessage(getString(R.string.error_response_from_server_incorrect));
                alertDialog.show();

            }
        });
    }
}
