package com.moviestanzania.viewholders;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.moviestanzania.R;
import com.moviestanzania.adapters.MoviesAdapter;

public class MoviesViewHolder extends RecyclerView.ViewHolder {
    private final TextView mTextMovieName;
    private final TextView mTextGenre;
    private final ImageView mImagePoster;

    public MoviesViewHolder(@NonNull View itemView, MoviesAdapter.OnItemClickListener listener, boolean nowShowing) {
        super(itemView);
        mImagePoster = itemView.findViewById(R.id.image_poster);
        mTextGenre = itemView.findViewById(R.id.text_genre);
        mTextMovieName = itemView.findViewById(R.id.text_movie_name);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = getAdapterPosition();
                listener.onClick(position, nowShowing);
            }
        });
    }

    public TextView getTextMovieName() {
        return mTextMovieName;
    }

    public TextView getTextGenre() {
        return mTextGenre;
    }

    public ImageView getImagePoster() {
        return mImagePoster;
    }
}
