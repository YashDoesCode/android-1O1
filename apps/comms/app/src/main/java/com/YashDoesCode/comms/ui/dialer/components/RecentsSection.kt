package com.YashDoesCode.comms.ui.dialer.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.CallMade
import androidx.compose.material.icons.automirrored.filled.CallMissed
import androidx.compose.material.icons.automirrored.filled.CallReceived
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Call
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.YashDoesCode.comms.domain.model.CallLogEntry
import com.YashDoesCode.comms.domain.model.CallType
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun RecentsSection(
    recentCalls: List<CallLogEntry>,
    onCallClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (recentCalls.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No recent calls found.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    } else {
        val dateFormat = SimpleDateFormat("MMM d, h:mm a", Locale.getDefault())

        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(recentCalls, key = { it.id }) { call ->
                RecentCallRow(
                    call = call,
                    formattedTime = dateFormat.format(Date(call.timestamp)),
                    onCallClick = { onCallClick(call.number) }
                )
                HorizontalDivider(modifier = Modifier.padding(start = 48.dp))
            }
        }
    }
}

@Composable
private fun RecentCallRow(
    call: CallLogEntry,
    formattedTime: String,
    onCallClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onCallClick)
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val (icon, color) = when (call.type) {
            CallType.INCOMING -> Icons.AutoMirrored.Filled.CallReceived to Color(0xFF1976D2) // Blue
            CallType.OUTGOING -> Icons.AutoMirrored.Filled.CallMade to Color(0xFF388E3C) // Green
            CallType.MISSED -> Icons.AutoMirrored.Filled.CallMissed to MaterialTheme.colorScheme.error // Red
            CallType.REJECTED -> Icons.Default.Block to Color.Gray
            CallType.UNKNOWN -> Icons.Default.Call to Color.Gray
        }

        Icon(
            imageVector = icon,
            contentDescription = call.type.name,
            tint = color,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = call.callerName ?: call.number,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = if (call.type == CallType.MISSED) FontWeight.Bold else FontWeight.Medium,
                    color = if (call.type == CallType.MISSED) MaterialTheme.colorScheme.error else LocalContentColor.current
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (call.callerName != null) {
                    Text(
                        text = call.number,
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                }
                Text(
                    text = formattedTime,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp
                    )
                )
            }
        }

        IconButton(onClick = onCallClick) {
            Icon(
                imageVector = Icons.Default.Call,
                contentDescription = "Call again",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}
