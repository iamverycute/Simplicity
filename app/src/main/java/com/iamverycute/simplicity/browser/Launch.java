package com.iamverycute.simplicity.browser;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Launch extends Activity implements TextView.OnEditorActionListener, DownloadListener, View.OnTouchListener, BinaryRequest.BinaryResponse {
    private WebView www;
    private final List<String> schemes = Arrays.asList("http", "https", "chrome");
    private int mRedirectedCount = 0;
    private ProgressBar progressBar;
    private InputMethodManager imm;

    @SuppressLint({"SetJavaScriptEnabled", "ClickableViewAccessibility"})
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        TextView Split = (TextView) findViewById(R.id.domain);
        Split.setHeight(getStatusBarHeight());
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        www = (WebView) findViewById(R.id.www);
        WebSettings settings = www.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setAllowContentAccess(true);
        settings.setAllowFileAccess(true);
        settings.setAllowFileAccessFromFileURLs(true);
        settings.setAllowUniversalAccessFromFileURLs(true);
        settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        settings.setSaveFormData(true);
        settings.setAppCacheEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setMediaPlaybackRequiresUserGesture(false);
        File filesDir = getExternalFilesDir("");
        if (filesDir != null) {
            String path = filesDir.getAbsolutePath();
            settings.setAppCachePath(path);
        }
        settings.setGeolocationEnabled(false);
        settings.setBlockNetworkImage(false);
        settings.setBlockNetworkLoads(false);
        settings.setJavaScriptCanOpenWindowsAutomatically(false);
        progressBar = (ProgressBar) findViewById(R.id.progress);
        www.setWebChromeClient(new WebChromeClient() {

            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                new AlertDialog.Builder(Launch.this).setTitle(view.getUrl()).setMessage(message).setNegativeButton("关闭", null).show();
                result.cancel();
                return true;
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                progressBar.setProgress(newProgress);
                if (newProgress != 100) {
                    progressBar.setVisibility(View.VISIBLE);
                }
                super.onProgressChanged(view, newProgress);
            }
        });
        WebViewClient client = new WebViewClient() {
            boolean mIsPageFinished = true;

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                mIsPageFinished = false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                mIsPageFinished = true;
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
                super.doUpdateVisitedHistory(view, url, isReload);
                if (!mIsPageFinished) {
                    mRedirectedCount++;
                }
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                Uri uri = request.getUrl();
                String currentUrl = uri.toString();
                if (schemes.contains(uri.getScheme())) {
                    view.loadUrl(currentUrl);
                }
                if (mIsPageFinished) {
                    mRedirectedCount = 0;
                }
                return true;
            }

            @SuppressLint("WebViewClientOnReceivedSslError")
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }
        };
        WebView.setWebContentsDebuggingEnabled(false);
        CookieManager.getInstance().setAcceptThirdPartyCookies(www, true);
        www.setWebViewClient(client);
        www.setOnTouchListener(this);
        www.setDownloadListener(this);
        www.loadUrl("cn.bing.com");
        EditText searchBox = (EditText) findViewById(R.id.box);
        searchBox.setOnEditorActionListener(this);
        search = (RelativeLayout) findViewById(R.id.search);
        ImageButton download = (ImageButton) findViewById(R.id.download);
        download.setOnClickListener(v -> startActivity(new Intent(this, DownloadListActivity.class)));
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null) {
            String url = intent.getStringExtra("url");
            if (url != null && !url.trim().equals(""))
                if (www != null) www.loadUrl(url);
        }
    }

    private RelativeLayout search;

    @Override
    public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            String inputText = String.valueOf(textView.getText()).trim();
            if (!inputText.isEmpty()) {
                String ifUrl = inputText;
                if (!inputText.startsWith("http")) {
                    ifUrl = "http://" + ifUrl;
                }
                boolean isUrl = Patterns.WEB_URL.matcher(ifUrl).matches();
                if (isUrl) {
                    www.loadUrl(ifUrl);
                } else {
                    www.loadUrl("https://cn.bing.com/search?q=" + inputText);
                }
            }
            search.setVisibility(View.GONE);
        }
        if (imm != null) {
            imm.hideSoftInputFromWindow(textView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
        return true;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        search.setVisibility(View.GONE);
        return view.performClick();
    }

    private final List<DownloadItem> downloadHistories = new ArrayList<>();
    private String fileName;

    @Override
    public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
        fileName = URLUtil.guessFileName(url, contentDisposition, mimetype);
        new AlertDialog.Builder(this).setTitle("下载文件").setMessage(fileName).setNegativeButton("取消", null).setPositiveButton("下载", (dialogInterface, i) -> {
            if (ActivityCompat.checkSelfPermission(Launch.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(Launch.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                return;
            }
            File downloadDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "Download");
            if (downloadDir.exists()) {
                if (downloadDir.canWrite()) {
                    fileName = isSingleFileName(fileName);
                    File downloadFile = new File(downloadDir.getAbsolutePath(), fileName);
                    DownloadItem item = new DownloadItem();
                    item.setDownloadFile(downloadFile);
                    int size = downloadHistories.size();
                    item.setRequest(new BinaryRequest(Request.Method.GET, url, Launch.this, size));
                    downloadHistories.add(item);
                    Volley.newRequestQueue(Launch.this).add(item.getRequest());
                    Toast.makeText(Launch.this, "开始下载" + downloadFile.getName(), Toast.LENGTH_SHORT).show();
                }
            }

        }).show();
    }

    @Override
    public void onResponse(byte[] data, int index) {
        File downloadFile = downloadHistories.get(index).getDownloadFile();
        try (FileOutputStream out = new FileOutputStream(downloadFile)) {
            out.write(data);
            Toast.makeText(this, downloadFile.getName() + ",下载成功", Toast.LENGTH_SHORT).show();
        } catch (IOException ignored) {
        }
    }

    @Override
    public void onError(VolleyError error, int index) {
    }

    private String isSingleFileName(String fileName) {
        long count = downloadHistories.stream().filter(item -> item.getDownloadFile().getName().equals(fileName)).count();
        if (count > 0) {
            return isSingleFileName("1." + fileName);
        }
        return fileName;
    }

    @SuppressLint("InternalInsetResource")
    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    @Override
    public void onBackPressed() {
        if (!search.isShown()) {
            search.setVisibility(View.VISIBLE);
        } else {
            if (www.canGoBack()) {
                if (mRedirectedCount > 0) {
                    while (mRedirectedCount > 0) {
                        www.goBack();
                        mRedirectedCount--;
                    }
                    mRedirectedCount = 0;
                } else {
                    www.goBack();
                }
            } else {
                super.onBackPressed();
            }
        }
    }
}