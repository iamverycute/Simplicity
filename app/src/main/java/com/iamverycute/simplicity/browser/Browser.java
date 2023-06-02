package com.iamverycute.simplicity.browser;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;

import java.net.MalformedURLException;
import java.net.URL;

public class Browser extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Uri uri = getIntent().getData();
        if (uri != null) {
            try {
                URL  url = new URL(uri.getScheme(), uri.getHost(), uri.getPath());
                String formatUrl = url + "?" + uri.getQuery();
                startActivity(new Intent(this, Launch.class).putExtra("url", formatUrl));
            } catch (MalformedURLException ignored) {
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
