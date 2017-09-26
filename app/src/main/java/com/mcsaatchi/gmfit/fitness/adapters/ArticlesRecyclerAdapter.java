package com.mcsaatchi.gmfit.fitness.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.ArticlesResponseBody;
import com.mcsaatchi.gmfit.common.classes.Helpers;
import com.mcsaatchi.gmfit.fitness.activities.ArticleDetailsActivity;
import com.squareup.picasso.Picasso;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

public class ArticlesRecyclerAdapter extends RecyclerView.Adapter {
  private Context context;
  private List<ArticlesResponseBody> articlesResponseBodies;
  private String section;

  public ArticlesRecyclerAdapter(Context context, List<ArticlesResponseBody> articlesResponseBodies,
      String section) {
    this.context = context;
    this.articlesResponseBodies = articlesResponseBodies;
    this.section = section;
  }

  @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    LayoutInflater inflater =
        (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    View view = inflater.inflate(R.layout.list_item_articles_list, parent, false);

    return new ViewHolder(view);
  }

  @Override public void onBindViewHolder(RecyclerView.ViewHolder h, int position) {
    final ViewHolder holder = (ViewHolder) h;

    holder.articleNameTV.setText(articlesResponseBodies.get(position).getTitle());

    try {
      holder.articleDateTV.setText(new SimpleDateFormat("dd MMMM, yyyy").format(
          new SimpleDateFormat("yyyy-MM-dd").parse(
              articlesResponseBodies.get(position).getDatePublishing().split(" ")[0])));
    } catch (ParseException e) {
      e.printStackTrace();
    }

    Picasso.with(context)
        .load(articlesResponseBodies.get(position).getImage())
        .resize(
            context.getResources().getDimensionPixelSize(R.dimen.article_image_dimens_horizontal),
            context.getResources().getDimensionPixelSize(R.dimen.article_image_dimens_vertical))
        .centerCrop()
        .into(holder.articleImageIV);
  }

  @Override public int getItemCount() {
    return articlesResponseBodies.size();
  }

  public ArticlesResponseBody getItem(int position) {
    return articlesResponseBodies.get(position);
  }

  private class ViewHolder extends RecyclerView.ViewHolder {
    private TextView articleNameTV, articleDateTV;
    private ImageView articleImageIV, indicatorArrowIV;

    public ViewHolder(View itemView) {
      super(itemView);

      articleNameTV = itemView.findViewById(R.id.articleNameTV);
      articleDateTV = itemView.findViewById(R.id.articleDateTV);
      articleImageIV = itemView.findViewById(R.id.articleImageIV);
      indicatorArrowIV = itemView.findViewById(R.id.indicatorArrowIV);

      if (Helpers.isLanguageArabic()) {
        indicatorArrowIV.setScaleX(-1);
      }

      itemView.setOnClickListener(view -> {
        Intent intent = new Intent(context, ArticleDetailsActivity.class);
        intent.putExtra("ARTICLE", articlesResponseBodies.get(getAdapterPosition()));
        intent.putExtra("SECTION", section);
        context.startActivity(intent);
      });
    }
  }
}
