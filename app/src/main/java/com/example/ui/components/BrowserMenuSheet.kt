package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BrowserSettings
import com.example.model.TabItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrowserMenuSheet(
    activeTab: TabItem,
    settings: BrowserSettings,
    onToggleDesktopMode: () -> Unit,
    onToggleAdBlock: () -> Unit,
    onToggleNightMode: () -> Unit,
    onOpenBookmarksHistory: () -> Unit,
    onOpenDownloads: () -> Unit,
    onNewIncognitoTab: () -> Unit,
    onOpenFindInPage: () -> Unit,
    onOpenPageInfo: () -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = "工具箱与设置",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Grid 1: Main Quick Tools (3 Columns)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                MenuItemTile(
                    title = "书签与历史",
                    icon = Icons.Outlined.BookmarkBorder,
                    onClick = {
                        onDismiss()
                        onOpenBookmarksHistory()
                    },
                    modifier = Modifier.weight(1f)
                )

                MenuItemTile(
                    title = "下载内容",
                    icon = Icons.Outlined.Download,
                    onClick = {
                        onDismiss()
                        onOpenDownloads()
                    },
                    modifier = Modifier.weight(1f)
                )

                MenuItemTile(
                    title = "无痕标签",
                    icon = Icons.Outlined.VisibilityOff,
                    tint = Color(0xFFA855F7),
                    onClick = {
                        onDismiss()
                        onNewIncognitoTab()
                    },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                MenuItemTile(
                    title = "复制链接",
                    icon = Icons.Outlined.ContentCopy,
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("URL", activeTab.url)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "链接已复制到剪贴板", Toast.LENGTH_SHORT).show()
                        onDismiss()
                    },
                    modifier = Modifier.weight(1f)
                )

                MenuItemTile(
                    title = "分享网页",
                    icon = Icons.Outlined.Share,
                    onClick = {
                        val sendIntent: Intent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, activeTab.url)
                            type = "text/plain"
                        }
                        context.startActivity(Intent.createChooser(sendIntent, "分享链接"))
                        onDismiss()
                    },
                    modifier = Modifier.weight(1f)
                )

                MenuItemTile(
                    title = "页内查找",
                    icon = Icons.Outlined.Search,
                    onClick = {
                        onDismiss()
                        onOpenFindInPage()
                    },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))

            // Switches list for Toggles
            MenuSwitchRow(
                title = "桌面版网站 (Desktop Mode)",
                subtitle = "请求电脑版网页布局",
                icon = Icons.Outlined.DesktopWindows,
                isChecked = settings.isDesktopMode,
                onCheckedChange = { onToggleDesktopMode() }
            )

            MenuSwitchRow(
                title = "广告拦截 (AdBlock)",
                subtitle = "自动过滤恶意广告与追踪器",
                icon = Icons.Outlined.Shield,
                isChecked = settings.isAdBlockEnabled,
                onCheckedChange = { onToggleAdBlock() }
            )

            MenuSwitchRow(
                title = "夜间模式 (Dark Web)",
                subtitle = "深色渲染网页以保护视力",
                icon = Icons.Outlined.DarkMode,
                isChecked = settings.isNightMode,
                onCheckedChange = { onToggleNightMode() }
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun MenuItemTile(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tint: Color = MaterialTheme.colorScheme.primary
) {
    Card(
        modifier = modifier
            .padding(4.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = tint,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun MenuSwitchRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange
        )
    }
}
