package com.mcsaatchi.gmfit.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.classes.ApiHelper;
import com.mcsaatchi.gmfit.classes.Cons;
import com.mcsaatchi.gmfit.classes.Helpers;
import com.mcsaatchi.gmfit.fragments.Fitness_Fragment;
import com.mcsaatchi.gmfit.fragments.MainProfile_Fragment;
import com.mcsaatchi.gmfit.fragments.Nutrition_Fragment;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnMenuTabClickListener;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Main_Activity extends Base_Activity {

    public static int USER_AUTHORISED_REQUEST_CODE = 5;
    private static OkHttpClient client = new OkHttpClient();
    private BottomBar bottomBar;
    private Fragment fragmentReplace;
    private SharedPreferences prefs;
    private static final String DEBUG_TAG = "Main_Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(Helpers.createActivityBundleWithProperties(0, false));

        setContentView(R.layout.activity_main);

        prefs = getSharedPreferences(Cons.SHARED_PREFS_TITLE, Context.MODE_PRIVATE);

        Log.d("USER_ACCESS_TOKEN", "onCreate: User access token is : " + prefs.getString(Cons.PREF_USER_ACCESS_TOKEN, Cons.NO_ACCESS_TOKEN_FOUND_IN_PREFS));

        bottomBar = BottomBar.attachShy((CoordinatorLayout) findViewById(R.id.myCoordinator),
                findViewById(R.id.myScrollingContent), savedInstanceState);

        bottomBar.noTopOffset();
        bottomBar.noNavBarGoodness();
        bottomBar.ignoreNightMode();

        bottomBar.setItemsFromMenu(R.menu.bottom_navigation, new OnMenuTabClickListener() {
            @Override
            public void onMenuTabSelected(@IdRes int menuItemId) {
                fragmentReplace = null;

                switch (menuItemId) {
                    case R.id.item_one:
                        fragmentReplace = new Fitness_Fragment();
                        break;
                    case R.id.item_two:
                        fragmentReplace = new Nutrition_Fragment();
                        break;
                    case R.id.item_three:
                        fragmentReplace = new Nutrition_Fragment();
                        break;
                    case R.id.item_four:
                        fragmentReplace = new Nutrition_Fragment();
                        break;
                    case R.id.item_five:
                        fragmentReplace = new MainProfile_Fragment();
                        break;
                }

                getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                        .replace(R.id.fragment_container, fragmentReplace).commit();
            }

            @Override
            public void onMenuTabReSelected(@IdRes int menuItemId) {
            }
        });

        bottomBar.mapColorForTab(0, ContextCompat.getColor(this, R.color.colorAccent));
        bottomBar.mapColorForTab(1, ContextCompat.getColor(this, android.R.color.holo_green_dark));
        bottomBar.mapColorForTab(2, "#7B1FA2");
        bottomBar.mapColorForTab(3, "#FF5252");
        bottomBar.mapColorForTab(4, ContextCompat.getColor(this, android.R.color.holo_green_dark));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        fragmentReplace.onActivityResult(USER_AUTHORISED_REQUEST_CODE, resultCode, data);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.signOut:
                if (Helpers.isInternetAvailable(Main_Activity.this)) {

                    new AsyncTask<String, String, String>() {
                        ProgressDialog signingOutDialog;

                        protected void onPreExecute() {
                            signingOutDialog = new ProgressDialog(Main_Activity.this);
                            signingOutDialog.setTitle(getString(R.string.signing_out_dialog_title));
                            signingOutDialog.setMessage(getString(R.string.signing_out_dialog_message));
                            signingOutDialog.show();
                        }

                        protected String doInBackground(String... aParams) {
                            Request request = new Request.Builder()
                                    .addHeader(Cons.USER_ACCESS_TOKEN_HEADER_PARAMETER, prefs.getString(Cons.PREF_USER_ACCESS_TOKEN, ""))
                                    .url(Cons.ROOT_URL_ADDRESS + Cons.API_NAME_LOGOUT)
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
                                Helpers.showNoInternetDialog(Main_Activity.this);
                            } else {

                                int responseCode = ApiHelper.parseAPIResponseForCode(aResult);

                                AlertDialog alertDialog = new AlertDialog.Builder(Main_Activity.this).create();
                                alertDialog.setTitle(R.string.signing_out_dialog_title);
                                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.ok),
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });

                                signingOutDialog.dismiss();

                                switch (responseCode) {
                                    case Cons.API_RESPONSE_NOT_PARSED_CORRECTLY:
                                        alertDialog.setMessage(getString(R.string.error_response_from_server_incorrect));
                                        alertDialog.show();
                                        break;
                                    case Cons.API_REQUEST_SUCCEEDED_CODE:
                                        //Set a shared pref for Logged In / Logged Out status maybe
                                        prefs.edit().putString(Cons.PREF_USER_ACCESS_TOKEN, Cons.NO_ACCESS_TOKEN_FOUND_IN_PREFS).apply();

                                        Intent intent = new Intent(Main_Activity.this, Login_Activity.class);
                                        startActivity(intent);
                                        finish();

                                        break;
                                }
                            }
                        }
                    }.execute();
                }

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        bottomBar.onSaveInstanceState(outState);
    }
}
