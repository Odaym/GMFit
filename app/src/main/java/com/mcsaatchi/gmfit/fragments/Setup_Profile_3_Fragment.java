package com.mcsaatchi.gmfit.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.classes.Constants;
import com.mcsaatchi.gmfit.data_access.DataAccessHandler;
import com.mcsaatchi.gmfit.rest.ActivityLevelsResponse;
import com.mcsaatchi.gmfit.rest.ActivityLevelsResponseBody;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import worker8.com.github.radiogroupplus.RadioGroupPlus;

public class Setup_Profile_3_Fragment extends Fragment {

  @Bind(R.id.activityLevelsRadioButtonsGroup) RadioGroupPlus activityLevelsRadioButtonsGroup;
  private SharedPreferences prefs;

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {

    View fragmentView = inflater.inflate(R.layout.fragment_setup_profile_3, container, false);

    ButterKnife.bind(this, fragmentView);

    prefs = getActivity().getSharedPreferences(Constants.SHARED_PREFS_TITLE, Context.MODE_PRIVATE);

    getActivityLevels();

    return fragmentView;
  }

  private void getActivityLevels() {
    final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
    alertDialog.setTitle(R.string.fetching_activity_levels_dialog_title);
    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.ok),
        new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
          }
        });

    DataAccessHandler.getInstance()
        .getActivityLevels(prefs.getString(Constants.PREF_USER_ACCESS_TOKEN,
            Constants.NO_ACCESS_TOKEN_FOUND_IN_PREFS), new Callback<ActivityLevelsResponse>() {
          @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP) @Override
          public void onResponse(Call<ActivityLevelsResponse> call,
              Response<ActivityLevelsResponse> response) {
            switch (response.code()) {
              case 200:

                List<ActivityLevelsResponseBody> activityLevels =
                    response.body().getData().getBody();

                for (int i = 0; i < activityLevels.size(); i++) {
                  View listItemRadioButton = getActivity().getLayoutInflater()
                      .inflate(R.layout.activity_levels_list_item_radio_button_row, null);

                  final RadioButton radioButtonItem =
                      (RadioButton) listItemRadioButton.findViewById(
                          R.id.activityLevelRadioButtonItem);
                  radioButtonItem.setText(activityLevels.get(i).getName());
                  radioButtonItem.setId(activityLevels.get(i).getId());

                  TextView radioButtonHintTV =
                      (TextView) listItemRadioButton.findViewById(R.id.radioButtonHintTV);
                  radioButtonHintTV.setText(activityLevels.get(i).getDescription());

                  radioButtonItem.setOnClickListener(new View.OnClickListener() {
                    @Override public void onClick(View view) {
                      prefs.edit()
                          .putInt(Constants.EXTRAS_USER_PROFILE_ACTIVITY_LEVEL_ID,
                              radioButtonItem.getId())
                          .apply();
                      prefs.edit()
                          .putString(Constants.EXTRAS_USER_PROFILE_ACTIVITY_LEVEL,
                              radioButtonItem.getText().toString())
                          .apply();
                    }
                  });

                  activityLevelsRadioButtonsGroup.addView(listItemRadioButton);
                }

                break;
            }
          }

          @Override public void onFailure(Call<ActivityLevelsResponse> call, Throwable t) {
            alertDialog.setMessage(getString(R.string.error_response_from_server_incorrect));
            alertDialog.show();
          }
        });
  }
}
