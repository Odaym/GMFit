package com.mcsaatchi.gmfit.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.activities.SpecifyMealAmount_Activity;
import com.mcsaatchi.gmfit.classes.Constants;
import com.mcsaatchi.gmfit.models.MealItem;

import java.util.Collections;
import java.util.List;

public class UserMeals_RecyclerAdapter extends RecyclerView.Adapter<UserMeals_RecyclerAdapter.MyViewHolder>
        implements ItemTouchHelperAdapter {

    private List<MealItem> mealItems;
    private Context context;

    public UserMeals_RecyclerAdapter(Context context, List<MealItem> mealItems) {
        this.mealItems = mealItems;
        this.context = context;
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

        if (meal.getAmount() != null) {
            holder.entryDescriptionTV.setText(meal.getAmount() + " servings");
        } else {
            holder.entryDescriptionTV.setText("1 serving");
        }

        holder.entryUnitsTV.setText(meal.getTotalCalories() + " kcal");
    }

    @Override
    public int getItemCount() {
        return mealItems.size();
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(mealItems, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(mealItems, i, i - 1);
            }
        }

        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onItemDismiss(int position) {
        notifyItemRemoved(position);

        try {
            mealItems.remove(position);
        } catch (IndexOutOfBoundsException ignored) {
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView entryTitleTV, entryDescriptionTV, entryUnitsTV;

        MyViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            entryTitleTV = (TextView) view.findViewById(R.id.entryTitleTV);
            entryDescriptionTV = (TextView) view.findViewById(R.id.entryDescriptionTV);
            entryUnitsTV = (TextView) view.findViewById(R.id.entryUnitsTV);
        }

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(context, SpecifyMealAmount_Activity.class);
            intent.putExtra(Constants.EXTRAS_MEAL_OBJECT_DETAILS, mealItems.get(getAdapterPosition()));
            intent.putExtra(Constants.EXTRAS_MEAL_ITEM_PURPOSE_EDITING, true);
            context.startActivity(intent);
        }
    }
}
