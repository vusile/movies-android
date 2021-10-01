package com.moviestanzania.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.koushikdutta.ion.Ion;
import com.moviestanzania.R;
import com.moviestanzania.objects.Movie;
import com.moviestanzania.viewholders.MoviesViewHolder;

import java.util.ArrayList;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesViewHolder> {

    private ArrayList<Movie> mMovies;
    private OnItemClickListener mListener;
    private boolean mNowShowing;

    @NonNull
    @Override
    public MoviesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movie_item, parent, false);

        return new MoviesViewHolder(view, mListener, mNowShowing);
    }

    @Override
    public void onBindViewHolder(@NonNull MoviesViewHolder holder, int position) {
        Movie movie = mMovies.get(position);
        holder.getTextMovieName().setText(movie.getName());
        holder.getTextGenre().setText(movie.getGenres());

        Ion.with(holder.getImagePoster())
                .placeholder(R.drawable.ic_poster_placeholder)
                .error(R.drawable.ic_poster_placeholder)
                .load(movie.getPoster());

        holder.getTextGenre().setText(movie.getGenres());
    }

    @Override
    public int getItemCount() {
        return mMovies.size();
    }

    public MoviesAdapter(Context context, ArrayList<Movie> movies, boolean nowShowing) {
        mMovies = movies;
        mNowShowing = nowShowing;
    }

    public interface OnItemClickListener {
        void onClick(int position, boolean mNowShowing);
    }

    public void setOnItemClickListener (OnItemClickListener listener) {
        mListener = listener;
    }
}
