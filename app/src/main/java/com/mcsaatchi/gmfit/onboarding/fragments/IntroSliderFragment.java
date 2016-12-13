package com.mcsaatchi.gmfit.onboarding.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class IntroSliderFragment extends Fragment {

  private static final String LAYOUT_RES_ID = "layoutResId";
  private int layoutResId;

  public static IntroSliderFragment newInstance(int layoutResId) {
    IntroSliderFragment frag = new IntroSliderFragment();

    Bundle b = new Bundle();
    b.putInt(LAYOUT_RES_ID, layoutResId);
    frag.setArguments(b);

    return frag;
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    if (!getArguments().containsKey(LAYOUT_RES_ID)) {
      throw new RuntimeException("Fragment must contain a layoutResId argument!");
    }

    layoutResId = getArguments().getInt(LAYOUT_RES_ID);
  }

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {

    return getActivity().getLayoutInflater().inflate(layoutResId, container, false);
  }
}