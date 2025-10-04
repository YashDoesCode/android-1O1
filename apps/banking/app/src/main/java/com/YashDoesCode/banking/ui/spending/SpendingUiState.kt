package com.YashDoesCode.banking.ui.spending

import com.YashDoesCode.banking.domain.model.Account
import com.YashDoesCode.banking.domain.model.CategorySpending
import com.YashDoesCode.banking.domain.model.formatMoney

data class SpendingUiState(
    val spendingList: List<CategorySpending> = emptyList(),
    val accounts: List<Account> = emptyList(),
    val selectedAccountId: String? = null,
    val isLoading: Boolean = false
) {
    val totalSpentMinor: Long
        get() = spendingList.sumOf { it.totalSpentMinor }

    val formattedTotalSpent: String
        get() = formatMoney(totalSpentMinor)
}

sealed interface SpendingAction {
    data class SelectAccount(val accountId: String?) : SpendingAction
    data object Refresh : SpendingAction
}
