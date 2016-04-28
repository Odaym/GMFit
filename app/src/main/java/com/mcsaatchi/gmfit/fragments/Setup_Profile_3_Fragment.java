package com.mcsaatchi.gmfit.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.activities.LearnMoreGoogleFit_Activity;

public class Setup_Profile_3_Fragment extends Fragment {

    private TextView learnMoreGoogleFitTV;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View fragmentView = inflater.inflate(R.layout.fragment_setup_profile_3, container, false);

        learnMoreGoogleFitTV = (TextView) fragmentView.findViewById(R.id.learnMoreGoogleFitTV);

        hookupLearnMoreButton(learnMoreGoogleFitTV);

        return fragmentView;
    }

    private void hookupLearnMoreButton(TextView learnMoreGoogleFitTV) {
        SpannableString ss = new SpannableString(getString(R.string.connect_google_fit_learn));
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                startActivity(new Intent(getActivity(), LearnMoreGoogleFit_Activity.class));
            }
        };

        ss.setSpan(clickableSpan, 0, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        learnMoreGoogleFitTV.setText(ss);
        learnMoreGoogleFitTV.setMovementMethod(LinkMovementMethod.getInstance());
        learnMoreGoogleFitTV.setHighlightColor(Color.BLUE);
    }
}
