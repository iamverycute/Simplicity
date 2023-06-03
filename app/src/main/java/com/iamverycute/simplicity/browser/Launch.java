package com.iamverycute.simplicity.browser;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.HttpAuthHandler;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.telly.groundy.util.DownloadUtils;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class Launch extends Activity implements TextView.OnEditorActionListener, DownloadListener, View.OnTouchListener {
    private WebView www;
    private final List<String> schemes = Arrays.asList("http", "https", "chrome");
    private int mRedirectedCount = 0;
    private ProgressBar progressBar;
    private InputMethodManager imm;

    private FrameLayout videoFull;

    @SuppressLint({"SetJavaScriptEnabled", "ClickableViewAccessibility"})
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        setContentView(R.layout.main);
        TextView Split = findViewById(R.id.domain);
        Split.setHeight(getStatusBarHeight());
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        www = findViewById(R.id.www);
        WebSettings settings = www.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setAllowContentAccess(true);
        settings.setAllowFileAccess(true);
        settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        settings.setDatabaseEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setBuiltInZoomControls(true);
        settings.setSupportZoom(true);
        settings.setDisplayZoomControls(false);
        settings.setMediaPlaybackRequiresUserGesture(true);
        settings.setGeolocationEnabled(false);
        settings.setBlockNetworkImage(false);
        settings.setBlockNetworkLoads(false);
        settings.setJavaScriptCanOpenWindowsAutomatically(false);
        progressBar = findViewById(R.id.progress);
        www.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onShowCustomView(View view, CustomViewCallback callback) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                videoFull.setVisibility(View.VISIBLE);
                videoFull.addView(view);
                super.onShowCustomView(view, callback);
            }

            @Override
            public void onHideCustomView() {
                if (videoFull.isShown()) {
                    videoFull.setVisibility(View.GONE);
                    videoFull.removeAllViews();
                }
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                super.onHideCustomView();
            }

            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                result.cancel();
                new SimpleUtils.SimpleDialog(Launch.this, view.getUrl(), message).addCancelBtn(R.string.confirm, null).show();
                return true;
            }

            @Override
            public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
                result.cancel();
                return true;
            }

            @Override
            public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
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

            private SimpleUtils.SimpleDialog.InputValues values;

            @Override
            public void onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler, String host, String realm) {
                values = new SimpleUtils.SimpleDialog(Launch.this, getString(R.string.basic), null).isHttpBasic().addCancelBtn(R.string.cancel, null)
                        .addOKBtn(R.string.confirm, (_a, _b) -> {
                            String text1 = String.valueOf(values.arg0.getText());
                            String text2 = String.valueOf(values.arg1.getText());
                            handler.proceed(text1, text2);
                            view.loadUrl(view.getUrl() + "?v=" + System.currentTimeMillis());
                        }).showForResult();
            }
        };
        WebView.setWebContentsDebuggingEnabled(false);
        CookieManager.getInstance().setAcceptThirdPartyCookies(www, true);
        www.setWebViewClient(client);
        www.setOnTouchListener(this);
        www.setDownloadListener(this);
        String getUrl = parseUrl(getIntent());
        if (parseUrl(getIntent()) == null) {
            www.loadUrl("cn.bing.com");
        } else {
            www.loadUrl(getUrl);
        }
        videoFull = findViewById(R.id.video_full);
        EditText searchBox = findViewById(R.id.box);
        searchBox.setOnEditorActionListener(this);
        search = findViewById(R.id.search);
        ImageButton download = findViewById(R.id.download);
        download.setOnClickListener(v -> startActivity(new Intent(this, DownloadListActivity.class)));
        scheduled = Executors.newSingleThreadScheduledExecutor();
    }

    private ScheduledExecutorService scheduled;

    private String parseUrl(Intent intent) {
        if (intent != null) {
            Uri uri = intent.getData();
            if (uri != null) {
                try {
                    URL url = new URL(uri.getScheme(), uri.getHost(), uri.getPath());
                    String formatUrl = url.toString();
                    String params = uri.getQuery();
                    if (params != null && !params.isEmpty()) {
                        formatUrl += "?" + params;
                    }
                    return formatUrl;
                } catch (MalformedURLException ignored) {
                }
            }
        }
        return null;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String getUrl = parseUrl(intent);
        if (getUrl != null) {
            www.loadUrl(getUrl);
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

    public static final List<DownloadItem> downloadHistories = new ArrayList<>();
    private String fileName;

    @Override
    public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
        fileName = URLUtil.guessFileName(url, contentDisposition, mimetype);
        if (fileName.endsWith(".bin")) {
            try {
                URL urlObj = new URL(url);
                String path = urlObj.getPath();
                int index = path.lastIndexOf("/") + 1;
                fileName = path.substring(index);
            } catch (MalformedURLException ignored) {
            }
        }
        new SimpleUtils.SimpleDialog(Launch.this, getString(R.string.down_title), fileName).addCancelBtn(R.string.cancel, null).addOKBtn(R.string.confirm, (dialogInterface, i) -> {
            File downloadDir = new SimpleUtils.SimpleDirectory().DownloadDir(Launch.this);
            if (downloadDir != null) {
                fileName = isSingleFileName(fileName);
                File downloadFile = new File(downloadDir.getAbsolutePath(), fileName);
                DownloadItem item = new DownloadItem();
                item.setDownloadFile(downloadFile);
                downloadHistories.add(item);
                scheduled.submit(() -> {
                    try {
                        DownloadUtils.downloadFile(this, url, downloadFile, (url1, progress) -> item.setProgress(progress));
                    } catch (IOException ignored) {
                    }
                });
                Toast.makeText(Launch.this, "开始下载：" + downloadFile.getName(), Toast.LENGTH_SHORT).show();
            }
        }).show();
    }

    private String isSingleFileName(String fileName) {
        long count = downloadHistories.stream().filter(item -> item.getDownloadFile().getName().equals(fileName)).count();
        if (count > 0) {
            return isSingleFileName("1." + fileName);
        }
        return fileName;
    }

    @SuppressLint({"DiscouragedApi", "InternalInsetResource"})
    public int getStatusBarHeight() {
        int height = 0;
        int resId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resId > 0) {
            height = getResources().getDimensionPixelSize(resId);
        }
        return height;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
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
                    moveTaskToBack(true);
                }
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}