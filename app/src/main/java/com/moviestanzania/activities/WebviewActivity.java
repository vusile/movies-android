package com.moviestanzania.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.moviestanzania.R;

public class WebviewActivity extends AppCompatActivity {

    private static final String EXTRA_URL = "url";
    private WebView mWebView;
    private String mUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        mWebView = findViewById(R.id.webview);
        getExtras();
    }

    public static Intent getIntent(Context context, String url) {
        Intent intent = new Intent(context, WebviewActivity.class);

        intent.putExtra(EXTRA_URL, url);

        return intent;
    }

    private void getExtras() {
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            if (extras.containsKey(EXTRA_URL)) {
                mUrl = extras.getString(EXTRA_URL);
                mWebView.getSettings().setJavaScriptEnabled(true);
                mWebView.setWebViewClient(new WebViewClient() {
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        return false;
                    }
                });
                mWebView.loadUrl(mUrl);
            }
        }
    }
}