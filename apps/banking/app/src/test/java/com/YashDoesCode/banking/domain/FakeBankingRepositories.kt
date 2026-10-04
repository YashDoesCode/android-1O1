package com.YashDoesCode.banking.domain

import com.YashDoesCode.banking.domain.model.*
import com.YashDoesCode.banking.domain.repository.AccountRepository
import com.YashDoesCode.banking.domain.repository.TransactionRepository
import com.YashDoesCode.banking.domain.repository.TransferRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import java.util.UUID

class FakeAccountRepository : AccountRepository {
    private val _accounts = MutableStateFlow<List<Account>>(emptyList())

    fun setAccounts(accounts: List<Account>) {
        _accounts.value = accounts
    }

    override fun getAccounts(): Flow<List<Account>> = _accounts

    override fun getAccountById(accountId: String): Flow<Account?> =
        _accounts.map { list -> list.find { it.id == accountId } }

    override suspend fun getAccountByIdDirect(accountId: String): Account? =
        _accounts.value.find { it.id == accountId }

    fun updateAccountBalance(accountId: String, newBalanceMinor: Long) {
        _accounts.value = _accounts.value.map {
            if (it.id == accountId) it.copy(balanceMinor = newBalanceMinor) else it
        }
    }
}

class FakeTransactionRepository : TransactionRepository {
    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    private val _categories = MutableStateFlow<List<Category>>(emptyList())

    fun setTransactions(transactions: List<Transaction>) {
        _transactions.value = transactions
    }

    fun setCategories(categories: List<Category>) {
        _categories.value = categories
    }

    override fun getTransactions(accountId: String?): Flow<List<Transaction>> =
        _transactions.map { list ->
            if (accountId != null) list.filter { it.accountId == accountId } else list
        }

    override fun getTransactionsByCategory(categoryId: String): Flow<List<Transaction>> =
        _transactions.map { list -> list.filter { it.category.id == categoryId } }

    override fun getCategories(): Flow<List<Category>> = _categories

    override suspend fun addTransaction(transaction: Transaction) {
        _transactions.value = listOf(transaction) + _transactions.value
    }
}

class FakeTransferRepository(
    private val accountRepository: FakeAccountRepository,
    private val transactionRepository: FakeTransactionRepository
) : TransferRepository {

    override suspend fun transferFunds(request: TransferRequest): TransferResult {
        if (request.amountMinor <= 0L) {
            return TransferResult.InvalidAmount("Transfer amount must be greater than zero.")
        }
        if (request.fromAccountId == request.toAccountId) {
            return TransferResult.SameAccount
        }

        val fromAccount = accountRepository.getAccountByIdDirect(request.fromAccountId)
            ?: return TransferResult.AccountNotFound(request.fromAccountId)

        val toAccount = accountRepository.getAccountByIdDirect(request.toAccountId)
            ?: return TransferResult.AccountNotFound(request.toAccountId)

        if (fromAccount.balanceMinor < request.amountMinor) {
            return TransferResult.InsufficientFunds
        }

        val newFromBalance = fromAccount.balanceMinor - request.amountMinor
        val newToBalance = toAccount.balanceMinor + request.amountMinor

        accountRepository.updateAccountBalance(fromAccount.id, newFromBalance)
        accountRepository.updateAccountBalance(toAccount.id, newToBalance)

        val transferId = UUID.randomUUID().toString()
        val now = System.currentTimeMillis()
        val transferCat = Category("cat_transfer", "Transfers", "SwapHoriz", 0xFF7E57C2)

        transactionRepository.addTransaction(
            Transaction(
                id = "tx_${UUID.randomUUID()}",
                accountId = fromAccount.id,
                amountMinor = request.amountMinor,
                type = TransactionType.TRANSFER,
                category = transferCat,
                merchantOrPayee = toAccount.name,
                description = request.note.ifBlank { "Transfer to ${toAccount.name}" },
                timestamp = now,
                runningBalanceMinor = newFromBalance
            )
        )

        transactionRepository.addTransaction(
            Transaction(
                id = "tx_${UUID.randomUUID()}",
                accountId = toAccount.id,
                amountMinor = request.amountMinor,
                type = TransactionType.CREDIT,
                category = transferCat,
                merchantOrPayee = fromAccount.name,
                description = request.note.ifBlank { "Transfer from ${fromAccount.name}" },
                timestamp = now,
                runningBalanceMinor = newToBalance
            )
        )

        return TransferResult.Success(
            transferId = transferId,
            fromAccountBalanceMinor = newFromBalance,
            toAccountBalanceMinor = newToBalance
        )
    }
}
