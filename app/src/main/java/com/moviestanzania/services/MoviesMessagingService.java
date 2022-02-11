package com.moviestanzania.services;

import static android.app.PendingIntent.FLAG_CANCEL_CURRENT;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.moviestanzania.Constants;
import com.moviestanzania.R;
import com.moviestanzania.activities.MainActivity;
import com.moviestanzania.activities.MoviesDetailActivity;
import com.moviestanzania.objects.Movie;
import com.moviestanzania.utils.TokenUtil;

import java.io.IOException;
import java.net.URL;

public class MoviesMessagingService extends FirebaseMessagingService{
    private static final String CHANNEL_ID = "movies";
    private int mMovieId;

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        sendTokenToServer(token);
    }

    private void sendTokenToServer(String token) {
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(getString(R.string.token), MODE_PRIVATE);
        int tokenId = sharedPreferences.getInt(getString(R.string.token_id), 0);

        if(tokenId != 0) {
            TokenUtil.updateToken(getApplicationContext(), token, String.valueOf(tokenId));
        } else {
            TokenUtil.saveToken(getApplicationContext(), token);
        }
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        mMovieId = Integer.parseInt(remoteMessage.getData().get("id"));
        getMovie(remoteMessage);

    }

    private void getMovie(RemoteMessage remoteMessage) {
        Ion.with(this)
                .load(Constants.apiBase + "movies/" + mMovieId)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        JsonObject moviesObject = result.getAsJsonObject("data");
                        Movie movie = Movie.getMovie(moviesObject);

                        String posterUrl = remoteMessage.getData().get("poster");
                        Intent intent = MoviesDetailActivity.getIntent(getBaseContext(), movie);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        PendingIntent pendingIntent = PendingIntent.getActivity(getBaseContext(), movie.getId(), intent, FLAG_CANCEL_CURRENT);

                        Bitmap poster = null;
                        try {
                            URL url = new URL(posterUrl);
                            poster = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                        } catch (IOException exception) {
                            System.out.println(exception);
                        }

                        NotificationCompat.Builder builder = new NotificationCompat.Builder(getBaseContext(), CHANNEL_ID)
                                .setSmallIcon(R.drawable.notification_icon)
                                .setContentTitle(remoteMessage.getData().get("notification_title"))
                                .setContentText(remoteMessage.getData().get("notification_body"))
                                .setStyle(new NotificationCompat.BigTextStyle()
                                        .bigText(remoteMessage.getData().get("notification_body")))
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                .addAction(R.drawable.notification_icon, getString(R.string.view_trailer),
                                        pendingIntent)
                                .setLargeIcon(poster)
                                .setStyle(new NotificationCompat.BigPictureStyle()
                                        .bigPicture(poster)
                                        .bigLargeIcon(null))
                                // Set the intent that will fire when the user taps the notification
                                .setContentIntent(pendingIntent)
                                .setAutoCancel(true);

                        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getBaseContext());

                        notificationManager.notify(mMovieId, builder.build());
                    }
                });
    }
}
