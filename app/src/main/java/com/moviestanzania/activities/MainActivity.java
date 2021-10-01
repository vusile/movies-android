package com.moviestanzania.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.moviestanzania.Constants;
import com.moviestanzania.R;
import com.moviestanzania.adapters.MoviesAdapter;
import com.moviestanzania.objects.Movie;
import com.moviestanzania.utils.NetworkUtil;
import com.moviestanzania.utils.TokenUtil;

import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements MoviesAdapter.OnItemClickListener {

    private static final String CHANNEL_ID = "movies";
    private ArrayList<Movie> mListNowShowingMovies;
    private ArrayList<Movie> mListComingSoonMovies;
    private LinearLayout mContentLayout;
    private LinearLayout mNoContentLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContentLayout = findViewById(R.id.content);
        mNoContentLayout = findViewById(R.id.no_content);
        createNotificationChannel();
    }

    private void setupAd() {
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        FrameLayout frameLayout = findViewById(R.id.frame_ad_home);
        AdView adView = new AdView(this);
        adView.setAdUnitId(Constants.homeBannerAdUnitId);
        adView.setAdSize(AdSize.BANNER);
        frameLayout.addView(adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }

    @Override
    protected void onResume() {
        super.onResume();
        boot();
    }

    private void boot() {
        if(NetworkUtil.isNetworkAvailable(this)) {
            getMovies();
            getToken();
        } else {
            mContentLayout.setVisibility(View.GONE);
            mNoContentLayout.setVisibility(View.VISIBLE);
        }
    }

    private void getToken() {
        SharedPreferences sharedPref = this.getSharedPreferences(getString(R.string.token), Context.MODE_PRIVATE);
        int tokenId = sharedPref.getInt(getString(R.string.token_id), 0);

        if(tokenId == 0) {
            FirebaseMessaging.getInstance().getToken()
                    .addOnCompleteListener(new OnCompleteListener<String>() {
                        @Override
                        public void onComplete(@NonNull Task<String> task) {
                            if (!task.isSuccessful()) {
                                return;
                            }
                            // Get new FCM registration token
                            String token = task.getResult();
                            TokenUtil.saveToken(MainActivity.this, token);
                        }
                    });
        }
    }

    private void getMovies() {
        mListNowShowingMovies = new ArrayList<>();
        mListComingSoonMovies = new ArrayList<>();
        Ion.with(this)
                .load(Constants.apiBase + "movies")
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        JsonArray moviesJsonArray = result.getAsJsonArray("data");
                        for(Movie movie: Movie.getMovies(moviesJsonArray)) {
                            if(movie.isNowShowing()) {
                                mListNowShowingMovies.add(movie);
                            } else {
                                mListComingSoonMovies.add(movie);
                            }
                        }
                        setupViews();
                    }
                });
    }

    private void setupViews() {
        setupAd();
        mContentLayout.setVisibility(View.VISIBLE);
        mNoContentLayout.setVisibility(View.GONE);
        MoviesAdapter nowShowingAdapter = new MoviesAdapter(this, mListNowShowingMovies, true);
        nowShowingAdapter.setOnItemClickListener(this);
        RecyclerView mRvNowShowingMovies = findViewById(R.id.rv_now_showing);
        LinearLayoutManager nowShowingLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRvNowShowingMovies.setHasFixedSize(true);
        mRvNowShowingMovies.setLayoutManager(nowShowingLayoutManager);
        mRvNowShowingMovies.setAdapter(nowShowingAdapter);


        if(mListComingSoonMovies.size() > 0) {
            MoviesAdapter comingSoonAdapter = new MoviesAdapter(this, mListComingSoonMovies, false);
            comingSoonAdapter.setOnItemClickListener(this);
            TextView textComingSoon = findViewById(R.id.text_coming_soon);
            textComingSoon.setVisibility(View.VISIBLE);
            RecyclerView mRvComingSoonMovies = findViewById(R.id.rv_coming_soon);
            mRvComingSoonMovies.setVisibility(View.VISIBLE);
            mRvComingSoonMovies.setHasFixedSize(true);
            LinearLayoutManager comingSoonLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            mRvComingSoonMovies.setLayoutManager(comingSoonLayoutManager);
            mRvComingSoonMovies.setAdapter(comingSoonAdapter);
        }

        Objects.requireNonNull(getSupportActionBar()).setIcon(R.drawable.logo);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public void onClick(int position, boolean nowShowing) {
        Movie movie;
        if(nowShowing) {
             movie = mListNowShowingMovies.get(position);
        } else {
            movie = mListComingSoonMovies.get(position);
        }
        Intent intent = MoviesDetailActivity.getIntent(this, movie);
        startActivity(intent);
    }
}

