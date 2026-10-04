package com.YashDoesCode.comms.ui.dialer

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Contacts
import androidx.compose.material.icons.filled.Dialpad
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
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

    // Permission launcher for READ_CONTACTS and READ_CALL_LOG
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val hasContacts = permissions[Manifest.permission.READ_CONTACTS] ?: checkPermission(context, Manifest.permission.READ_CONTACTS)
        val hasCallLog = permissions[Manifest.permission.READ_CALL_LOG] ?: checkPermission(context, Manifest.permission.READ_CALL_LOG)
        viewModel.onAction(DialerAction.UpdatePermissions(hasContacts = hasContacts, hasCallLog = hasCallLog))
        viewModel.onAction(DialerAction.Refresh)
    }

    LaunchedEffect(Unit) {
        val hasContacts = checkPermission(context, Manifest.permission.READ_CONTACTS)
        val hasCallLog = checkPermission(context, Manifest.permission.READ_CALL_LOG)
        viewModel.onAction(DialerAction.UpdatePermissions(hasContacts = hasContacts, hasCallLog = hasCallLog))
        if (!hasContacts || !hasCallLog) {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.READ_CONTACTS,
                    Manifest.permission.READ_CALL_LOG
                )
            )
        }
    }

    // Helper to launch Phone Dialer via Intent.ACTION_DIAL
    // Intent.ACTION_DIAL opens the system dialer with the number prefilled and requires zero runtime permissions on API 28+ emulators and physical devices, whereas Intent.ACTION_CALL directly initiates a call and requires android.permission.CALL_PHONE.
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
                        hasPermission = uiState.hasCallLogPermission,
                        onRequestPermission = {
                            permissionLauncher.launch(arrayOf(Manifest.permission.READ_CALL_LOG))
                        },
                        onCallClick = onPlaceCall
                    )
                    DialerSubTab.CONTACTS -> ContactsSection(
                        contacts = uiState.filteredContacts,
                        searchQuery = uiState.searchQuery,
                        hasPermission = uiState.hasContactsPermission,
                        onRequestPermission = {
                            permissionLauncher.launch(arrayOf(Manifest.permission.READ_CONTACTS))
                        },
                        onAction = viewModel::onAction,
                        onCallContact = onPlaceCall
                    )
                }
            }
        }
    }
}

private fun checkPermission(context: Context, permission: String): Boolean {
    return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
}
