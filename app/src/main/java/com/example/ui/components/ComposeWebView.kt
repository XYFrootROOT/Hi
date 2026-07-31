package com.example.ui.components

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.view.ViewGroup
import android.webkit.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.example.model.BrowserSettings
import com.example.model.TabItem

// Common ad tracker domains for simple built-in AdBlock
private val AD_DOMAINS = setOf(
    "doubleclick.net",
    "adservice.google.com",
    "google-analytics.com",
    "popads.net",
    "ad.baidu.com",
    "cpro.baidu.com",
    "pos.baidu.com",
    "eclick.baidu.com"
)

private const val DESKTOP_USER_AGENT =
    "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun ComposeWebView(
    tab: TabItem,
    settings: BrowserSettings,
    findQuery: String?,
    onUpdateTabInfo: (title: String?, url: String?, canGoBack: Boolean, canGoForward: Boolean) -> Unit,
    onUpdateProgress: (progress: Int, isLoading: Boolean) -> Unit,
    onDownloadTriggered: (url: String, contentDisposition: String?, mimeType: String?) -> Unit,
    modifier: Modifier = Modifier,
    onWebViewCreated: (WebView) -> Unit = {}
) {
    var webViewInstance by remember { mutableStateOf<WebView?>(null) }

    // React to Find in Page query changes
    LaunchedEffect(findQuery) {
        if (!findQuery.isNullOrBlank()) {
            webViewInstance?.findAllAsync(findQuery)
        } else {
            webViewInstance?.clearMatches()
        }
    }

    AndroidView(
        modifier = modifier.fillMaxSize(),
        factory = { context ->
            WebView(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )

                configureWebSettings(this, settings)

                webViewClient = object : WebViewClient() {
                    override fun shouldInterceptRequest(
                        view: WebView?,
                        request: WebResourceRequest?
                    ): WebResourceResponse? {
                        if (settings.isAdBlockEnabled && request != null) {
                            val host = request.url.host?.lowercase() ?: ""
                            if (AD_DOMAINS.any { host.contains(it) }) {
                                // Block ad request with empty response
                                return WebResourceResponse("text/plain", "UTF-8", null)
                            }
                        }
                        return super.shouldInterceptRequest(view, request)
                    }

                    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                        super.onPageStarted(view, url, favicon)
                        onUpdateProgress(15, true)
                        onUpdateTabInfo(view?.title, url, view?.canGoBack() == true, view?.canGoForward() == true)
                    }

                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                        onUpdateProgress(100, false)
                        onUpdateTabInfo(view?.title, url, view?.canGoBack() == true, view?.canGoForward() == true)

                        if (settings.isNightMode && view != null) {
                            // Inject dark mode CSS for dark mode experience
                            val css = "document.body.style.backgroundColor='#121212'; document.body.style.color='#e0e0e0';"
                            view.evaluateJavascript(css, null)
                        }
                    }

                    override fun doUpdateVisitedHistory(view: WebView?, url: String?, isReload: Boolean) {
                        super.doUpdateVisitedHistory(view, url, isReload)
                        onUpdateTabInfo(view?.title, url, view?.canGoBack() == true, view?.canGoForward() == true)
                    }
                }

                webChromeClient = object : WebChromeClient() {
                    override fun onProgressChanged(view: WebView?, newProgress: Int) {
                        super.onProgressChanged(view, newProgress)
                        onUpdateProgress(newProgress, newProgress < 100)
                    }

                    override fun onReceivedTitle(view: WebView?, title: String?) {
                        super.onReceivedTitle(view, title)
                        onUpdateTabInfo(title, view?.url, view?.canGoBack() == true, view?.canGoForward() == true)
                    }
                }

                setDownloadListener { url, userAgent, contentDisposition, mimetype, contentLength ->
                    onDownloadTriggered(url, contentDisposition, mimetype)
                }

                webViewInstance = this
                onWebViewCreated(this)

                if (tab.url != "about:blank" && tab.url.isNotBlank()) {
                    loadUrl(tab.url)
                }
            }
        },
        update = { webView ->
            // Check if settings or URL changed
            configureWebSettings(webView, settings)

            if (webView.url != tab.url && tab.url != "about:blank" && tab.url.isNotBlank()) {
                webView.loadUrl(tab.url)
            }
        }
    )
}

private fun configureWebSettings(webView: WebView, settings: BrowserSettings) {
    webView.settings.apply {
        javaScriptEnabled = true
        domStorageEnabled = true
        databaseEnabled = true
        useWideViewPort = true
        loadWithOverviewMode = true
        setSupportZoom(true)
        builtInZoomControls = true
        displayZoomControls = false
        allowFileAccess = true
        allowContentAccess = true

        // User Agent configuration (Desktop vs Mobile)
        userAgentString = if (settings.isDesktopMode) {
            DESKTOP_USER_AGENT
        } else {
            null // Default mobile UA
        }

        // Night mode force dark setting if supported by AGP / Android system
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val forceDarkOption = if (settings.isNightMode) {
                WebSettings.FORCE_DARK_ON
            } else {
                WebSettings.FORCE_DARK_OFF
            }
            try {
                @Suppress("DEPRECATION")
                forceDark = forceDarkOption
            } catch (_: Exception) {}
        }
    }
}
