package com.YashDoesCode.browser.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.DesktopWindows
import androidx.compose.material.icons.outlined.Smartphone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.YashDoesCode.browser.core.domain.model.Tab
import com.YashDoesCode.browser.ui.FullBrowserAction

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OmniboxBar(
    activeTab: Tab,
    tabsCount: Int,
    isBookmarked: Boolean,
    onAction: (FullBrowserAction) -> Unit,
    modifier: Modifier = Modifier
) {
    var textInput by remember(activeTab.url) { mutableStateOf(activeTab.url) }
    val focusManager = LocalFocusManager.current
    var showMenu by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxWidth()) {
        Surface(
            tonalElevation = 3.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Back Button
                IconButton(
                    onClick = { onAction(FullBrowserAction.GoBack) },
                    enabled = activeTab.canGoBack
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }

                // Forward Button
                IconButton(
                    onClick = { onAction(FullBrowserAction.GoForward) },
                    enabled = activeTab.canGoForward
                ) {
                    Icon(Icons.Default.ArrowForward, contentDescription = "Forward")
                }

                // Reload or Stop
                IconButton(
                    onClick = {
                        if (activeTab.isLoading) {
                            onAction(FullBrowserAction.Stop)
                        } else {
                            onAction(FullBrowserAction.Reload)
                        }
                    }
                ) {
                    Icon(
                        if (activeTab.isLoading) Icons.Default.Close else Icons.Default.Refresh,
                        contentDescription = if (activeTab.isLoading) "Stop" else "Reload"
                    )
                }

                // URL / Search Input
                TextField(
                    value = textInput,
                    onValueChange = { textInput = it },
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 4.dp),
                    singleLine = true,
                    placeholder = { Text("Search or type URL") },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Go),
                    keyboardActions = KeyboardActions(
                        onGo = {
                            focusManager.clearFocus()
                            onAction(FullBrowserAction.NavigateToUrl(textInput))
                        }
                    ),
                    trailingIcon = {
                        IconButton(onClick = { onAction(FullBrowserAction.ToggleBookmark) }) {
                            Icon(
                                imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Outlined.BookmarkBorder,
                                contentDescription = "Bookmark",
                                tint = if (isBookmarked) MaterialTheme.colorScheme.primary else LocalContentColor.current
                            )
                        }
                    },
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                        unfocusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent
                    )
                )

                // Desktop Mode Toggle
                IconButton(onClick = { onAction(FullBrowserAction.ToggleDesktopMode) }) {
                    Icon(
                        imageVector = if (activeTab.isDesktopMode) Icons.Outlined.DesktopWindows else Icons.Outlined.Smartphone,
                        contentDescription = "Desktop Mode",
                        tint = if (activeTab.isDesktopMode) MaterialTheme.colorScheme.primary else LocalContentColor.current
                    )
                }

                // Tabs Counter Button
                TextButton(onClick = { onAction(FullBrowserAction.ShowTabsOverview(true)) }) {
                    Surface(
                        shape = MaterialTheme.shapes.extraSmall,
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface)
                    ) {
                        Text(
                            text = "$tabsCount",
                            style = MaterialTheme.typography.labelLarge,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                // Overflow Menu
                Box {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Menu")
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("New Tab") },
                            leadingIcon = { Icon(Icons.Default.Add, contentDescription = null) },
                            onClick = {
                                showMenu = false
                                onAction(FullBrowserAction.NewTab())
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Bookmarks") },
                            leadingIcon = { Icon(Icons.Default.Bookmarks, contentDescription = null) },
                            onClick = {
                                showMenu = false
                                onAction(FullBrowserAction.ShowBookmarks(true))
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("History") },
                            leadingIcon = { Icon(Icons.Default.History, contentDescription = null) },
                            onClick = {
                                showMenu = false
                                onAction(FullBrowserAction.ShowHistory(true))
                            }
                        )
                    }
                }
            }
        }

        // Loading Progress Bar
        AnimatedVisibility(visible = activeTab.isLoading) {
            LinearProgressIndicator(
                progress = { activeTab.progress / 100f },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
