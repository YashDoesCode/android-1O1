package com.YashDoesCode.comms.ui.mail.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.YashDoesCode.comms.domain.model.EmailThread
import com.YashDoesCode.comms.ui.mail.MailAction
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun InboxThreadList(
    threads: List<EmailThread>,
    searchQuery: String,
    filterUnreadOnly: Boolean,
    filterStarredOnly: Boolean,
    onAction: (MailAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { onAction(MailAction.UpdateSearchQuery(it)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                placeholder = { Text("Search emails...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                singleLine = true,
                shape = MaterialTheme.shapes.medium
            )

            // Filter Chips Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = !filterUnreadOnly && !filterStarredOnly,
                    onClick = {
                        onAction(MailAction.SetFilterUnread(false))
                        onAction(MailAction.SetFilterStarred(false))
                    },
                    label = { Text("All") }
                )
                FilterChip(
                    selected = filterUnreadOnly,
                    onClick = { onAction(MailAction.SetFilterUnread(!filterUnreadOnly)) },
                    label = { Text("Unread") }
                )
                FilterChip(
                    selected = filterStarredOnly,
                    onClick = { onAction(MailAction.SetFilterStarred(!filterStarredOnly)) },
                    label = { Text("Starred") }
                )
            }

            if (threads.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No emails found.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
                val dateFormat = SimpleDateFormat("MMM d", Locale.getDefault())
                val now = System.currentTimeMillis()

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(threads, key = { it.id }) { thread ->
                        val isToday = (now - thread.lastMessageTimestamp) < 1000 * 60 * 60 * 24
                        val formattedDate = if (isToday) {
                            timeFormat.format(Date(thread.lastMessageTimestamp))
                        } else {
                            dateFormat.format(Date(thread.lastMessageTimestamp))
                        }

                        ThreadRow(
                            thread = thread,
                            formattedTime = formattedDate,
                            onClick = { onAction(MailAction.SelectThread(thread.id)) },
                            onToggleStar = { onAction(MailAction.ToggleStarThread(thread.id)) }
                        )
                        HorizontalDivider(modifier = Modifier.padding(start = 56.dp))
                    }
                }
            }
        }

        // Compose FAB
        FloatingActionButton(
            onClick = { onAction(MailAction.SetComposeOpen(true)) },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp),
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ) {
            Icon(Icons.Default.Edit, contentDescription = "Compose Email")
        }
    }
}

@Composable
private fun ThreadRow(
    thread: EmailThread,
    formattedTime: String,
    onClick: () -> Unit,
    onToggleStar: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isUnread = thread.unreadCount > 0

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.Top
    ) {
        // Avatar Circle
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(
                    if (isUnread) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = thread.senderName.firstOrNull()?.uppercase() ?: "@",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = if (isUnread) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = thread.senderName,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = if (isUnread) FontWeight.Bold else FontWeight.Medium
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )
                Text(
                    text = formattedTime,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = if (isUnread) FontWeight.Bold else FontWeight.Normal,
                        color = if (isUnread) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp
                    )
                )
            }

            Text(
                text = thread.subject,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = if (isUnread) FontWeight.SemiBold else FontWeight.Normal
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = thread.snippet,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }

        IconButton(onClick = onToggleStar) {
            Icon(
                imageVector = if (thread.isStarred) Icons.Default.Star else Icons.Outlined.StarBorder,
                contentDescription = "Star email",
                tint = if (thread.isStarred) Color(0xFFFFB300) else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
