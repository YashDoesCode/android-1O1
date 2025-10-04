package com.YashDoesCode.banking.ui

import com.YashDoesCode.banking.domain.FakeAccountRepository
import com.YashDoesCode.banking.domain.FakeTransactionRepository
import com.YashDoesCode.banking.domain.model.Account
import com.YashDoesCode.banking.domain.model.AccountType
import com.YashDoesCode.banking.domain.model.Category
import com.YashDoesCode.banking.domain.model.Transaction
import com.YashDoesCode.banking.domain.model.TransactionType
import com.YashDoesCode.banking.domain.usecase.GetAccountsUseCase
import com.YashDoesCode.banking.domain.usecase.GetCategoriesUseCase
import com.YashDoesCode.banking.domain.usecase.GetTransactionsUseCase
import com.YashDoesCode.banking.ui.transactions.TransactionsAction
import com.YashDoesCode.banking.ui.transactions.TransactionsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TransactionsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var accountRepository: FakeAccountRepository
    private lateinit var transactionRepository: FakeTransactionRepository
    private lateinit var viewModel: TransactionsViewModel

    private val catFood = Category("cat_food", "Food", "Restaurant", 0xFFEF5350)
    private val catSalary = Category("cat_salary", "Salary", "Work", 0xFF66BB6A)

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        accountRepository = FakeAccountRepository()
        transactionRepository = FakeTransactionRepository()
        viewModel = TransactionsViewModel(
            getTransactionsUseCase = GetTransactionsUseCase(transactionRepository),
            getCategoriesUseCase = GetCategoriesUseCase(transactionRepository),
            getAccountsUseCase = GetAccountsUseCase(accountRepository)
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun loadData_populatesTransactions() = runTest {
        val accounts = listOf(
            Account("acc_1", "Checking", "****1111", AccountType.CHECKING, 100000L)
        )
        accountRepository.setAccounts(accounts)
        transactionRepository.setCategories(listOf(catFood, catSalary))

        val txs = listOf(
            Transaction("tx_2", "acc_1", 2000L, TransactionType.DEBIT, catFood, "Coffee", "Coffee", 2000L, 98000L),
            Transaction("tx_1", "acc_1", 50000L, TransactionType.CREDIT, catSalary, "Salary", "Paycheck", 1000L, 100000L)
        )
        transactionRepository.setTransactions(txs)
        testScheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(2, state.transactions.size)
        assertEquals(98000L, state.transactions[0].runningBalanceMinor)
        assertEquals(100000L, state.transactions[1].runningBalanceMinor)
    }

    @Test
    fun filterByCategory_filtersDisplayedTransactions() = runTest {
        transactionRepository.setCategories(listOf(catFood, catSalary))
        val txs = listOf(
            Transaction("tx_2", "acc_1", 2000L, TransactionType.DEBIT, catFood, "Coffee", "Coffee", 2000L),
            Transaction("tx_1", "acc_1", 50000L, TransactionType.CREDIT, catSalary, "Salary", "Paycheck", 1000L)
        )
        transactionRepository.setTransactions(txs)
        testScheduler.advanceUntilIdle()

        viewModel.onAction(TransactionsAction.SelectCategory("cat_food"))
        testScheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("cat_food", state.selectedCategoryId)
        assertEquals(1, state.filteredTransactions.size)
        assertEquals("Coffee", state.filteredTransactions[0].merchantOrPayee)
    }

    @Test
    fun searchQuery_filtersByTitleOrMerchant() = runTest {
        transactionRepository.setCategories(listOf(catFood, catSalary))
        val txs = listOf(
            Transaction("tx_2", "acc_1", 2000L, TransactionType.DEBIT, catFood, "Starbucks", "Latte", 2000L),
            Transaction("tx_1", "acc_1", 50000L, TransactionType.CREDIT, catSalary, "TechCorp", "Monthly Pay", 1000L)
        )
        transactionRepository.setTransactions(txs)
        testScheduler.advanceUntilIdle()

        viewModel.onAction(TransactionsAction.UpdateSearchQuery("starbucks"))
        testScheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(1, state.filteredTransactions.size)
        assertEquals("Starbucks", state.filteredTransactions[0].merchantOrPayee)
    }
}
