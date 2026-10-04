package com.YashDoesCode.banking.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.YashDoesCode.banking.ui.components.BankingBottomBar
import com.YashDoesCode.banking.ui.components.BankingTab
import com.YashDoesCode.banking.ui.overview.AccountsOverviewScreen
import com.YashDoesCode.banking.ui.overview.OverviewViewModel
import com.YashDoesCode.banking.ui.spending.SpendingScreen
import com.YashDoesCode.banking.ui.spending.SpendingViewModel
import com.YashDoesCode.banking.ui.transactions.TransactionHistoryScreen
import com.YashDoesCode.banking.ui.transactions.TransactionsViewModel
import com.YashDoesCode.banking.ui.transfer.TransferScreen
import com.YashDoesCode.banking.ui.transfer.TransferViewModel

@Composable
fun MainBankingScreen(
    overviewViewModel: OverviewViewModel,
    transactionsViewModel: TransactionsViewModel,
    transferViewModel: TransferViewModel,
    spendingViewModel: SpendingViewModel,
    modifier: Modifier = Modifier
) {
    var currentTab by rememberSaveable { mutableStateOf(BankingTab.ACCOUNTS) }

    Scaffold(
        bottomBar = {
            BankingBottomBar(
                currentTab = currentTab,
                onTabSelected = { currentTab = it }
            )
        },
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        when (currentTab) {
            BankingTab.ACCOUNTS -> {
                AccountsOverviewScreen(
                    viewModel = overviewViewModel,
                    onNavigateToTransfers = { currentTab = BankingTab.TRANSFER },
                    onNavigateToTransactions = { currentTab = BankingTab.TRANSACTIONS },
                    onNavigateToSpending = { currentTab = BankingTab.SPENDING },
                    modifier = Modifier.padding(paddingValues)
                )
            }
            BankingTab.TRANSACTIONS -> {
                TransactionHistoryScreen(
                    viewModel = transactionsViewModel,
                    modifier = Modifier.padding(paddingValues)
                )
            }
            BankingTab.TRANSFER -> {
                TransferScreen(
                    viewModel = transferViewModel,
                    modifier = Modifier.padding(paddingValues)
                )
            }
            BankingTab.SPENDING -> {
                SpendingScreen(
                    viewModel = spendingViewModel,
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }
}
