package com.mcsaatchi.gmfit.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.mcsaatchi.gmfit.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class AddNewMedicalCondition_Activity extends Base_Activity {

    @Bind(R.id.medicalConditionsRadioGroup)
    RadioGroup medicalConditionsRadioGroup;
    @Bind(R.id.toolbar)
    Toolbar toolbar;

    private String[] medicalConditions = new String[]{"Hypertension", "Diabetes", "Obesity", "Asthma", "Arthritis", "Osteoporosis"};

    private List<RadioButton> radioButtonsList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_medical_conditions);

        ButterKnife.bind(this);

        setupToolbar(toolbar, R.string.add_medical_condition_activity_title, true);

        for (String medicalCondition : medicalConditions) {
            RelativeLayout radioButtonView = (RelativeLayout) getLayoutInflater().inflate(R.layout.radio_button_list_item_add_medical_condition, null);


            final RadioButton actualRadioBTN = (RadioButton) radioButtonView.findViewById(R.id.actualRadioBTN);

            actualRadioBTN.setText(medicalCondition);

            radioButtonView.setPadding(0, 0, 0, getResources().getDimensionPixelSize(R.dimen.default_margin_0));

            radioButtonView.setLayoutParams(new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT));

            medicalConditionsRadioGroup.addView(radioButtonView);

            actualRadioBTN.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                    for (int i = 0; i < radioButtonsList.size(); i++) {
                        radioButtonsList.get(i).setChecked(false);
                    }

                    actualRadioBTN.setChecked(checked);
                }
            });

            radioButtonsList.add(actualRadioBTN);
        }
    }
}
