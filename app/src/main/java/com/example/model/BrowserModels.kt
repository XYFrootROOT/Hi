package com.example.model

import androidx.compose.ui.graphics.Color
import java.util.UUID

data class TabItem(
    val id: String = UUID.randomUUID().toString(),
    val url: String = "about:blank",
    val title: String = "新标签页",
    val faviconUrl: String? = null,
    val isLoading: Boolean = false,
    val progress: Int = 0,
    val canGoBack: Boolean = false,
    val canGoForward: Boolean = false,
    val isIncognito: Boolean = false,
    val themeColor: Color = Color(0xFF1E293B)
)

data class SpeedDialItem(
    val title: String,
    val url: String,
    val iconLetter: String,
    val bgColor: Color,
    val category: String = "热门"
)

data class SearchEngine(
    val name: String,
    val searchUrlPattern: String, // e.g. "https://www.baidu.com/s?wd=%s"
    val iconLetter: String
) {
    fun getSearchUrl(query: String): String {
        val encodedQuery = java.net.URLEncoder.encode(query, "UTF-8")
        return String.format(searchUrlPattern, encodedQuery)
    }

    companion object {
        val DefaultEngines = listOf(
            SearchEngine("百度", "https://www.baidu.com/s?wd=%s", "百"),
            SearchEngine("Bing", "https://cn.bing.com/search?q=%s", "必"),
            SearchEngine("Google", "https://www.google.com/search?q=%s", "谷"),
            SearchEngine("搜狗", "https://www.sogou.com/web?query=%s", "搜"),
            SearchEngine("DuckDuckGo", "https://duckduckgo.com/?q=%s", "鸭")
        )
    }
}

data class NewsItem(
    val id: String,
    val title: String,
    val source: String,
    val timeAgo: String,
    val category: String,
    val url: String,
    val readCount: String = "10万+阅读"
)

data class BrowserSettings(
    val selectedEngineIndex: Int = 0,
    val isAdBlockEnabled: Boolean = true,
    val isDesktopMode: Boolean = false,
    val isNightMode: Boolean = false,
    val isIncognitoDefault: Boolean = false,
    val blockPopups: Boolean = true
)
