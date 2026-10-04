package com.YashDoesCode.comms.ui.mail.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.YashDoesCode.comms.ui.mail.MailAction

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComposeEmailScreen(
    recipient: String,
    subject: String,
    body: String,
    isSending: Boolean,
    onAction: (MailAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("New Message") },
                navigationIcon = {
                    IconButton(onClick = { onAction(MailAction.SetComposeOpen(false)) }) {
                        Icon(Icons.Default.Close, contentDescription = "Close compose")
                    }
                },
                actions = {
                    if (isSending) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .size(24.dp)
                                .padding(end = 16.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        IconButton(onClick = { onAction(MailAction.SendComposeEmail) }) {
                            Icon(
                                Icons.AutoMirrored.Filled.Send,
                                contentDescription = "Send Email",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            )
        },
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = recipient,
                onValueChange = { onAction(MailAction.UpdateComposeRecipient(it)) },
                label = { Text("To") },
                placeholder = { Text("recipient@example.com") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = subject,
                onValueChange = { onAction(MailAction.UpdateComposeSubject(it)) },
                label = { Text("Subject") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = body,
                onValueChange = { onAction(MailAction.UpdateComposeBody(it)) },
                label = { Text("Compose email") },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
        }
    }
}
