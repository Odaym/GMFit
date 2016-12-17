package com.mcsaatchi.gmfit.insurance.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.picasso.CircleTransform;
import com.mcsaatchi.gmfit.insurance.activities.UpdatePasswordActivity;
import com.squareup.picasso.Picasso;

public class InsuranceLoginFragment extends Fragment {
  public static final int INFO_UPDATED_SUCCESSFULLY_AFTER_LOGIN = 537;
  @Bind(R.id.memberImageIV) ImageView memberImageIV;
  @Bind(R.id.loginBTN) Button loginBTN;

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {

    View fragmentView = inflater.inflate(R.layout.fragment_insurance_login, container, false);

    ButterKnife.bind(this, fragmentView);

    Picasso.with(getActivity())
        .load(R.drawable.welcome_slider_banner)
        .transform(new CircleTransform())
        .into(memberImageIV);

    loginBTN.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        Intent intent = new Intent(getActivity(), UpdatePasswordActivity.class);
        startActivityForResult(intent, INFO_UPDATED_SUCCESSFULLY_AFTER_LOGIN);
      }
    });

    return fragmentView;
  }

  @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    switch (resultCode) {
      case INFO_UPDATED_SUCCESSFULLY_AFTER_LOGIN:
        getFragmentManager().beginTransaction()
            .replace(R.id.root_frame, new InsuranceHomeFragment())
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .addToBackStack(null)
            .commitAllowingStateLoss();

        break;
    }
  }
}
