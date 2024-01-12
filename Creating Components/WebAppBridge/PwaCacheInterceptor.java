package com.keeper.components.webbridge;

import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public final class PwaCacheInterceptor {

    private PwaCacheInterceptor() {
    }

    public static WebResourceResponse intercept(WebResourceRequest request) {
        String url = request.getUrl().toString();
        if (url.endsWith(".png") || url.endsWith(".jpg") || url.endsWith(".css") || url.endsWith(".js")) {
            try {
                HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
                conn.setConnectTimeout(3000);
                conn.setReadTimeout(5000);
                InputStream stream = conn.getInputStream();
                String mime = conn.getContentType();
                return new WebResourceResponse(mime, "UTF-8", stream);
            } catch (Exception ignored) {
            }
        }
        return null;
    }
}
