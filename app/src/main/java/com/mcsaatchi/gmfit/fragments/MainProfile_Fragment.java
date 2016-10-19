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
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.activities.Login_Activity;
import com.mcsaatchi.gmfit.classes.CircleTransform;
import com.mcsaatchi.gmfit.classes.Constants;
import com.mcsaatchi.gmfit.classes.Helpers;
import com.mcsaatchi.gmfit.data_access.DataAccessHandler;
import com.mcsaatchi.gmfit.rest.DefaultGetResponse;
import com.mcsaatchi.gmfit.rest.EmergencyProfileResponse;
import com.mcsaatchi.gmfit.rest.UserProfileResponse;
import com.mcsaatchi.gmfit.rest.UserProfileResponseDatum;
import com.mcsaatchi.gmfit.rest.UserProfileResponseMedicalCondition;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainProfile_Fragment extends Fragment {
    private static final String TAG = "MainProfile_Fragment";
    private static final int REQUEST_WRITE_STORAGE = 112;

    @Bind(R.id.userProfileIV)
    ImageView userProfileIV;
    @Bind(R.id.userFullNameTV)
    TextView userFullNameTV;
    @Bind(R.id.userEmailTV)
    TextView userEmailTV;
    @Bind(R.id.countryValueTV)
    TextView countryValueTV;
    @Bind(R.id.weightEntryValueTV)
    TextView weightEntryValueTV;
    @Bind(R.id.goalsEntryValueTV)
    TextView goalsEntryValueTV;
    @Bind(R.id.medicalConditionsValueTV)
    TextView medicalConditionsValueTV;
    @Bind(R.id.metricSystemValueTV)
    TextView metricSystemValueTV;

    @Bind(R.id.weightLayout)
    RelativeLayout weightLayout;
    @Bind(R.id.goalsLayout)
    RelativeLayout goalsLayout;
    @Bind(R.id.medicalConditionsLayout)
    RelativeLayout medicalConditionsLayout;
    @Bind(R.id.appRemindersLayout)
    RelativeLayout appRemindersLayout;
    @Bind(R.id.countryLayout)
    RelativeLayout countryLayout;
    @Bind(R.id.metricLayout)
    RelativeLayout metricLayout;

    @Bind(R.id.shareEmergencyProfileBTN)
    Button shareEmergencyProfileBTN;

    @Bind(R.id.logoutBTN)
    Button logoutBTN;

    private MenuItem saveChangesItem;

    private SharedPreferences prefs;

    private List<UserProfileResponseMedicalCondition> userMedicalConditions = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View fragmentView = inflater.inflate(R.layout.fragment_main_profile, container, false);

        ButterKnife.bind(this, fragmentView);

        setHasOptionsMenu(true);

        prefs = getActivity().getSharedPreferences(Constants.SHARED_PREFS_TITLE, Context.MODE_PRIVATE);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.profile_tab_title);

        Picasso.with(getActivity()).load(R.drawable.fragment_intro_picture).resize(100, 100).transform(new CircleTransform()).centerInside().into(userProfileIV);

        getUserProfile();

        weightLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
                dialogBuilder.setTitle(R.string.profile_edit_weight_dialog_title);

                View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.profile_edit_weight_dialog, null);
                final EditText editWeightET = (EditText) dialogView.findViewById(R.id.dialogWeightET);
                editWeightET.setText(weightEntryValueTV.getText().toString());
                editWeightET.setSelection(editWeightET.getText().toString().length());

                dialogBuilder.setView(dialogView);
                dialogBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        showSaveChangesMenuItem();
                    }
                });
                dialogBuilder.setNegativeButton(R.string.decline_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                AlertDialog alertDialog = dialogBuilder.create();
                alertDialog.show();
            }
        });

        goalsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
                dialogBuilder.setTitle(R.string.profile_edit_goal_dialog_title);

                View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.profile_edit_goal_dialog, null);

                dialogBuilder.setView(dialogView);
                dialogBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        showSaveChangesMenuItem();
                    }
                });
                dialogBuilder.setNegativeButton(R.string.decline_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                AlertDialog alertDialog = dialogBuilder.create();
                alertDialog.show();
            }
        });

        medicalConditionsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getMedicalConditionsAndShowDialog();
            }
        });

        logoutBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Helpers.isInternetAvailable(getActivity())) {
                    signOutUser();
                } else {
                    Helpers.showNoInternetDialog(getActivity());
                }
            }
        });

        shareEmergencyProfileBTN.setOnClickListener(new View.OnClickListener() {
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
                        requestEmergencyProfile();
                    }
                } else {
                    Helpers.showNoInternetDialog(getActivity());
                }
            }
        });



        return fragmentView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.user_profile, menu);

        saveChangesItem = menu.findItem(R.id.save_changes);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_WRITE_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //reload my activity with permission granted or use the features what required the permission
                    requestEmergencyProfile();
                } else {
                    Toast.makeText(getActivity(), "The app was not allowed to write to your storage. Hence, it cannot function properly. Please consider granting it " +
                            "this permission", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void getMedicalConditionsAndShowDialog() {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        dialogBuilder.setTitle(R.string.profile_edit_medical_conditions_dialog_title);

        View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.profile_edit_medical_conditions_layout, null);

        final RadioGroup medicalRdGroup = (RadioGroup) dialogView.findViewById(R.id.medicalConditionsRdGroup);

        for (int i = 0; i < userMedicalConditions.size(); i++) {
            final RadioButton radioButton = new RadioButton(getActivity());
            radioButton.setPadding(0, getResources().getDimensionPixelSize(R.dimen.default_margin_1), 0, getResources().getDimensionPixelSize(R.dimen.default_margin_1));
            radioButton.setText(userMedicalConditions.get(i).getName());

            if (userMedicalConditions.get(i).getSelected().equals("1"))
                radioButton.setChecked(true);

            medicalRdGroup.addView(radioButton);
        }

        dialogBuilder.setView(dialogView);
        dialogBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                showSaveChangesMenuItem();
            }
        });
        dialogBuilder.setNegativeButton(R.string.decline_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }

    private void showSaveChangesMenuItem(){
        if (!saveChangesItem.isVisible())
            saveChangesItem.setVisible(true);
    }

    private void getUserProfile() {
        final ProgressDialog waitingDialog = new ProgressDialog(getActivity());
        waitingDialog.setTitle(getString(R.string.loading_user_profile_dialog_title));
        waitingDialog.setMessage(getString(R.string.please_wait_dialog_message));
        waitingDialog.show();

        final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setTitle(R.string.loading_user_profile_dialog_title);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        if (waitingDialog.isShowing())
                            waitingDialog.dismiss();
                    }
                });

        DataAccessHandler.getInstance().getUserProfile(prefs.getString(Constants.PREF_USER_ACCESS_TOKEN, Constants.NO_ACCESS_TOKEN_FOUND_IN_PREFS), new Callback<UserProfileResponse>() {
            @Override
            public void onResponse(Call<UserProfileResponse> call, Response<UserProfileResponse> response) {
                switch (response.code()) {
                    case 200:
                        UserProfileResponseDatum userProfileData = response.body().getData().getBody().getData();

                        if (userProfileData != null) {

                            userMedicalConditions = userProfileData.getMedicalConditions();

                            if (userProfileData.getName() != null && !userProfileData.getName().isEmpty())
                                userFullNameTV.setText(userProfileData.getName());

                            if (userProfileData.getEmail() != null && !userProfileData.getEmail().isEmpty())
                                userEmailTV.setText(userProfileData.getEmail());

                            if (userProfileData.getWeight() != null && !userProfileData.getWeight().isEmpty())
                                weightEntryValueTV.setText(userProfileData.getWeight());

                            if (userProfileData.getCountry() != null && !userProfileData.getCountry().isEmpty())
                                countryValueTV.setText(userProfileData.getCountry());

                            if (userProfileData.getMetricSystem() != null && !userProfileData.getMetricSystem().isEmpty())
                                metricSystemValueTV.setText(userProfileData.getMetricSystem());

                            waitingDialog.dismiss();

                            break;
                        }
                }
            }

            @Override
            public void onFailure(Call<UserProfileResponse> call, Throwable t) {
                Log.d(TAG, "onFailure: User profile request failed!");
            }
        });
    }

    private void signOutUser() {
        final ProgressDialog waitingDialog = new ProgressDialog(getActivity());
        waitingDialog.setTitle(getString(R.string.signing_out_dialog_title));
        waitingDialog.setMessage(getString(R.string.please_wait_dialog_message));
        waitingDialog.show();

        final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setTitle(R.string.signing_out_dialog_title);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        if (waitingDialog.isShowing())
                            waitingDialog.dismiss();
                    }
                });

        DataAccessHandler.getInstance().signOutUser(prefs.getString(Constants.PREF_USER_ACCESS_TOKEN, Constants
                .NO_ACCESS_TOKEN_FOUND_IN_PREFS), new Callback<DefaultGetResponse>() {
            @Override
            public void onResponse(Call<DefaultGetResponse> call, Response<DefaultGetResponse> response) {
                switch (response.code()) {
                    case 200:
                        waitingDialog.dismiss();

                        prefs.edit().putString(Constants.PREF_USER_ACCESS_TOKEN, Constants.NO_ACCESS_TOKEN_FOUND_IN_PREFS).apply();
                        prefs.edit().putBoolean(Constants.EXTRAS_USER_LOGGED_IN, false).apply();

                        getActivity().finish();

                        Intent intent = new Intent(getActivity(), Login_Activity.class);
                        startActivity(intent);
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

    private void requestEmergencyProfile() {
        DataAccessHandler.getInstance().getEmergencyProfile(prefs.getString(Constants.PREF_USER_ACCESS_TOKEN, Constants.NO_ACCESS_TOKEN_FOUND_IN_PREFS), new Callback<EmergencyProfileResponse>() {
            @Override
            public void onResponse(Call<EmergencyProfileResponse> call, Response<EmergencyProfileResponse> response) {
                switch (response.code()) {
                    case 200:
                        if (response.body().getData().getBody() != null) {
                            new DownloadPDFFile().execute(response.body().getData().getBody().getUserPdf(), "my_emergency_profile.pdf");
                        }
                        break;
                }
            }

            @Override
            public void onFailure(Call<EmergencyProfileResponse> call, Throwable t) {

            }
        });
    }

    private class DownloadPDFFile extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            String fileUrl = strings[0];
            String fileName = strings[1];
            String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
            File folder = new File(extStorageDirectory, "GMFit");
            folder.mkdir();

            File pdfFile = new File(folder, fileName);

            try {
                pdfFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            FileDownloader.downloadFile(fileUrl, pdfFile);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            File pdfFile = new File(Environment.getExternalStorageDirectory() + "/GMFit/" + "my_emergency_profile.pdf");  // -> filename = maven.pdf
            Uri uri = Uri.fromFile(pdfFile);

            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("message/rfc822");
            i.putExtra(Intent.EXTRA_EMAIL,
                    new String[]{"support@gmfit.com"});
            i.putExtra(Intent.EXTRA_STREAM, uri);
            i.putExtra(Intent.EXTRA_SUBJECT, "EMERGENCY PROFILE");
            i.putExtra(Intent.EXTRA_TEXT, "This is an emergency, please check my profile.");
            try {
                startActivity(Intent.createChooser(i, "Send email through"));
            } catch (ActivityNotFoundException ex) {
                Toast.makeText(getActivity(),
                        "No email client", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    public static class FileDownloader {
        private static final int MEGABYTE = 1024 * 1024;

        static void downloadFile(String fileUrl, File directory) {
            try {

                URL url = new URL(fileUrl);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                FileOutputStream fileOutputStream = new FileOutputStream(directory);

                byte[] buffer = new byte[MEGABYTE];
                int bufferLength = 0;
                while ((bufferLength = inputStream.read(buffer)) > 0) {
                    fileOutputStream.write(buffer, 0, bufferLength);
                }

                fileOutputStream.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
