package com.YashDoesCode.banking.ui

import com.YashDoesCode.banking.domain.FakeAccountRepository
import com.YashDoesCode.banking.domain.FakeTransactionRepository
import com.YashDoesCode.banking.domain.model.Account
import com.YashDoesCode.banking.domain.model.AccountType
import com.YashDoesCode.banking.domain.model.Category
import com.YashDoesCode.banking.domain.model.Transaction
import com.YashDoesCode.banking.domain.model.TransactionType
import com.YashDoesCode.banking.domain.usecase.GetAccountsUseCase
import com.YashDoesCode.banking.domain.usecase.GetSpendingByCategoryUseCase
import com.YashDoesCode.banking.ui.spending.SpendingAction
import com.YashDoesCode.banking.ui.spending.SpendingViewModel
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
class SpendingViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var accountRepository: FakeAccountRepository
    private lateinit var transactionRepository: FakeTransactionRepository
    private lateinit var viewModel: SpendingViewModel

    private val catFood = Category("cat_food", "Food", "Restaurant", 0xFFEF5350)
    private val catShopping = Category("cat_shop", "Shopping", "Mall", 0xFFAB47BC)

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        accountRepository = FakeAccountRepository()
        transactionRepository = FakeTransactionRepository()
        viewModel = SpendingViewModel(
            getSpendingByCategoryUseCase = GetSpendingByCategoryUseCase(transactionRepository),
            getAccountsUseCase = GetAccountsUseCase(accountRepository)
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun observeSpending_calculatesCategoryBreakdown() = runTest {
        val accounts = listOf(
            Account("acc_1", "Checking", "****1111", AccountType.CHECKING, 100000L),
            Account("acc_2", "Credit", "****2222", AccountType.CREDIT_CARD, -5000L)
        )
        accountRepository.setAccounts(accounts)
        transactionRepository.setCategories(listOf(catFood, catShopping))

        val txs = listOf(
            Transaction("tx_1", "acc_1", 3000L, TransactionType.DEBIT, catFood, "Dinner", "Bistro", 1000L),
            Transaction("tx_2", "acc_1", 7000L, TransactionType.DEBIT, catShopping, "Clothes", "Zara", 2000L)
        )
        transactionRepository.setTransactions(txs)
        testScheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(2, state.spendingList.size)
        assertEquals(10000L, state.totalSpentMinor)
        assertEquals(70.0f, state.spendingList[0].percentage, 0.01f) // Shopping 70%
        assertEquals(30.0f, state.spendingList[1].percentage, 0.01f) // Food 30%
    }

    @Test
    fun selectAccount_filtersSpendingBreakdown() = runTest {
        transactionRepository.setCategories(listOf(catFood, catShopping))
        val txs = listOf(
            Transaction("tx_1", "acc_1", 3000L, TransactionType.DEBIT, catFood, "Dinner", "Bistro", 1000L),
            Transaction("tx_2", "acc_2", 7000L, TransactionType.DEBIT, catShopping, "Clothes", "Zara", 2000L)
        )
        transactionRepository.setTransactions(txs)
        testScheduler.advanceUntilIdle()

        viewModel.onAction(SpendingAction.SelectAccount("acc_1"))
        testScheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("acc_1", state.selectedAccountId)
        assertEquals(1, state.spendingList.size)
        assertEquals("cat_food", state.spendingList[0].category.id)
        assertEquals(3000L, state.totalSpentMinor)
    }
}
