package com.YashDoesCode.banking.ui.overview

import com.YashDoesCode.banking.domain.model.Account
import com.YashDoesCode.banking.domain.model.Transaction
import com.YashDoesCode.banking.domain.model.formatMoney

data class OverviewUiState(
    val userName: String = "User",
    val accounts: List<Account> = emptyList(),
    val recentTransactions: List<Transaction> = emptyList(),
    val selectedAccount: Account? = null,
    val isLoading: Boolean = false
) {
    val totalNetWorthMinor: Long
        get() = accounts.sumOf { it.balanceMinor }

    val formattedNetWorth: String
        get() = formatMoney(totalNetWorthMinor)
}

sealed interface OverviewAction {
    data class SelectAccount(val account: Account?) : OverviewAction
    data object Refresh : OverviewAction
}
