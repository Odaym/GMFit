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
import com.mcsaatchi.gmfit.fitness.activities.ArticleDetailsActivity;
import com.squareup.picasso.Picasso;
import java.util.List;

public class ArticlesRecyclerAdapter extends RecyclerView.Adapter {
  private Context context;
  private List<ArticlesResponseBody> articlesResponseBodies;

  public ArticlesRecyclerAdapter(Context context,
      List<ArticlesResponseBody> articlesResponseBodies) {
    this.context = context;
    this.articlesResponseBodies = articlesResponseBodies;
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
    holder.articleDateTV.setText(
        articlesResponseBodies.get(position).getDatePublishing().split(" ")[0]);
    Picasso.with(context)
        .load(articlesResponseBodies.get(position).getImage())
        .resize(context.getResources().getDimensionPixelSize(R.dimen.article_image_dimens),
            context.getResources().getDimensionPixelSize(R.dimen.article_image_dimens))
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
    private ImageView articleImageIV;

    public ViewHolder(View itemView) {
      super(itemView);

      articleNameTV = (TextView) itemView.findViewById(R.id.articleNameTV);
      articleDateTV = (TextView) itemView.findViewById(R.id.articleDateTV);
      articleImageIV = (ImageView) itemView.findViewById(R.id.articleImageIV);

      itemView.setOnClickListener(view -> {
        Intent intent = new Intent(context, ArticleDetailsActivity.class);
        intent.putExtra("ARTICLE", articlesResponseBodies.get(getAdapterPosition()));
        context.startActivity(intent);
      });
    }
  }
}
