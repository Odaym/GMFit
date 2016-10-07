package com.mcsaatchi.gmfit.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.classes.UserTest;

import java.util.List;

public class UserTestsRecycler_Adapter extends RecyclerView.Adapter<UserTestsRecycler_Adapter.MyViewHolder> {

    private List<UserTest> userTests;
    private Context context;

    public UserTestsRecycler_Adapter(Context context, List<UserTest> userTests) {
        this.userTests = userTests;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_one_with_icon_and_text, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        UserTest userTest = userTests.get(position);

        holder.itemNameTV.setText(userTest.getName());

        holder.itemSubtitleTV.setText(userTest.getDateTaken());
    }

    @Override
    public int getItemCount() {
        return userTests.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView itemNameTV, itemSubtitleTV;

        MyViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            itemNameTV = (TextView) view.findViewById(R.id.itemNameTV);
            itemSubtitleTV = (TextView) view.findViewById(R.id.itemSubtitleTV);
        }

        @Override
        public void onClick(View view) {
//            Intent intent = new Intent(context, SpecifyMealAmount_Activity.class);
//            intent.putExtra(Constants.EXTRAS_MEAL_OBJECT_DETAILS, userTests.get(getAdapterPosition()));
//            intent.putExtra(Constants.EXTRAS_MEAL_ITEM_PURPOSE_EDITING, true);
//            context.startActivity(intent);
        }
    }
}
