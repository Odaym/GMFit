package com.mcsaatchi.gmfit.profile.activities;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.common.activities.BaseActivity;

public class ContactUsActivity extends BaseActivity {

  @Bind(R.id.toolbar) Toolbar toolbar;

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_contact_us);

    ButterKnife.bind(this);

    setupToolbar(toolbar, getResources().getString(R.string.contact_us_activity_title), true);
  }
}
