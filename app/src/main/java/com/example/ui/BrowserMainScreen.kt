package com.example.ui

import android.webkit.WebView
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.*

@Composable
fun BrowserMainScreen(
    viewModel: BrowserViewModel = viewModel()
) {
    val tabs by viewModel.tabs.collectAsStateWithLifecycle()
    val activeTabId by viewModel.activeTabId.collectAsStateWithLifecycle()
    val activeTab by viewModel.activeTab.collectAsStateWithLifecycle()
    val settings by viewModel.settings.collectAsStateWithLifecycle()

    val bookmarks by viewModel.bookmarks.collectAsStateWithLifecycle()
    val history by viewModel.history.collectAsStateWithLifecycle()
    val downloads by viewModel.downloads.collectAsStateWithLifecycle()
    val isBookmarked by viewModel.isCurrentPageBookmarked.collectAsStateWithLifecycle()

    val isTabOverviewOpen by viewModel.isTabOverviewOpen.collectAsStateWithLifecycle()
    val isMenuOpen by viewModel.isMenuOpen.collectAsStateWithLifecycle()
    val isBookmarksHistoryOpen by viewModel.isBookmarksHistoryOpen.collectAsStateWithLifecycle()
    val isDownloadsOpen by viewModel.isDownloadsOpen.collectAsStateWithLifecycle()
    val isPageInfoOpen by viewModel.isPageInfoOpen.collectAsStateWithLifecycle()
    val findQuery by viewModel.findInPageQuery.collectAsStateWithLifecycle()

    var activeWebView by remember { mutableStateOf<WebView?>(null) }
    var isFindBarVisible by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            Column {
                AddressBar(
                    tab = activeTab,
                    isBookmarked = isBookmarked,
                    selectedEngineIndex = settings.selectedEngineIndex,
                    onNavigate = { input -> viewModel.navigateActiveTabTo(input) },
                    onRefresh = { activeWebView?.reload() },
                    onStop = { activeWebView?.stopLoading() },
                    onToggleBookmark = { viewModel.toggleBookmarkCurrentPage() },
                    onSelectEngine = { viewModel.setSelectedSearchEngine(it) },
                    onOpenPageInfo = { viewModel.setPageInfoOpen(true) }
                )

                if (isFindBarVisible) {
                    FindInPageBar(
                        onQueryChange = { query -> viewModel.setFindInPageQuery(query) },
                        onClose = { isFindBarVisible = false }
                    )
                }
            }
        },
        bottomBar = {
            BottomNavBar(
                activeTab = activeTab,
                tabCount = tabs.size,
                onBack = { activeWebView?.goBack() },
                onForward = { activeWebView?.goForward() },
                onHome = { viewModel.navigateActiveTabTo("about:blank") },
                onOpenTabs = { viewModel.setTabOverviewOpen(true) },
                onOpenMenu = { viewModel.setMenuOpen(true) }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (activeTab.url == "about:blank") {
                StartScreen(
                    selectedEngineIndex = settings.selectedEngineIndex,
                    isIncognito = activeTab.isIncognito,
                    onNavigate = { url -> viewModel.navigateActiveTabTo(url) },
                    onSelectEngine = { index -> viewModel.setSelectedSearchEngine(index) }
                )
            } else {
                key(activeTab.id) {
                    ComposeWebView(
                        tab = activeTab,
                        settings = settings,
                        findQuery = findQuery,
                        onUpdateTabInfo = { title, url, canBack, canForward ->
                            viewModel.updateTabInfo(activeTab.id, title, url, canBack, canForward)
                        },
                        onUpdateProgress = { progress, isLoading ->
                            viewModel.updateTabProgress(activeTab.id, progress, isLoading)
                        },
                        onDownloadTriggered = { url, contentDisp, mimeType ->
                            viewModel.triggerDownload(url, contentDisp, mimeType)
                        },
                        onWebViewCreated = { webView ->
                            activeWebView = webView
                        }
                    )
                }
            }
        }
    }

    // Modal Sheets & Dialogs
    if (isTabOverviewOpen) {
        TabOverviewSheet(
            tabs = tabs,
            activeTabId = activeTabId,
            onSwitchTab = { tabId -> viewModel.switchTab(tabId) },
            onCloseTab = { tabId -> viewModel.closeTab(tabId) },
            onNewTab = { isIncognito -> viewModel.openNewTab("about:blank", isIncognito) },
            onCloseAll = { viewModel.closeAllTabs() },
            onDismiss = { viewModel.setTabOverviewOpen(false) }
        )
    }

    if (isMenuOpen) {
        BrowserMenuSheet(
            activeTab = activeTab,
            settings = settings,
            onToggleDesktopMode = { viewModel.toggleDesktopMode() },
            onToggleAdBlock = { viewModel.toggleAdBlock() },
            onToggleNightMode = { viewModel.toggleNightMode() },
            onOpenBookmarksHistory = { viewModel.setBookmarksHistoryOpen(true) },
            onOpenDownloads = { viewModel.setDownloadsOpen(true) },
            onNewIncognitoTab = { viewModel.openNewTab("about:blank", true) },
            onOpenFindInPage = { isFindBarVisible = true },
            onOpenPageInfo = { viewModel.setPageInfoOpen(true) },
            onDismiss = { viewModel.setMenuOpen(false) }
        )
    }

    if (isBookmarksHistoryOpen) {
        BookmarksHistorySheet(
            bookmarks = bookmarks,
            history = history,
            onOpenUrl = { url -> viewModel.navigateActiveTabTo(url) },
            onDeleteBookmark = { bm -> viewModel.deleteBookmark(bm) },
            onDeleteHistory = { id -> viewModel.deleteHistory(id) },
            onClearAllHistory = { viewModel.clearAllHistory() },
            onDismiss = { viewModel.setBookmarksHistoryOpen(false) }
        )
    }

    if (isDownloadsOpen) {
        DownloadsSheet(
            downloads = downloads,
            onDeleteDownload = { id -> viewModel.deleteDownload(id) },
            onDismiss = { viewModel.setDownloadsOpen(false) }
        )
    }

    if (isPageInfoOpen) {
        PageInfoDialog(
            tab = activeTab,
            isAdBlockEnabled = settings.isAdBlockEnabled,
            onDismiss = { viewModel.setPageInfoOpen(false) }
        )
    }
}
