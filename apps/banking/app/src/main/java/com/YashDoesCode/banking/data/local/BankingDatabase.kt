package com.YashDoesCode.banking.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.withTransaction
import androidx.sqlite.db.SupportSQLiteDatabase
import com.YashDoesCode.banking.data.local.dao.AccountDao
import com.YashDoesCode.banking.data.local.dao.CategoryDao
import com.YashDoesCode.banking.data.local.dao.TransactionDao
import com.YashDoesCode.banking.data.local.entity.AccountEntity
import com.YashDoesCode.banking.data.local.entity.CategoryEntity
import com.YashDoesCode.banking.data.local.entity.TransactionEntity
import com.YashDoesCode.banking.domain.model.TransferRequest
import com.YashDoesCode.banking.domain.model.TransferResult
import com.YashDoesCode.banking.domain.model.TransactionType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID

@Database(
    entities = [AccountEntity::class, CategoryEntity::class, TransactionEntity::class],
    version = 1,
    exportSchema = false
)
abstract class BankingDatabase : RoomDatabase() {

    abstract fun accountDao(): AccountDao
    abstract fun categoryDao(): CategoryDao
    abstract fun transactionDao(): TransactionDao

    /**
     * Executes atomic fund transfer between two accounts within a single Room transaction.
     * Guarantees that account balances and transaction history records can never desynchronize.
     */
    suspend fun executeTransfer(request: TransferRequest): TransferResult = withTransaction {
        if (request.amountMinor <= 0L) {
            return@withTransaction TransferResult.InvalidAmount("Transfer amount must be greater than zero.")
        }
        if (request.fromAccountId == request.toAccountId) {
            return@withTransaction TransferResult.SameAccount
        }

        val fromAccount = accountDao().getAccountByIdDirect(request.fromAccountId)
            ?: return@withTransaction TransferResult.AccountNotFound(request.fromAccountId)

        val toAccount = accountDao().getAccountByIdDirect(request.toAccountId)
            ?: return@withTransaction TransferResult.AccountNotFound(request.toAccountId)

        if (fromAccount.balanceMinor < request.amountMinor) {
            return@withTransaction TransferResult.InsufficientFunds
        }

        val newFromBalance = fromAccount.balanceMinor - request.amountMinor
        val newToBalance = toAccount.balanceMinor + request.amountMinor

        accountDao().updateBalance(fromAccount.id, newFromBalance)
        accountDao().updateBalance(toAccount.id, newToBalance)

        val transferId = UUID.randomUUID().toString()
        val now = System.currentTimeMillis()

        // Debit transaction record for sender
        val senderTx = TransactionEntity(
            id = "tx_${UUID.randomUUID()}",
            accountId = fromAccount.id,
            amountMinor = request.amountMinor,
            type = TransactionType.TRANSFER.name,
            categoryId = "cat_transfer",
            merchantOrPayee = toAccount.name,
            description = if (request.note.isNotBlank()) request.note else "Transfer to ${toAccount.name}",
            timestamp = now,
            runningBalanceMinor = newFromBalance
        )

        // Credit transaction record for receiver
        val receiverTx = TransactionEntity(
            id = "tx_${UUID.randomUUID()}",
            accountId = toAccount.id,
            amountMinor = request.amountMinor,
            type = TransactionType.CREDIT.name,
            categoryId = "cat_transfer",
            merchantOrPayee = fromAccount.name,
            description = if (request.note.isNotBlank()) request.note else "Transfer from ${fromAccount.name}",
            timestamp = now,
            runningBalanceMinor = newToBalance
        )

        transactionDao().insertTransactions(listOf(senderTx, receiverTx))

        return@withTransaction TransferResult.Success(
            transferId = transferId,
            fromAccountBalanceMinor = newFromBalance,
            toAccountBalanceMinor = newToBalance
        )
    }

    companion object {
        @Volatile
        private var INSTANCE: BankingDatabase? = null

        fun getInstance(context: Context): BankingDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }
        }

        private fun buildDatabase(context: Context): BankingDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                BankingDatabase::class.java,
                "banking.db"
            ).addCallback(object : Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    CoroutineScope(Dispatchers.IO).launch {
                        getInstance(context).let { database ->
                            BankingDataSeeder.seedIfEmpty(
                                database.accountDao(),
                                database.categoryDao(),
                                database.transactionDao()
                            )
                        }
                    }
                }
            }).build()
        }
    }
}
