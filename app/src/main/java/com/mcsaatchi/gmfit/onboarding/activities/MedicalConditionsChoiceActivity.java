package com.mcsaatchi.gmfit.onboarding.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.common.activities.BaseActivity;
import com.mcsaatchi.gmfit.onboarding.adapters.MedicalChoiceRecyclerAdapter;
import com.mcsaatchi.gmfit.onboarding.models.MedicalCondition;
import java.util.ArrayList;

import static com.mcsaatchi.gmfit.onboarding.fragments.SetupProfile4Fragment.MEDICAL_CONDITIONS_SELECTED;

public class MedicalConditionsChoiceActivity extends BaseActivity {
  //@Bind(R.id.medicalConditionsRecyclerView) RecyclerView medicalConditionsRecyclerView;
  @Bind(R.id.toolbar) Toolbar toolbar;

  @Bind(R.id.medicalChoicesContainer) LinearLayout medicalChoicesContainer;

  private ArrayList<MedicalCondition> medicalConditions = new ArrayList<>();

  private MedicalChoiceRecyclerAdapter medicalConditionsChoiceListAdapter;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_medical_conditions_choice);

    ButterKnife.bind(this);

    setupToolbar(getClass().getSimpleName(), toolbar, getString(R.string.medical_conditions_entry),
        true);

    if (getIntent().getExtras() != null) {
      medicalConditions = getIntent().getExtras().getParcelableArrayList("MEDICAL_CONDITIONS");
    }

    setMedicalConditionsChoiceList(medicalConditions);
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.medical_conditions_choice, menu);
    return super.onCreateOptionsMenu(menu);
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.doneBTN:
        finish();
        break;
    }
    return super.onOptionsItemSelected(item);
  }

  @Override public void finish() {
    Intent intent = new Intent();
    intent.putParcelableArrayListExtra("MEDICAL_CONDITIONS", medicalConditions);
    setResult(MEDICAL_CONDITIONS_SELECTED, intent);

    super.finish();
  }

  private void setMedicalConditionsChoiceList(ArrayList<MedicalCondition> medicalConditions) {
    ArrayList<CheckBox> allCheckBoxes = new ArrayList<>();

    for (int i = 0; i < medicalConditions.size(); i++) {
      View itemView = LayoutInflater.from(this)
          .inflate(R.layout.list_item_medical_condition_choice, medicalChoicesContainer, false);

      CheckBox medicalCheckbox = (CheckBox) itemView.findViewById(R.id.medicalCheckbox);

      medicalCheckbox.setText(medicalConditions.get(i).getMedicalCondition());

      if (medicalConditions.get(i).isSelected()) {
        medicalCheckbox.setChecked(true);
      }

      int tempI = i;
      medicalCheckbox.setOnCheckedChangeListener((compoundButton, selected) -> {
        if (medicalCheckbox.getText().equals("None")) {
          for (int j = 0; j < allCheckBoxes.size(); j++) {
            allCheckBoxes.get(j).setChecked(false);
            medicalConditions.get(j).setSelected(false);
          }
        } else {
          medicalConditions.get(tempI).setSelected(selected);
        }
      });

      allCheckBoxes.add(medicalCheckbox);
      medicalChoicesContainer.addView(itemView);
    }
  }
}
