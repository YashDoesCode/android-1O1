package com.YashDoesCode.banking

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.YashDoesCode.banking.ui.MainBankingScreen
import com.YashDoesCode.banking.ui.overview.OverviewViewModel
import com.YashDoesCode.banking.ui.spending.SpendingViewModel
import com.YashDoesCode.banking.ui.theme.BankingTheme
import com.YashDoesCode.banking.ui.transactions.TransactionsViewModel
import com.YashDoesCode.banking.ui.transfer.TransferViewModel

class MainActivity : ComponentActivity() {

    private val overviewViewModel: OverviewViewModel by viewModels {
        val app = application as BankingApp
        OverviewViewModel.Factory(app.container)
    }

    private val transactionsViewModel: TransactionsViewModel by viewModels {
        val app = application as BankingApp
        TransactionsViewModel.Factory(app.container)
    }

    private val transferViewModel: TransferViewModel by viewModels {
        val app = application as BankingApp
        TransferViewModel.Factory(app.container)
    }

    private val spendingViewModel: SpendingViewModel by viewModels {
        val app = application as BankingApp
        SpendingViewModel.Factory(app.container)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BankingTheme {
                MainBankingScreen(
                    overviewViewModel = overviewViewModel,
                    transactionsViewModel = transactionsViewModel,
                    transferViewModel = transferViewModel,
                    spendingViewModel = spendingViewModel
                )
            }
        }
    }
}
