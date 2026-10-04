package com.YashDoesCode.comms.ui.mail

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.YashDoesCode.comms.ui.mail.components.ComposeEmailScreen
import com.YashDoesCode.comms.ui.mail.components.InboxThreadList
import com.YashDoesCode.comms.ui.mail.components.ThreadDetailScreen

@Composable
fun MailScreen(
    viewModel: MailViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.toastMessage) {
        uiState.toastMessage?.let { message ->
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
            viewModel.onAction(MailAction.ClearToastMessage)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        when {
            uiState.isComposeOpen -> {
                BackHandler {
                    viewModel.onAction(MailAction.SetComposeOpen(false))
                }
                ComposeEmailScreen(
                    recipient = uiState.composeRecipient,
                    subject = uiState.composeSubject,
                    body = uiState.composeBody,
                    isSending = uiState.isSending,
                    onAction = viewModel::onAction,
                    modifier = Modifier.padding(paddingValues)
                )
            }
            uiState.selectedThread != null -> {
                BackHandler {
                    viewModel.onAction(MailAction.SelectThread(null))
                }
                ThreadDetailScreen(
                    thread = uiState.selectedThread!!,
                    onAction = viewModel::onAction,
                    modifier = Modifier.padding(paddingValues)
                )
            }
            else -> {
                InboxThreadList(
                    threads = uiState.filteredThreads,
                    searchQuery = uiState.searchQuery,
                    filterUnreadOnly = uiState.filterUnreadOnly,
                    filterStarredOnly = uiState.filterStarredOnly,
                    onAction = viewModel::onAction,
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }
}
