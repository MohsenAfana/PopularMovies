package com.mohsenafana.movies.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mohsenafana.movies.R;
import com.mohsenafana.movies.model.MovieReviewEntity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MoviesReviewsAdapter extends RecyclerView.Adapter<MoviesReviewsAdapter.TrailerHolder> {


    private Context mContext;
    private List<MovieReviewEntity> mReviewList;
    private LayoutInflater mInflater;

    public MoviesReviewsAdapter(Context context) {
        this.mContext = context;
        this.mReviewList = new ArrayList<>();
        this.mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @NonNull
    @Override
    public TrailerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = mInflater.inflate(R.layout.item_review, parent, false);
        return new TrailerHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull TrailerHolder holder, int position) {
        MovieReviewEntity review = mReviewList.get(position);
        holder.bindData(review);
    }

    @Override
    public int getItemCount() {
        return mReviewList == null ? 0 : mReviewList.size();
    }

    public void add(MovieReviewEntity item) {
        mReviewList.add(item);
        notifyItemInserted(mReviewList.size() - 1);
    }

    public void addAll(List<MovieReviewEntity> appendedItemList) {
        if (appendedItemList == null || appendedItemList.size() <= 0) {
            return;
        }
        if (this.mReviewList == null) {
            this.mReviewList = new ArrayList<>();
        }
        this.mReviewList.addAll(appendedItemList);
        notifyDataSetChanged();
    }

    public class TrailerHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_review_body)
        TextView mReviewBodyTextView;
        @BindView(R.id.tv_review_author)
        TextView mReviewAuthorTextView;

        public TrailerHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }


        // Reviewer : What does  @SuppressLint("SetTextI18n") mean ?
        @SuppressLint("SetTextI18n")
        private void bindData(MovieReviewEntity review) {
            mReviewBodyTextView.setText(review.getContent());
            mReviewAuthorTextView.setText(review.getAuthor());
        }
    }


}
