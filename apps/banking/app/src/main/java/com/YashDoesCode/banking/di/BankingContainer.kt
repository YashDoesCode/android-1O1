package com.YashDoesCode.banking.di

import android.content.Context
import com.YashDoesCode.banking.data.local.BankingDatabase
import com.YashDoesCode.banking.data.repository.AccountRepositoryImpl
import com.YashDoesCode.banking.data.repository.TransactionRepositoryImpl
import com.YashDoesCode.banking.data.repository.TransferRepositoryImpl
import com.YashDoesCode.banking.domain.repository.AccountRepository
import com.YashDoesCode.banking.domain.repository.TransactionRepository
import com.YashDoesCode.banking.domain.repository.TransferRepository
import com.YashDoesCode.banking.domain.session.MockSessionProvider
import com.YashDoesCode.banking.domain.session.SessionProvider
import com.YashDoesCode.banking.domain.usecase.*

class BankingContainer(private val context: Context) {

    val database: BankingDatabase by lazy {
        BankingDatabase.getInstance(context)
    }

    val accountRepository: AccountRepository by lazy {
        AccountRepositoryImpl(database.accountDao())
    }

    val transactionRepository: TransactionRepository by lazy {
        TransactionRepositoryImpl(database.transactionDao(), database.categoryDao())
    }

    val transferRepository: TransferRepository by lazy {
        TransferRepositoryImpl(database)
    }

    val sessionProvider: SessionProvider by lazy {
        MockSessionProvider()
    }

    val getAccountsUseCase: GetAccountsUseCase by lazy {
        GetAccountsUseCase(accountRepository)
    }

    val getAccountByIdUseCase: GetAccountByIdUseCase by lazy {
        GetAccountByIdUseCase(accountRepository)
    }

    val getTransactionsUseCase: GetTransactionsUseCase by lazy {
        GetTransactionsUseCase(transactionRepository)
    }

    val getCategoriesUseCase: GetCategoriesUseCase by lazy {
        GetCategoriesUseCase(transactionRepository)
    }

    val transferFundsUseCase: TransferFundsUseCase by lazy {
        TransferFundsUseCase(transferRepository)
    }

    val getSpendingByCategoryUseCase: GetSpendingByCategoryUseCase by lazy {
        GetSpendingByCategoryUseCase(transactionRepository)
    }
}
