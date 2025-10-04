package com.YashDoesCode.banking.ui.transactions

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.YashDoesCode.banking.ui.components.TransactionItemRow

@Composable
fun TransactionHistoryScreen(
    viewModel: TransactionsViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        // Title Header
        Text(
            text = "Transaction History",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(top = 16.dp, bottom = 12.dp)
        )

        // Search Field
        OutlinedTextField(
            value = uiState.searchQuery,
            onValueChange = { viewModel.onAction(TransactionsAction.UpdateSearchQuery(it)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            placeholder = { Text("Search transactions...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
            trailingIcon = {
                if (uiState.searchQuery.isNotEmpty()) {
                    IconButton(onClick = { viewModel.onAction(TransactionsAction.UpdateSearchQuery("")) }) {
                        Icon(Icons.Default.Close, contentDescription = "Clear search")
                    }
                }
            },
            singleLine = true,
            shape = MaterialTheme.shapes.medium
        )

        // Account Filter Chips
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                FilterChip(
                    selected = uiState.selectedAccountId == null,
                    onClick = { viewModel.onAction(TransactionsAction.SelectAccount(null)) },
                    label = { Text("All Accounts") }
                )
            }
            items(uiState.accounts, key = { it.id }) { account ->
                FilterChip(
                    selected = uiState.selectedAccountId == account.id,
                    onClick = {
                        val newSelection = if (uiState.selectedAccountId == account.id) null else account.id
                        viewModel.onAction(TransactionsAction.SelectAccount(newSelection))
                    },
                    label = { Text(account.name) }
                )
            }
        }

        // Category Filter Chips
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                FilterChip(
                    selected = uiState.selectedCategoryId == null,
                    onClick = { viewModel.onAction(TransactionsAction.SelectCategory(null)) },
                    label = { Text("All Categories") }
                )
            }
            items(uiState.categories, key = { it.id }) { category ->
                FilterChip(
                    selected = uiState.selectedCategoryId == category.id,
                    onClick = {
                        val newSelection = if (uiState.selectedCategoryId == category.id) null else category.id
                        viewModel.onAction(TransactionsAction.SelectCategory(newSelection))
                    },
                    label = { Text(category.name) }
                )
            }
        }

        // Transaction List
        if (uiState.filteredTransactions.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.FilterList,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "No matching transactions found.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(uiState.filteredTransactions, key = { it.id }) { transaction ->
                    TransactionItemRow(
                        transaction = transaction,
                        showRunningBalance = true
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(start = 60.dp),
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                    )
                }
            }
        }
    }
}
