package com.YashDoesCode.banking.ui.transactions

import com.YashDoesCode.banking.domain.model.Account
import com.YashDoesCode.banking.domain.model.Category
import com.YashDoesCode.banking.domain.model.Transaction

data class TransactionsUiState(
    val transactions: List<Transaction> = emptyList(),
    val categories: List<Category> = emptyList(),
    val accounts: List<Account> = emptyList(),
    val selectedCategoryId: String? = null,
    val selectedAccountId: String? = null,
    val searchQuery: String = "",
    val isLoading: Boolean = false
) {
    val filteredTransactions: List<Transaction>
        get() {
            var list = transactions
            if (selectedAccountId != null) {
                list = list.filter { it.accountId == selectedAccountId }
            }
            if (selectedCategoryId != null) {
                list = list.filter { it.category.id == selectedCategoryId }
            }
            if (searchQuery.isNotBlank()) {
                list = list.filter {
                    it.merchantOrPayee.contains(searchQuery, ignoreCase = true) ||
                    it.description.contains(searchQuery, ignoreCase = true) ||
                    it.category.name.contains(searchQuery, ignoreCase = true)
                }
            }
            return list
        }
}

sealed interface TransactionsAction {
    data class SelectCategory(val categoryId: String?) : TransactionsAction
    data class SelectAccount(val accountId: String?) : TransactionsAction
    data class UpdateSearchQuery(val query: String) : TransactionsAction
    data object ClearFilters : TransactionsAction
}
