# JavaScriptInterface Security Considerations

- Rejects reflection-based bridge invocation by requiring strict JSON payload validation.
- Annotates only target exposed methods with `@JavascriptInterface` on Android 4.2+ (API 17+).
- Validates origin domain headers on every incoming message to prevent untrusted iframe exploitation.
- Dispatches UI-bound calls onto `Looper.getMainLooper()` while running heavy operations in worker executors.
- Sanitizes incoming payload strings to prevent injection into native SQLite or command executors.
- Prohibits passing direct object references across the web-to-native memory boundary.
