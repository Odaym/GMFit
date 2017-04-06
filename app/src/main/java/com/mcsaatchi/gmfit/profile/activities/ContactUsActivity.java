package com.mcsaatchi.gmfit.profile.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.common.activities.BaseActivity;

public class ContactUsActivity extends BaseActivity {

  @Bind(R.id.toolbar) Toolbar toolbar;
  @Bind(R.id.emailAddressTV) TextView emailAddressTV;
  @Bind(R.id.phoneNumber1) TextView phoneNumber1TV;
  @Bind(R.id.phoneNumber2) TextView phoneNumber2TV;

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_contact_us);

    ButterKnife.bind(this);

    setupToolbar(getClass().getSimpleName(), toolbar,
        getResources().getString(R.string.contact_us_activity_title), true);
  }

  @OnClick(R.id.emailAddressLayout) public void handleContactEmailPressed() {
    Intent intent = new Intent(Intent.ACTION_SEND);
    intent.setType("message/rfc822");
    intent.putExtra(Intent.EXTRA_SUBJECT, "");
    intent.putExtra(Intent.EXTRA_TEXT, "");
    intent.putExtra(android.content.Intent.EXTRA_EMAIL,
        new String[] { emailAddressTV.getText().toString() });

    Intent mailer = Intent.createChooser(intent, null);
    startActivity(mailer);
  }

  @OnClick(R.id.phoneNumber1Layout) public void handlePhoneNumber1Clicked() {
    Intent intent = new Intent(Intent.ACTION_DIAL);
    intent.setData(Uri.parse("tel:" + phoneNumber1TV.getText().toString()));
    startActivity(intent);
  }

  @OnClick(R.id.phoneNumber2Layout) public void handlePhoneNumber2Clicked() {
    Intent intent = new Intent(Intent.ACTION_DIAL);
    intent.setData(Uri.parse("tel:" + phoneNumber2TV.getText().toString()));
    startActivity(intent);
  }
}
