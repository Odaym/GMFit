package com.mcsaatchi.gmfit.insurance.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mcsaatchi.gmfit.R;

public class ItemLabel extends LinearLayout {
    private TextView itemNameTv;
    private TextView itemInfoTv;

    public ItemLabel(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = mInflater.inflate(R.layout.item_label, this, true);
        itemNameTv = (TextView) v.findViewById(R.id.item_name);
        itemInfoTv = (TextView) v.findViewById(R.id.item_selected);
    }

    public void setLabel(String name, String info) {
        itemNameTv.setText(name);
        itemInfoTv.setText(info);
    }
}