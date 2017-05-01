package com.mcsaatchi.gmfit.insurance.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.InquiriesListResponseInnerData;
import com.mcsaatchi.gmfit.insurance.activities.inquiry.InquiryNotesActivity;
import java.util.List;
import org.apache.commons.lang3.text.WordUtils;

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
    View view = inflater.inflate(R.layout.list_item_inquiry, parent, false);

    return new ViewHolder(view);
  }

  @Override public void onBindViewHolder(RecyclerView.ViewHolder h, int position) {
    final ViewHolder holder = (ViewHolder) h;

    holder.bind(inquiriesList.get(position));
  }

  @Override public int getItemCount() {
    return inquiriesList.size();
  }

  private class ViewHolder extends RecyclerView.ViewHolder {
    private TextView requestTitleTV, requestSubCategoryTV, requestCategoryTV, requestCreatedOnTV;
    private RelativeLayout parentLayout;

    public ViewHolder(View itemView) {
      super(itemView);

      parentLayout = (RelativeLayout) itemView.findViewById(R.id.parentLayout);
      requestTitleTV = (TextView) itemView.findViewById(R.id.requestTitleTV);
      requestCreatedOnTV = (TextView) itemView.findViewById(R.id.requestCreatedOnTV);
      requestSubCategoryTV = (TextView) itemView.findViewById(R.id.requestSubCategoryTV);
      requestCategoryTV = (TextView) itemView.findViewById(R.id.requestCategoryTV);
    }

    public void bind(InquiriesListResponseInnerData inquiryItem) {
      requestTitleTV.setText(WordUtils.capitalizeFully(inquiryItem.getTitle()));

      requestCategoryTV.setText(inquiryItem.getCategoryName());

      requestSubCategoryTV.setText(inquiryItem.getSubCategoryName());

      requestCreatedOnTV.setText(inquiryItem.getStatus() + " - " + inquiryItem.getCreatedOn());

      parentLayout.setOnClickListener(view -> {
        Intent intent = new Intent(context, InquiryNotesActivity.class);
        intent.putExtra("INQUIRY_OBJECT", inquiryItem);
        context.startActivity(intent);
      });
    }
  }
}
