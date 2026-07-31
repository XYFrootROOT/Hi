package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.BookmarkEntity
import com.example.data.BrowserDatabase
import com.example.data.BrowserRepository
import com.example.data.DownloadEntity
import com.example.data.HistoryEntity
import com.example.model.BrowserSettings
import com.example.model.SearchEngine
import com.example.model.TabItem
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class BrowserViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = BrowserRepository(BrowserDatabase.getDatabase(application))

    private val _tabs = MutableStateFlow<List<TabItem>>(listOf(TabItem(url = "about:blank", title = "新标签页")))
    val tabs: StateFlow<List<TabItem>> = _tabs.asStateFlow()

    private val _activeTabId = MutableStateFlow<String>(_tabs.value.first().id)
    val activeTabId: StateFlow<String> = _activeTabId.asStateFlow()

    val activeTab: StateFlow<TabItem> = combine(_tabs, _activeTabId) { tabsList, activeId ->
        tabsList.find { it.id == activeId } ?: tabsList.firstOrNull() ?: TabItem()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), _tabs.value.first())

    private val _settings = MutableStateFlow(BrowserSettings())
    val settings: StateFlow<BrowserSettings> = _settings.asStateFlow()

    // Dialog & UI Sheet States
    private val _isTabOverviewOpen = MutableStateFlow(false)
    val isTabOverviewOpen: StateFlow<Boolean> = _isTabOverviewOpen.asStateFlow()

    private val _isMenuOpen = MutableStateFlow(false)
    val isMenuOpen: StateFlow<Boolean> = _isMenuOpen.asStateFlow()

    private val _isBookmarksHistoryOpen = MutableStateFlow(false)
    val isBookmarksHistoryOpen: StateFlow<Boolean> = _isBookmarksHistoryOpen.asStateFlow()

    private val _isDownloadsOpen = MutableStateFlow(false)
    val isDownloadsOpen: StateFlow<Boolean> = _isDownloadsOpen.asStateFlow()

    private val _isPageInfoOpen = MutableStateFlow(false)
    val isPageInfoOpen: StateFlow<Boolean> = _isPageInfoOpen.asStateFlow()

    private val _findInPageQuery = MutableStateFlow<String?>(null)
    val findInPageQuery: StateFlow<String?> = _findInPageQuery.asStateFlow()

    // Room Database Data Streams
    val bookmarks: StateFlow<List<BookmarkEntity>> = repository.bookmarks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val history: StateFlow<List<HistoryEntity>> = repository.history
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val downloads: StateFlow<List<DownloadEntity>> = repository.downloads
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val isCurrentPageBookmarked: StateFlow<Boolean> = combine(activeTab, bookmarks) { tab, bList ->
        bList.any { it.url == tab.url }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    // Navigation & URL Processing
    fun processUrlOrQuery(input: String): String {
        val trimmed = input.trim()
        if (trimmed.isEmpty()) return "about:blank"
        if (trimmed.startsWith("about:") || trimmed.startsWith("javascript:")) return trimmed

        val isUrl = trimmed.contains(".") && !trimmed.contains(" ") ||
                trimmed.startsWith("http://") || trimmed.startsWith("https://") || trimmed.startsWith("file://")

        return if (isUrl) {
            if (!trimmed.startsWith("http://") && !trimmed.startsWith("https://") && !trimmed.startsWith("file://")) {
                "https://$trimmed"
            } else {
                trimmed
            }
        } else {
            val engine = SearchEngine.DefaultEngines.getOrElse(_settings.value.selectedEngineIndex) { SearchEngine.DefaultEngines.first() }
            engine.getSearchUrl(trimmed)
        }
    }

    fun navigateActiveTabTo(input: String) {
        val targetUrl = processUrlOrQuery(input)
        val currentActiveId = _activeTabId.value
        _tabs.value = _tabs.value.map { tab ->
            if (tab.id == currentActiveId) {
                tab.copy(url = targetUrl, isLoading = true, progress = 10)
            } else tab
        }
    }

    fun updateTabInfo(tabId: String, title: String?, url: String?, canGoBack: Boolean, canGoForward: Boolean) {
        _tabs.value = _tabs.value.map { tab ->
            if (tab.id == tabId) {
                val newUrl = url ?: tab.url
                val newTitle = title ?: tab.title
                if (newUrl != "about:blank" && !tab.isIncognito && newUrl.isNotBlank()) {
                    viewModelScope.launch {
                        repository.addHistory(newTitle, newUrl)
                    }
                }
                tab.copy(
                    url = newUrl,
                    title = if (newUrl == "about:blank") "新标签页" else (newTitle.ifBlank { newUrl }),
                    canGoBack = canGoBack,
                    canGoForward = canGoForward
                )
            } else tab
        }
    }

    fun updateTabProgress(tabId: String, progress: Int, isLoading: Boolean) {
        _tabs.value = _tabs.value.map { tab ->
            if (tab.id == tabId) {
                tab.copy(progress = progress, isLoading = isLoading)
            } else tab
        }
    }

    // Tabs Management
    fun openNewTab(url: String = "about:blank", isIncognito: Boolean = false) {
        val newTab = TabItem(
            url = url,
            title = if (url == "about:blank") "新标签页" else "加载中...",
            isIncognito = isIncognito
        )
        _tabs.value = _tabs.value + newTab
        _activeTabId.value = newTab.id
        _isTabOverviewOpen.value = false
    }

    fun closeTab(tabId: String) {
        val currentTabs = _tabs.value
        if (currentTabs.size <= 1) {
            // If last tab is closed, reset to new empty tab
            val freshTab = TabItem()
            _tabs.value = listOf(freshTab)
            _activeTabId.value = freshTab.id
            return
        }

        val index = currentTabs.indexOfFirst { it.id == tabId }
        val updatedList = currentTabs.filterNot { it.id == tabId }
        _tabs.value = updatedList

        if (_activeTabId.value == tabId) {
            val nextIndex = (index - 1).coerceAtLeast(0)
            _activeTabId.value = updatedList[nextIndex].id
        }
    }

    fun switchTab(tabId: String) {
        if (_tabs.value.any { it.id == tabId }) {
            _activeTabId.value = tabId
            _isTabOverviewOpen.value = false
        }
    }

    fun closeAllTabs() {
        val freshTab = TabItem()
        _tabs.value = listOf(freshTab)
        _activeTabId.value = freshTab.id
        _isTabOverviewOpen.value = false
    }

    // Bookmarks & History
    fun toggleBookmarkCurrentPage() {
        val tab = activeTab.value
        if (tab.url == "about:blank") return
        viewModelScope.launch {
            repository.toggleBookmark(tab.title, tab.url)
        }
    }

    fun deleteBookmark(bookmark: BookmarkEntity) {
        viewModelScope.launch {
            repository.deleteBookmark(bookmark)
        }
    }

    fun deleteHistory(id: Long) {
        viewModelScope.launch {
            repository.deleteHistoryItem(id)
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            repository.clearHistory()
        }
    }

    // Settings
    fun setSelectedSearchEngine(index: Int) {
        _settings.value = _settings.value.copy(selectedEngineIndex = index)
    }

    fun toggleDesktopMode() {
        _settings.value = _settings.value.copy(isDesktopMode = !_settings.value.isDesktopMode)
    }

    fun toggleAdBlock() {
        _settings.value = _settings.value.copy(isAdBlockEnabled = !_settings.value.isAdBlockEnabled)
    }

    fun toggleNightMode() {
        _settings.value = _settings.value.copy(isNightMode = !_settings.value.isNightMode)
    }

    // Sheet visibility toggles
    fun setTabOverviewOpen(open: Boolean) { _isTabOverviewOpen.value = open }
    fun setMenuOpen(open: Boolean) { _isMenuOpen.value = open }
    fun setBookmarksHistoryOpen(open: Boolean) { _isBookmarksHistoryOpen.value = open }
    fun setDownloadsOpen(open: Boolean) { _isDownloadsOpen.value = open }
    fun setPageInfoOpen(open: Boolean) { _isPageInfoOpen.value = open }

    // Find in page
    fun setFindInPageQuery(query: String?) {
        _findInPageQuery.value = query
    }

    // Download simulated handler
    fun triggerDownload(url: String, contentDisposition: String?, mimeType: String?) {
        val fileName = url.substringAfterLast("/").substringBefore("?").ifBlank { "download_file" }
        viewModelScope.launch {
            repository.addDownload(fileName, url, "1.8 MB")
            _isDownloadsOpen.value = true
        }
    }

    fun deleteDownload(id: Long) {
        viewModelScope.launch {
            repository.deleteDownload(id)
        }
    }
}
