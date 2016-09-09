package com.mcsaatchi.gmfit.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.models.MealItem;

import java.util.List;

public class UserMeals_RecyclerAdapter extends RecyclerView.Adapter<UserMeals_RecyclerAdapter.MyViewHolder> {

    private List<MealItem> mealItems;

    public UserMeals_RecyclerAdapter(List<MealItem> mealItems) {
        this.mealItems = mealItems;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_new_meal_entry, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        MealItem meal = mealItems.get(position);

        holder.entryTitleTV.setText(meal.getName());

        holder.entryDescriptionTV.setText(meal.getTotalCalories() + " kcal");

        if (meal.getAmount() == null || meal.getMeasurementUnit() == null)
            holder.entryUnitsTV.setText("430 mg");
    }

    @Override
    public int getItemCount() {
        return mealItems.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView entryTitleTV, entryDescriptionTV, entryUnitsTV;

        public MyViewHolder(View view) {
            super(view);
            entryTitleTV = (TextView) view.findViewById(R.id.entryTitleTV);
            entryDescriptionTV = (TextView) view.findViewById(R.id.entryDescriptionTV);
            entryUnitsTV = (TextView) view.findViewById(R.id.entryUnitsTV);
        }
    }
}
