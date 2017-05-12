package com.mcsaatchi.gmfit.profile.activities;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.OperationContactsResponseBody;
import com.mcsaatchi.gmfit.common.activities.BaseActivity;
import com.mcsaatchi.gmfit.common.classes.SimpleDividerItemDecoration;
import com.mcsaatchi.gmfit.profile.adapters.OperationContactsRecyclerAdapter;
import java.util.List;

public class ContactUsListActivity extends BaseActivity
    implements ContactUsListActivityPresenter.ContactUsListActivityView {

  @Bind(R.id.toolbar) Toolbar toolbar;
  @Bind(R.id.contactCountriesRecycler) RecyclerView contactCountriesRecycler;

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_contact_us_list);

    ButterKnife.bind(this);

    setupToolbar(getClass().getSimpleName(), toolbar,
        getResources().getString(R.string.contact_us_activity_title), true);

    ContactUsListActivityPresenter presenter =
        new ContactUsListActivityPresenter(this, dataAccessHandler);

    presenter.getOperationContacts();
  }

  @Override public void displayOperationContacts(
      List<OperationContactsResponseBody> operationContactsResponseBody) {
    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
    OperationContactsRecyclerAdapter operationContactsRecyclerAdapter =
        new OperationContactsRecyclerAdapter(this, operationContactsResponseBody);

    contactCountriesRecycler.setLayoutManager(mLayoutManager);
    contactCountriesRecycler.setAdapter(operationContactsRecyclerAdapter);
    contactCountriesRecycler.setNestedScrollingEnabled(false);
    contactCountriesRecycler.addItemDecoration(new SimpleDividerItemDecoration(this));
  }
}
