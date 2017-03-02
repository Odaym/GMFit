package com.mcsaatchi.gmfit.onboarding.fragments;

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
import butterknife.Bind;
import butterknife.ButterKnife;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.GMFitApplication;
import com.mcsaatchi.gmfit.architecture.data_access.DataAccessHandler;
import com.mcsaatchi.gmfit.architecture.rest.UserGoalsResponse;
import com.mcsaatchi.gmfit.architecture.rest.UserGoalsResponseBody;
import com.mcsaatchi.gmfit.common.Constants;
import java.util.List;
import javax.inject.Inject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;
import worker8.com.github.radiogroupplus.RadioGroupPlus;

public class SetupProfile2Fragment extends Fragment {

  @Inject DataAccessHandler dataAccessHandler;
  @Inject SharedPreferences prefs;

  @Bind(R.id.goalRadioButtonsGroup) RadioGroupPlus goalRadioButtonsGroup;

  private boolean dataWasSelected = false;

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {

    View fragmentView = inflater.inflate(R.layout.fragment_setup_profile_2, container, false);

    ButterKnife.bind(this, fragmentView);
    ((GMFitApplication) getActivity().getApplication()).getAppComponent().inject(this);

    getUserGoals();

    return fragmentView;
  }

  private void getUserGoals() {
    final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
    alertDialog.setTitle(R.string.fetching_user_goals);
    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.ok),
        new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
          }
        });

    dataAccessHandler.getUserGoals(new Callback<UserGoalsResponse>() {
      @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP) @Override
      public void onResponse(Call<UserGoalsResponse> call, Response<UserGoalsResponse> response) {
        switch (response.code()) {
          case 200:

            List<UserGoalsResponseBody> activityLevels = response.body().getData().getBody();

            for (int i = 0; i < activityLevels.size(); i++) {
              View listItemRadioButton = getActivity().getLayoutInflater()
                  .inflate(R.layout.user_goals_list_item_radio_button_row, null);

              final RadioButton radioButtonItem =
                  (RadioButton) listItemRadioButton.findViewById(R.id.goalRadioButtonItem);
              radioButtonItem.setText(activityLevels.get(i).getName());
              radioButtonItem.setId(activityLevels.get(i).getId());

              radioButtonItem.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View view) {
                  dataWasSelected = true;

                  prefs.edit()
                      .putInt(Constants.EXTRAS_USER_PROFILE_GOAL_ID, radioButtonItem.getId())
                      .apply();
                  prefs.edit()
                      .putString(Constants.EXTRAS_USER_PROFILE_GOAL,
                          radioButtonItem.getText().toString())
                      .apply();
                }
              });

              goalRadioButtonsGroup.addView(listItemRadioButton);
            }

            break;
        }
      }

      @Override public void onFailure(Call<UserGoalsResponse> call, Throwable t) {
        Timber.d("Call failed with error : %s", t.getMessage());
        alertDialog.setMessage(
            getActivity().getResources().getString(R.string.server_error_got_returned));
        alertDialog.show();
      }
    });
  }

  public boolean wasDataSelected() {
    return dataWasSelected;
  }
}
