package com.mcsaatchi.gmfit.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.mcsaatchi.gmfit.R;
import com.squareup.picasso.Picasso;

public class IntroSlider_Fragment extends Fragment {

    private static final String LAYOUT_RES_ID = "layoutResId";
    private int layoutResId;

    public static IntroSlider_Fragment newInstance(int layoutResId) {
        IntroSlider_Fragment frag = new IntroSlider_Fragment();

        Bundle b = new Bundle();
        b.putInt(LAYOUT_RES_ID, layoutResId);
        frag.setArguments(b);

        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!getArguments().containsKey(LAYOUT_RES_ID))
            throw new RuntimeException("Fragment must contain a layoutResId argument!");

        layoutResId = getArguments().getInt(LAYOUT_RES_ID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = getActivity().getLayoutInflater().inflate(layoutResId, container, false);

        ImageView introIMG = (ImageView) view.findViewById(R.id.introIMG);

        switch (layoutResId) {
            case R.layout.fragment_intro_slide_1:
                Picasso.with(getActivity()).load(R.drawable.fragment_intro_picture).into(introIMG);
                break;
            case R.layout.fragment_intro_slide_2:
                Picasso.with(getActivity()).load(R.drawable.fragment_intro_picture).into(introIMG);
                break;
            case R.layout.fragment_intro_slide_3:
                Picasso.with(getActivity()).load(R.drawable.fragment_intro_picture).into(introIMG);
                break;
            case R.layout.fragment_intro_slide_4:
                Picasso.with(getActivity()).load(R.drawable.fragment_intro_picture).into(introIMG);
                break;
        }

        return view;
    }
}