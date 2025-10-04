package com.YashDoesCode.banking.ui.transfer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.YashDoesCode.banking.di.BankingContainer
import com.YashDoesCode.banking.domain.model.TransferRequest
import com.YashDoesCode.banking.domain.model.TransferResult
import com.YashDoesCode.banking.domain.usecase.GetAccountsUseCase
import com.YashDoesCode.banking.domain.usecase.TransferFundsUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class TransferViewModel(
    private val getAccountsUseCase: GetAccountsUseCase,
    private val transferFundsUseCase: TransferFundsUseCase
) : ViewModel() {

    constructor(container: BankingContainer) : this(
        getAccountsUseCase = container.getAccountsUseCase,
        transferFundsUseCase = container.transferFundsUseCase
    )

    private val _uiState = MutableStateFlow(TransferUiState())
    val uiState: StateFlow<TransferUiState> = _uiState.asStateFlow()

    init {
        observeAccounts()
    }

    fun onAction(action: TransferAction) {
        when (action) {
            is TransferAction.SelectFromAccount -> {
                _uiState.update { it.copy(fromAccountId = action.accountId) }
            }
            is TransferAction.SelectToAccount -> {
                _uiState.update { it.copy(toAccountId = action.accountId) }
            }
            is TransferAction.UpdateAmount -> {
                _uiState.update { it.copy(amountInput = action.amount) }
            }
            is TransferAction.UpdateNote -> {
                _uiState.update { it.copy(noteInput = action.note) }
            }
            is TransferAction.SubmitTransfer -> submitTransfer()
            is TransferAction.DismissStatus -> {
                _uiState.update { it.copy(status = TransferStatus.Idle) }
            }
        }
    }

    private fun submitTransfer() {
        val state = _uiState.value
        val fromId = state.fromAccountId ?: return
        val toId = state.toAccountId ?: return
        val amount = state.amountMinor

        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true) }
            val request = TransferRequest(
                fromAccountId = fromId,
                toAccountId = toId,
                amountMinor = amount,
                note = state.noteInput.trim()
            )

            val result = transferFundsUseCase(request)
            val newStatus = when (result) {
                is TransferResult.Success -> {
                    TransferStatus.Success(
                        transferId = result.transferId,
                        fromBalanceMinor = result.fromAccountBalanceMinor,
                        toBalanceMinor = result.toAccountBalanceMinor
                    )
                }
                is TransferResult.InsufficientFunds -> {
                    TransferStatus.Error("Insufficient funds in the selected source account.")
                }
                is TransferResult.SameAccount -> {
                    TransferStatus.Error("Source and destination accounts must be different.")
                }
                is TransferResult.InvalidAmount -> {
                    TransferStatus.Error(result.reason)
                }
                is TransferResult.AccountNotFound -> {
                    TransferStatus.Error("Selected account could not be found.")
                }
                is TransferResult.Failure -> {
                    TransferStatus.Error(result.message)
                }
            }

            _uiState.update {
                it.copy(
                    isSubmitting = false,
                    status = newStatus,
                    amountInput = if (result is TransferResult.Success) "" else it.amountInput,
                    noteInput = if (result is TransferResult.Success) "" else it.noteInput
                )
            }
        }
    }

    private fun observeAccounts() {
        viewModelScope.launch {
            getAccountsUseCase().collect { accounts ->
                _uiState.update { current ->
                    val defaultFrom = current.fromAccountId ?: accounts.firstOrNull()?.id
                    val defaultTo = current.toAccountId ?: accounts.getOrNull(1)?.id
                    current.copy(
                        accounts = accounts,
                        fromAccountId = defaultFrom,
                        toAccountId = defaultTo
                    )
                }
            }
        }
    }

    class Factory(private val container: BankingContainer) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return TransferViewModel(container) as T
        }
    }
}
