package com.moviestanzania.services;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.moviestanzania.R;
import com.moviestanzania.activities.MoviesDetailActivity;
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
        String posterUrl = remoteMessage.getData().get("poster");
        Intent intent = MoviesDetailActivity.getIntent(this, mMovieId);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        Bitmap poster = null;
        try {
            URL url = new URL(posterUrl);
            poster = BitmapFactory.decodeStream(url.openConnection().getInputStream());
        } catch (IOException e) {
            System.out.println(e);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
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

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        notificationManager.notify(mMovieId, builder.build());

    }
}
