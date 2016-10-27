package com.mcsaatchi.gmfit.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.activities.SpecifyMealAmount_Activity;
import com.mcsaatchi.gmfit.classes.Constants;
import com.mcsaatchi.gmfit.classes.EventBus_Poster;
import com.mcsaatchi.gmfit.classes.EventBus_Singleton;
import com.mcsaatchi.gmfit.data_access.DataAccessHandler;
import com.mcsaatchi.gmfit.models.MealItem;
import com.mcsaatchi.gmfit.rest.DefaultGetResponse;
import com.mcsaatchi.gmfit.touch_helpers.Drag_Swipe_ItemTouchHelperAdapter;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserMeals_RecyclerAdapterDragSwipe extends RecyclerView.Adapter<UserMeals_RecyclerAdapterDragSwipe.MyViewHolder>
        implements Drag_Swipe_ItemTouchHelperAdapter {

    private List<MealItem> mealItems;
    private Context context;
    private SharedPreferences prefs;

    public UserMeals_RecyclerAdapterDragSwipe(Context context, List<MealItem> mealItems) {
        this.mealItems = mealItems;
        this.context = context;

        if (context != null)
            prefs = context.getSharedPreferences(Constants.SHARED_PREFS_TITLE, Context.MODE_PRIVATE);
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
    public void onItemDismiss(int position) {

        try {
            deleteUserMeal(mealItems.get(position).getInstance_id());

            mealItems.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, mealItems.size());
        } catch (IndexOutOfBoundsException ignored) {
        }
    }

    private void deleteUserMeal(int instance_id) {
        DataAccessHandler.getInstance().deleteUserMeal(prefs.getString(Constants.PREF_USER_ACCESS_TOKEN, Constants.NO_ACCESS_TOKEN_FOUND_IN_PREFS),
                instance_id, new Callback<DefaultGetResponse>() {
                    @Override
                    public void onResponse(Call<DefaultGetResponse> call, Response<DefaultGetResponse> response) {
                        Log.d("TAG", "onResponse: Response code was : " + response.code());

                        switch (response.code()) {
                            case 200:
                                Log.d("TAG", "onResponse: Meal item removed!");

                                EventBus_Singleton.getInstance().post(new EventBus_Poster(Constants.EXTRAS_DELETED_MEAL_ENTRY));

                                break;
                        }
                    }

                    @Override
                    public void onFailure(Call<DefaultGetResponse> call, Throwable t) {
                        Log.d("TAG", "onFailure: FAilure");
                    }
                });
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
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
