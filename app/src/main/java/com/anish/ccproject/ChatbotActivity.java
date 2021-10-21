package com.anish.ccproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class ChatbotActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatbot);

        WebView myWebView = (WebView) findViewById(R.id.webView);
        //String websitelink = getIntent().getStringExtra("website-link");
        myWebView.loadUrl("https://midnightbot.github.io/CCL-Project/");
        // myWebView.loadUrl("https://midnightbot.github.io/leaderboar-try/");

        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
    }
}