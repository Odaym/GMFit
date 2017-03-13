package com.mcsaatchi.gmfit.insurance.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.rest.InquiriesListResponseInnerData;
import java.util.List;

public class InquiryStatusAdapter extends RecyclerView.Adapter {
  private List<InquiriesListResponseInnerData> inquiriesList;
  private Context context;

  public InquiryStatusAdapter(Context context, List<InquiriesListResponseInnerData> inquiriesList) {
    this.context = context;
    this.inquiriesList = inquiriesList;
  }

  @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    LayoutInflater inflater =
        (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    View view = inflater.inflate(R.layout.list_item_chronic_treatment, parent, false);

    return new ViewHolder(view);
  }

  @Override public void onBindViewHolder(RecyclerView.ViewHolder h, int position) {
    final ViewHolder holder = (ViewHolder) h;

    //holder.bind(inquiriesList.get(position));
  }

  @Override public int getItemCount() {
    return inquiriesList.size();
  }

  private class ViewHolder extends RecyclerView.ViewHolder {
    private View deleteLayout;
    private RelativeLayout parentLayout;
    private TextView treatmentNameTV, treatmentDescriptionTV, treatmentStatusTV;

    public ViewHolder(View itemView) {
      super(itemView);

      parentLayout = (RelativeLayout) itemView.findViewById(R.id.parentLayout);
      deleteLayout = itemView.findViewById(R.id.delete_layout);
      treatmentNameTV = (TextView) itemView.findViewById(R.id.treatmentNameTV);
      treatmentDescriptionTV = (TextView) itemView.findViewById(R.id.treatmentDescriptionTV);
      treatmentStatusTV = (TextView) itemView.findViewById(R.id.treatmentStatusTV);
    }

    //public void bind(InquiriesListResponseInnerData inquiryItem) {
    //  treatmentNameTV.setText(WordUtils.capitalizeFully(inquiryItem.getTitle()));
    //  treatmentStatusTV.setTextColor(context.getResources()
    //      .getColor(Helpers.determineStatusColor(inquiryItem.getStatus())));
    //  treatmentStatusTV.setText(inquiryItem.getStatus());
    //
    //  if (inquiryItem.getStartDate() != null && inquiryItem.getEndDate() != null) {
    //    treatmentDescriptionTV.setText(
    //        inquiryItem.getStartDate().split("T")[0] + " - " + inquiryItem.getEndDate()
    //            .split("T")[0]);
    //  } else {
    //    treatmentDescriptionTV.setText("Start and end dates not available yet");
    //  }
    //
    //  parentLayout.setOnClickListener(view -> {
    //    Intent intent = new Intent(context, ChronicStatusDetailsActivity.class);
    //    intent.putExtra("CHRONIC_OBJECT", inquiriesList.get(getAdapterPosition()));
    //    //intent.putExtra(Constants.EXTRAS_PURPOSE_EDIT_MEDICATION_REMINDER, true);
    //    //intent.putExtra(Constants.EXTRAS_MEDICATION_REMINDER_ITEM, medicationItem);
    //    context.startActivity(intent);
    //  });
    //}
  }
}
