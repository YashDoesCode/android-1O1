package com.keeper.components.webbridge;

import android.webkit.WebView;
import org.json.JSONObject;

public final class NativeToWebChannel {

    private final WebView webView;

    public NativeToWebChannel(WebView webView) {
        this.webView = webView;
    }

    public void dispatchEvent(String eventName, JSONObject data) {
        String jsonStr = data != null ? data.toString() : "{}";
        String js = String.format("window.dispatchEvent(new CustomEvent('%s', { detail: %s }));", eventName, jsonStr);
        webView.post(() -> webView.evaluateJavascript(js, null));
    }
}
