package com.YashDoesCode.banking.domain.model

enum class AccountType {
    CHECKING,
    SAVINGS,
    CREDIT_CARD,
    INVESTMENT
}

data class Account(
    val id: String,
    val name: String,
    val accountNumber: String,
    val type: AccountType,
    val balanceMinor: Long, // Amount in minor units (e.g., cents/paise) to prevent floating point inaccuracy
    val currency: String = "USD"
) {
    val formattedBalance: String
        get() = formatMoney(balanceMinor, currency)
}

enum class TransactionType {
    DEBIT,
    CREDIT,
    TRANSFER
}

data class Category(
    val id: String,
    val name: String,
    val iconName: String,
    val colorHex: Long
)

data class Transaction(
    val id: String,
    val accountId: String,
    val amountMinor: Long, // Positive value in minor units
    val type: TransactionType,
    val category: Category,
    val merchantOrPayee: String,
    val description: String,
    val timestamp: Long,
    val runningBalanceMinor: Long = 0L
) {
    val formattedAmount: String
        get() {
            val prefix = when (type) {
                TransactionType.DEBIT -> "-"
                TransactionType.CREDIT -> "+"
                TransactionType.TRANSFER -> ""
            }
            return "$prefix${formatMoney(amountMinor)}"
        }
}

data class TransferRequest(
    val fromAccountId: String,
    val toAccountId: String,
    val amountMinor: Long,
    val note: String = ""
)

sealed interface TransferResult {
    data class Success(
        val transferId: String,
        val fromAccountBalanceMinor: Long,
        val toAccountBalanceMinor: Long
    ) : TransferResult

    data object InsufficientFunds : TransferResult
    data object SameAccount : TransferResult
    data class InvalidAmount(val reason: String) : TransferResult
    data class AccountNotFound(val accountId: String) : TransferResult
    data class Failure(val message: String) : TransferResult
}

data class CategorySpending(
    val category: Category,
    val totalSpentMinor: Long,
    val percentage: Float
) {
    val formattedTotal: String
        get() = formatMoney(totalSpentMinor)
}

fun formatMoney(amountMinor: Long, currency: String = "USD"): String {
    val symbol = when (currency) {
        "USD" -> "$"
        "EUR" -> "€"
        "GBP" -> "£"
        "INR" -> "₹"
        else -> "$"
    }
    val absAmount = kotlin.math.abs(amountMinor)
    val units = absAmount / 100
    val minor = absAmount % 100
    val sign = if (amountMinor < 0) "-" else ""
    return String.format(java.util.Locale.US, "%s%s%,d.%02d", sign, symbol, units, minor)
}
