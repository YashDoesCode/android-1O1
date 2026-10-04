package com.YashDoesCode.banking.domain.repository

import com.YashDoesCode.banking.domain.model.*
import kotlinx.coroutines.flow.Flow

interface AccountRepository {
    fun getAccounts(): Flow<List<Account>>
    fun getAccountById(accountId: String): Flow<Account?>
    suspend fun getAccountByIdDirect(accountId: String): Account?
}

interface TransactionRepository {
    fun getTransactions(accountId: String? = null): Flow<List<Transaction>>
    fun getTransactionsByCategory(categoryId: String): Flow<List<Transaction>>
    fun getCategories(): Flow<List<Category>>
    suspend fun addTransaction(transaction: Transaction)
}

interface TransferRepository {
    suspend fun transferFunds(request: TransferRequest): TransferResult
}
