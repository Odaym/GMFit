package com.mcsaatchi.gmfit.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.classes.CircleTransform;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;

public class GetStarted_Activity extends Base_Activity {

    @Bind(R.id.getStartedIMG)
    ImageView getStartedIMG;
    @Bind(R.id.setup_profile_button)
    Button setupProfileBTN;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_get_started);

        ButterKnife.bind(this);

        Picasso.with(this).load(R.drawable.fragment_intro_picture).transform(new CircleTransform()).into
                (getStartedIMG);

        setupProfileBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GetStarted_Activity.this, SetupProfile_Activity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
