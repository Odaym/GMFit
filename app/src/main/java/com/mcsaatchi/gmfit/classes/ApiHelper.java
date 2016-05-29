package com.mcsaatchi.gmfit.classes;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

public class ApiHelper {
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
