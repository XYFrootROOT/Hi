package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.TabItem

@Composable
fun BottomNavBar(
    activeTab: TabItem,
    tabCount: Int,
    onBack: () -> Unit,
    onForward: () -> Unit,
    onHome: () -> Unit,
    onOpenTabs: () -> Unit,
    onOpenMenu: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isIncognito = activeTab.isIncognito

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = if (isIncognito) Color(0xFF0F172A) else MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp, horizontal = 8.dp)
                .navigationBarsPadding(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Back Button
            IconButton(
                onClick = onBack,
                enabled = activeTab.canGoBack
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "后退",
                    tint = if (activeTab.canGoBack) {
                        if (isIncognito) Color.White else MaterialTheme.colorScheme.onSurface
                    } else {
                        if (isIncognito) Color.Gray.copy(alpha = 0.4f) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                    }
                )
            }

            // Forward Button
            IconButton(
                onClick = onForward,
                enabled = activeTab.canGoForward
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "前进",
                    tint = if (activeTab.canGoForward) {
                        if (isIncognito) Color.White else MaterialTheme.colorScheme.onSurface
                    } else {
                        if (isIncognito) Color.Gray.copy(alpha = 0.4f) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                    }
                )
            }

            // Home Button
            IconButton(onClick = onHome) {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "主页",
                    tint = if (isIncognito) Color.White else MaterialTheme.colorScheme.onSurface
                )
            }

            // Tabs Counter Badge Button
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onOpenTabs() },
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .border(
                            width = 2.dp,
                            color = if (isIncognito) Color.White else MaterialTheme.colorScheme.onSurface,
                            shape = RoundedCornerShape(6.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = tabCount.toString(),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isIncognito) Color.White else MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // Menu Button
            IconButton(onClick = onOpenMenu) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "菜单",
                    tint = if (isIncognito) Color.White else MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}
