package com.YashDoesCode.banking.ui.spending

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.YashDoesCode.banking.di.BankingContainer
import com.YashDoesCode.banking.domain.usecase.GetAccountsUseCase
import com.YashDoesCode.banking.domain.usecase.GetSpendingByCategoryUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class SpendingViewModel(
    private val getSpendingByCategoryUseCase: GetSpendingByCategoryUseCase,
    private val getAccountsUseCase: GetAccountsUseCase
) : ViewModel() {

    constructor(container: BankingContainer) : this(
        getSpendingByCategoryUseCase = container.getSpendingByCategoryUseCase,
        getAccountsUseCase = container.getAccountsUseCase
    )

    private val _selectedAccountId = MutableStateFlow<String?>(null)
    private val _uiState = MutableStateFlow(SpendingUiState())
    val uiState: StateFlow<SpendingUiState> = _uiState.asStateFlow()

    init {
        observeData()
    }

    fun onAction(action: SpendingAction) {
        when (action) {
            is SpendingAction.SelectAccount -> {
                _selectedAccountId.value = action.accountId
                _uiState.update { it.copy(selectedAccountId = action.accountId) }
            }
            is SpendingAction.Refresh -> {
                observeData()
            }
        }
    }

    private fun observeData() {
        viewModelScope.launch {
            getAccountsUseCase().collect { accounts ->
                _uiState.update { it.copy(accounts = accounts) }
            }
        }

        viewModelScope.launch {
            _selectedAccountId.flatMapLatest { accountId ->
                getSpendingByCategoryUseCase(accountId)
            }.collect { spendingList ->
                _uiState.update {
                    it.copy(
                        spendingList = spendingList,
                        isLoading = false
                    )
                }
            }
        }
    }

    class Factory(private val container: BankingContainer) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SpendingViewModel(container) as T
        }
    }
}
