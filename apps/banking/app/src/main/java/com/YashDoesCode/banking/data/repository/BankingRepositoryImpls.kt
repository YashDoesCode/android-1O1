package com.YashDoesCode.banking.data.repository

import com.YashDoesCode.banking.data.local.BankingDatabase
import com.YashDoesCode.banking.data.local.dao.AccountDao
import com.YashDoesCode.banking.data.local.dao.CategoryDao
import com.YashDoesCode.banking.data.local.dao.TransactionDao
import com.YashDoesCode.banking.data.local.entity.CategoryEntity
import com.YashDoesCode.banking.domain.model.*
import com.YashDoesCode.banking.domain.repository.AccountRepository
import com.YashDoesCode.banking.domain.repository.TransactionRepository
import com.YashDoesCode.banking.domain.repository.TransferRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class AccountRepositoryImpl(
    private val accountDao: AccountDao
) : AccountRepository {

    override fun getAccounts(): Flow<List<Account>> {
        return accountDao.getAccounts()
            .map { list -> list.map { it.toDomain() } }
            .flowOn(Dispatchers.IO)
    }

    override fun getAccountById(accountId: String): Flow<Account?> {
        return accountDao.getAccountById(accountId)
            .map { it?.toDomain() }
            .flowOn(Dispatchers.IO)
    }

    override suspend fun getAccountByIdDirect(accountId: String): Account? {
        return withContext(Dispatchers.IO) {
            accountDao.getAccountByIdDirect(accountId)?.toDomain()
        }
    }
}

class TransactionRepositoryImpl(
    private val transactionDao: TransactionDao,
    private val categoryDao: CategoryDao
) : TransactionRepository {

    private val defaultCategory = Category("cat_other", "Other", "Category", 0xFF78909C)

    override fun getTransactions(accountId: String?): Flow<List<Transaction>> {
        val txFlow = if (accountId != null) {
            transactionDao.getTransactionsForAccount(accountId)
        } else {
            transactionDao.getTransactions()
        }

        val catFlow = categoryDao.getCategories()

        return combine(txFlow, catFlow) { txEntities, catEntities ->
            val catMap = catEntities.associate { it.id to it.toDomain() }
            txEntities.map { tx ->
                val category = catMap[tx.categoryId] ?: defaultCategory
                tx.toDomain(category)
            }
        }.flowOn(Dispatchers.IO)
    }

    override fun getTransactionsByCategory(categoryId: String): Flow<List<Transaction>> {
        val txFlow = transactionDao.getTransactionsForCategory(categoryId)
        val catFlow = categoryDao.getCategories()

        return combine(txFlow, catFlow) { txEntities, catEntities ->
            val catMap = catEntities.associate { it.id to it.toDomain() }
            txEntities.map { tx ->
                val category = catMap[tx.categoryId] ?: defaultCategory
                tx.toDomain(category)
            }
        }.flowOn(Dispatchers.IO)
    }

    override fun getCategories(): Flow<List<Category>> {
        return categoryDao.getCategories()
            .map { list -> list.map { it.toDomain() } }
            .flowOn(Dispatchers.IO)
    }

    override suspend fun addTransaction(transaction: Transaction) {
        withContext(Dispatchers.IO) {
            val entity = com.YashDoesCode.banking.data.local.entity.TransactionEntity(
                id = transaction.id,
                accountId = transaction.accountId,
                amountMinor = transaction.amountMinor,
                type = transaction.type.name,
                categoryId = transaction.category.id,
                merchantOrPayee = transaction.merchantOrPayee,
                description = transaction.description,
                timestamp = transaction.timestamp,
                runningBalanceMinor = transaction.runningBalanceMinor
            )
            transactionDao.insertTransaction(entity)
        }
    }
}

class TransferRepositoryImpl(
    private val database: BankingDatabase
) : TransferRepository {

    override suspend fun transferFunds(request: TransferRequest): TransferResult {
        return withContext(Dispatchers.IO) {
            database.executeTransfer(request)
        }
    }
}
