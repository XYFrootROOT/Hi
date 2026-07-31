package com.example.ui.components

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.TabItem

@Composable
fun PageInfoDialog(
    tab: TabItem,
    isAdBlockEnabled: Boolean,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val isHttps = tab.url.startsWith("https://")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (isHttps) Icons.Default.Lock else Icons.Default.Warning,
                    contentDescription = null,
                    tint = if (isHttps) Color(0xFF10B981) else Color(0xFFEF4444)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isHttps) "安全加密连接 (HTTPS)" else "未加密连接 (HTTP)",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "网站: ${tab.title.ifBlank { tab.url }}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = "网址: ${tab.url}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                HorizontalDivider()

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isAdBlockEnabled) "广告防护: 已启用" else "广告防护: 未启用",
                        fontSize = 13.sp
                    )
                }

                Text(
                    text = if (isHttps) "您发往此网站的信息（如密码或信用卡号）是私密的。"
                    else "此网站没有安全证书，请勿在此输入个人敏感信息。",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    Toast.makeText(context, "已成功清除缓存与Cookie", Toast.LENGTH_SHORT).show()
                    onDismiss()
                }
            ) {
                Icon(Icons.Default.CleaningServices, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("清除此网站缓存")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("完成")
            }
        }
    )
}
