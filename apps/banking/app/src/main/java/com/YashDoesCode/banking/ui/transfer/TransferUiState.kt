package com.YashDoesCode.banking.ui.transfer

import com.YashDoesCode.banking.domain.model.Account

sealed interface TransferStatus {
    data object Idle : TransferStatus
    data class Success(val transferId: String, val fromBalanceMinor: Long, val toBalanceMinor: Long) : TransferStatus
    data class Error(val message: String) : TransferStatus
}

data class TransferUiState(
    val accounts: List<Account> = emptyList(),
    val fromAccountId: String? = null,
    val toAccountId: String? = null,
    val amountInput: String = "",
    val noteInput: String = "",
    val isSubmitting: Boolean = false,
    val status: TransferStatus = TransferStatus.Idle
) {
    val fromAccount: Account?
        get() = accounts.find { it.id == fromAccountId }

    val toAccount: Account?
        get() = accounts.find { it.id == toAccountId }

    val amountMinor: Long
        get() {
            val clean = amountInput.replace("$", "").replace(",", "").trim()
            val parsed = clean.toDoubleOrNull() ?: return 0L
            return (parsed * 100.0).toLong()
        }

    val isValidInput: Boolean
        get() = fromAccountId != null &&
                toAccountId != null &&
                fromAccountId != toAccountId &&
                amountMinor > 0L &&
                (fromAccount?.balanceMinor ?: 0L) >= amountMinor
}

sealed interface TransferAction {
    data class SelectFromAccount(val accountId: String) : TransferAction
    data class SelectToAccount(val accountId: String) : TransferAction
    data class UpdateAmount(val amount: String) : TransferAction
    data class UpdateNote(val note: String) : TransferAction
    data object SubmitTransfer : TransferAction
    data object DismissStatus : TransferAction
}
