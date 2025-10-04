package com.YashDoesCode.banking.ui

import com.YashDoesCode.banking.domain.FakeAccountRepository
import com.YashDoesCode.banking.domain.FakeTransactionRepository
import com.YashDoesCode.banking.domain.model.Account
import com.YashDoesCode.banking.domain.model.AccountType
import com.YashDoesCode.banking.domain.model.Category
import com.YashDoesCode.banking.domain.model.Transaction
import com.YashDoesCode.banking.domain.model.TransactionType
import com.YashDoesCode.banking.domain.session.MockSessionProvider
import com.YashDoesCode.banking.domain.usecase.GetAccountsUseCase
import com.YashDoesCode.banking.domain.usecase.GetTransactionsUseCase
import com.YashDoesCode.banking.ui.overview.OverviewAction
import com.YashDoesCode.banking.ui.overview.OverviewViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class OverviewViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var accountRepository: FakeAccountRepository
    private lateinit var transactionRepository: FakeTransactionRepository
    private lateinit var viewModel: OverviewViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        accountRepository = FakeAccountRepository()
        transactionRepository = FakeTransactionRepository()
        viewModel = OverviewViewModel(
            getAccountsUseCase = GetAccountsUseCase(accountRepository),
            getTransactionsUseCase = GetTransactionsUseCase(transactionRepository),
            sessionProvider = MockSessionProvider()
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun observeAccountsAndTransactions_calculatesNetWorthCorrectly() = runTest {
        val accounts = listOf(
            Account("acc_1", "Checking", "****1111", AccountType.CHECKING, 500000L), // $5,000.00
            Account("acc_2", "Savings", "****2222", AccountType.SAVINGS, 1000000L)   // $10,000.00
        )
        accountRepository.setAccounts(accounts)

        val category = Category("cat_1", "Food", "Restaurant", 0xFFEF5350)
        val txs = listOf(
            Transaction("tx_1", "acc_1", 2500L, TransactionType.DEBIT, category, "Coffee", "Coffee", System.currentTimeMillis())
        )
        transactionRepository.setTransactions(txs)
        testScheduler.advanceUntilIdle()

        assertEquals(2, viewModel.uiState.value.accounts.size)
        assertEquals(1500000L, viewModel.uiState.value.totalNetWorthMinor) // $15,000.00
        assertEquals(1, viewModel.uiState.value.recentTransactions.size)
    }

    @Test
    fun selectAccount_updatesSelectedAccountInState() {
        val account = Account("acc_1", "Checking", "****1111", AccountType.CHECKING, 500000L)
        viewModel.onAction(OverviewAction.SelectAccount(account))
        assertEquals("acc_1", viewModel.uiState.value.selectedAccount?.id)

        viewModel.onAction(OverviewAction.SelectAccount(null))
        assertEquals(null, viewModel.uiState.value.selectedAccount)
    }
}
