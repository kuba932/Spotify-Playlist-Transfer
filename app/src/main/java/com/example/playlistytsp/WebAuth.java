package com.example.playlistytsp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.net.http.SslError;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


/**
 * Class designed solely for authorization purpose. It is responsible for initiating a WebView for a Spotify authorization process
 */

public class WebAuth extends AppCompatActivity {

    String url;
    WebView webView;
    String redirectUri;

    public WebAuth(){}


    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_auth);

        webView = findViewById(R.id.webview);

        webView.setWebViewClient(new MyWebViewClient(){
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                Log.e("webView error", error.toString());
                view.loadUrl(url);
            }


        });

        webView.getSettings().setJavaScriptEnabled(true);
        webView.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.setBackgroundColor(Color.TRANSPARENT);

        url = getIntent().getStringExtra("URL");
        redirectUri = getIntent().getStringExtra("redirect_uri");

        webView.loadUrl(url);

    }

    /**
     * MyWebViewClient entity is responsible for getting 'code' from redirect Uri.
     * This code is later needed to create a valid Spotify token.
     * currentURl - represent a current URl address and if it is the same as redirectUri, the 'code part' is being assigned to a proper variable ('code')
     */

    private class MyWebViewClient extends WebViewClient {

        String currentUrl;

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            view.loadUrl(url);

            currentUrl=url;
            String tempURLCut = currentUrl.substring(0, 70);

            if (tempURLCut.compareTo(redirectUri) == 0) {
                Intent resultIntent = new Intent();
                String codeRedirected = currentUrl.substring(76);
                resultIntent.putExtra("code", codeRedirected);

                setResult(RESULT_OK, resultIntent);
                finish();
            }

            return true;
        }
    }

    @Override
    public void onBackPressed() {

        if (webView.canGoBack()){
            webView.goBack();
        }else {
            super.onBackPressed();
        }
    }
}

