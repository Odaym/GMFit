package com.mcsaatchi.gmfit.insurance.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.picasso.CircleTransform;
import com.squareup.picasso.Picasso;

public class InsuranceLoginFragment extends Fragment {
  @Bind(R.id.memberImageIV) ImageView memberImageIV;

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {

    View fragmentView = inflater.inflate(R.layout.fragment_insurance_login, container, false);

    ButterKnife.bind(this, fragmentView);

    Picasso.with(getActivity())
        .load(R.drawable.welcome_slider_banner)
        .transform(new CircleTransform())
        .into(memberImageIV);

    return fragmentView;
  }
}
