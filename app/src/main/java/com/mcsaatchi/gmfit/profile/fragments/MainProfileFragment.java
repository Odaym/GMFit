package com.mcsaatchi.gmfit.profile.fragments;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.GMFitApplication;
import com.mcsaatchi.gmfit.architecture.data_access.DataAccessHandler;
import com.mcsaatchi.gmfit.architecture.otto.EventBusSingleton;
import com.mcsaatchi.gmfit.architecture.otto.RemindersStatusChangedEvent;
import com.mcsaatchi.gmfit.architecture.picasso.CircleTransform;
import com.mcsaatchi.gmfit.architecture.rest.DefaultGetResponse;
import com.mcsaatchi.gmfit.architecture.rest.EmergencyProfileResponse;
import com.mcsaatchi.gmfit.architecture.rest.MetaTextsResponse;
import com.mcsaatchi.gmfit.architecture.rest.UserProfileResponse;
import com.mcsaatchi.gmfit.architecture.rest.UserProfileResponseActivityLevel;
import com.mcsaatchi.gmfit.architecture.rest.UserProfileResponseDatum;
import com.mcsaatchi.gmfit.architecture.rest.UserProfileResponseGoal;
import com.mcsaatchi.gmfit.architecture.rest.UserProfileResponseMedicalCondition;
import com.mcsaatchi.gmfit.common.Constants;
import com.mcsaatchi.gmfit.common.classes.Helpers;
import com.mcsaatchi.gmfit.onboarding.activities.LoginActivity;
import com.mcsaatchi.gmfit.profile.activities.ChangePasswordActivity;
import com.mcsaatchi.gmfit.profile.activities.ContactUsActivity;
import com.mcsaatchi.gmfit.profile.activities.MetaTextsActivity;
import com.mcsaatchi.gmfit.profile.activities.RemindersActivity;
import com.mukesh.countrypicker.fragments.CountryPicker;
import com.mukesh.countrypicker.interfaces.CountryPickerListener;
import com.squareup.picasso.Picasso;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import javax.inject.Inject;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;
import worker8.com.github.radiogroupplus.RadioGroupPlus;

public class MainProfileFragment extends Fragment {
  private static final int REQUEST_WRITE_STORAGE = 112;
  private static final int REQUEST_PICK_IMAGE_GALLERY = 329;
  private static final int ASK_CAMERA_AND_STORAGE_PERMISSION = 834;
  private static final int CAPTURE_NEW_PICTURE_REQUEST_CODE = 871;

  @Bind(R.id.userProfileIV) ImageView userProfileIV;
  @Bind(R.id.userFullNameTV) TextView userFullNameTV;
  @Bind(R.id.userEmailTV) TextView userEmailTV;
  @Bind(R.id.countryValueTV) TextView countryValueTV;
  @Bind(R.id.weightEntryValueTV) TextView weightEntryValueTV;
  @Bind(R.id.goalsEntryValueTV) TextView goalsEntryValueTV;
  @Bind(R.id.medicalConditionsValueTV) TextView medicalConditionsValueTV;
  @Bind(R.id.metricSystemValueTV) TextView metricSystemValueTV;
  @Bind(R.id.activityLevelsEntryValueTV) TextView activityLevelsEntryValueTV;
  @Bind(R.id.appRemindersValueTV) TextView appRemindersValueTV;

  @Bind(R.id.weightLayout) RelativeLayout weightLayout;
  @Bind(R.id.goalsLayout) RelativeLayout goalsLayout;
  @Bind(R.id.medicalConditionsLayout) RelativeLayout medicalConditionsLayout;
  @Bind(R.id.appRemindersLayout) RelativeLayout appRemindersLayout;
  @Bind(R.id.countryLayout) RelativeLayout countryLayout;
  @Bind(R.id.metricLayout) RelativeLayout metricLayout;
  @Bind(R.id.termsConditionsLayout) RelativeLayout termsConditionsLayout;
  @Bind(R.id.privacyLayout) RelativeLayout privacyLayout;
  @Bind(R.id.contactUsLayout) RelativeLayout contactUsLayout;
  @Bind(R.id.shareAppLayout) RelativeLayout shareAppLayout;
  @Bind(R.id.changePasswordLayout) RelativeLayout changePasswordLayout;
  @Bind(R.id.changePasswordParentLayout) LinearLayout changePasswordParentLayout;

  @Bind(R.id.shareEmergencyProfileBTN) Button shareEmergencyProfileBTN;

  @Bind(R.id.logoutBTN) Button logoutBTN;

  @Inject SharedPreferences prefs;
  @Inject DataAccessHandler dataAccessHandler;

  private File photoFile;
  private Uri photoFileUri;
  private boolean profilePictureChanged = false;

  private double newUserWeight;

  private List<UserProfileResponseMedicalCondition> userMedicalConditions = new ArrayList<>();
  private List<UserProfileResponseActivityLevel> userActivityLevels = new ArrayList<>();

  private ArrayList userMetricSystems = new ArrayList<String>() {{
    add(0, "Metric");
  }};

  private List<UserProfileResponseGoal> userGoals = new ArrayList<>();

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {

    View fragmentView = inflater.inflate(R.layout.fragment_main_profile, container, false);

    ButterKnife.bind(this, fragmentView);
    ((GMFitApplication) getActivity().getApplication()).getAppComponent().inject(this);

    EventBusSingleton.getInstance().register(this);

    setHasOptionsMenu(true);

    ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.profile_tab_title);

    Picasso.with(getActivity())
        .load(R.drawable.pictures_placeholder)
        .transform(new CircleTransform())
        .into(userProfileIV);

    getUserProfile();

    /**
     * PROFILE IMAGE EDITOR
     */
    userProfileIV.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        String[] neededPermissions = new String[] {
            Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        boolean hasCameraPermission =
            (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED);

        boolean hasWriteStoragePermission = (ContextCompat.checkSelfPermission(getActivity(),
            Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);

        if (!hasCameraPermission || !hasWriteStoragePermission) {
          requestPermissions(neededPermissions, ASK_CAMERA_AND_STORAGE_PERMISSION);
        } else {
          showImagePickerDialog();
        }
      }
    });

    /**
     * WEIGHT EDITOR
     */
    weightLayout.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        dialogBuilder.setTitle(R.string.profile_edit_weight_dialog_title);

        View dialogView =
            LayoutInflater.from(getActivity()).inflate(R.layout.profile_edit_weight_dialog, null);
        final EditText editWeightET = (EditText) dialogView.findViewById(R.id.dialogWeightET);

        editWeightET.setText(
            String.valueOf(prefs.getFloat(Constants.EXTRAS_USER_PROFILE_WEIGHT, 0)));
        editWeightET.setSelection(editWeightET.getText().toString().length());

        dialogBuilder.setView(dialogView);
        dialogBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
          @Override public void onClick(DialogInterface dialogInterface, int i) {
            newUserWeight = Double.parseDouble(editWeightET.getText().toString());
            weightEntryValueTV.setText(
                String.valueOf(String.format(Locale.getDefault(), "%.1f", newUserWeight)));

            prefs.edit()
                .putFloat(Constants.EXTRAS_USER_PROFILE_WEIGHT, (float) newUserWeight)
                .apply();

            updateUserProfile();
          }
        });
        dialogBuilder.setNegativeButton(R.string.decline_cancel,
            new DialogInterface.OnClickListener() {
              @Override public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
              }
            });

        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
      }
    });

    /**
     * USER GOALS EDITOR
     */
    goalsLayout.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        dialogBuilder.setTitle(R.string.profile_edit_goal_dialog_title);

        View dialogView =
            LayoutInflater.from(getActivity()).inflate(R.layout.profile_edit_goal_dialog, null);

        final RadioGroup goalRadioButtonsGroup =
            (RadioGroup) dialogView.findViewById(R.id.goalRadioButtonsGroup);

        for (int i = 0; i < userGoals.size(); i++) {
          View listItemRadioButton =
              getActivity().getLayoutInflater().inflate(R.layout.list_item_edit_goal_dialog, null);

          final RadioButton radioButtonItem =
              (RadioButton) listItemRadioButton.findViewById(R.id.editGoalRadioBTN);
          radioButtonItem.setText(userGoals.get(i).getName());
          radioButtonItem.setId(Integer.parseInt(userGoals.get(i).getId()));

          if (radioButtonItem.getText()
              .toString()
              .equals(prefs.getString(Constants.EXTRAS_USER_PROFILE_GOAL, ""))) {
            radioButtonItem.setChecked(true);
          }

          goalRadioButtonsGroup.addView(listItemRadioButton);
        }

        dialogBuilder.setView(dialogView);
        dialogBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
          @Override public void onClick(DialogInterface dialogInterface, int i) {
            RadioButton selectedRadioButton = (RadioButton) goalRadioButtonsGroup.findViewById(
                goalRadioButtonsGroup.getCheckedRadioButtonId());

            int newUserGoalId = selectedRadioButton.getId();
            String newUserGoalText = selectedRadioButton.getText().toString();

            goalsEntryValueTV.setText(newUserGoalText);

            prefs.edit().putInt(Constants.EXTRAS_USER_PROFILE_GOAL_ID, newUserGoalId).apply();
            prefs.edit().putString(Constants.EXTRAS_USER_PROFILE_GOAL, newUserGoalText).apply();

            updateUserProfile();
          }
        });
        dialogBuilder.setNegativeButton(R.string.decline_cancel,
            new DialogInterface.OnClickListener() {
              @Override public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
              }
            });

        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
      }
    });

    /**
     * ACTIVITY LEVELS EDITOR
     */
    activityLevelsEntryValueTV.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        dialogBuilder.setTitle(R.string.profile_edit_activity_levels_dialog_title);

        View dialogView = LayoutInflater.from(getActivity())
            .inflate(R.layout.profile_edit_radio_options_layout, null);

        final RadioGroupPlus activityLevelRadioGRP =
            (RadioGroupPlus) dialogView.findViewById(R.id.collectionsRadioGroup);

        for (int i = 0; i < userActivityLevels.size(); i++) {
          View listItemRadioButton = getActivity().getLayoutInflater()
              .inflate(R.layout.list_item_edit_radio_options_dialog, null);

          final RadioButton radioButtonItem =
              (RadioButton) listItemRadioButton.findViewById(R.id.editRadioOptionsRadioBTN);
          radioButtonItem.setText(userActivityLevels.get(i).getName());
          radioButtonItem.setId(Integer.parseInt(userActivityLevels.get(i).getId()));

          if (radioButtonItem.getText()
              .toString()
              .equals(prefs.getString(Constants.EXTRAS_USER_PROFILE_ACTIVITY_LEVEL, ""))) {
            radioButtonItem.setChecked(true);
          }

          activityLevelRadioGRP.addView(listItemRadioButton);
        }

        dialogBuilder.setView(dialogView);
        dialogBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
          @Override public void onClick(DialogInterface dialogInterface, int position) {
            RadioButton selectedRadioButton = (RadioButton) activityLevelRadioGRP.findViewById(
                activityLevelRadioGRP.getCheckedRadioButtonId());

            if (selectedRadioButton != null) {
              activityLevelsEntryValueTV.setText(selectedRadioButton.getText().toString());

              prefs.edit()
                  .putString(Constants.EXTRAS_USER_PROFILE_ACTIVITY_LEVEL,
                      selectedRadioButton.getText().toString())
                  .apply();
              prefs.edit()
                  .putInt(Constants.EXTRAS_USER_PROFILE_ACTIVITY_LEVEL_ID,
                      selectedRadioButton.getId())
                  .apply();

              updateUserProfile();
            } else {
              Log.d("TAG", "onClick: Selected Radio Button was null!");
            }
          }
        });

        dialogBuilder.setNegativeButton(R.string.decline_cancel,
            new DialogInterface.OnClickListener() {
              @Override public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
              }
            });

        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
      }
    });

    /**
     * MEDICAL CONDITIONS EDITOR
     */
    medicalConditionsLayout.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        dialogBuilder.setTitle(R.string.profile_edit_medical_conditions_dialog_title);

        View dialogView = LayoutInflater.from(getActivity())
            .inflate(R.layout.profile_edit_radio_options_layout, null);

        final RadioGroupPlus medicalRdGroup =
            (RadioGroupPlus) dialogView.findViewById(R.id.collectionsRadioGroup);

        for (int i = 0; i < userMedicalConditions.size(); i++) {
          View listItemRadioButton = getActivity().getLayoutInflater()
              .inflate(R.layout.list_item_edit_radio_options_dialog, null);

          final RadioButton radioButtonItem =
              (RadioButton) listItemRadioButton.findViewById(R.id.editRadioOptionsRadioBTN);
          radioButtonItem.setText(userMedicalConditions.get(i).getName());
          radioButtonItem.setId(Integer.parseInt(userMedicalConditions.get(i).getId()));

          if (radioButtonItem.getText()
              .toString()
              .equals(prefs.getString(Constants.EXTRAS_USER_PROFILE_USER_MEDICAL_CONDITION, ""))) {
            radioButtonItem.setChecked(true);
          }

          medicalRdGroup.addView(listItemRadioButton);
        }

        dialogBuilder.setView(dialogView);
        dialogBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
          @Override public void onClick(DialogInterface dialogInterface, int position) {
            RadioButton selectedRadioButton =
                (RadioButton) medicalRdGroup.findViewById(medicalRdGroup.getCheckedRadioButtonId());

            if (selectedRadioButton != null) {
              medicalConditionsValueTV.setText(selectedRadioButton.getText().toString());

              prefs.edit()
                  .putString(Constants.EXTRAS_USER_PROFILE_USER_MEDICAL_CONDITION,
                      selectedRadioButton.getText().toString())
                  .apply();
              prefs.edit()
                  .putInt(Constants.EXTRAS_USER_PROFILE_USER_MEDICAL_CONDITION_ID,
                      selectedRadioButton.getId())
                  .apply();

              updateUserProfile();
            } else {
              Log.d("TAG", "onClick: Selected Radio Button was null!");
            }
          }
        });

        dialogBuilder.setNegativeButton(R.string.decline_cancel,
            new DialogInterface.OnClickListener() {
              @Override public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
              }
            });

        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
      }
    });

    /**
     * METRIC SYSTEM EDITOR
     */
    metricLayout.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        dialogBuilder.setTitle(R.string.profile_edit_measurement_unit_dialog_title);

        View dialogView = LayoutInflater.from(getActivity())
            .inflate(R.layout.profile_edit_measurement_unit_layout, null);

        final RadioGroupPlus metricRadioGroup =
            (RadioGroupPlus) dialogView.findViewById(R.id.measuremeantUnitRdGroup);

        for (int i = 0; i < userMetricSystems.size(); i++) {
          View listItemRadioButton = getActivity().getLayoutInflater()
              .inflate(R.layout.list_item_edit_metric_system_dialog, null);

          final RadioButton radioButtonItem =
              (RadioButton) listItemRadioButton.findViewById(R.id.editMetricSystemRadioBTN);
          radioButtonItem.setText(userMetricSystems.get(i).toString());
          radioButtonItem.setId(userMetricSystems.indexOf(radioButtonItem.getText().toString()));

          if (radioButtonItem.getText()
              .toString()
              .equalsIgnoreCase(
                  prefs.getString(Constants.EXTRAS_USER_PROFILE_MEASUREMENT_SYSTEM, ""))) {
            radioButtonItem.setChecked(true);
          }

          metricRadioGroup.addView(listItemRadioButton);
        }

        dialogBuilder.setView(dialogView);
        dialogBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
          @Override public void onClick(DialogInterface dialogInterface, int position) {
            RadioButton selectedRadioButton = (RadioButton) metricRadioGroup.findViewById(
                metricRadioGroup.getCheckedRadioButtonId());

            metricSystemValueTV.setText(selectedRadioButton.getText().toString());

            prefs.edit()
                .putString(Constants.EXTRAS_USER_PROFILE_MEASUREMENT_SYSTEM,
                    selectedRadioButton.getText().toString())
                .apply();

            updateUserProfile();
          }
        });
        dialogBuilder.setNegativeButton(R.string.decline_cancel,
            new DialogInterface.OnClickListener() {
              @Override public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
              }
            });

        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
      }
    });

    /**
     * LOGOUT FROM THE APP
     */
    logoutBTN.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        if (Helpers.isInternetAvailable(getActivity())) {
          signOutUser();
        } else {
          Helpers.showNoInternetDialog(getActivity());
        }
      }
    });

    /**
     * COUNTRY OF ORIGIN EDITOR
     */
    countryLayout.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        final CountryPicker picker =
            CountryPicker.newInstance(getString(R.string.choose_country_hint));

        picker.show(getActivity().getSupportFragmentManager(), "COUNTRY_PICKER");
        picker.setListener(new CountryPickerListener() {
          @Override public void onSelectCountry(String countryName, String code, String dialCode,
              int flagDrawableResID) {
            countryValueTV.setText(countryName);

            prefs.edit().putString(Constants.EXTRAS_USER_PROFILE_NATIONALITY, countryName).apply();

            picker.dismiss();

            updateUserProfile();
          }
        });
      }
    });

    /**
     * SHOW EMERGENCY PROFILE
     */
    shareEmergencyProfileBTN.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        if (Helpers.isInternetAvailable(getActivity())) {

          boolean hasPermission = (ContextCompat.checkSelfPermission(getActivity(),
              Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
          if (!hasPermission) {
            requestPermissions(new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE },
                REQUEST_WRITE_STORAGE);
          } else {
            requestEmergencyProfile();
          }
        } else {
          Helpers.showNoInternetDialog(getActivity());
        }
      }
    });

    /**
     * TERMS AND CONDITIONS
     */
    termsConditionsLayout.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        getMetaTexts("terms");
      }
    });

    /**
     * PRIVACY POLICY
     */
    privacyLayout.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        getMetaTexts("privacy");
      }
    });

    /**
     * CONTACT US
     */
    contactUsLayout.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        Intent intent = new Intent(getActivity(), ContactUsActivity.class);
        startActivity(intent);
      }
    });

    /**
     * SET APP REMINDERS
     */
    appRemindersLayout.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        Intent intent = new Intent(getActivity(), RemindersActivity.class);
        startActivity(intent);
      }
    });

    /**
     * SHARE THE APP
     */
    shareAppLayout.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        try {
          Intent i = new Intent(Intent.ACTION_SEND);
          i.setType("text/plain");
          i.putExtra(Intent.EXTRA_SUBJECT, "GMFit");
          String sAux = "\nCheck out GlobeMed's new application!\n\n";
          sAux = sAux + "https://play.google.com/store/apps/details?id=Orion.Soft \n\n";
          i.putExtra(Intent.EXTRA_TEXT, sAux);
          startActivity(Intent.createChooser(i, "Choose sharing method"));
        } catch (Exception e) {
          //e.toString();
        }
      }
    });

    /**
     * If the user is logged in through Facebook, Token is not -1 (empty)
     */
    if (!prefs.getString(Constants.EXTRAS_USER_FACEBOOK_TOKEN, "-1").equals("-1")) {
      changePasswordParentLayout.setVisibility(View.GONE);
    }

    /**
     * CHANGE PASSWORD LAYOUT
     */
    changePasswordLayout.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        Intent intent = new Intent(getActivity(), ChangePasswordActivity.class);
        startActivity(intent);
      }
    });

    return fragmentView;
  }

  @Override public void onDestroy() {
    super.onDestroy();
    EventBusSingleton.getInstance().unregister(this);
  }

  @Override public void onRequestPermissionsResult(int requestCode, String[] permissions,
      int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    switch (requestCode) {
      case REQUEST_WRITE_STORAGE:
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
          //reload my activity with permission granted or use the features what required the permission
          requestEmergencyProfile();
        } else {
          Toast.makeText(getActivity(),
              "The app was not allowed to write to your storage. Hence, it cannot function properly. Please consider granting it "
                  + "this permission", Toast.LENGTH_LONG).show();
        }
        break;

      case ASK_CAMERA_AND_STORAGE_PERMISSION:
        if (grantResults.length > 0
            && grantResults[0] == PackageManager.PERMISSION_GRANTED
            && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
          showImagePickerDialog();
        } else {
          Toast.makeText(getActivity(),
              "The app was not allowed to write to your storage or take use the device's Camera. Hence, it cannot function properly."
                  + "Please consider granting it these permissions", Toast.LENGTH_LONG).show();
        }
        break;
    }
  }

  @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    switch (requestCode) {
      case CAPTURE_NEW_PICTURE_REQUEST_CODE:
        if (photoFile.getTotalSpace() > 0) {
          setProfilePicture(photoFile.getAbsolutePath());
        } else {
          Timber.d("No picture was taken, photoFile size : %d", photoFile.getTotalSpace());
        }

        break;
      case REQUEST_PICK_IMAGE_GALLERY:
        if (data != null) {
          Uri selectedImageUri = data.getData();
          String selectedImagePath = getPhotoPathFromGallery(selectedImageUri);

          setProfilePicture(selectedImagePath);
        }
    }
  }

  private void updateReminderTextviewFromStatus(RemindersStatusChangedEvent event) {
    if (event.isReminderOn()) {
      appRemindersValueTV.setText("On");
    } else {
      appRemindersValueTV.setText("Off");
    }
  }

  private void setProfilePicture(String finalImagePath) {
    String currentImagePath = prefs.getString(Constants.EXTRAS_USER_PROFILE_IMAGE, "");

    /**
     * If the current picture is different than the one the user just took, change it
     */
    if (!currentImagePath.equals(finalImagePath)) {
      prefs.edit().putString(Constants.EXTRAS_USER_PROFILE_IMAGE, finalImagePath).apply();

      Picasso.with(getActivity())
          .load(new File(prefs.getString(Constants.EXTRAS_USER_PROFILE_IMAGE, "")))
          .resize(500, 500)
          .transform(new CircleTransform())
          .centerInside()
          .into(userProfileIV);

      profilePictureChanged = true;

      updateUserProfile();
    } else {
      Timber.d("Profile pictures are the same");
    }
  }

  public void openTakePictureIntent() {
    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
      photoFile = null;
      try {
        photoFile = createImageFile(constructImageFilename());
        photoFileUri = Uri.fromFile(photoFile);
      } catch (IOException ex) {
        ex.printStackTrace();
      }

      if (photoFile != null) {
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoFileUri);
        startActivityForResult(takePictureIntent, CAPTURE_NEW_PICTURE_REQUEST_CODE);
      }
    }
  }

  public String getPhotoPathFromGallery(Uri uri) {
    if (uri == null) {
      // TODO perform some logging or show user feedback
      return null;
    }

    String[] projection = { MediaStore.Images.Media.DATA };
    Cursor cursor = getActivity().getContentResolver().query(uri, projection, null, null, null);
    if (cursor != null) {
      int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
      cursor.moveToFirst();
      return cursor.getString(column_index);
    }

    return uri.getPath();
  }

  private void showImagePickerDialog() {
    AlertDialog.Builder builderSingle = new AlertDialog.Builder(getActivity());
    builderSingle.setTitle("Set profile picture");

    final ArrayAdapter<String> arrayAdapter =
        new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1);
    arrayAdapter.add(getResources().getString(R.string.choose_picture_from_gallery));
    arrayAdapter.add(getResources().getString(R.string.take_new_picture));

    builderSingle.setNegativeButton(R.string.decline_cancel, new DialogInterface.OnClickListener() {
      @Override public void onClick(DialogInterface dialog, int which) {
        dialog.dismiss();
      }
    });

    builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
      @Override public void onClick(DialogInterface dialog, int which) {
        String strName = arrayAdapter.getItem(which);
        if (strName != null) {
          switch (strName) {
            case "Choose from gallery":
              Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                  android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
              startActivityForResult(galleryIntent, REQUEST_PICK_IMAGE_GALLERY);
              break;
            case "Take a new picture":
              openTakePictureIntent();
              break;
          }
        }
      }
    });
    builderSingle.show();
  }

  private File createImageFile(String imagePath) throws IOException {
    return new File(imagePath);
  }

  private String constructImageFilename() {
    String timeStamp =
        new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
    String imageFileName = "JPEG_" + timeStamp;

    File mediaStorageDir =
        new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            "GMFit");

    if (!mediaStorageDir.exists()) {
      if (!mediaStorageDir.mkdirs()) {
        Log.d("Constants.DEBUG_TAG", "failed to create directory");
        return null;
      }
    }

    return mediaStorageDir.getPath() + File.separator + imageFileName;
  }

  private void getMetaTexts(final String section) {
    final ProgressDialog waitingDialog = new ProgressDialog(getActivity());
    waitingDialog.setTitle(getResources().getString(R.string.loading_data_dialog_title));
    waitingDialog.setMessage(getResources().getString(R.string.please_wait_dialog_message));
    waitingDialog.show();

    final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
    alertDialog.setTitle(R.string.loading_data_dialog_title);
    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getResources().getString(R.string.ok),
        new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();

            if (waitingDialog.isShowing()) waitingDialog.dismiss();
          }
        });

    dataAccessHandler.getMetaTexts(section, new Callback<MetaTextsResponse>() {
      @Override
      public void onResponse(Call<MetaTextsResponse> call, Response<MetaTextsResponse> response) {

        switch (response.code()) {
          case 200:
            waitingDialog.dismiss();

            Intent intent = new Intent(getActivity(), MetaTextsActivity.class);
            switch (section) {
              case "terms":
                intent.putExtra(Constants.BUNDLE_ACTIVITY_TITLE,
                    getResources().getString(R.string.terms_and_conditions_entry));
                break;
              case "privacy":
                intent.putExtra(Constants.BUNDLE_ACTIVITY_TITLE,
                    getResources().getString(R.string.privacy_and_security_entry));
                break;
            }

            intent.putExtra(Constants.EXTRAS_META_HTML_CONTENT,
                response.body().getData().getBody());
            startActivity(intent);
        }
      }

      @Override public void onFailure(Call<MetaTextsResponse> call, Throwable t) {
        Timber.d("Call failed with error : %s", t.getMessage());
        alertDialog.setMessage(getString(R.string.error_response_from_server_incorrect));
        alertDialog.show();
      }
    });
  }

  private void getUserProfile() {
    dataAccessHandler.getUserProfile(new Callback<UserProfileResponse>() {
      @Override public void onResponse(Call<UserProfileResponse> call,
          Response<UserProfileResponse> response) {
        switch (response.code()) {
          case 200:
            UserProfileResponseDatum userProfileData =
                response.body().getData().getBody().getData();

            SharedPreferences.Editor prefsEditor = prefs.edit();

            if (userProfileData != null) {

              userMedicalConditions = userProfileData.getMedicalConditions();
              userGoals = userProfileData.getUserGoals();
              userActivityLevels = userProfileData.getActivityLevels();

              /**
               * Set the medical condition
               */
              if (prefs.getInt(Constants.EXTRAS_USER_PROFILE_USER_MEDICAL_CONDITION_ID, -1) == -1) {
                prefsEditor.putString(Constants.EXTRAS_USER_PROFILE_USER_MEDICAL_CONDITION, "None");
                prefsEditor.putInt(Constants.EXTRAS_USER_PROFILE_USER_MEDICAL_CONDITION_ID, -1);

                medicalConditionsValueTV.setText("None");
              } else {
                for (int i = 0; i < userMedicalConditions.size(); i++) {
                  if (userMedicalConditions.get(i).getSelected().equals("1")) {
                    prefsEditor.putString(Constants.EXTRAS_USER_PROFILE_USER_MEDICAL_CONDITION,
                        userMedicalConditions.get(i).getName());
                    prefsEditor.putInt(Constants.EXTRAS_USER_PROFILE_USER_MEDICAL_CONDITION_ID,
                        Integer.parseInt(userMedicalConditions.get(i).getId()));

                    medicalConditionsValueTV.setText(userMedicalConditions.get(i).getName());
                  }
                }
              }

              /**
               * Set the activity level
               */
              if (prefs.getInt(Constants.EXTRAS_USER_PROFILE_ACTIVITY_LEVEL_ID, -1) == -1) {
                prefsEditor.putString(Constants.EXTRAS_USER_PROFILE_ACTIVITY_LEVEL,
                    "Lightly Active (1-3 times per week)");
                prefsEditor.putInt(Constants.EXTRAS_USER_PROFILE_ACTIVITY_LEVEL_ID, 2);

                activityLevelsEntryValueTV.setText("Lightly Active (1-3 times per week)");
              } else {
                for (int i = 0; i < userActivityLevels.size(); i++) {
                  if (userActivityLevels.get(i).getSelected().equals("1")) {
                    prefsEditor.putString(Constants.EXTRAS_USER_PROFILE_ACTIVITY_LEVEL,
                        userActivityLevels.get(i).getName());
                    prefsEditor.putInt(Constants.EXTRAS_USER_PROFILE_ACTIVITY_LEVEL_ID,
                        Integer.parseInt(userActivityLevels.get(i).getId()));

                    activityLevelsEntryValueTV.setText(userActivityLevels.get(i).getName());
                  }
                }
              }

              /**
               * Set the user goals
               */
              for (int i = 0; i < userGoals.size(); i++) {
                if (userGoals.get(i).getSelected().equals("1")) {
                  prefsEditor.putString(Constants.EXTRAS_USER_PROFILE_GOAL,
                      userGoals.get(i).getName());
                  prefsEditor.putInt(Constants.EXTRAS_USER_PROFILE_GOAL_ID,
                      Integer.parseInt(userGoals.get(i).getId()));

                  goalsEntryValueTV.setText(userGoals.get(i).getName());
                }
              }

              /**
               * Set the name
               */
              if (userProfileData.getName() != null && !userProfileData.getName().isEmpty()) {
                prefsEditor.putString(Constants.EXTRAS_USER_PROFILE_USER_FULL_NAME,
                    userProfileData.getName());
                userFullNameTV.setText(userProfileData.getName());
              }

              /**
               * Set the email
               */
              if (userProfileData.getEmail() != null && !userProfileData.getEmail().isEmpty()) {
                prefsEditor.putString(Constants.EXTRAS_USER_PROFILE_USER_EMAIL,
                    userProfileData.getEmail());
                userEmailTV.setText(userProfileData.getEmail());
              }

              /**
               * Set the weight
               */
              if (userProfileData.getWeight() != null && !userProfileData.getWeight().isEmpty()) {
                prefsEditor.putFloat(Constants.EXTRAS_USER_PROFILE_WEIGHT,
                    Float.parseFloat(userProfileData.getWeight()));
                weightEntryValueTV.setText(String.valueOf(String.format(Locale.getDefault(), "%.1f",
                    Float.parseFloat(userProfileData.getWeight()))));
              }

              /**
               * Set the country
               */
              if (userProfileData.getCountry() != null && !userProfileData.getCountry().isEmpty()) {
                prefsEditor.putString(Constants.EXTRAS_USER_PROFILE_NATIONALITY,
                    userProfileData.getCountry());
                countryValueTV.setText(userProfileData.getCountry());
              }

              /**
               * Set the metric system
               */
              if (userProfileData.getMetricSystem() != null && !userProfileData.getMetricSystem()
                  .isEmpty()) {
                prefsEditor.putString(Constants.EXTRAS_USER_PROFILE_MEASUREMENT_SYSTEM,
                    userProfileData.getMetricSystem());

                String cap = userProfileData.getMetricSystem().substring(0, 1).toUpperCase()
                    + userProfileData.getMetricSystem().substring(1);

                metricSystemValueTV.setText(cap);
              }

              /**
               * Set the gender
               */
              if (userProfileData.getGender() != null && !userProfileData.getGender().isEmpty()) {
                int finalGender = userProfileData.getGender().equals("Male") ? 0 : 1;
                prefsEditor.putInt(Constants.EXTRAS_USER_PROFILE_GENDER, finalGender);
              }

              /**
               * Set the profile picture
               */
              if (userProfileData.getProfile_picture() != null
                  && !userProfileData.getProfile_picture().equals("http://gmfit.mcsaatchi.me/")
                  && getActivity() != null) {

                prefsEditor.putString(Constants.EXTRAS_USER_PROFILE_IMAGE,
                    userProfileData.getProfile_picture());

                Picasso.with(getActivity())
                    .load(userProfileData.getProfile_picture())
                    .resize(500, 500)
                    .transform(new CircleTransform())
                    .centerInside()
                    .into(userProfileIV);
              }

              prefsEditor.apply();

              break;
            }
        }
      }

      @Override public void onFailure(Call<UserProfileResponse> call, Throwable t) {
        Timber.d("Call failed with error : %s", t.getMessage());
        final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setMessage(getString(R.string.error_response_from_server_incorrect));
        alertDialog.show();
      }
    });
  }

  private void updateUserProfile() {
    String dateOfBirth = prefs.getString(Constants.EXTRAS_USER_PROFILE_DATE_OF_BIRTH, "1990-01-01");
    String bloodType = prefs.getString(Constants.EXTRAS_USER_PROFILE_BLOOD_TYPE, "A+");
    String nationality = prefs.getString(Constants.EXTRAS_USER_PROFILE_NATIONALITY, "");
    String measurementSystem =
        prefs.getString(Constants.EXTRAS_USER_PROFILE_MEASUREMENT_SYSTEM, "metric");
    int userGoalId = prefs.getInt(Constants.EXTRAS_USER_PROFILE_GOAL_ID, 0);
    int activityLevelId = prefs.getInt(Constants.EXTRAS_USER_PROFILE_ACTIVITY_LEVEL_ID, 2);
    int medicalCondition =
        prefs.getInt(Constants.EXTRAS_USER_PROFILE_USER_MEDICAL_CONDITION_ID, -1);
    int gender = prefs.getInt(Constants.EXTRAS_USER_PROFILE_GENDER, 1);
    float height = prefs.getFloat(Constants.EXTRAS_USER_PROFILE_HEIGHT, 180);
    float weight = prefs.getFloat(Constants.EXTRAS_USER_PROFILE_WEIGHT, 82);

    final ProgressDialog waitingDialog = new ProgressDialog(getActivity());
    waitingDialog.setTitle(getString(R.string.updating_user_profile_dialog_title));
    waitingDialog.setMessage(getString(R.string.please_wait_dialog_message));
    waitingDialog.show();

    final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
    alertDialog.setTitle(R.string.updating_user_profile_dialog_title);
    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.ok),
        new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();

            if (waitingDialog.isShowing()) waitingDialog.dismiss();
          }
        });

    dataAccessHandler.updateUserProfile(dateOfBirth, bloodType, nationality, medicalCondition,
        measurementSystem.toLowerCase(), userGoalId, activityLevelId, gender, height, weight, "1",
        new Callback<DefaultGetResponse>() {
          @Override public void onResponse(Call<DefaultGetResponse> call,
              Response<DefaultGetResponse> response) {
            switch (response.code()) {
              case 200:
                if (profilePictureChanged) {
                  updateUserPicture(waitingDialog);
                } else {
                  waitingDialog.dismiss();
                  Toast.makeText(getActivity(), "Your profile was updated succesfully",
                      Toast.LENGTH_SHORT).show();
                }

                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                    Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);

                break;
            }
          }

          @Override public void onFailure(Call<DefaultGetResponse> call, Throwable t) {
            Timber.d("Call failed with error : %s", t.getMessage());
            alertDialog.setMessage(getString(R.string.error_response_from_server_incorrect));
            alertDialog.show();
          }
        });
  }

  private void updateUserPicture(final ProgressDialog waitingDialog) {
    HashMap<String, RequestBody> profilePictureParts = new HashMap<>();

    String userPicturePath = prefs.getString(Constants.EXTRAS_USER_PROFILE_IMAGE, "");

    File imageFile = new File(userPicturePath);

    RequestBody imageFilePart = RequestBody.create(MediaType.parse("image/jpeg"), imageFile);

    profilePictureParts.put("picture\"; filename=\"" + userPicturePath + ".jpg", imageFilePart);

    dataAccessHandler.updateUserPicture(profilePictureParts, new Callback<DefaultGetResponse>() {
      @Override
      public void onResponse(Call<DefaultGetResponse> call, Response<DefaultGetResponse> response) {
        switch (response.code()) {
          case 200:

            waitingDialog.dismiss();
            Toast.makeText(getActivity(), "Your profile was updated successfully",
                Toast.LENGTH_SHORT).show();

            profilePictureChanged = false;

            break;
        }
      }

      @Override public void onFailure(Call<DefaultGetResponse> call, Throwable t) {
        Timber.d("Call failed with error : %s", t.getMessage());
        final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setMessage(getString(R.string.error_response_from_server_incorrect));
        alertDialog.show();
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

            if (waitingDialog.isShowing()) waitingDialog.dismiss();
          }
        });

    dataAccessHandler.signOutUser(new Callback<DefaultGetResponse>() {
      @Override
      public void onResponse(Call<DefaultGetResponse> call, Response<DefaultGetResponse> response) {
        switch (response.code()) {
          case 200:
            waitingDialog.dismiss();

            prefs.edit()
                .putString(Constants.PREF_USER_ACCESS_TOKEN,
                    Constants.NO_ACCESS_TOKEN_FOUND_IN_PREFS)
                .apply();
            prefs.edit().putBoolean(Constants.EXTRAS_USER_LOGGED_IN, false).apply();

            if (!prefs.getString(Constants.EXTRAS_USER_FACEBOOK_TOKEN, "-1").equals("-1")) {
              FacebookSdk.sdkInitialize(getActivity());
              LoginManager.getInstance().logOut();
              prefs.edit().putString(Constants.EXTRAS_USER_FACEBOOK_TOKEN, "-1").apply();
            }

            getActivity().finish();

            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
            break;
        }
      }

      @Override public void onFailure(Call<DefaultGetResponse> call, Throwable t) {
        Timber.d("Call failed with error : %s", t.getMessage());
        alertDialog.setMessage(getString(R.string.error_response_from_server_incorrect));
        alertDialog.show();
        getActivity().finish();
      }
    });
  }

  private void requestEmergencyProfile() {
    dataAccessHandler.getEmergencyProfile(new Callback<EmergencyProfileResponse>() {
      @Override public void onResponse(Call<EmergencyProfileResponse> call,
          Response<EmergencyProfileResponse> response) {
        switch (response.code()) {
          case 200:
            if (response.body().getData().getBody() != null) {
              new DownloadPDFFile().execute(response.body().getData().getBody().getUserPdf(),
                  "my_emergency_profile.pdf");
            }
            break;
        }
      }

      @Override public void onFailure(Call<EmergencyProfileResponse> call, Throwable t) {
        Timber.d("Call failed with error : %s", t.getMessage());
        final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setMessage(getString(R.string.error_response_from_server_incorrect));
        alertDialog.show();
      }
    });
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

  private class DownloadPDFFile extends AsyncTask<String, Void, Void> {

    @Override protected Void doInBackground(String... strings) {
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

    @Override protected void onPostExecute(Void aVoid) {
      super.onPostExecute(aVoid);

      File pdfFile = new File(
          Environment.getExternalStorageDirectory() + "/GMFit/" + "my_emergency_profile.pdf");
      Uri uri = Uri.fromFile(pdfFile);

      Intent i = new Intent(Intent.ACTION_SEND);
      i.setType("message/rfc822");
      i.putExtra(Intent.EXTRA_EMAIL, new String[] { "support@gmfit.com" });
      i.putExtra(Intent.EXTRA_STREAM, uri);
      i.putExtra(Intent.EXTRA_SUBJECT, "EMERGENCY PROFILE");
      i.putExtra(Intent.EXTRA_TEXT, "This is an emergency, please check my profile.");
      try {
        startActivity(Intent.createChooser(i, "Send email through"));
      } catch (ActivityNotFoundException ex) {
        Toast.makeText(getActivity(), "No email client", Toast.LENGTH_SHORT).show();
      }
    }
  }
}
