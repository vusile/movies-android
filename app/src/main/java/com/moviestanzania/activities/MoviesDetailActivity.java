package com.moviestanzania.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.moviestanzania.Constants;
import com.moviestanzania.R;
import com.moviestanzania.objects.Movie;
import com.moviestanzania.objects.Theater;
import com.moviestanzania.views.LabelValueView;
import com.moviestanzania.views.TheaterShowTimesView;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import java.util.Objects;

public class MoviesDetailActivity extends AppCompatActivity {

    private static final String EXTRA_MOVIE_ID = "movie_id";
    private static final String EXTRA_MOVIE = "movie";
    private int mMovieId;
    private Movie mMovie;
    private YouTubePlayerView mYouTubePlayerView;
    private TextView mTextMovieName;
    private LabelValueView mLabelValueDuration;
    private LabelValueView mLabelValueGenre;
    private LabelValueView mLabelValueStarring;
    private LabelValueView mLabelValueDirector;
    private TextView mTextSynopsis;
    private TextView mTextTitleShowTimes;
    private LinearLayout mShowTimesContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies_detail);

        getExtras();
    }

    private void setupAd() {
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        FrameLayout frameLayout = findViewById(R.id.frame_ad_movie_detail);
        AdView adView = new AdView(this);
        adView.setAdUnitId(Constants.detailPageBannerAdUnitId);
        adView.setAdSize(AdSize.BANNER);
        frameLayout.addView(adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }

    private void setupViews() {
        Objects.requireNonNull(getSupportActionBar()).setIcon(R.drawable.logo);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mYouTubePlayerView = findViewById(R.id.youtube_player_view_detail);
        mTextMovieName = findViewById(R.id.text_movie_name_detail);
        mLabelValueDuration = findViewById(R.id.label_value_duration_detail);
        mLabelValueGenre = findViewById(R.id.label_value_genres_detail);
        mLabelValueStarring = findViewById(R.id.label_value_starring_detail);
        mLabelValueDirector = findViewById(R.id.label_value_director_detail);
        mTextSynopsis = findViewById(R.id.text_synopsis_detail);
        mTextTitleShowTimes = findViewById(R.id.text_show_times_title);
        mShowTimesContainer = findViewById(R.id.container_show_times_and_theaters_detail);
    }

    private void getExtras() {
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            if (extras.containsKey(EXTRA_MOVIE)) {
                mMovie = extras.getParcelable(EXTRA_MOVIE);
                getGoing();
            }

            if (extras.containsKey(EXTRA_MOVIE_ID)) {
                mMovieId = extras.getInt(EXTRA_MOVIE_ID);
                getMovie();
            }
        }
    }

    private void getMovie() {
        Ion.with(this)
                .load(Constants.apiBase + "movies/" + mMovieId)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        JsonObject moviesObject = result.getAsJsonObject("data");
                        mMovie = Movie.getMovie(moviesObject);
                        getGoing();
                    }
                });
    }

    public void getGoing() {
        setupViews();
        populateViews();
        setupAd();
    }

    private void populateViews() {
        getLifecycle().addObserver(mYouTubePlayerView);

        mYouTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                String videoId = mMovie.getTrailer();
                youTubePlayer.cueVideo(videoId, 0);
            }
        });

        mTextMovieName.setText(mMovie.getName());
        mLabelValueDuration.setLabel(getString(R.string.label_duration));
        mLabelValueDuration.setValue(mMovie.getDuration());
        mLabelValueGenre.setLabel(getString(R.string.label_genre));
        mLabelValueGenre.setValue(mMovie.getGenres());
        mLabelValueStarring.setLabel(getString(R.string.label_starring));
        mLabelValueStarring.setValue(mMovie.getStarring());
        mLabelValueDirector.setLabel(getString(R.string.label_director));
        mLabelValueDirector.setValue(mMovie.getDirector());
        mTextSynopsis.setText(mMovie.getSynopsis());

        if(mMovie.isNowShowing()) {
            mTextTitleShowTimes.setText(R.string.showing_at);
            mShowTimesContainer.setVisibility(View.VISIBLE);

            for(Theater theater : mMovie.getTheaters()) {
                TheaterShowTimesView showTimesView = new TheaterShowTimesView(this);
                showTimesView.setTheaterName(theater.getName());
                showTimesView.setShowTimes(theater.getShowTimes());
                showTimesView.setPricing(theater.getPricing());

                if(theater.getBooking() != null) {
                    showTimesView.getButtonBook().setOnClickListener(new BookButtonOnClickListener(theater.getBooking()));
                    showTimesView.getButtonBook().setVisibility(View.VISIBLE);
                } else {
                    showTimesView.getButtonBook().setVisibility(View.GONE);
                }
                mShowTimesContainer.addView(showTimesView);
            }
        } else {
            mTextTitleShowTimes.setText(R.string.coming_soon);
            mShowTimesContainer.setVisibility(View.GONE);
        }

    }

    public static Intent getIntent(Context context, int movieId) {
        Intent intent = new Intent(context, MoviesDetailActivity.class);

        intent.putExtra(EXTRA_MOVIE_ID, movieId);

        return intent;
    }

    public static Intent getIntent(Context context, Movie movie) {
        Intent intent = new Intent(context, MoviesDetailActivity.class);

        intent.putExtra(EXTRA_MOVIE, movie);

        return intent;
    }

    private class BookButtonOnClickListener implements View.OnClickListener {

        String mBookingUrl;

        BookButtonOnClickListener(String url) {
            mBookingUrl = url;
        }

        @Override
        public void onClick(View v) {
            startActivity(WebviewActivity.getIntent(MoviesDetailActivity.this, mBookingUrl));
        }
    }
}