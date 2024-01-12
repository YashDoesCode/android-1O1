package com.keeper.components.webbridge;

import android.content.Context;
import android.webkit.WebResourceResponse;
import androidx.webkit.WebViewAssetLoader;

public final class OfflineAssetLoader {

    private final WebViewAssetLoader assetLoader;

    public OfflineAssetLoader(Context context) {
        this.assetLoader = new WebViewAssetLoader.Builder()
                .addPathHandler("/assets/", new WebViewAssetLoader.AssetsPathHandler(context))
                .addPathHandler("/res/", new WebViewAssetLoader.ResourcesPathHandler(context))
                .build();
    }

    public WebResourceResponse interceptRequest(android.net.Uri url) {
        return assetLoader.shouldInterceptRequest(url);
    }
}
