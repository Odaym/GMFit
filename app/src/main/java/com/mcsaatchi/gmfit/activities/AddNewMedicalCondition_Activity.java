package com.mcsaatchi.gmfit.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

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

        setupToolbar(toolbar, R.string.add_medical_condition_activity_title, true);
        addTopPaddingToolbar(toolbar);

        for (String medicalCondition : medicalConditions) {
            RelativeLayout checkBoxView = (RelativeLayout) getLayoutInflater().inflate(R.layout.radiobutton_list_item_add_medical_condition, null);


            final RadioButton actualRadioButton = (RadioButton) checkBoxView.findViewById(R.id.actualRadioButton);

            actualRadioButton.setText(medicalCondition);

            checkBoxView.setPadding(0, 0, 0, getResources().getDimensionPixelSize(R.dimen.default_margin_0));

            checkBoxView.setLayoutParams(new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT));

            medicalConditionsRadioGroup.addView(checkBoxView);
        }
    }
}
