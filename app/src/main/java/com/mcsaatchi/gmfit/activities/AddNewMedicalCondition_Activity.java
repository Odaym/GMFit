package com.mcsaatchi.gmfit.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.mcsaatchi.gmfit.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class AddNewMedicalCondition_Activity extends Base_Activity {

    @Bind(R.id.medicalConditionsRadioGroup)
    RadioGroup medicalConditionsRadioGroup;
    @Bind(R.id.toolbar)
    Toolbar toolbar;

    private String[] medicalConditions = new String[]{"Hypertension", "Diabetes", "Obesity", "Asthma", "Arthritis", "Osteoporosis"};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_medical_conditions);

        ButterKnife.bind(this);

        toolbar.setTitle(getString(R.string.add_medical_condition_activity_title));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        for (int i = 0; i < medicalConditions.length; i++) {
            RadioButton radioButtonView = (RadioButton) getLayoutInflater().inflate(R.layout.radio_button_list_item_add_medical_condition, null);

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, getResources().getDimensionPixelSize(R.dimen.radiobutton_row_height));

            radioButtonView.setLayoutParams(lp);

            radioButtonView.setText(medicalConditions[i]);

            medicalConditionsRadioGroup.addView(radioButtonView);
        }
    }
}
