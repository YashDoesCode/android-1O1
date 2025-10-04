package com.YashDoesCode.comms.ui.dialer

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Contacts
import androidx.compose.material.icons.filled.Dialpad
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.YashDoesCode.comms.ui.dialer.components.ContactsSection
import com.YashDoesCode.comms.ui.dialer.components.DialpadSection
import com.YashDoesCode.comms.ui.dialer.components.RecentsSection

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialerScreen(
    viewModel: DialerViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // Helper to launch Phone Dialer via Intent.ACTION_DIAL
    // NOTE: Intent.ACTION_DIAL requires NO runtime permission and functions reliably on emulators.
    val onPlaceCall: (String) -> Unit = { phoneNumber ->
        val cleanNumber = phoneNumber.filter { it.isDigit() || it == '+' || it == '*' || it == '#' }
        if (cleanNumber.isNotEmpty()) {
            val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$cleanNumber")).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(dialIntent)
        }
    }

    Scaffold(
        topBar = {
            PrimaryTabRow(
                selectedTabIndex = uiState.activeSubTab.ordinal,
                modifier = Modifier.fillMaxWidth()
            ) {
                Tab(
                    selected = uiState.activeSubTab == DialerSubTab.KEYPAD,
                    onClick = { viewModel.onAction(DialerAction.SelectSubTab(DialerSubTab.KEYPAD)) },
                    text = { Text("Keypad") },
                    icon = { Icon(Icons.Default.Dialpad, contentDescription = null) }
                )
                Tab(
                    selected = uiState.activeSubTab == DialerSubTab.RECENTS,
                    onClick = { viewModel.onAction(DialerAction.SelectSubTab(DialerSubTab.RECENTS)) },
                    text = { Text("Recents") },
                    icon = { Icon(Icons.Default.History, contentDescription = null) }
                )
                Tab(
                    selected = uiState.activeSubTab == DialerSubTab.CONTACTS,
                    onClick = { viewModel.onAction(DialerAction.SelectSubTab(DialerSubTab.CONTACTS)) },
                    text = { Text("Contacts") },
                    icon = { Icon(Icons.Default.Contacts, contentDescription = null) }
                )
            }
        },
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            AnimatedContent(
                targetState = uiState.activeSubTab,
                label = "DialerSubTabAnimation"
            ) { subTab ->
                when (subTab) {
                    DialerSubTab.KEYPAD -> DialpadSection(
                        digits = uiState.digits,
                        onAction = viewModel::onAction,
                        onCallClick = onPlaceCall
                    )
                    DialerSubTab.RECENTS -> RecentsSection(
                        recentCalls = uiState.recentCalls,
                        onCallClick = onPlaceCall
                    )
                    DialerSubTab.CONTACTS -> ContactsSection(
                        contacts = uiState.filteredContacts,
                        searchQuery = uiState.searchQuery,
                        onAction = viewModel::onAction,
                        onCallContact = onPlaceCall
                    )
                }
            }
        }
    }
}
