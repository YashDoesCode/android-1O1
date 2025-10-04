package com.YashDoesCode.banking.ui.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.YashDoesCode.banking.di.BankingContainer
import com.YashDoesCode.banking.domain.usecase.GetAccountsUseCase
import com.YashDoesCode.banking.domain.usecase.GetCategoriesUseCase
import com.YashDoesCode.banking.domain.usecase.GetTransactionsUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class TransactionsViewModel(
    private val getTransactionsUseCase: GetTransactionsUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val getAccountsUseCase: GetAccountsUseCase
) : ViewModel() {

    constructor(container: BankingContainer) : this(
        getTransactionsUseCase = container.getTransactionsUseCase,
        getCategoriesUseCase = container.getCategoriesUseCase,
        getAccountsUseCase = container.getAccountsUseCase
    )

    private val _uiState = MutableStateFlow(TransactionsUiState())
    val uiState: StateFlow<TransactionsUiState> = _uiState.asStateFlow()

    init {
        observeData()
    }

    fun onAction(action: TransactionsAction) {
        when (action) {
            is TransactionsAction.SelectCategory -> {
                _uiState.update { it.copy(selectedCategoryId = action.categoryId) }
            }
            is TransactionsAction.SelectAccount -> {
                _uiState.update { it.copy(selectedAccountId = action.accountId) }
            }
            is TransactionsAction.UpdateSearchQuery -> {
                _uiState.update { it.copy(searchQuery = action.query) }
            }
            is TransactionsAction.ClearFilters -> {
                _uiState.update {
                    it.copy(
                        selectedCategoryId = null,
                        selectedAccountId = null,
                        searchQuery = ""
                    )
                }
            }
        }
    }

    private fun observeData() {
        viewModelScope.launch {
            combine(
                getTransactionsUseCase(),
                getCategoriesUseCase(),
                getAccountsUseCase()
            ) { transactions, categories, accounts ->
                _uiState.update { current ->
                    current.copy(
                        transactions = transactions,
                        categories = categories,
                        accounts = accounts,
                        isLoading = false
                    )
                }
            }.collect()
        }
    }

    class Factory(private val container: BankingContainer) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return TransactionsViewModel(container) as T
        }
    }
}
