package com.mcsaatchi.gmfit.fragments;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.activities.UserPolicy_Activity;
import com.mcsaatchi.gmfit.classes.ApiHelper;
import com.mcsaatchi.gmfit.classes.Cons;
import com.mcsaatchi.gmfit.classes.Helpers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainProfile_Fragment extends Fragment {
    private static final int REQUEST_WRITE_STORAGE = 112;
    private static OkHttpClient client = new OkHttpClient();
    @Bind(R.id.userPolicyBTN)
    Button userPolicyBTN;
    @Bind(R.id.emergencyProfileBTN)
    Button emergencyProfileBTN;
    private SharedPreferences prefs;

    private InputStream usableInputStream;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View fragmentView = inflater.inflate(R.layout.fragment_main_profile, container, false);

        ButterKnife.bind(this, fragmentView);

        prefs = getActivity().getSharedPreferences(Cons.SHARED_PREFS_TITLE, Context.MODE_PRIVATE);

        userPolicyBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Helpers.isInternetAvailable(getActivity())) {
                    new AsyncTask<String, String, String>() {
                        ProgressDialog grabbingUserPolicyDialog;

                        protected void onPreExecute() {
                            grabbingUserPolicyDialog = new ProgressDialog(getActivity());
                            grabbingUserPolicyDialog.setTitle(getString(R.string.grabbing_user_policy_dialog_title));
                            grabbingUserPolicyDialog.setMessage(getString(R.string.grabbing_user_policy_dialog_message));
                            grabbingUserPolicyDialog.show();
                        }

                        protected String doInBackground(String... aParams) {
                            Request request = new Request.Builder()
                                    .addHeader(Cons.USER_ACCESS_TOKEN_HEADER_PARAMETER, prefs.getString(Cons.PREF_USER_ACCESS_TOKEN, ""))
                                    .url(Cons.ROOT_URL_ADDRESS + Cons.API_NAME_USER_POLICY)
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
                                Helpers.showNoInternetDialog(getActivity());
                            } else {

                                int responseCode = ApiHelper.parseAPIResponseForCode(aResult);

                                AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                                alertDialog.setTitle(R.string.grabbing_user_policy_dialog_title);
                                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.ok),
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });

                                grabbingUserPolicyDialog.dismiss();

                                switch (responseCode) {
                                    case Cons.API_RESPONSE_NOT_PARSED_CORRECTLY:

                                        alertDialog.setMessage(getString(R.string.error_response_from_server_incorrect));
                                        alertDialog.show();
                                        break;
                                    case Cons.API_REQUEST_SUCCEEDED_CODE:
                                        String userPolicyString = ApiHelper.parseResponseForUserPolicy(getActivity(), aResult);

                                        if (userPolicyString != null) {
                                            Intent intent = new Intent(getActivity(), UserPolicy_Activity.class);
                                            intent.putExtra(Cons.EXTRAS_USER_POLICY, userPolicyString);
                                            startActivity(intent);
                                        } else {
                                            alertDialog.setMessage(getString(R.string.error_response_from_server_incorrect));
                                            alertDialog.show();
                                        }

                                        break;
                                }
                            }
                        }
                    }.execute();
                } else {
                    Helpers.showNoInternetDialog(getActivity());
                }
            }
        });

        emergencyProfileBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Helpers.isInternetAvailable(getActivity())) {

                    boolean hasPermission = (ContextCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
                    if (!hasPermission) {
                        ActivityCompat.requestPermissions(getActivity(),
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                REQUEST_WRITE_STORAGE);
                    } else {
                        fireUpEmergencyProfileAsyncTask();
                    }
                } else {
                    Helpers.showNoInternetDialog(getActivity());
                }
            }
        });

        return fragmentView;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_WRITE_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //reload my activity with permission granted or use the features what required the permission
                    Toast.makeText(getActivity(), "Just got the permission", Toast.LENGTH_SHORT).show();
                    fireUpEmergencyProfileAsyncTask();
                } else {
                    Toast.makeText(getActivity(), "The app was not allowed to write to your storage. Hence, it cannot function properly. Please consider granting it " +
                            "this permission", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void fireUpEmergencyProfileAsyncTask() {
        Toast.makeText(getActivity(), "Now inside AsyncTask", Toast.LENGTH_SHORT).show();

        new AsyncTask<String, String, InputStream>() {
            ProgressDialog downloadingPDFProfileDialog;

            protected void onPreExecute() {
                downloadingPDFProfileDialog = new ProgressDialog(getActivity());
                downloadingPDFProfileDialog.setTitle(getString(R.string.downloading_pdf_profile_dialog_title));
                downloadingPDFProfileDialog.setMessage(getString(R.string.downloading_pdf_profile_dialog_message));
                downloadingPDFProfileDialog.show();
            }

            protected InputStream doInBackground(String... aParams) {
                Request request = new Request.Builder()
                        .addHeader(Cons.USER_ACCESS_TOKEN_HEADER_PARAMETER, prefs.getString(Cons.PREF_USER_ACCESS_TOKEN, ""))
                        .url(Cons.ROOT_URL_ADDRESS + Cons.API_NAME_EMERGENCY)
                        .build();

                try {
                    Response response = client.newCall(request).execute();
                    return response.body().byteStream();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return null;
            }

            protected void onPostExecute(InputStream aResult) {
                Log.d("ASYNCRESULT", "onPostExecute: Response was : \n" + aResult);

                if (aResult == null) {
                    Helpers.showNoInternetDialog(getActivity());
                } else {

                    usableInputStream = aResult;

                    downloadingPDFProfileDialog.dismiss();

                    getUserEmergencyProfile(usableInputStream);
                }
            }
        }.execute();
    }

    private void getUserEmergencyProfile(final InputStream inputStreamResult) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                byte[] buffer = new byte[8 * 1024];

                OutputStream output = null;

                File folder = new File(Environment.getExternalStorageDirectory() + "/GMFit/");

                boolean createDirectorySuccess = true;

                if (!folder.exists()) {
                    createDirectorySuccess = folder.mkdir();
                }

                if (createDirectorySuccess) {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ddMMyy-hhmmss_SSS", Locale.getDefault());
                    String profileFileName = folder.toString() + File.separator + simpleDateFormat.format(new Date()) + ".pdf";

                    Log.d("PROFILE", "run: Profile filename " + profileFileName);
                    try {
                        output = new FileOutputStream(profileFileName);

                        int bytesRead;
                        while ((bytesRead = inputStreamResult.read(buffer)) > 0) {
                            output.write(buffer, 0, bytesRead);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            if (output != null)
                                output.close();
                            inputStreamResult.close();

                            Uri uri = Uri.fromFile(new File(profileFileName));


                            Intent i = new Intent(Intent.ACTION_SEND);
                            i.setType("message/rfc822");
                            i.putExtra(Intent.EXTRA_EMAIL,
                                    new String[]{"support@gmfit.com"});
                            i.putExtra(Intent.EXTRA_STREAM, uri);
                            i.putExtra(Intent.EXTRA_SUBJECT, "EMERGENCY PROFILE");
//                                + Build.MANUFACTURER + " " + Build.MODEL + " (" + Build.DEVICE + ") - "
//                                + Build.VERSION.RELEASE);
                            i.putExtra(Intent.EXTRA_TEXT, "This is an emergency, please check my profile.");
                            try {
                                startActivity(Intent.createChooser(i, "Send email through"));
                            } catch (ActivityNotFoundException ex) {
                                Toast.makeText(getActivity(),
                                        "No email client", Toast.LENGTH_SHORT)
                                        .show();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    //TODO:
                    //Directory creation failed
                }
            }
        }).start();
    }
}
