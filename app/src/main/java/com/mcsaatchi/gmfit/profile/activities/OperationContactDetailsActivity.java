package com.mcsaatchi.gmfit.profile.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.util.Linkify;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.OperationContactsResponseBody;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.OperationContactsResponseLocation;
import com.mcsaatchi.gmfit.common.activities.BaseActivity;
import com.mcsaatchi.gmfit.common.classes.SimpleDividerItemDecoration;
import com.mcsaatchi.gmfit.profile.adapters.OperationContactAddressesRecyclerAdapter;
import java.util.Iterator;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import timber.log.Timber;

public class OperationContactDetailsActivity extends BaseActivity {
  @Bind(R.id.toolbar) Toolbar toolbar;
  @Bind(R.id.addressesRecyclerView) RecyclerView addressesRecyclerView;
  @Bind(R.id.emailAddressesTV) TextView emailAddressesTV;
  @Bind(R.id.websiteValueTV) TextView websiteValueTV;
  @Bind(R.id.emailAddressesLayout) LinearLayout emailAddressesLayout;
  @Bind(R.id.socialMediaLinksSection) LinearLayout socialMediaLinksSection;
  @Bind(R.id.websitesLayout) LinearLayout websitesLayout;
  @Bind(R.id.facebookIMG) ImageView facebookIMG;
  @Bind(R.id.twitterIMG) ImageView twitterIMG;
  @Bind(R.id.linkedinIMG) ImageView linkedinIMG;
  @Bind(R.id.youtubeIMG) ImageView youtubeIMG;
  @Bind(R.id.googlePlusIMG) ImageView googlePlusIMG;

  private OperationContactsResponseBody operationContactsResponseBody;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_operation_contact_details);

    ButterKnife.bind(this);

    if (getIntent().getExtras() != null) {
      operationContactsResponseBody = getIntent().getExtras().getParcelable("OPERATION_CONTACT");

      setupToolbar(getClass().getSimpleName(), toolbar, operationContactsResponseBody.getName(),
          true);

      displayOperationContactAddresses(operationContactsResponseBody.getLocations());

      if (operationContactsResponseBody.getEmails() != null) {
        emailAddressesTV.setText(operationContactsResponseBody.getEmails());
        emailAddressesTV.setOnClickListener(view -> {
          final Intent emailIntent = new Intent(Intent.ACTION_SEND);

          emailIntent.setType("plain/text");
          emailIntent.putExtra(Intent.EXTRA_EMAIL,
              new String[] { operationContactsResponseBody.getEmails() });
          emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject");
          emailIntent.putExtra(Intent.EXTRA_TEXT, "Text");

          startActivity(Intent.createChooser(emailIntent, "Send us an email"));
        });
      }

      final JSONArray jsonArray;
      try {
        jsonArray = new JSONArray(operationContactsResponseBody.getSocialMediaLinks());

        for (int i = 0; i < jsonArray.length(); i++) {

          JSONObject obj = new JSONObject(String.valueOf(jsonArray.get(i)));

          Iterator<String> iterator = obj.keys();
          while (iterator.hasNext()) {
            String key = iterator.next();

            try {
              String value = obj.getString(key);

              Timber.d("Value is %s", value);

              Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(value));

              switch (value) {
                case "website":
                  websitesLayout.setVisibility(View.VISIBLE);
                  websiteValueTV.setText(value);
                  Linkify.addLinks(websiteValueTV, Linkify.WEB_URLS);
                  break;
                case "Facebook":
                case "facebook":
                  facebookIMG.setOnClickListener(view -> startActivity(browserIntent));
                  socialMediaLinksSection.setVisibility(View.VISIBLE);
                  facebookIMG.setVisibility(View.VISIBLE);
                  break;
                case "Twitter":
                case "twitter":
                  twitterIMG.setOnClickListener(view -> startActivity(browserIntent));
                  socialMediaLinksSection.setVisibility(View.VISIBLE);
                  twitterIMG.setVisibility(View.VISIBLE);
                  break;
                case "G+":
                  googlePlusIMG.setOnClickListener(view -> startActivity(browserIntent));
                  socialMediaLinksSection.setVisibility(View.VISIBLE);
                  googlePlusIMG.setVisibility(View.VISIBLE);
                  break;
                case "LinkedIn":
                  linkedinIMG.setOnClickListener(view -> startActivity(browserIntent));
                  socialMediaLinksSection.setVisibility(View.VISIBLE);
                  linkedinIMG.setVisibility(View.VISIBLE);
                  break;
                case "YouTube":
                  youtubeIMG.setOnClickListener(view -> startActivity(browserIntent));
                  socialMediaLinksSection.setVisibility(View.VISIBLE);
                  youtubeIMG.setVisibility(View.VISIBLE);
                  break;
              }
            } catch (JSONException ignored) {
            }
          }
        }
      } catch (JSONException e) {
        e.printStackTrace();
      }
    }
  }

  private void displayOperationContactAddresses(List<OperationContactsResponseLocation> locations) {
    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
    OperationContactAddressesRecyclerAdapter operationContactsRecyclerAdapter =
        new OperationContactAddressesRecyclerAdapter(this, locations);

    addressesRecyclerView.setLayoutManager(mLayoutManager);
    addressesRecyclerView.setAdapter(operationContactsRecyclerAdapter);
    addressesRecyclerView.setNestedScrollingEnabled(false);
    addressesRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(this));
  }
}
