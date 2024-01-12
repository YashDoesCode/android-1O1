package com.keeper.components.webbridge;

import android.content.Context;
import android.webkit.WebSettings;
import android.webkit.WebView;

public final class AndroidWebBridge {

    private final WebView webView;
    private final Context context;

    public AndroidWebBridge(Context context, WebView webView) {
        this.context = context;
        this.webView = webView;
        configureSettings();
    }

    private void configureSettings() {
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setAllowFileAccess(false);
        settings.setAllowContentAccess(true);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
    }

    public void registerBridge(Object bridgeInterface, String namespace) {
        webView.addJavascriptInterface(bridgeInterface, namespace);
    }

    public void evaluateScript(String script) {
        webView.evaluateJavascript(script, null);
    }
}
