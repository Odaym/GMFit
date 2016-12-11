package com.mcsaatchi.gmfit.insurance.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mcsaatchi.gmfit.R;

public class CustomToggle extends LinearLayout implements View.OnClickListener {
    private static final String TAG = CustomToggle.class.getSimpleName();
    private TextView toggleNameTv;
    private TextView toggleOptionOneTv;
    private TextView toggleOptionTwoTv;
    private View containerOptionOne;
    private View containerOptionTwo;
    private OnToggleListener onToggleListener;

    @Override
    public void onClick(View view) {
        if (onToggleListener == null) {
            Log.w(TAG, "setUp() " + CustomToggle.class.getSimpleName());
            return;
        }
        switch (view.getId()) {
            case R.id.containerOptionOne:
                onToggleListener.selected(toggleOptionOneTv.getText().toString());
                containerOptionOne.setBackground(getResources().getDrawable(R.drawable.toggle_button_selected));
                toggleOptionOneTv.setTextColor(getResources().getColor(R.color.white));
                containerOptionTwo.setBackground(getResources().getDrawable(R.drawable.toggle_button_unselected));
                toggleOptionTwoTv.setTextColor(getResources().getColor(R.color.buttons_blue));
                break;
            case R.id.containerOptionTwo:
                onToggleListener.selected(toggleOptionTwoTv.getText().toString());
                containerOptionTwo.setBackground(getResources().getDrawable(R.drawable.toggle_button_selected));
                toggleOptionTwoTv.setTextColor(getResources().getColor(R.color.white));
                containerOptionOne.setBackground(getResources().getDrawable(R.drawable.toggle_button_unselected));
                toggleOptionOneTv.setTextColor(getResources().getColor(R.color.buttons_blue));
                break;
        }
    }

    public interface OnToggleListener {
        void selected(String option);
    }

    public CustomToggle(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.custom_toggle, this, true);
        toggleNameTv = (TextView) v.findViewById(R.id.toggleName);
        toggleOptionOneTv = (TextView) v.findViewById(R.id.toggleOptionOne);
        toggleOptionTwoTv = (TextView) v.findViewById(R.id.toggleOptionTwo);
        containerOptionOne = v.findViewById(R.id.containerOptionOne);
        containerOptionTwo = v.findViewById(R.id.containerOptionTwo);
        containerOptionOne.setOnClickListener(this);
        containerOptionTwo.setOnClickListener(this);
    }

    public void setUp(String toggleName, String optionOne, String optionTwo, OnToggleListener onToggleListener) {
        toggleNameTv.setText(toggleName);
        toggleOptionOneTv.setText(optionOne);
        toggleOptionTwoTv.setText(optionTwo);
        this.onToggleListener = onToggleListener;
    }
}
