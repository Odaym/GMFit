package com.mcsaatchi.gmfit.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.classes.Helpers;

import butterknife.Bind;
import butterknife.ButterKnife;

public class TOS_Activity extends Base_Activity {
    @Bind(R.id.termsOfUseTV)
    TextView termsOfUseTV;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(Helpers.createActivityBundleWithProperties(R.string.tos_activity_title, true));

        setContentView(R.layout.activity_terms_of_use);

        ButterKnife.bind(this);

        termsOfUseTV.setMovementMethod(LinkMovementMethod.getInstance());
    }
}
