package com.mcsaatchi.gmfit.nutrition.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.GMFitApplication;
import com.mcsaatchi.gmfit.architecture.data_access.DataAccessHandler;
import com.mcsaatchi.gmfit.architecture.otto.EventBusSingleton;
import com.mcsaatchi.gmfit.architecture.otto.MealEntryManipulatedEvent;
import com.mcsaatchi.gmfit.architecture.rest.DefaultGetResponse;
import com.mcsaatchi.gmfit.architecture.touch_helpers.DragSwipeItemTouchHelperAdapter;
import com.mcsaatchi.gmfit.common.Constants;
import com.mcsaatchi.gmfit.nutrition.activities.SpecifyMealAmountActivity;
import com.mcsaatchi.gmfit.nutrition.models.MealItem;
import java.util.List;
import javax.inject.Inject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class UserMealsRecyclerAdapterDragSwipe
    extends RecyclerView.Adapter<UserMealsRecyclerAdapterDragSwipe.MyViewHolder>
    implements DragSwipeItemTouchHelperAdapter {

  @Inject DataAccessHandler dataAccessHandler;
  @Inject SharedPreferences prefs;
  private List<MealItem> mealItems;
  private Context context;

  public UserMealsRecyclerAdapterDragSwipe(Context context, List<MealItem> mealItems) {
    this.mealItems = mealItems;
    this.context = context;

    ((GMFitApplication) context.getApplicationContext()).getAppComponent().inject(this);
  }

  @Override public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View itemView = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.list_item_new_meal_entry, parent, false);

    return new MyViewHolder(itemView);
  }

  @Override public void onBindViewHolder(MyViewHolder holder, int position) {

    MealItem meal = mealItems.get(position);

    holder.entryTitleTV.setText(meal.getName());

    if (meal.getAmount() != null) {
      if (Float.parseFloat(meal.getAmount()) == 1) {
        holder.entryDescriptionTV.setText("1 serving");
      } else {
        holder.entryDescriptionTV.setText(meal.getAmount() + " servings");
      }
    }

    holder.entryUnitsTV.setText(meal.getTotalCalories() + " kcal");
  }

  @Override public int getItemCount() {
    return mealItems.size();
  }

  @Override public void onItemDismiss(int position) {

    try {
      deleteUserMeal(mealItems.get(position).getInstance_id());

      mealItems.remove(position);
      notifyItemRemoved(position);
      notifyItemRangeChanged(position, mealItems.size());
    } catch (IndexOutOfBoundsException ignored) {
    }
  }

  private void deleteUserMeal(int instance_id) {
    dataAccessHandler.deleteUserMeal(instance_id, new Callback<DefaultGetResponse>() {
      @Override
      public void onResponse(Call<DefaultGetResponse> call, Response<DefaultGetResponse> response) {
        Log.d("TAG", "onResponse: Response code was : " + response.code());

        switch (response.code()) {
          case 200:
            Log.d("TAG", "onResponse: Meal item removed!");

            EventBusSingleton.getInstance().post(new MealEntryManipulatedEvent());

            break;
        }
      }

      @Override public void onFailure(Call<DefaultGetResponse> call, Throwable t) {
        Timber.d("Call failed with error : %s", t.getMessage());
        final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setMessage(
            context.getResources().getString(R.string.error_response_from_server_incorrect));
        alertDialog.show();
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

    @Override public void onClick(View view) {
      Intent intent = new Intent(context, SpecifyMealAmountActivity.class);
      intent.putExtra(Constants.EXTRAS_MEAL_OBJECT_DETAILS, mealItems.get(getAdapterPosition()));
      intent.putExtra(Constants.EXTRAS_MEAL_ITEM_PURPOSE_EDITING, true);
      context.startActivity(intent);
    }
  }
}
