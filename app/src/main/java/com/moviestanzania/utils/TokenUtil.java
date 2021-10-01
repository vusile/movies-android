package com.moviestanzania.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.moviestanzania.Constants;
import com.moviestanzania.R;

public class TokenUtil {
    public static void saveToken(Context context, String token) {
        Ion.with(context)
                .load(Constants.apiBase + "save-token")
                .setBodyParameter("token", token)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        // do stuff with the result or error
                        if(result.getAsJsonObject("data") != null) {
                            context.getSharedPreferences(context.getString(R.string.token), Context.MODE_PRIVATE)
                                    .edit().putInt(context.getString(R.string.token_id), result.getAsJsonObject("data").get("id").getAsInt())
                                    .apply();
                        } else {
                            getTokenId(context, token);
                        }
                    }
                });

    }

    private static void getTokenId(Context context, String token) {
        Ion.with(context)
                .load(Constants.apiBase + "token-id")
                .setBodyParameter("token", token)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        // do stuff with the result or error
                        context.getSharedPreferences(context.getString(R.string.token), Context.MODE_PRIVATE)
                                .edit().putInt(context.getString(R.string.token_id), result.getAsJsonObject("data").get("id").getAsInt())
                                .apply();
                    }
                });
    }

    public static void updateToken(Context context, String token, String tokenId) {
        Ion.with(context)
                .load(Constants.apiBase + "update-token/" + tokenId)
                .setBodyParameter("token", token)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        // do stuff with the result or error
                        Log.d("TAG", "Fetching token successful in server");;
                    }
                });
    }
}
