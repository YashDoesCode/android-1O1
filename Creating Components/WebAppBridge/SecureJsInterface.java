package com.keeper.components.webbridge;

import android.webkit.JavascriptInterface;
import org.json.JSONObject;

public class SecureJsInterface {

    public interface BridgeListener {
        void onNativeAction(String action, JSONObject payload);
    }

    private final BridgeListener listener;

    public SecureJsInterface(BridgeListener listener) {
        this.listener = listener;
    }

    @JavascriptInterface
    public void postMessage(String jsonString) {
        if (jsonString == null || jsonString.isEmpty()) {
            return;
        }
        try {
            JSONObject obj = new JSONObject(jsonString);
            String action = obj.optString("action", "unknown");
            JSONObject payload = obj.optJSONObject("payload");
            if (listener != null) {
                listener.onNativeAction(action, payload != null ? payload : new JSONObject());
            }
        } catch (Exception ignored) {
        }
    }
}
