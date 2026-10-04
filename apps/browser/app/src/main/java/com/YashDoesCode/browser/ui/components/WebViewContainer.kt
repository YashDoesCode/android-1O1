package com.YashDoesCode.browser.ui.components

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.DownloadListener
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.YashDoesCode.browser.core.domain.model.Tab
import com.YashDoesCode.browser.ui.FullBrowserAction

private const val DESKTOP_USER_AGENT =
    "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebViewContainer(
    activeTab: Tab,
    onAction: (FullBrowserAction) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Cache WebView instances per tab ID using remember
    val webView = remember(activeTab.id) {
        WebView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )

            settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                databaseEnabled = true
                useWideViewPort = true
                loadWithOverviewMode = true
                setSupportZoom(true)
                builtInZoomControls = true
                displayZoomControls = false
                // Safe defaults: file access disabled
                allowFileAccess = false
                allowContentAccess = false
            }

            CookieManager.getInstance().setAcceptCookie(true)
            CookieManager.getInstance().setAcceptThirdPartyCookies(this, true)

            setDownloadListener { url, userAgent, contentDisposition, mimeType, contentLength ->
                try {
                    val request = DownloadManager.Request(Uri.parse(url)).apply {
                        setMimeType(mimeType)
                        addRequestHeader("User-Agent", userAgent)
                        addRequestHeader("Cookie", CookieManager.getInstance().getCookie(url))
                        setTitle(Uri.parse(url).lastPathSegment ?: "download")
                        setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                        setDestinationInExternalPublicDir(
                            Environment.DIRECTORY_DOWNLOADS,
                            Uri.parse(url).lastPathSegment ?: "download"
                        )
                    }
                    val dm = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                    dm.enqueue(request)
                    Toast.makeText(context, "Downloading file...", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Toast.makeText(context, "Download failed: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Configure clients and properties when activeTab state changes
    AndroidView(
        factory = { webView },
        modifier = modifier,
        update = { view ->
            val defaultUserAgent = view.settings.userAgentString

            // User-agent toggle
            if (activeTab.isDesktopMode) {
                view.settings.userAgentString = DESKTOP_USER_AGENT
                view.settings.useWideViewPort = true
                view.settings.loadWithOverviewMode = true
            } else {
                view.settings.userAgentString = null // Reset to default
            }

            view.webViewClient = object : WebViewClient() {
                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    super.onPageStarted(view, url, favicon)
                    onAction(
                        FullBrowserAction.UpdateTabProgress(
                            tabId = activeTab.id,
                            progress = 10,
                            title = view?.title,
                            url = url,
                            canGoBack = view?.canGoBack() == true,
                            canGoForward = view?.canGoForward() == true
                        )
                    )
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    onAction(
                        FullBrowserAction.UpdateTabProgress(
                            tabId = activeTab.id,
                            progress = 100,
                            title = view?.title,
                            url = url,
                            canGoBack = view?.canGoBack() == true,
                            canGoForward = view?.canGoForward() == true
                        )
                    )
                }

                override fun onReceivedError(
                    view: WebView?,
                    request: WebResourceRequest?,
                    error: WebResourceError?
                ) {
                    super.onReceivedError(view, request, error)
                }

                override fun shouldOverrideUrlLoading(
                    view: WebView?,
                    request: WebResourceRequest?
                ): Boolean {
                    // Stay inside the webview for standard HTTP/HTTPS schemes
                    val url = request?.url?.toString() ?: return false
                    return !(url.startsWith("http://") || url.startsWith("https://"))
                }
            }

            view.webChromeClient = object : WebChromeClient() {
                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                    super.onProgressChanged(view, newProgress)
                    onAction(
                        FullBrowserAction.UpdateTabProgress(
                            tabId = activeTab.id,
                            progress = newProgress,
                            title = view?.title,
                            url = view?.url,
                            canGoBack = view?.canGoBack() == true,
                            canGoForward = view?.canGoForward() == true
                        )
                    )
                }

                override fun onReceivedTitle(view: WebView?, title: String?) {
                    super.onReceivedTitle(view, title)
                    onAction(
                        FullBrowserAction.UpdateTabProgress(
                            tabId = activeTab.id,
                            progress = activeTab.progress,
                            title = title,
                            url = view?.url,
                            canGoBack = view?.canGoBack() == true,
                            canGoForward = view?.canGoForward() == true
                        )
                    )
                }
            }

            // Load URL if changed
            if (view.url != activeTab.url && activeTab.url.isNotEmpty()) {
                view.loadUrl(activeTab.url)
            }
        }
    )

    DisposableEffect(activeTab.id) {
        onDispose {
            // Keep WebView cleanly managed
        }
    }
}
