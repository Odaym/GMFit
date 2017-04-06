package com.mcsaatchi.gmfit.onboarding.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.GMFitApplication;
import com.mcsaatchi.gmfit.architecture.data_access.DataAccessHandler;
import com.mcsaatchi.gmfit.architecture.rest.UserGoalsResponseBody;
import com.mcsaatchi.gmfit.common.Constants;
import com.mcsaatchi.gmfit.common.fragments.BaseFragment;
import com.mcsaatchi.gmfit.onboarding.activities.SetupProfileFragmentsPresenter;
import java.util.List;
import javax.inject.Inject;
import worker8.com.github.radiogroupplus.RadioGroupPlus;

public class SetupProfile2Fragment extends BaseFragment
    implements SetupProfileFragmentsPresenter.SetupProfileFragmentsView_2 {

  @Inject DataAccessHandler dataAccessHandler;
  @Inject SharedPreferences prefs;

  @Bind(R.id.goalRadioButtonsGroup) RadioGroupPlus goalRadioButtonsGroup;

  private boolean dataWasSelected = false;

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {

    View fragmentView = inflater.inflate(R.layout.fragment_setup_profile_2, container, false);

    ButterKnife.bind(this, fragmentView);
    ((GMFitApplication) getActivity().getApplication()).getAppComponent().inject(this);

    SetupProfileFragmentsPresenter presenter =
        new SetupProfileFragmentsPresenter(this, dataAccessHandler);

    presenter.getUserGoals();

    return fragmentView;
  }

  public boolean wasDataSelected() {
    return dataWasSelected;
  }

  @Override public void populateUserGoals(List<UserGoalsResponseBody> userGoals) {
    for (int i = 0; i < userGoals.size(); i++) {
      View listItemRadioButton = getActivity().getLayoutInflater()
          .inflate(R.layout.user_goals_list_item_radio_button_row, null);

      final RadioButton radioButtonItem =
          (RadioButton) listItemRadioButton.findViewById(R.id.goalRadioButtonItem);
      radioButtonItem.setText(userGoals.get(i).getName());
      radioButtonItem.setId(userGoals.get(i).getId());

      radioButtonItem.setOnClickListener(view -> {
        dataWasSelected = true;

        prefs.edit().putInt(Constants.EXTRAS_USER_PROFILE_GOAL_ID, radioButtonItem.getId()).apply();
        prefs.edit()
            .putString(Constants.EXTRAS_USER_PROFILE_GOAL, radioButtonItem.getText().toString())
            .apply();
      });

      goalRadioButtonsGroup.addView(listItemRadioButton);
    }
  }
}