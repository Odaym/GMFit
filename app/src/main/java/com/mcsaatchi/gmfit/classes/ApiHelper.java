package com.mcsaatchi.gmfit.classes;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.activities.GetStarted_Activity;
import com.mcsaatchi.gmfit.activities.Main_Activity;
import com.mcsaatchi.gmfit.activities.UserPolicy_Activity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.Callable;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ApiHelper {

    public static void runApiAsyncTask(final Context context, final String ApiName, final int requestType, final JSONObject jsonParams, final int
            dialogTitleResId, final int dialogMessageResId, final Callable<Void> successCallback) {

        if (Helpers.isInternetAvailable(context)) {
            new AsyncTask<String, String, String>() {
                private ProgressDialog waitingDialog;
                private OkHttpClient client = new OkHttpClient();
                private SharedPreferences prefs = context.getSharedPreferences(Cons.SHARED_PREFS_TITLE, Context.MODE_PRIVATE);

                protected void onPreExecute() {
                    waitingDialog = new ProgressDialog(context);
                    waitingDialog.setTitle(context.getString(dialogTitleResId));
                    waitingDialog.setMessage(context.getString(dialogMessageResId));
                    waitingDialog.show();
                }

                protected String doInBackground(String... aParams) {
                    String userAccessToken = prefs.getString(Cons.PREF_USER_ACCESS_TOKEN, Cons.NO_ACCESS_TOKEN_FOUND_IN_PREFS);

                    RequestBody body;

                    Request request;

//                    Log.d("ACCESS", "doInBackground: ACCESS TOKEN " + userAccessToken);

                    if (requestType == Cons.POST_REQUEST_TYPE) {
                        body = RequestBody.create(Cons.JSON_FORMAT_IDENTIFIER, jsonParams.toString());

                        request = new Request.Builder()
                                .url(Cons.ROOT_URL_ADDRESS + ApiName)
                                .addHeader(Cons.USER_ACCESS_TOKEN_HEADER_PARAMETER, userAccessToken)
                                .post(body)
                                .build();
                    } else {
                        request = new Request.Builder()
                                .url(Cons.ROOT_URL_ADDRESS + ApiName)
                                .addHeader(Cons.USER_ACCESS_TOKEN_HEADER_PARAMETER, userAccessToken)
                                .build();
                    }

                    try {
                        Response response = client.newCall(request).execute();
                        return response.body().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    return null;
                }

                protected void onPostExecute(String aResult) {
                    Log.d("API_REQUEST", "doInBackground: JSON response body was : " + aResult);

                    waitingDialog.dismiss();

                    if (aResult == null) {
                        Helpers.showNoInternetDialog(context);
                    } else {
                        int responseCode = ApiHelper.parseAPIResponseForCode(aResult);

                        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                        alertDialog.setTitle(dialogTitleResId);
                        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, context.getString(R.string.ok),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });

                        switch (responseCode) {
                            case Cons.API_RESPONSE_NOT_PARSED_CORRECTLY:
                                alertDialog.setMessage(context.getString(R.string.error_response_from_server_incorrect));
                                alertDialog.show();
                                break;
                            //TODO:
                            //449 is too common for everything! fix it!
                            case Cons.API_RESPONSE_INVALID_PARAMETERS:
                                alertDialog.setMessage(context.getString(R.string.email_already_taken_api_response));
                                alertDialog.show();
                                break;
                            case Cons.LOGIN_API_WRONG_CREDENTIALS:
                                alertDialog.setMessage(context.getString(R.string.login_failed_wrong_credentials));
                                alertDialog.show();
                                break;
                            case Cons.API_REQUEST_SUCCEEDED_CODE:
                                //If no successCallback was passed to this function, fall into this case where you filter out what you need and act
                                if (successCallback == null) {
                                    switch (ApiName) {
                                        case Cons.API_NAME_REGISTER:
                                            int registerOperationStatusCode = ApiHelper.parseAndSaveRegisterationToken(context, aResult);

                                            switch (registerOperationStatusCode) {
                                                case Cons.REGISTRATION_PROCESS_SUCCEEDED_TOKEN_SAVED:
                                                    EventBus_Singleton.getInstance().post(new EventBus_Poster(Cons.EVENT_SIGNNED_UP_SUCCESSFULLY_CLOSE_LOGIN_ACTIVITY));

                                                    Intent intent = new Intent(context, GetStarted_Activity.class);
                                                    context.startActivity(intent);
                                                    ((Activity) context).finish();
                                                    break;
                                                case Cons.API_RESPONSE_NOT_PARSED_CORRECTLY:
                                                    alertDialog.setMessage(context.getString(R.string.error_response_from_server_incorrect));
                                                    alertDialog.show();
                                                    break;
                                            }

                                            break;
                                        case Cons.API_NAME_SIGN_IN:
                                            int loginOperationStatusCode = ApiHelper.parseAndSaveRegisterationToken(context, aResult);

                                            switch (loginOperationStatusCode) {
                                                case Cons.REGISTRATION_PROCESS_SUCCEEDED_TOKEN_SAVED:
                                                    EventBus_Singleton.getInstance().post(new EventBus_Poster(Cons.EVENT_SIGNNED_UP_SUCCESSFULLY_CLOSE_LOGIN_ACTIVITY));

                                                    Intent intent = new Intent(context, Main_Activity.class);
                                                    context.startActivity(intent);
                                                    ((Activity) context).finish();
                                                    break;
                                                case Cons.API_RESPONSE_NOT_PARSED_CORRECTLY:
                                                    alertDialog.setMessage(context.getString(R.string.error_response_from_server_incorrect));
                                                    alertDialog.show();
                                                    break;
                                            }

                                            break;
                                        case Cons.API_NAME_USER_POLICY:
                                            String userPolicyString = ApiHelper.parseResponseForUserPolicy(context, aResult);

                                            if (userPolicyString != null) {
                                                Intent intent = new Intent(context, UserPolicy_Activity.class);
                                                intent.putExtra(Cons.EXTRAS_USER_POLICY, userPolicyString);
                                                context.startActivity(intent);
                                            } else {
                                                alertDialog.setMessage(context.getString(R.string.error_response_from_server_incorrect));
                                                alertDialog.show();
                                            }

                                            break;
                                    }
                                } else {
                                    try {
                                        successCallback.call();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }

                                break;
                        }
                    }
                }
            }.execute();
        } else {
            Helpers.showNoInternetDialog(context);
        }
    }

    public static int parseAPIResponseForCode(String responseObject) {
        try {
            JSONObject jsonResponse = new JSONObject(responseObject);
            JSONObject dataJSONObject = jsonResponse.getJSONObject("data");

            return dataJSONObject.getInt("code");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return Cons.API_RESPONSE_NOT_PARSED_CORRECTLY;
    }

    public static String parseResponseForUserPolicy(Context context, String responseObject) {
        try {
            JSONObject jsonResponse = new JSONObject(responseObject);
            JSONObject dataJSONObject = jsonResponse.getJSONObject("data");
            JSONObject bodyJSONObject = dataJSONObject.getJSONObject("body");

            return bodyJSONObject.getString("user_policy");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static int parseAndSaveRegisterationToken(Context context, String responseObject) {
        try {
            JSONObject jsonResponse = new JSONObject(responseObject);
            JSONObject dataJSONObject = jsonResponse.getJSONObject("data");
            JSONObject bodyJSONObject = dataJSONObject.getJSONObject("body");

            String userAccessToken = bodyJSONObject.getString("token");

            SharedPreferences prefs = context.getSharedPreferences(Cons.SHARED_PREFS_TITLE, Context.MODE_PRIVATE);

            prefs.edit().putString(Cons.PREF_USER_ACCESS_TOKEN, "Bearer " + userAccessToken).apply();

            return Cons.REGISTRATION_PROCESS_SUCCEEDED_TOKEN_SAVED;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return Cons.API_RESPONSE_NOT_PARSED_CORRECTLY;
    }
}
