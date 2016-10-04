package com.mcsaatchi.gmfit.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mcsaatchi.gmfit.R;

import butterknife.ButterKnife;

public class Health_Fragment extends Fragment {

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View fragmentView = inflater.inflate(R.layout.fragment_health, container, false);

        ButterKnife.bind(this, fragmentView);

        setHasOptionsMenu(true);

        return fragmentView;
    }
}
