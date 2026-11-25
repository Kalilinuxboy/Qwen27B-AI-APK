package com.kalilinuxboy.qwen27b;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    private static final int FILE_CHOOSER_REQUEST = 701;
    private static final String PREFS = "qwen_settings";
    private static final String URL_KEY = "backend_url";
    private WebView webView;
    private EditText urlInput;
    private ValueCallback<Uri[]> fileCallback;

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        CookieManager.getInstance().setAcceptCookie(true);
        showConnectScreen();
    }

    private void showConnectScreen() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(28, 48, 28, 24);
        root.setBackgroundColor(0xFF0B0B1A);

        TextView title = new TextView(this);
        title.setText("Qwen 27B AI"); title.setTextColor(0xFFFFFFFF);
        title.setTextSize(26); title.setPadding(0, 0, 0, 12);
        root.addView(title);

        TextView help = new TextView(this);
        help.setText("Kaggle Notebook-এর শেষ সেলে পাওয়া সম্পূর্ণ Cloudflare URL (?k=... সহ) এখানে দিন। Kaggle session চালু থাকতে হবে।");
        help.setTextColor(0xFFD0D0E8); help.setTextSize(15); root.addView(help);

        urlInput = new EditText(this);
        urlInput.setSingleLine(true); urlInput.setHint("https://....trycloudflare.com/?k=...");
        urlInput.setTextColor(0xFFFFFFFF); urlInput.setHintTextColor(0xFF9999B8);
        urlInput.setSelectAllOnFocus(true); urlInput.setPadding(14, 12, 14, 12);
        urlInput.setBackgroundColor(0xFF23234A);
        SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        urlInput.setText(prefs.getString(URL_KEY, ""));
        LinearLayout.LayoutParams inputParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        inputParams.topMargin = 22; root.addView(urlInput, inputParams);

        Button connect = new Button(this); connect.setText("Connect");
        LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        buttonParams.topMargin = 16; root.addView(connect, buttonParams);
        connect.setOnClickListener(v -> connect());

        TextView note = new TextView(this);
        note.setText("ফ্রি Kaggle GPU সবসময় চালু থাকে না। নতুন session-এ URL বদলালে এখানে নতুন URL বসাতে হবে। URL গোপন রাখুন।");
        note.setTextColor(0xFFFFD18A); note.setTextSize(13);
        LinearLayout.LayoutParams noteParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        noteParams.topMargin = 22; root.addView(note, noteParams);
        setContentView(root);
    }

    private void connect() {
        String url = urlInput.getText().toString().trim();
        Uri uri = Uri.parse(url);
        String host = uri.getHost();
        if (!"https".equals(uri.getScheme()) || host == null || !host.endsWith(".trycloudflare.com") || uri.getQueryParameter("k") == null) {
            Toast.makeText(this, "সম্পূর্ণ https Cloudflare URL দিন, ?k= token-সহ", Toast.LENGTH_LONG).show();
            return;
        }
        getSharedPreferences(PREFS, MODE_PRIVATE).edit().putString(URL_KEY, url).apply();
        showWebView(url);
    }

    private void showWebView(String url) {
        webView = new WebView(this);
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true); settings.setDomStorageEnabled(true);
        settings.setAllowFileAccess(false); settings.setMediaPlaybackRequiresUserGesture(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String host = request.getUrl().getHost();
                return host == null || !host.endsWith(".trycloudflare.com");
            }
        });
        webView.setWebChromeClient(new WebChromeClient() {
            @Override public boolean onShowFileChooser(WebView view, ValueCallback<Uri[]> callback, FileChooserParams params) {
                if (fileCallback != null) fileCallback.onReceiveValue(null);
                fileCallback = callback;
                try { startActivityForResult(params.createIntent(), FILE_CHOOSER_REQUEST); }
                catch (Exception e) { fileCallback = null; callback.onReceiveValue(null); return false; }
                return true;
            }
        });
        setContentView(webView); webView.loadUrl(url);
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILE_CHOOSER_REQUEST && fileCallback != null) {
            Uri[] result = WebChromeClient.FileChooserParams.parseResult(resultCode, data);
            fileCallback.onReceiveValue(result); fileCallback = null;
        }
    }

    @Override public void onBackPressed() {
        if (webView != null && webView.canGoBack()) webView.goBack();
        else if (webView != null) { webView.destroy(); webView = null; showConnectScreen(); }
        else super.onBackPressed();
    }

    @Override protected void onDestroy() {
        if (fileCallback != null) fileCallback.onReceiveValue(null);
        if (webView != null) webView.destroy();
        super.onDestroy();
    }
}
