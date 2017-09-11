package com.mcsaatchi.gmfit.onboarding.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.classes.GMFitApplication;
import com.mcsaatchi.gmfit.architecture.retrofit.architecture.DataAccessHandlerImpl;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.ActivityLevelsResponseBody;
import com.mcsaatchi.gmfit.common.Constants;
import com.mcsaatchi.gmfit.common.fragments.BaseFragment;
import com.mcsaatchi.gmfit.onboarding.activities.SetupProfileFragmentsPresenter;
import java.util.List;
import javax.inject.Inject;
import worker8.com.github.radiogroupplus.RadioGroupPlus;

public class SetupProfile3Fragment extends BaseFragment
    implements SetupProfileFragmentsPresenter.SetupProfileFragmentsView_3 {

  @Bind(R.id.activityLevelsRadioButtonsGroup) RadioGroupPlus activityLevelsRadioButtonsGroup;

  @Inject DataAccessHandlerImpl dataAccessHandler;
  @Inject SharedPreferences prefs;

  private boolean dataWasSelected = false;

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {

    View fragmentView = inflater.inflate(R.layout.fragment_setup_profile_3, container, false);

    ButterKnife.bind(this, fragmentView);
    ((GMFitApplication) getActivity().getApplication()).getAppComponent().inject(this);

    SetupProfileFragmentsPresenter presenter =
        new SetupProfileFragmentsPresenter(this, dataAccessHandler);

    presenter.getActivityLevels();

    return fragmentView;
  }

  public boolean wasDataSelected() {
    return dataWasSelected;
  }

  @Override public void populateActivityLevels(List<ActivityLevelsResponseBody> activityLevels) {
    for (int i = 0; i < activityLevels.size(); i++) {
      View listItemRadioButton = getActivity().getLayoutInflater()
          .inflate(R.layout.activity_levels_list_item_radio_button_row, null);

      final RadioButton radioButtonItem =
          listItemRadioButton.findViewById(R.id.activityLevelRadioButtonItem);
      radioButtonItem.setText(activityLevels.get(i).getName());
      radioButtonItem.setId(activityLevels.get(i).getId());

      TextView radioButtonHintTV = listItemRadioButton.findViewById(R.id.radioButtonHintTV);
      radioButtonHintTV.setText(activityLevels.get(i).getDescription());

      radioButtonItem.setOnClickListener(view -> {
        dataWasSelected = true;

        prefs.edit()
            .putInt(Constants.EXTRAS_USER_PROFILE_ACTIVITY_LEVEL_ID, radioButtonItem.getId())
            .apply();
        prefs.edit()
            .putString(Constants.EXTRAS_USER_PROFILE_ACTIVITY_LEVEL,
                radioButtonItem.getText().toString())
            .apply();
      });

      activityLevelsRadioButtonsGroup.addView(listItemRadioButton);
    }
  }
}
