package com.YashDoesCode.banking.ui.transfer

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.YashDoesCode.banking.domain.model.Account
import com.YashDoesCode.banking.domain.model.formatMoney

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransferScreen(
    viewModel: TransferViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    var showFromAccountPicker by remember { mutableStateOf(false) }
    var showToAccountPicker by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Title Header
        Text(
            text = "Transfer Funds",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // From Account Selector Card
        AccountSelectionCard(
            label = "From Account",
            selectedAccount = uiState.fromAccount,
            onClick = { showFromAccountPicker = true }
        )

        // Swap Icon
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            IconButton(
                onClick = {
                    val from = uiState.fromAccountId
                    val to = uiState.toAccountId
                    if (from != null && to != null) {
                        viewModel.onAction(TransferAction.SelectFromAccount(to))
                        viewModel.onAction(TransferAction.SelectToAccount(from))
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Default.SwapVert,
                    contentDescription = "Swap accounts",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        // To Account Selector Card
        AccountSelectionCard(
            label = "To Account",
            selectedAccount = uiState.toAccount,
            onClick = { showToAccountPicker = true }
        )

        // Amount Input Field
        OutlinedTextField(
            value = uiState.amountInput,
            onValueChange = { viewModel.onAction(TransferAction.UpdateAmount(it)) },
            label = { Text("Transfer Amount") },
            placeholder = { Text("0.00") },
            prefix = { Text("$", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        // Quick Amount Chips
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("25", "50", "100", "250", "500").forEach { amountStr ->
                SuggestionChip(
                    onClick = { viewModel.onAction(TransferAction.UpdateAmount(amountStr)) },
                    label = { Text("$$amountStr") }
                )
            }
        }

        // Note Input Field
        OutlinedTextField(
            value = uiState.noteInput,
            onValueChange = { viewModel.onAction(TransferAction.UpdateNote(it)) },
            label = { Text("Note / Description (Optional)") },
            placeholder = { Text("e.g. Monthly Savings, Rent split") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Transfer Button
        Button(
            onClick = { viewModel.onAction(TransferAction.SubmitTransfer) },
            enabled = uiState.isValidInput && !uiState.isSubmitting,
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
        ) {
            if (uiState.isSubmitting) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = "Transfer Now",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
        }
    }

    // Modal Sheet: Pick From Account
    if (showFromAccountPicker) {
        ModalBottomSheet(onDismissRequest = { showFromAccountPicker = false }) {
            AccountPickerContent(
                title = "Select Source Account",
                accounts = uiState.accounts,
                selectedAccountId = uiState.fromAccountId,
                onAccountSelected = {
                    viewModel.onAction(TransferAction.SelectFromAccount(it.id))
                    showFromAccountPicker = false
                }
            )
        }
    }

    // Modal Sheet: Pick To Account
    if (showToAccountPicker) {
        ModalBottomSheet(onDismissRequest = { showToAccountPicker = false }) {
            AccountPickerContent(
                title = "Select Destination Account",
                accounts = uiState.accounts,
                selectedAccountId = uiState.toAccountId,
                onAccountSelected = {
                    viewModel.onAction(TransferAction.SelectToAccount(it.id))
                    showToAccountPicker = false
                }
            )
        }
    }

    // Result Feedback Dialog
    when (val status = uiState.status) {
        is TransferStatus.Success -> {
            AlertDialog(
                onDismissRequest = { viewModel.onAction(TransferAction.DismissStatus) },
                icon = {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color(0xFF2E7D32),
                        modifier = Modifier.size(48.dp)
                    )
                },
                title = { Text("Transfer Complete", fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Your funds have been transferred successfully.")
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                        Text(
                            text = "New ${uiState.fromAccount?.name} Balance: ${formatMoney(status.fromBalanceMinor)}",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = "New ${uiState.toAccount?.name} Balance: ${formatMoney(status.toBalanceMinor)}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                },
                confirmButton = {
                    Button(onClick = { viewModel.onAction(TransferAction.DismissStatus) }) {
                        Text("Done")
                    }
                }
            )
        }
        is TransferStatus.Error -> {
            AlertDialog(
                onDismissRequest = { viewModel.onAction(TransferAction.DismissStatus) },
                icon = {
                    Icon(
                        imageVector = Icons.Default.ErrorOutline,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(48.dp)
                    )
                },
                title = { Text("Transfer Failed", fontWeight = FontWeight.Bold) },
                text = { Text(status.message) },
                confirmButton = {
                    TextButton(onClick = { viewModel.onAction(TransferAction.DismissStatus) }) {
                        Text("Dismiss")
                    }
                }
            )
        }
        TransferStatus.Idle -> Unit
    }
}

@Composable
private fun AccountSelectionCard(
    label: String,
    selectedAccount: Account?,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium.copy(color = MaterialTheme.colorScheme.primary)
                )
                Spacer(modifier = Modifier.height(4.dp))
                if (selectedAccount != null) {
                    Text(
                        text = selectedAccount.name,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "${selectedAccount.accountNumber} · ${selectedAccount.formattedBalance}",
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                } else {
                    Text(
                        text = "Select Account",
                        style = MaterialTheme.typography.titleMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                }
            }

            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun AccountPickerContent(
    title: String,
    accounts: List<Account>,
    selectedAccountId: String?,
    onAccountSelected: (Account) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        accounts.forEach { account ->
            val isSelected = account.id == selectedAccountId
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onAccountSelected(account) }
                    .padding(vertical = 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = account.name,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        )
                        Text(
                            text = "${account.accountNumber} (${account.type.name})",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        text = account.formattedBalance,
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
    }
}
