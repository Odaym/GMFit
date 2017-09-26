package com.mcsaatchi.gmfit.profile.fragments;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.classes.GMFitApplication;
import com.mcsaatchi.gmfit.architecture.countrypicker.CountryPicker;
import com.mcsaatchi.gmfit.architecture.otto.EventBusSingleton;
import com.mcsaatchi.gmfit.architecture.otto.ProfileUpdatedEvent;
import com.mcsaatchi.gmfit.architecture.picasso.CircleTransform;
import com.mcsaatchi.gmfit.architecture.retrofit.architecture.DataAccessHandlerImpl;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.AchievementsResponseBody;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.UserProfileResponseActivityLevel;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.UserProfileResponseDatum;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.UserProfileResponseGoal;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.UserProfileResponseMedicalCondition;
import com.mcsaatchi.gmfit.common.Constants;
import com.mcsaatchi.gmfit.common.classes.Helpers;
import com.mcsaatchi.gmfit.common.fragments.BaseFragment;
import com.mcsaatchi.gmfit.onboarding.activities.LoginActivity;
import com.mcsaatchi.gmfit.onboarding.activities.MedicalConditionsChoiceActivity;
import com.mcsaatchi.gmfit.onboarding.models.MedicalCondition;
import com.mcsaatchi.gmfit.profile.activities.ChangePasswordActivity;
import com.mcsaatchi.gmfit.profile.activities.ContactUsListActivity;
import com.mcsaatchi.gmfit.profile.activities.EditProfileActivity;
import com.mcsaatchi.gmfit.profile.activities.MealRemindersActivity;
import com.mcsaatchi.gmfit.profile.activities.MetaTextsActivity;
import com.mcsaatchi.gmfit.profile.adapters.AchievementsRecyclerAdapter;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import javax.inject.Inject;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import timber.log.Timber;
import worker8.com.github.radiogroupplus.RadioGroupPlus;

import static com.facebook.FacebookSdk.getApplicationContext;
import static com.mcsaatchi.gmfit.onboarding.fragments.SetupProfile4Fragment.MEDICAL_CONDITIONS_SELECTED;

public class MainProfileFragment extends BaseFragment
    implements MainProfileFragmentPresenter.MainProfileFragmentView {
  private static final int REQUEST_WRITE_STORAGE = 112;
  private static final int REQUEST_PICK_IMAGE_GALLERY = 329;
  private static final int ASK_CAMERA_AND_STORAGE_PERMISSION = 834;
  private static final int CAPTURE_NEW_PICTURE_REQUEST_CODE = 871;

  @Bind(R.id.userProfileIV) ImageView userProfileIV;
  @Bind(R.id.userFullNameTV) TextView userFullNameTV;
  @Bind(R.id.userEmailTV) TextView userEmailTV;
  @Bind(R.id.countryValueTV) TextView countryValueTV;
  @Bind(R.id.weightEntryValueTV) TextView weightEntryValueTV;
  @Bind(R.id.achievementsInfoTV) TextView achievementsInfoTV;
  @Bind(R.id.goalsEntryValueTV) TextView goalsEntryValueTV;
  @Bind(R.id.medicalConditionsValueTV) TextView medicalConditionsValueTV;
  @Bind(R.id.activityLevelsEntryValueTV) TextView activityLevelsEntryValueTV;
  @Bind(R.id.changePasswordParentLayout) LinearLayout changePasswordParentLayout;
  @Bind(R.id.achievementsRecycler) RecyclerView achievementsRecycler;

  @Inject SharedPreferences prefs;
  @Inject DataAccessHandlerImpl dataAccessHandler;

  private HashMap<String, RequestBody> medicalConditionParts;
  private ArrayList<MedicalCondition> medicalConditions = new ArrayList<>();
  private ArrayList<Integer> medicalConditionIDs = new ArrayList<>();

  private MainProfileFragmentPresenter presenter;
  private File photoFile;
  private Uri photoFileUri;
  private boolean profilePictureChanged = false;

  private double newUserWeight;

  private List<UserProfileResponseMedicalCondition> userMedicalConditions = new ArrayList<>();
  private List<UserProfileResponseActivityLevel> userActivityLevels = new ArrayList<>();

  private List<UserProfileResponseGoal> userGoals = new ArrayList<>();

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {

    View fragmentView = inflater.inflate(R.layout.fragment_main_profile, container, false);

    ButterKnife.bind(this, fragmentView);
    ((GMFitApplication) getActivity().getApplication()).getAppComponent().inject(this);

    EventBusSingleton.getInstance().register(this);

    setHasOptionsMenu(true);

    presenter = new MainProfileFragmentPresenter(this, dataAccessHandler);

    ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.profile_tab_title);

    Picasso.with(getActivity())
        .load(R.drawable.pictures_placeholder)
        .transform(new CircleTransform())
        .into(userProfileIV);

    presenter.getUserProfile();

    presenter.getUserAchievements();

    //If the user is logged in through Facebook, Token is not -1 (empty)
    if (!prefs.getString(Constants.EXTRAS_USER_FACEBOOK_TOKEN, "-1").equals("-1")) {
      changePasswordParentLayout.setVisibility(View.GONE);
    }

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
          presenter.requestEmergencyProfile();
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
      case MEDICAL_CONDITIONS_SELECTED:
        if (data != null) {
          String valueForCondition = "";

          ArrayList<MedicalCondition> conditionsFromExtras =
              data.getExtras().getParcelableArrayList("MEDICAL_CONDITIONS");

          medicalConditionIDs.clear();

          boolean nothingSelected = true;

          if (conditionsFromExtras != null) {
            for (int i = 0; i < conditionsFromExtras.size(); i++) {
              if (conditionsFromExtras.get(i).isSelected()) {
                if (conditionsFromExtras.get(i).getMedicalCondition().equals("None")) {
                  valueForCondition += "";
                  medicalConditionIDs.add(-1);
                } else {
                  nothingSelected = false;
                  medicalConditionIDs.add(conditionsFromExtras.get(i).getId());
                  valueForCondition += conditionsFromExtras.get(i).getMedicalCondition() + ", ";
                }
              }
            }

            if (nothingSelected) {
              valueForCondition += "";
              medicalConditionIDs.add(-1);
            }

            medicalConditionsValueTV.setText(valueForCondition.replaceAll(", $", ""));

            medicalConditionParts = constructMedicalConditionsForRequest(medicalConditionIDs);

            medicalConditions = conditionsFromExtras;

            updateUserProfile();
          }
        } else {
          Timber.d("Conditions returned is null");
        }

        break;
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

  @Override public void populateUserProfileInformation(UserProfileResponseDatum userProfileData) {
    SharedPreferences.Editor prefsEditor = prefs.edit();

    medicalConditions.clear();

    if (userProfileData != null) {
      userMedicalConditions = userProfileData.getMedicalConditions();
      userGoals = userProfileData.getUserGoals();
      userActivityLevels = userProfileData.getActivityLevels();

      for (int i = 0; i < userMedicalConditions.size(); i++) {
        if (userMedicalConditions.get(i).getName() != null) {

          //Initialize Medical Conditions array from the response array
          MedicalCondition MC = new MedicalCondition();
          MC.setMedicalCondition(userMedicalConditions.get(i).getName());
          MC.setId(Integer.parseInt(userMedicalConditions.get(i).getId()));

          if (userMedicalConditions.get(i).getSelected().equals("0")) {
            MC.setSelected(false);
          } else {
            MC.setSelected(true);
          }

          medicalConditions.add(MC);
        }
      }

      medicalConditionIDs.clear();
      String medicalConditionString = "";
      for (int i = 0; i < medicalConditions.size(); i++) {
        try {
          if (userMedicalConditions.get(i).getSelected().equals("0")) {
            medicalConditionsValueTV.setText("None");
          } else {
            medicalConditionString += userMedicalConditions.get(i).getName() + ", ";
            medicalConditionIDs.add(Integer.parseInt(userMedicalConditions.get(i).getId()));
          }
        } catch (IndexOutOfBoundsException ignored) {
        }
      }

      if (medicalConditionString.equals("None")) {
        medicalConditionIDs.add(-1);
      }

      medicalConditionParts = constructMedicalConditionsForRequest(medicalConditionIDs);

      medicalConditionsValueTV.setText(medicalConditionString.replaceAll(", $", ""));

      //Set the activity level
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

      //Set the blood type
      if (userProfileData.getBloodType() != null) {
        prefsEditor.putString(Constants.EXTRAS_USER_PROFILE_BLOOD_TYPE,
            userProfileData.getBloodType());
      }

      //Set the user goals
      for (int i = 0; i < userGoals.size(); i++) {
        if (userGoals.get(i).getSelected().equals("1")) {
          prefsEditor.putString(Constants.EXTRAS_USER_PROFILE_GOAL, userGoals.get(i).getName());
          prefsEditor.putInt(Constants.EXTRAS_USER_PROFILE_GOAL_ID,
              Integer.parseInt(userGoals.get(i).getId()));

          goalsEntryValueTV.setText(userGoals.get(i).getName());
        }
      }

      //Set the mobile number
      if (userProfileData.getPhone_number() != null && !userProfileData.getPhone_number()
          .isEmpty()) {
        prefsEditor.putString(Constants.EXTRAS_USER_PROFILE_PHONE_NUMBER,
            userProfileData.getPhone_number());
      }

      //Set the birthday
      if (userProfileData.getBirthday() != null && !userProfileData.getBirthday().isEmpty()) {
        prefsEditor.putString(Constants.EXTRAS_USER_PROFILE_DATE_OF_BIRTH,
            userProfileData.getBirthday());
      }

      //Set the name
      if (userProfileData.getName() != null && !userProfileData.getName().isEmpty()) {
        prefsEditor.putString(Constants.EXTRAS_USER_PROFILE_USER_FULL_NAME,
            userProfileData.getName());
        userFullNameTV.setText(userProfileData.getName());
      }

      //Set the email
      if (userProfileData.getEmail() != null && !userProfileData.getEmail().isEmpty()) {
        prefsEditor.putString(Constants.EXTRAS_USER_PROFILE_USER_EMAIL, userProfileData.getEmail());
        userEmailTV.setText(userProfileData.getEmail());
      }

      //Set the weight
      if (userProfileData.getWeight() != null && !userProfileData.getWeight().isEmpty()) {
        prefsEditor.putFloat(Constants.EXTRAS_USER_PROFILE_WEIGHT,
            Float.parseFloat(userProfileData.getWeight()));
        weightEntryValueTV.setText(String.valueOf(String.format(Locale.getDefault(), "%.1f",
            Float.parseFloat(userProfileData.getWeight()))));
      }

      //Set the country
      if (userProfileData.getCountry() != null && !userProfileData.getCountry().isEmpty()) {
        prefsEditor.putString(Constants.EXTRAS_USER_PROFILE_NATIONALITY,
            userProfileData.getCountry());
        countryValueTV.setText(userProfileData.getCountry());
      }

      //Set the gender
      if (userProfileData.getGender() != null && !userProfileData.getGender().isEmpty()) {
        int finalGender = userProfileData.getGender().equals("Male") ? 0 : 1;
        prefsEditor.putInt(Constants.EXTRAS_USER_PROFILE_GENDER, finalGender);
      }

      //Set the profile picture
      if (userProfileData.getProfile_picture() != null && !userProfileData.getProfile_picture()
          .equals(Constants.BASE_URL_ADDRESS) && getActivity() != null) {

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
    }
  }

  @Override public void wipeCredentialsOnSignOut() {
    prefs.edit()
        .putString(Constants.PREF_USER_ACCESS_TOKEN, Constants.NO_ACCESS_TOKEN_FOUND_IN_PREFS)
        .apply();
    prefs.edit().putBoolean(Constants.EXTRAS_USER_LOGGED_IN, false).apply();
    prefs.edit().putFloat(Constants.EXTRAS_USER_PROFILE_WEIGHT, 0f).apply();

    prefs.edit().putString(Constants.EXTRAS_USER_EMAIL, "").apply();
    prefs.edit().putString(Constants.EXTRAS_USER_PASSWORD, "").apply();
    prefs.edit().putString(Constants.EXTRAS_INSURANCE_USER_USERNAME, "").apply();
    prefs.edit().putString(Constants.EXTRAS_INSURANCE_USER_PASSWORD, "").apply();
    prefs.edit().putString(Constants.EXTRAS_INSURANCE_CONTRACT_NUMBER, "").apply();
    prefs.edit().putString(Constants.EXTRAS_INSURANCE_COUNTRY_NAME, "").apply();
    prefs.edit().putString(Constants.EXTRAS_INSURANCE_COUNTRY_CRM_CODE, "").apply();
    prefs.edit().putString(Constants.EXTRAS_INSURANCE_COUNTRY_ISO_CODE, "").apply();

    if (!prefs.getString(Constants.EXTRAS_USER_FACEBOOK_TOKEN, "-1").equals("-1")) {
      LoginManager.getInstance().logOut();
      prefs.edit().putString(Constants.EXTRAS_USER_FACEBOOK_TOKEN, "-1").apply();
    }

    getActivity().finish();

    Intent intent = new Intent(getActivity(), LoginActivity.class);
    startActivity(intent);
  }

  @Override public void openMetaTextsActivity(String metaContents, String section) {
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

    intent.putExtra(Constants.EXTRAS_META_HTML_CONTENT, metaContents);
    startActivity(intent);
  }

  @Override public void updateProfilePicture() {
    if (profilePictureChanged) {
      updateUserPicture();
    } else {
      Toast.makeText(getActivity(), "Your profile was updated successfully", Toast.LENGTH_SHORT)
          .show();
    }

    InputMethodManager imm =
        (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
    imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
  }

  @Override public void displayPictureChangeSuccessful() {
    Toast.makeText(getActivity(), "Your profile was updated successfully", Toast.LENGTH_SHORT)
        .show();

    profilePictureChanged = false;
  }

  @Override public void openShareFileDialog() {
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

  @Override
  public void displayUserAchievements(List<AchievementsResponseBody> achievementsResponseBodies) {
    AchievementsRecyclerAdapter userActivitiesListRecyclerAdapter =
        new AchievementsRecyclerAdapter(getActivity(), achievementsResponseBodies);
    achievementsRecycler.setLayoutManager(
        new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
    achievementsRecycler.setAdapter(userActivitiesListRecyclerAdapter);

    calculateRemainingAchievements(achievementsResponseBodies);
  }

  @Subscribe public void handleSuccessfulProfileUpdate(ProfileUpdatedEvent event) {
    presenter.getUserProfile();
  }

  @OnClick(R.id.userProfileIV) public void handleUserProfilePressed() {
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

  @OnClick(R.id.weightLayout) public void handleWeightLayoutPressed() {
    final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
    dialogBuilder.setTitle(R.string.profile_edit_weight_dialog_title);

    View dialogView =
        LayoutInflater.from(getActivity()).inflate(R.layout.profile_edit_weight_dialog, null);
    final EditText editWeightET = dialogView.findViewById(R.id.dialogWeightET);

    editWeightET.setText(String.valueOf(prefs.getFloat(Constants.EXTRAS_USER_PROFILE_WEIGHT, 0)));
    editWeightET.setSelection(editWeightET.getText().toString().length());

    dialogBuilder.setView(dialogView);
    dialogBuilder.setPositiveButton(R.string.ok, (dialogInterface, i) -> {
      if (!editWeightET.getText().toString().equals(".")) {

        newUserWeight = Double.parseDouble(editWeightET.getText().toString());

        if (newUserWeight == 0) {
          Toast.makeText(getActivity(), getString(R.string.weight_must_not_be_zero),
              Toast.LENGTH_SHORT).show();
        } else {
          weightEntryValueTV.setText(
              String.valueOf(String.format(Locale.getDefault(), "%.1f", newUserWeight)));

          prefs.edit()
              .putFloat(Constants.EXTRAS_USER_PROFILE_WEIGHT, (float) newUserWeight)
              .apply();

          updateUserProfile();
        }
      }
    });
    dialogBuilder.setNegativeButton(R.string.decline_cancel,
        (dialogInterface, i) -> dialogInterface.dismiss());

    AlertDialog alertDialog = dialogBuilder.create();
    alertDialog.show();
  }

  @OnClick(R.id.goalsLayout) public void handleGoalsLayoutPressed() {
    final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
    dialogBuilder.setTitle(R.string.profile_edit_goal_dialog_title);

    View dialogView =
        LayoutInflater.from(getActivity()).inflate(R.layout.profile_edit_goal_dialog, null);

    final RadioGroup goalRadioButtonsGroup = dialogView.findViewById(R.id.goalRadioButtonsGroup);

    for (int i = 0; i < userGoals.size(); i++) {
      View listItemRadioButton =
          getActivity().getLayoutInflater().inflate(R.layout.list_item_edit_goal_dialog, null);

      final RadioButton radioButtonItem = listItemRadioButton.findViewById(R.id.editGoalRadioBTN);
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
    dialogBuilder.setPositiveButton(R.string.ok, (dialogInterface, i) -> {
      RadioButton selectedRadioButton =
          goalRadioButtonsGroup.findViewById(goalRadioButtonsGroup.getCheckedRadioButtonId());

      int newUserGoalId = selectedRadioButton.getId();
      String newUserGoalText = selectedRadioButton.getText().toString();

      goalsEntryValueTV.setText(newUserGoalText);

      prefs.edit().putInt(Constants.EXTRAS_USER_PROFILE_GOAL_ID, newUserGoalId).apply();
      prefs.edit().putString(Constants.EXTRAS_USER_PROFILE_GOAL, newUserGoalText).apply();

      updateUserProfile();
    });
    dialogBuilder.setNegativeButton(R.string.decline_cancel,
        (dialogInterface, i) -> dialogInterface.dismiss());

    AlertDialog alertDialog = dialogBuilder.create();
    alertDialog.show();
  }

  @OnClick(R.id.activityLevelsLayout) public void handleActivityLevelsLayoutPressed() {
    final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
    dialogBuilder.setTitle(R.string.profile_edit_activity_levels_dialog_title);

    View dialogView = LayoutInflater.from(getActivity())
        .inflate(R.layout.profile_edit_radio_options_layout, null);

    final RadioGroupPlus activityLevelRadioGRP =
        dialogView.findViewById(R.id.collectionsRadioGroup);

    for (int i = 0; i < userActivityLevels.size(); i++) {
      View listItemRadioButton = getActivity().getLayoutInflater()
          .inflate(R.layout.list_item_edit_radio_options_dialog, null);

      final RadioButton radioButtonItem =
          listItemRadioButton.findViewById(R.id.editRadioOptionsRadioBTN);
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
    dialogBuilder.setPositiveButton(R.string.ok, (dialogInterface, position) -> {
      RadioButton selectedRadioButton =
          activityLevelRadioGRP.findViewById(activityLevelRadioGRP.getCheckedRadioButtonId());

      if (selectedRadioButton != null) {
        activityLevelsEntryValueTV.setText(selectedRadioButton.getText().toString());

        prefs.edit()
            .putString(Constants.EXTRAS_USER_PROFILE_ACTIVITY_LEVEL,
                selectedRadioButton.getText().toString())
            .apply();
        prefs.edit()
            .putInt(Constants.EXTRAS_USER_PROFILE_ACTIVITY_LEVEL_ID, selectedRadioButton.getId())
            .apply();

        updateUserProfile();
      } else {
        Log.d("TAG", "onClick: Selected Radio Button was null!");
      }
    });

    dialogBuilder.setNegativeButton(R.string.decline_cancel,
        (dialogInterface, i) -> dialogInterface.dismiss());

    AlertDialog alertDialog = dialogBuilder.create();
    alertDialog.show();
  }

  @OnClick(R.id.medicalConditionsLayout) public void handleMedicalConditionsLayoutPressed() {
    Intent intent = new Intent(getActivity(), MedicalConditionsChoiceActivity.class);
    intent.putExtra("MEDICAL_CONDITIONS", medicalConditions);
    startActivityForResult(intent, MEDICAL_CONDITIONS_SELECTED);
  }

  @OnClick(R.id.countryLayout) public void handleCountryLayoutPressed() {
    final CountryPicker picker = CountryPicker.newInstance(getString(R.string.choose_country_hint));

    picker.show(getActivity().getSupportFragmentManager(), "COUNTRY_PICKER");
    picker.setListener((countryName, code, dialCode, flagDrawableResID) -> {
      countryValueTV.setText(countryName);

      prefs.edit().putString(Constants.EXTRAS_USER_PROFILE_NATIONALITY, countryName).apply();

      picker.dismiss();

      updateUserProfile();
    });
  }

  @OnClick(R.id.shareEmergencyProfileBTN) public void handleShareEmergencyProfilePressed() {
    if (Helpers.isInternetAvailable(getActivity())) {

      boolean hasPermission = (ContextCompat.checkSelfPermission(getActivity(),
          Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
      if (!hasPermission) {
        requestPermissions(new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE },
            REQUEST_WRITE_STORAGE);
      } else {
        presenter.requestEmergencyProfile();
      }
    } else {
      Helpers.showNoInternetDialog(getActivity());
    }
  }

  @OnClick(R.id.shareAppLayout) public void handleShareAppLayoutPressed() {
    try {
      Intent i = new Intent(Intent.ACTION_SEND);
      i.setType("text/plain");
      i.putExtra(Intent.EXTRA_SUBJECT, "GMFit");
      String sAux = "\nCheck out GlobeMed's new application!\n\n";
      sAux = sAux + "https://play.google.com/store/apps/details?id=com.mcsaatchi.gmfit";
      i.putExtra(Intent.EXTRA_TEXT, sAux);
      startActivity(Intent.createChooser(i, "Choose sharing method"));
    } catch (Exception e) {
      //e.toString();
    }
  }

  @OnClick(R.id.logoutBTN) public void handleLogoutButtonPressed() {
    if (Helpers.isInternetAvailable(getActivity())) {
      presenter.signOutUser();
    } else {
      Helpers.showNoInternetDialog(getActivity());
    }
  }

  @OnClick(R.id.appRemindersLayout) public void handleAppRemindersLayoutPressed() {
    Intent intent = new Intent(getActivity(), MealRemindersActivity.class);
    startActivity(intent);
  }

  @OnClick(R.id.termsConditionsLayout) public void handleTermsConditionsLayoutPressed() {
    presenter.getMetaTexts("terms");
  }

  @OnClick(R.id.privacyLayout) public void handlePrivacyLayoutPressed() {
    presenter.getMetaTexts("privacy");
  }

  @OnClick(R.id.contactUsLayout) public void handleContactUsLayoutPressed() {
    Intent intent = new Intent(getActivity(), ContactUsListActivity.class);
    startActivity(intent);
  }

  @OnClick(R.id.changePasswordLayout) public void handleChangePasswordLayoutPressed() {
    Intent intent = new Intent(getActivity(), ChangePasswordActivity.class);
    startActivity(intent);
  }

  @OnClick(R.id.editProfileLayout) public void handleEditProfileLayoutPressed() {
    Intent intent = new Intent(getActivity(), EditProfileActivity.class);
    intent.putExtra("MEDICAL_CONDITIONS", medicalConditions);
    startActivity(intent);
  }

  private void setProfilePicture(String finalImagePath) {
    String currentImagePath = prefs.getString(Constants.EXTRAS_USER_PROFILE_IMAGE, "");

    //If the current picture is different than the one the user just took, change it
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

  private void openTakePictureIntent() {
    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {

      photoFile = null;
      try {
        photoFile = createImageFile(constructImageFilename());
        FacebookSdk.sdkInitialize(getActivity());
        photoFileUri = FileProvider.getUriForFile(getActivity(),
            getApplicationContext().getPackageName() + ".provider", photoFile);
      } catch (IOException ex) {
        ex.printStackTrace();
      }

      if (photoFile != null) {
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoFileUri);
        startActivityForResult(takePictureIntent, CAPTURE_NEW_PICTURE_REQUEST_CODE);
      }
    }
  }

  private String getPhotoPathFromGallery(Uri uri) {
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

    builderSingle.setNegativeButton(R.string.decline_cancel, (dialog, which) -> dialog.dismiss());

    builderSingle.setAdapter(arrayAdapter, (dialog, which) -> {
      String strName = arrayAdapter.getItem(which);
      if (strName != null) {
        switch (strName) {
          case "Choose from gallery":
            Intent galleryIntent =
                new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(galleryIntent, REQUEST_PICK_IMAGE_GALLERY);
            break;
          case "Take a new picture":
            openTakePictureIntent();
            break;
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

  private void updateUserProfile() {
    String dateOfBirth = prefs.getString(Constants.EXTRAS_USER_PROFILE_DATE_OF_BIRTH, "1990-01-01");
    String bloodType = prefs.getString(Constants.EXTRAS_USER_PROFILE_BLOOD_TYPE, "A+");
    String nationality = prefs.getString(Constants.EXTRAS_USER_PROFILE_NATIONALITY, "");
    String measurementSystem =
        prefs.getString(Constants.EXTRAS_USER_PROFILE_MEASUREMENT_SYSTEM, "metric");
    int userGoalId = prefs.getInt(Constants.EXTRAS_USER_PROFILE_GOAL_ID, 0);
    int activityLevelId = prefs.getInt(Constants.EXTRAS_USER_PROFILE_ACTIVITY_LEVEL_ID, 2);
    int gender = prefs.getInt(Constants.EXTRAS_USER_PROFILE_GENDER, 1);
    float height = prefs.getFloat(Constants.EXTRAS_USER_PROFILE_HEIGHT, 180);
    float weight = prefs.getFloat(Constants.EXTRAS_USER_PROFILE_WEIGHT, 82);

    presenter.updateUserProfile(Helpers.toRequestBody(dateOfBirth),
        Helpers.toRequestBody(bloodType), Helpers.toRequestBody(nationality), medicalConditionParts,
        Helpers.toRequestBody(measurementSystem.toLowerCase()),
        Helpers.toRequestBody(String.valueOf(userGoalId)),
        Helpers.toRequestBody(String.valueOf(activityLevelId)),
        Helpers.toRequestBody(String.valueOf(gender)),
        Helpers.toRequestBody(String.valueOf(height)),
        Helpers.toRequestBody(String.valueOf(weight)), Helpers.toRequestBody("1"),
        profilePictureChanged);
  }

  private void updateUserPicture() {
    HashMap<String, RequestBody> profilePictureParts = new HashMap<>();

    String userPicturePath = prefs.getString(Constants.EXTRAS_USER_PROFILE_IMAGE, "");

    File imageFile = new File(userPicturePath);

    RequestBody imageFilePart = RequestBody.create(MediaType.parse("image/jpeg"), imageFile);

    profilePictureParts.put("picture\"; filename=\"" + userPicturePath + ".jpg", imageFilePart);

    presenter.updateUserPicture(profilePictureParts);
  }

  private HashMap<String, RequestBody> constructMedicalConditionsForRequest(
      ArrayList<Integer> medicalConditionIDs) {
    HashMap<String, RequestBody> medicalConditionParts = new HashMap<>();

    for (int i = 0; i < medicalConditionIDs.size(); i++) {
      medicalConditionParts.put("medical_conditions[" + i + "]",
          Helpers.toRequestBody(String.valueOf(medicalConditionIDs.get(i))));
    }

    return medicalConditionParts;
  }

  private void calculateRemainingAchievements(
      List<AchievementsResponseBody> achievementsResponseBodyList) {

    int achievementsDone = 0;

    for (int i = 0; i < achievementsResponseBodyList.size(); i++) {
      if (achievementsResponseBodyList.get(i).getIsDone()) {
        achievementsDone++;
      }
    }

    achievementsInfoTV.setText(achievementsDone + " out of " + achievementsResponseBodyList.size());
  }
}
