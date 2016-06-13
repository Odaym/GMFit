package com.mcsaatchi.gmfit.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.andreabaccega.widget.FormEditText;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.classes.Cons;
import com.mcsaatchi.gmfit.classes.Helpers;
import com.mcsaatchi.gmfit.models.DefaultResponse;
import com.mcsaatchi.gmfit.rest.RestClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class SignUp_Activity extends Base_Activity {

    @Bind(R.id.emailET)
    FormEditText emailET;
    @Bind(R.id.passwordET)
    FormEditText passwordET;
    @Bind(R.id.confirmPasswordET)
    FormEditText confirmPasswordET;
    @Bind(R.id.createAccountBTN)
    Button createAccountBTN;
    @Bind(R.id.creatingAccountTOSTV)
    TextView creatingAccountTOSTV;
    private ArrayList<FormEditText> allFields = new ArrayList<>();
    private static final String TAG = "SignUp_Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(Helpers.createActivityBundleWithProperties(R.string.sign_up_activity_title, true));

        setContentView(R.layout.activity_sign_up);

        ButterKnife.bind(this);

        allFields.add(emailET);
        allFields.add(passwordET);

        passwordET.setTypeface(Typeface.DEFAULT);
        confirmPasswordET.setTypeface(Typeface.DEFAULT);

        createAccountBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Helpers.validateFields(allFields)) {
                    try {
                        final JSONObject jsonForRequest = new JSONObject();
                        jsonForRequest.put(Cons.REQUEST_PARAM_EMAIL, emailET.getText().toString());
                        jsonForRequest.put(Cons.REQUEST_PARAM_PASSWORD, passwordET.getText().toString());
//                        ApiHelper.runApiAsyncTask(SignUp_Activity.this, Cons.API_NAME_REGISTER, Cons.POST_REQUEST_TYPE, jsonForRequest, R.string
//                                .setting_up_profile_dialog_title, R.string.setting_up_profile_dialog_message, null);

                        registerUser(jsonForRequest);

                    } catch (JSONException e) {
                        e.printStackTrace();
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

    private void registerUser(JSONObject userCredentials) {
        Observable<DefaultResponse> registerUserObservable = new RestClient().getGMFitService().registerUser(userCredentials);

        registerUserObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .subscribe(new Subscriber<DefaultResponse>() {
                    @Override
                    public void onCompleted() {
                        Toast.makeText(SignUp_Activity.this,
                                "Completed",
                                Toast.LENGTH_SHORT)
                                .show();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(SignUp_Activity.this,
                                e.getMessage(),
                                Toast.LENGTH_SHORT)
                                .show();
                    }

                    @Override
                    public void onNext(DefaultResponse response) {
                        Log.d(TAG, "onResponse: Call succeeded, RESPONSE : " + response.getData().getBody());


                        Log.d(TAG, "onResponse: Call succeeded, here's the response BODY : " + response.getData().getMessage());
                        Log.d(TAG, "onResponse: Call succeeded, here's the response MESSAGE : " + response.getData().getMessage());
                        Log.d(TAG, "onResponse: Call succeeded, here's the response CODE: " + response.getData().getCode());
                    }
                });
    }
}
