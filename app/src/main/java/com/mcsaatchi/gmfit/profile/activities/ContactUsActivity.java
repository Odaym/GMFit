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
  @Bind(R.id.contact1NumberTV) TextView contact1NumberTV;
  @Bind(R.id.contact1EmailTV) TextView contact1EmailTV;

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_contact_us);

    ButterKnife.bind(this);

    setupToolbar(getClass().getSimpleName(), toolbar,
        getResources().getString(R.string.contact_us_activity_title), true);
  }

  @OnClick(R.id.contact1EmailTV) public void handleContact1EmailPressed() {
    Intent intent = new Intent(Intent.ACTION_SEND);
    intent.setType("message/rfc822");
    intent.putExtra(Intent.EXTRA_SUBJECT, "");
    intent.putExtra(Intent.EXTRA_TEXT, "");
    intent.putExtra(android.content.Intent.EXTRA_EMAIL,
        new String[] { contact1EmailTV.getText().toString() });

    Intent mailer = Intent.createChooser(intent, null);
    startActivity(mailer);
  }

  @OnClick(R.id.contact1NumberTV) public void handleContact1NumberPressed() {
    Intent intent = new Intent(Intent.ACTION_DIAL);
    intent.setData(Uri.parse("tel:" + contact1NumberTV.getText().toString()));
    startActivity(intent);
  }
}
