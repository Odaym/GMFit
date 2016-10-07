package com.mcsaatchi.gmfit.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.rest.MedicalTestsResponseBody;

import java.util.List;

public class AvailableTestsRecycler_Adapter extends RecyclerView.Adapter<AvailableTestsRecycler_Adapter.MyViewHolder> {

    private List<MedicalTestsResponseBody> availableTests;
    private Context context;

    public AvailableTestsRecycler_Adapter(Context context, List<MedicalTestsResponseBody> availableTests) {
        this.availableTests = availableTests;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_available_tests, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        MedicalTestsResponseBody testResponseBody = availableTests.get(position);

        holder.itemNameTV.setText(testResponseBody.getName());
    }

    @Override
    public int getItemCount() {
        return availableTests.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView itemNameTV;

        MyViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            itemNameTV = (TextView) view.findViewById(R.id.itemNameTV);
        }

        @Override
        public void onClick(View view) {
//            Intent intent = new Intent(context, SpecifyMealAmount_Activity.class);
//            intent.putExtra(Constants.EXTRAS_MEAL_OBJECT_DETAILS, availableTests.get(getAdapterPosition()));
//            intent.putExtra(Constants.EXTRAS_MEAL_ITEM_PURPOSE_EDITING, true);
//            context.startActivity(intent);
        }
    }
}
