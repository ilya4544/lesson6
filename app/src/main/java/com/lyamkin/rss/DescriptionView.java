package com.lyamkin.rss;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;


public class DescriptionView extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.description);
        setTitle(getIntent().getStringExtra("title"));
        WebView webView = ((WebView) findViewById(R.id.web_view));
        webView.getSettings().setDefaultTextEncodingName("utf-8");
        webView.loadDataWithBaseURL(null, getIntent().getStringExtra("description"), "text/html", "en_US", null);
    }
}
