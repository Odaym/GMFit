package com.mcsaatchi.gmfit.fitness.activities;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.otto.EventBusSingleton;
import com.mcsaatchi.gmfit.architecture.otto.FitnessActivityEvent;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.ActivitiesListResponseBody;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.ActivitiesListResponseDatum;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.UserActivitiesResponseBody;
import com.mcsaatchi.gmfit.common.activities.BaseActivity;
import com.mcsaatchi.gmfit.common.classes.FontTextView;
import com.mcsaatchi.gmfit.common.classes.Helpers;
import com.mcsaatchi.gmfit.common.classes.SimpleDividerItemDecoration;
import com.mcsaatchi.gmfit.fitness.adapters.ActivityLevelsChoiceRecyclerAdapter;
import com.mcsaatchi.gmfit.health.models.SelectionItem;
import com.mcsaatchi.gmfit.insurance.widget.CustomDatePickerFitnessActivity;
import com.squareup.picasso.Picasso;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AddActivityDetailsActivity extends BaseActivity
    implements AddActivityDetailsActivityPresenter.AddActivityDetailsActivityView {
  @Bind(R.id.toolbar) Toolbar toolbar;
  @Bind(R.id.activityPictureIV) ImageView activityPictureIV;
  @Bind(R.id.activityLevelsRecycler) RecyclerView activityLevelsRecycler;
  @Bind(R.id.timeSpentActivityET) EditText timeSpentActivityET;
  @Bind(R.id.activityTimeTV) TextView activityTimeTV;
  @Bind(R.id.datePicker) CustomDatePickerFitnessActivity datePicker;
  @Bind(R.id.caloriesValueTV) FontTextView caloriesValueTV;
  @Bind(R.id.deleteActivityLayout) LinearLayout deleteActivityLayout;
  @Bind(R.id.addActivityBTN) Button addActivityBTN;

  private AddActivityDetailsActivityPresenter presenter;
  private ArrayList<SelectionItem> activityLevelChoices;
  private String activityDate = null;
  private boolean call_purpose_edit;
  private int activityLevelID = -1;
  private int activityInstanceID;

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_add_activity_details);

    ButterKnife.bind(this);

    presenter = new AddActivityDetailsActivityPresenter(this, dataAccessHandler);

    if (getIntent().getExtras() != null) {
      call_purpose_edit = getIntent().getExtras().getBoolean("CALL_PURPOSE_EDIT");

      if (call_purpose_edit) {
        deleteActivityLayout.setVisibility(View.VISIBLE);
        addActivityBTN.setVisibility(View.INVISIBLE);

        UserActivitiesResponseBody userActivitiesResponseBody =
            getIntent().getExtras().getParcelable("ACTIVITY_ITEM");

        if (userActivitiesResponseBody != null) {
          activityLevelID = userActivitiesResponseBody.getActivityLevelId();
          activityInstanceID = userActivitiesResponseBody.getInstanceId();

          setupToolbar(getClass().getSimpleName(), toolbar,
              userActivitiesResponseBody.getActivityName(), true);

          setupDatePicker();

          activityDate = userActivitiesResponseBody.getDate().split(" ")[0];

          datePicker.setSelectedItem(activityDate);

          timeSpentActivityET.setText(userActivitiesResponseBody.getDuration());
          timeSpentActivityET.setSelection(timeSpentActivityET.getText().toString().length());

          caloriesValueTV.setText(String.format("%.2f", userActivitiesResponseBody.getCalories()));

          presenter.getAllActivities(userActivitiesResponseBody.getActivityId());
        }
      } else {
        ActivitiesListResponseBody activitiesListResponseBody =
            getIntent().getExtras().getParcelable("ACTIVITY_ITEM");

        setupToolbar(getClass().getSimpleName(), toolbar, activitiesListResponseBody.getName(),
            true);

        Picasso.with(this)
            .load(activitiesListResponseBody.getIcon())
            .resize(200, 200)
            .into(activityPictureIV);

        setupDatePicker();

        timeSpentActivityET.setSelection(timeSpentActivityET.getText().toString().length());

        /**
         * Set calories value to Rate of activity * minutes spent
         */
        //caloriesValueTV.setText(Integer.parseInt(timeSpentActivityET.getText().toString()) * activitiesListResponseBody.get)

        activityLevelChoices =
            setupActivityLevelChoices(activitiesListResponseBody.getActivityLevels());

        setupActivityLevelsList(activityLevelChoices, activityLevelID);
      }
    }
  }

  @Override public void finishAndLoadActivities() {
    EventBusSingleton.getInstance().post(new FitnessActivityEvent());
    finish();
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.add_user_activity, menu);

    return super.onCreateOptionsMenu(menu);
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.doneBTN:
        for (SelectionItem activityLevelChoice : activityLevelChoices) {
          if (activityLevelChoice.isItemSelected()) {
            activityLevelID = activityLevelChoice.getId();
          }
        }

        gatherInfoAndSubmitActivity(true);
        break;
    }

    return super.onOptionsItemSelected(item);
  }

  @OnClick(R.id.deleteActivityLayout) public void handleDeleteActivityPressed() {
    presenter.deleteUserActivity(activityInstanceID);
  }

  @OnClick(R.id.addActivityBTN) public void handleAddActivityPressed() {
    ArrayList<String> errorMessages = new ArrayList<>();

    for (SelectionItem activityLevelChoice : activityLevelChoices) {
      if (activityLevelChoice.isItemSelected()) {
        activityLevelID = activityLevelChoice.getId();
      }
    }

    if (activityLevelID == -1) {
      errorMessages.add("Activity Level is required.");
    }
    if (timeSpentActivityET.getText().toString().isEmpty()) {
      errorMessages.add("Time Spent on actity is required.");
    }
    if (activityDate == null) {
      errorMessages.add("Activity Date is required");
    }

    if (!errorMessages.isEmpty()) {
      String finalErrorMessage = "";

      for (String errorMessage : errorMessages) {
        finalErrorMessage += errorMessage + "\n\n";
      }

      final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
      alertDialog.setTitle(R.string.required_fields_dialog_title);
      alertDialog.setMessage(finalErrorMessage);
      alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getResources().getString(R.string.ok),
          (dialog, which) -> dialog.dismiss());
      alertDialog.show();
    } else {
      gatherInfoAndSubmitActivity(false);
    }
  }

  @Override
  public void displayActivityLevels(ActivitiesListResponseBody activitiesListResponseBody) {
    activityLevelChoices =
        setupActivityLevelChoices(activitiesListResponseBody.getActivityLevels());

    setupActivityLevelsList(activityLevelChoices, activityLevelID);
  }

  @Override public void displayActivityIcon(String icon) {
    Picasso.with(this).load(icon).resize(200, 200).into(activityPictureIV);
  }

  private void gatherInfoAndSubmitActivity(boolean editingActivity) {
    if (editingActivity) {
      presenter.editFitnessActivity(String.valueOf(activityInstanceID),
          timeSpentActivityET.getText().toString(), activityDate, String.valueOf(activityLevelID));
    } else {
      presenter.addFitnessActivity(String.valueOf(activityLevelID),
          timeSpentActivityET.getText().toString(), Helpers.getCalendarDate());
    }
  }

  private void setupDatePicker() {
    datePicker.setUpDatePicker("Activity Date", "Choose a date", (year, month, dayOfMonth) -> {
      Calendar calendar = Calendar.getInstance();
      calendar.set(Calendar.YEAR, year);
      calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
      calendar.set(Calendar.MONTH, month);

      Date d = new Date(calendar.getTimeInMillis());

      SimpleDateFormat dateFormatter = new SimpleDateFormat("dd MMM, yyyy", Locale.getDefault());
      activityDate = dateFormatter.format(d);
      datePicker.setSelectedItem(activityDate);
    });
  }

  private ArrayList<SelectionItem> setupActivityLevelChoices(
      List<ActivitiesListResponseDatum> activityLevels) {
    ArrayList<SelectionItem> activityLevelChoices = new ArrayList<>();

    for (int i = 0; i < activityLevels.size(); i++) {
      SelectionItem item = new SelectionItem(activityLevels.get(i).getName(), false);
      item.setId(activityLevels.get(i).getId());
      activityLevelChoices.add(item);
    }

    return activityLevelChoices;
  }

  private void setupActivityLevelsList(ArrayList<SelectionItem> activityLevelsToSetup,
      int activityLevelID) {
    ActivityLevelsChoiceRecyclerAdapter activityLevelsChoiceRecyclerAdapter =
        new ActivityLevelsChoiceRecyclerAdapter(activityLevelsToSetup, activityLevelID);
    activityLevelsRecycler.setLayoutManager(new LinearLayoutManager(this));
    activityLevelsRecycler.addItemDecoration(new SimpleDividerItemDecoration(this));
    activityLevelsRecycler.setAdapter(activityLevelsChoiceRecyclerAdapter);
  }
}
