package com.mcsaatchi.gmfit.profile.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.OperationContactsResponseBody;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.OperationContactsResponseLocation;
import com.mcsaatchi.gmfit.common.activities.BaseActivity;
import com.mcsaatchi.gmfit.common.classes.SimpleDividerItemDecoration;
import com.mcsaatchi.gmfit.profile.adapters.OperationContactAddressesRecyclerAdapter;
import java.util.List;

public class OperationContactDetailsActivity extends BaseActivity {
  @Bind(R.id.toolbar) Toolbar toolbar;
  @Bind(R.id.addressesRecyclerView) RecyclerView addressesRecyclerView;
  @Bind(R.id.emailAddressesTV) TextView emailAddressesTV;
  @Bind(R.id.websiteValueTV) TextView websiteValueTV;

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

      emailAddressesTV.setText(operationContactsResponseBody.getEmails());

      websiteValueTV.setText("This contact does not have a website.");
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
