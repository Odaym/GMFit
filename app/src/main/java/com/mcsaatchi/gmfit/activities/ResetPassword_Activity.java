package com.mcsaatchi.gmfit.activities;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.andreabaccega.widget.FormEditText;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.classes.Cons;
import com.mcsaatchi.gmfit.classes.Helpers;
import com.mcsaatchi.gmfit.rest.DefaultGetResponse;
import com.mcsaatchi.gmfit.rest.RestClient;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResetPassword_Activity extends Base_Activity {

    @Bind(R.id.passwordET)
    FormEditText passwordET;
    @Bind(R.id.verifyCodeET)
    FormEditText verifyCodeET;
    @Bind(R.id.submitResetPasswordBTN)
    Button submitResetPasswordBTN;
    @Bind(R.id.toolbar)
    Toolbar toolbar;

    private SharedPreferences prefs;

    private ArrayList<FormEditText> allFields = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_reset_password);

        ButterKnife.bind(this);

        prefs = getSharedPreferences(Cons.SHARED_PREFS_TITLE, Context.MODE_PRIVATE);

        setupToolbar(toolbar, R.string.reset_password_activity_title, true);
        addTopPaddingToolbar(toolbar);

        allFields.add(passwordET);
        allFields.add(verifyCodeET);

        submitResetPasswordBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Helpers.isInternetAvailable(ResetPassword_Activity.this)) {
                    if (Helpers.validateFields(allFields)) {
                        finalizeResetPassword(verifyCodeET.getText().toString(), passwordET.getText().toString());
                    }
                }
            }
        });
    }

    private void finalizeResetPassword(String token, String newPassword) {
        final ProgressDialog waitingDialog = new ProgressDialog(this);
        waitingDialog.setTitle(getString(R.string.resetting_password_dialog_title));
        waitingDialog.setMessage(getString(R.string.resetting_password_dialog_message));
        waitingDialog.show();

        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle(R.string.resetting_password_dialog_title);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        if (waitingDialog.isShowing())
                            waitingDialog.dismiss();
                    }
                });

        Call<DefaultGetResponse> resetPasswordCall = new RestClient().getGMFitService().finalizeResetPassword(new ResetPasswordRequest(token, newPassword));

        resetPasswordCall.enqueue(new Callback<DefaultGetResponse>() {
            @Override
            public void onResponse(Call<DefaultGetResponse> call, Response<DefaultGetResponse> response) {
                switch (response.code()) {
                    case 200:
                        waitingDialog.dismiss();

                        finish();

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

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setupToolbar(Toolbar toolbar) {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(getDrawable(R.drawable.ic_arrow_left));
        toolbar.setTitleTextAppearance(this, R.style.Toolbar_TitleTextStyle);
    }

    public class ResetPasswordRequest {
        final String password;
        final String token;

        public ResetPasswordRequest(String token, String password) {
            this.token = token;
            this.password = password;
        }
    }
}
