package com.mcsaatchi.gmfit.insurance.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.mcsaatchi.gmfit.R;

public class InsuranceRootFragment extends Fragment {
  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_insurance_root, container, false);

    getFragmentManager().beginTransaction()
        .replace(R.id.root_frame, new InsuranceLoginFragment())
        .commit();

    return view;
  }
}
