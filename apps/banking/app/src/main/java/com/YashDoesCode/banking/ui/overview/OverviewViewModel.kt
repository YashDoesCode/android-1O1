package com.YashDoesCode.banking.ui.overview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.YashDoesCode.banking.di.BankingContainer
import com.YashDoesCode.banking.domain.session.SessionProvider
import com.YashDoesCode.banking.domain.usecase.GetAccountsUseCase
import com.YashDoesCode.banking.domain.usecase.GetTransactionsUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class OverviewViewModel(
    private val getAccountsUseCase: GetAccountsUseCase,
    private val getTransactionsUseCase: GetTransactionsUseCase,
    private val sessionProvider: SessionProvider
) : ViewModel() {

    constructor(container: BankingContainer) : this(
        getAccountsUseCase = container.getAccountsUseCase,
        getTransactionsUseCase = container.getTransactionsUseCase,
        sessionProvider = container.sessionProvider
    )

    private val _uiState = MutableStateFlow(
        OverviewUiState(userName = sessionProvider.currentUserName.substringBefore(" "))
    )
    val uiState: StateFlow<OverviewUiState> = _uiState.asStateFlow()

    init {
        observeData()
    }

    fun onAction(action: OverviewAction) {
        when (action) {
            is OverviewAction.SelectAccount -> {
                _uiState.update { it.copy(selectedAccount = action.account) }
            }
            is OverviewAction.Refresh -> {
                observeData()
            }
        }
    }

    private fun observeData() {
        viewModelScope.launch {
            combine(
                getAccountsUseCase(),
                getTransactionsUseCase()
            ) { accounts, transactions ->
                _uiState.update { current ->
                    current.copy(
                        accounts = accounts,
                        recentTransactions = transactions.take(6),
                        isLoading = false
                    )
                }
            }.collect()
        }
    }

    class Factory(private val container: BankingContainer) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return OverviewViewModel(container) as T
        }
    }
}
