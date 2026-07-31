package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.SearchEngine
import com.example.model.TabItem

@Composable
fun AddressBar(
    tab: TabItem,
    isBookmarked: Boolean,
    selectedEngineIndex: Int,
    onNavigate: (String) -> Unit,
    onRefresh: () -> Unit,
    onStop: () -> Unit,
    onToggleBookmark: () -> Unit,
    onSelectEngine: (Int) -> Unit,
    onOpenPageInfo: () -> Unit,
    modifier: Modifier = Modifier
) {
    var inputText by remember(tab.url) {
        mutableStateOf(if (tab.url == "about:blank") "" else tab.url)
    }
    var isFocused by remember { mutableStateOf(false) }
    var showEngineMenu by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }

    val currentEngine = SearchEngine.DefaultEngines.getOrElse(selectedEngineIndex) { SearchEngine.DefaultEngines.first() }

    val isHttps = tab.url.startsWith("https://")
    val isIncognito = tab.isIncognito

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                if (isIncognito) Color(0xFF0F172A) else MaterialTheme.colorScheme.surface
            )
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(
                    if (isIncognito) Color(0xFF1E293B)
                    else MaterialTheme.colorScheme.surfaceContainerHigh
                )
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Security Lock / Incognito / Engine selector icon
            Box {
                IconButton(
                    onClick = {
                        if (isFocused) {
                            showEngineMenu = true
                        } else {
                            onOpenPageInfo()
                        }
                    },
                    modifier = Modifier.size(36.dp)
                ) {
                    if (isFocused) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = currentEngine.iconLetter,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    } else if (isIncognito) {
                        Icon(
                            imageVector = Icons.Default.VisibilityOff,
                            contentDescription = "无痕模式",
                            tint = Color(0xFFA855F7),
                            modifier = Modifier.size(20.dp)
                        )
                    } else if (isHttps) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "安全连接",
                            tint = Color(0xFF10B981),
                            modifier = Modifier.size(18.dp)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Outlined.Public,
                            contentDescription = "网页",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                DropdownMenu(
                    expanded = showEngineMenu,
                    onDismissRequest = { showEngineMenu = false }
                ) {
                    Text(
                        text = "选择默认搜索引擎",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                    SearchEngine.DefaultEngines.forEachIndexed { index, engine ->
                        DropdownMenuItem(
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        shape = CircleShape,
                                        color = MaterialTheme.colorScheme.secondaryContainer,
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(engine.iconLetter, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(engine.name)
                                }
                            },
                            onClick = {
                                onSelectEngine(index)
                                showEngineMenu = false
                            },
                            trailingIcon = {
                                if (index == selectedEngineIndex) {
                                    Icon(Icons.Default.Check, contentDescription = "Selected", tint = MaterialTheme.colorScheme.primary)
                                }
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(4.dp))

            // Main URL or Title text field
            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                placeholder = {
                    Text(
                        text = "搜索或输入网址",
                        fontSize = 14.sp,
                        color = if (isIncognito) Color.LightGray else MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedTextColor = if (isIncognito) Color.White else MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = if (isIncognito) Color.White else MaterialTheme.colorScheme.onSurface
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Go),
                keyboardActions = KeyboardActions(
                    onGo = {
                        keyboardController?.hide()
                        focusManager.clearFocus()
                        onNavigate(inputText)
                    }
                ),
                modifier = Modifier
                    .weight(1f)
                    .focusRequester(focusRequester)
                    .onFocusChanged { focusState ->
                        isFocused = focusState.isFocused
                        if (focusState.isFocused && inputText.isEmpty() && tab.url != "about:blank") {
                            inputText = tab.url
                        }
                    }
            )

            // Right action icons inside address bar
            if (inputText.isNotEmpty() && isFocused) {
                IconButton(
                    onClick = { inputText = "" },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "清空",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }
            } else if (!isFocused) {
                // Bookmark star
                IconButton(
                    onClick = onToggleBookmark,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = if (isBookmarked) Icons.Filled.Star else Icons.Outlined.StarBorder,
                        contentDescription = "收藏",
                        tint = if (isBookmarked) Color(0xFFF59E0B) else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Refresh or Stop
                IconButton(
                    onClick = {
                        if (tab.isLoading) onStop() else onRefresh()
                    },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = if (tab.isLoading) Icons.Default.Close else Icons.Default.Refresh,
                        contentDescription = if (tab.isLoading) "停止" else "刷新",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // Animated Progress Bar directly attached below Address Bar
        if (tab.isLoading) {
            LinearProgressIndicator(
                progress = { tab.progress / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.5.dp)
                    .padding(top = 2.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = Color.Transparent
            )
        }
    }
}
