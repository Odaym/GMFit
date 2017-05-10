package com.mcsaatchi.gmfit.insurance.activities.reimbursement;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.common.activities.BaseActivity;
import com.mcsaatchi.gmfit.common.classes.SimpleDividerItemDecoration;
import com.mcsaatchi.gmfit.insurance.adapters.FilterChoiceRecyclerAdapter;
import com.mcsaatchi.gmfit.insurance.models.FilterChoice;
import java.util.ArrayList;
import java.util.Calendar;

import static com.mcsaatchi.gmfit.insurance.activities.approval_request.ApprovalRequestsTrackActivity.SEARCH_CRITERIA_SELECTED;

public class ReimbursementSearchFilterActivity extends BaseActivity {

  @Bind(R.id.toolbar) Toolbar toolbar;
  @Bind(R.id.statusFilterRecycler) RecyclerView statusFilterRecycler;
  @Bind(R.id.yearFilterRecycler) RecyclerView yearFilterRecyclerView;

  private ArrayList<FilterChoice> statuses, years;

  private FilterChoiceRecyclerAdapter filterChoiceRecyclerAdapter;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_reimbursement_search_filter);

    ButterKnife.bind(this);

    setupToolbar(getClass().getSimpleName(), toolbar,
        getString(R.string.search_filters_activity_title), true);

    statuses = getStatusChoices();
    years = getYearsChoices();

    setupYearsRecycler(years);
    setupStatusesRecycler(statuses);
  }

  @OnClick(R.id.applySearchFiltersBTN) public void handleApplySearchFilter() {
    ArrayList<String> finalStatuses = new ArrayList<>();
    ArrayList<String> finalYears = new ArrayList<>();

    for (int i = 0; i < statuses.size(); i++) {
      if (statuses.get(i).isSelected() && statuses.get(i).getName().equals("All Statuses")) {
        finalStatuses.clear();

        for (int i1 = 0; i1 < statuses.size(); i1++) {
          if (!statuses.get(i1).getName().equals("All Statuses")) {
            finalStatuses.add(statuses.get(i1).getName());
          }
        }
      } else if (statuses.get(i).isSelected()) {
        finalStatuses.add(statuses.get(i).getName());
      }
    }

    for (int i = 0; i < years.size(); i++) {
      if (years.get(i).isSelected() && years.get(i).getName().equals("All Years")) {
        finalYears.clear();

        for (int i1 = 0; i1 < years.size(); i1++) {
          if (!years.get(i1).getName().equals("All Years")) {
            finalYears.add(years.get(i1).getName());
          }
        }
      } else if (years.get(i).isSelected()) {
        finalYears.add(years.get(i).getName());
      }
    }

    Intent intent = new Intent();
    intent.putExtra("STATUSES_SEARCH_CRITERIA", finalStatuses);
    intent.putExtra("YEAR_SEARCH_CRITERIA", finalYears);
    setResult(SEARCH_CRITERIA_SELECTED, intent);
    finish();
  }

  private ArrayList<FilterChoice> getStatusChoices() {
    ArrayList<FilterChoice> choicesToReturn = new ArrayList<>();
    choicesToReturn.add(new FilterChoice("All Statuses", false));
    choicesToReturn.add(new FilterChoice("Submitted", false));
    choicesToReturn.add(new FilterChoice("Rejected", false));
    choicesToReturn.add(new FilterChoice("Suspended", false));
    choicesToReturn.add(new FilterChoice("Under Processing", false));
    choicesToReturn.add(new FilterChoice("Closed", false));
    choicesToReturn.add(new FilterChoice("Under Payment", false));
    choicesToReturn.add(new FilterChoice("Paid", false));
    choicesToReturn.add(new FilterChoice("Received", false));
    choicesToReturn.add(new FilterChoice("Approved", false));
    choicesToReturn.add(new FilterChoice("Posted", false));
    choicesToReturn.add(new FilterChoice("Dispensed", false));

    return choicesToReturn;
  }

  private ArrayList<FilterChoice> getYearsChoices() {
    Calendar cal = Calendar.getInstance();
    ArrayList<FilterChoice> yearsToReturn = new ArrayList<>();
    yearsToReturn.add(new FilterChoice("All Years", false));
    yearsToReturn.add(new FilterChoice(String.valueOf(cal.get(Calendar.YEAR)), false));
    yearsToReturn.add(new FilterChoice(String.valueOf(cal.get(Calendar.YEAR) - 1), false));
    yearsToReturn.add(new FilterChoice(String.valueOf(cal.get(Calendar.YEAR) - 2), false));

    return yearsToReturn;
  }

  private void setupStatusesRecycler(ArrayList<FilterChoice> statuses) {
    filterChoiceRecyclerAdapter = new FilterChoiceRecyclerAdapter(statuses);
    statusFilterRecycler.setLayoutManager(new LinearLayoutManager(this));
    statusFilterRecycler.addItemDecoration(new SimpleDividerItemDecoration(this));
    statusFilterRecycler.setAdapter(filterChoiceRecyclerAdapter);
  }

  private void setupYearsRecycler(ArrayList<FilterChoice> years) {
    filterChoiceRecyclerAdapter = new FilterChoiceRecyclerAdapter(years);
    yearFilterRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    yearFilterRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(this));
    yearFilterRecyclerView.setAdapter(filterChoiceRecyclerAdapter);
  }
}
