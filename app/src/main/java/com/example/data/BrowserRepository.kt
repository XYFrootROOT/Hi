package com.example.data

import kotlinx.coroutines.flow.Flow

class BrowserRepository(private val database: BrowserDatabase) {
    val bookmarks: Flow<List<BookmarkEntity>> = database.bookmarkDao().getAllBookmarks()
    val history: Flow<List<HistoryEntity>> = database.historyDao().getAllHistory()
    val downloads: Flow<List<DownloadEntity>> = database.downloadDao().getAllDownloads()

    suspend fun isBookmarked(url: String): Boolean {
        return database.bookmarkDao().getBookmarkByUrl(url) != null
    }

    suspend fun toggleBookmark(title: String, url: String) {
        val existing = database.bookmarkDao().getBookmarkByUrl(url)
        if (existing != null) {
            database.bookmarkDao().deleteBookmark(existing)
        } else {
            database.bookmarkDao().insertBookmark(BookmarkEntity(title = title, url = url))
        }
    }

    suspend fun deleteBookmark(bookmark: BookmarkEntity) {
        database.bookmarkDao().deleteBookmark(bookmark)
    }

    suspend fun addHistory(title: String, url: String) {
        if (title.isBlank() || url.startsWith("about:") || url.isBlank()) return
        database.historyDao().insertHistory(
            HistoryEntity(title = title.ifBlank { url }, url = url)
        )
    }

    suspend fun deleteHistoryItem(id: Long) {
        database.historyDao().deleteHistoryById(id)
    }

    suspend fun clearHistory() {
        database.historyDao().clearAllHistory()
    }

    suspend fun addDownload(fileName: String, url: String, fileSize: String): Long {
        val entity = DownloadEntity(
            fileName = fileName,
            url = url,
            fileSize = fileSize,
            progress = 100,
            status = "COMPLETED"
        )
        database.downloadDao().insertDownload(entity)
        return entity.id
    }

    suspend fun deleteDownload(id: Long) {
        database.downloadDao().deleteDownloadById(id)
    }
}
