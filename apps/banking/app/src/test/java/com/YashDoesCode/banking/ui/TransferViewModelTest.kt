package com.YashDoesCode.banking.ui

import com.YashDoesCode.banking.domain.FakeAccountRepository
import com.YashDoesCode.banking.domain.FakeTransactionRepository
import com.YashDoesCode.banking.domain.FakeTransferRepository
import com.YashDoesCode.banking.domain.model.Account
import com.YashDoesCode.banking.domain.model.AccountType
import com.YashDoesCode.banking.domain.usecase.GetAccountsUseCase
import com.YashDoesCode.banking.domain.usecase.TransferFundsUseCase
import com.YashDoesCode.banking.ui.transfer.TransferAction
import com.YashDoesCode.banking.ui.transfer.TransferStatus
import com.YashDoesCode.banking.ui.transfer.TransferViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TransferViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var accountRepository: FakeAccountRepository
    private lateinit var transactionRepository: FakeTransactionRepository
    private lateinit var transferRepository: FakeTransferRepository
    private lateinit var viewModel: TransferViewModel

    private val accChecking = Account("acc_1", "Checking", "****1111", AccountType.CHECKING, 100000L) // $1,000.00
    private val accSavings = Account("acc_2", "Savings", "****2222", AccountType.SAVINGS, 50000L)     // $500.00

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        accountRepository = FakeAccountRepository()
        transactionRepository = FakeTransactionRepository()
        transferRepository = FakeTransferRepository(accountRepository, transactionRepository)
        viewModel = TransferViewModel(
            getAccountsUseCase = GetAccountsUseCase(accountRepository),
            transferFundsUseCase = TransferFundsUseCase(transferRepository)
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun init_defaultsFromAndToAccounts() = runTest {
        accountRepository.setAccounts(listOf(accChecking, accSavings))
        testScheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("acc_1", state.fromAccountId)
        assertEquals("acc_2", state.toAccountId)
    }

    @Test
    fun successfulTransfer_updatesStatusAndClearsInputs() = runTest {
        accountRepository.setAccounts(listOf(accChecking, accSavings))
        testScheduler.advanceUntilIdle()

        viewModel.onAction(TransferAction.UpdateAmount("200.00"))
        viewModel.onAction(TransferAction.UpdateNote("Savings deposit"))
        viewModel.onAction(TransferAction.SubmitTransfer)
        testScheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.status is TransferStatus.Success)
        assertEquals("", state.amountInput)
        assertEquals("", state.noteInput)
    }

    @Test
    fun insufficientFunds_showsErrorStatus() = runTest {
        accountRepository.setAccounts(listOf(accChecking, accSavings))
        testScheduler.advanceUntilIdle()

        viewModel.onAction(TransferAction.UpdateAmount("2000.00")) // Exceeds $1000
        viewModel.onAction(TransferAction.SubmitTransfer)
        testScheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.status is TransferStatus.Error)
    }

    @Test
    fun sameAccount_showsErrorStatus() = runTest {
        accountRepository.setAccounts(listOf(accChecking, accSavings))
        testScheduler.advanceUntilIdle()

        viewModel.onAction(TransferAction.SelectToAccount("acc_1"))
        viewModel.onAction(TransferAction.UpdateAmount("50.00"))
        viewModel.onAction(TransferAction.SubmitTransfer)
        testScheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.status is TransferStatus.Error)
    }
}
