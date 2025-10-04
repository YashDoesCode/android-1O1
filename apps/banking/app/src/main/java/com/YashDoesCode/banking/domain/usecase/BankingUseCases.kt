package com.YashDoesCode.banking.domain.usecase

import com.YashDoesCode.banking.domain.model.*
import com.YashDoesCode.banking.domain.repository.AccountRepository
import com.YashDoesCode.banking.domain.repository.TransactionRepository
import com.YashDoesCode.banking.domain.repository.TransferRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetAccountsUseCase(
    private val repository: AccountRepository
) {
    operator fun invoke(): Flow<List<Account>> = repository.getAccounts()
}

class GetAccountByIdUseCase(
    private val repository: AccountRepository
) {
    operator fun invoke(accountId: String): Flow<Account?> = repository.getAccountById(accountId)
}

class GetTransactionsUseCase(
    private val repository: TransactionRepository
) {
    operator fun invoke(accountId: String? = null): Flow<List<Transaction>> =
        repository.getTransactions(accountId)
}

class GetCategoriesUseCase(
    private val repository: TransactionRepository
) {
    operator fun invoke(): Flow<List<Category>> = repository.getCategories()
}

class TransferFundsUseCase(
    private val repository: TransferRepository
) {
    suspend operator fun invoke(request: TransferRequest): TransferResult {
        if (request.amountMinor <= 0L) {
            return TransferResult.InvalidAmount("Transfer amount must be greater than zero.")
        }
        if (request.fromAccountId == request.toAccountId) {
            return TransferResult.SameAccount
        }
        return repository.transferFunds(request)
    }
}

class GetSpendingByCategoryUseCase(
    private val repository: TransactionRepository
) {
    operator fun invoke(accountId: String? = null): Flow<List<CategorySpending>> {
        return repository.getTransactions(accountId).map { transactions ->
            // Filter only DEBIT transactions (expenses)
            val debitTxs = transactions.filter { it.type == TransactionType.DEBIT }
            val totalDebit = debitTxs.sumOf { it.amountMinor }

            if (totalDebit == 0L) {
                emptyList()
            } else {
                debitTxs.groupBy { it.category }
                    .map { (category, txList) ->
                        val categoryTotal = txList.sumOf { it.amountMinor }
                        val percentage = (categoryTotal.toFloat() / totalDebit.toFloat()) * 100f
                        CategorySpending(
                            category = category,
                            totalSpentMinor = categoryTotal,
                            percentage = percentage
                        )
                    }
                    .sortedByDescending { it.totalSpentMinor }
            }
        }
    }
}
