package com.YashDoesCode.banking.domain

import com.YashDoesCode.banking.domain.model.Account
import com.YashDoesCode.banking.domain.model.AccountType
import com.YashDoesCode.banking.domain.model.TransferRequest
import com.YashDoesCode.banking.domain.model.TransferResult
import com.YashDoesCode.banking.domain.usecase.TransferFundsUseCase
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class TransferFundsValidationTest {

    private lateinit var accountRepository: FakeAccountRepository
    private lateinit var transactionRepository: FakeTransactionRepository
    private lateinit var transferRepository: FakeTransferRepository
    private lateinit var transferFundsUseCase: TransferFundsUseCase

    @Before
    fun setUp() {
        accountRepository = FakeAccountRepository()
        transactionRepository = FakeTransactionRepository()
        transferRepository = FakeTransferRepository(accountRepository, transactionRepository)
        transferFundsUseCase = TransferFundsUseCase(transferRepository)

        accountRepository.setAccounts(
            listOf(
                Account("acc_checking", "Checking", "****1111", AccountType.CHECKING, 100000L), // $1,000.00
                Account("acc_savings", "Savings", "****2222", AccountType.SAVINGS, 50000L)       // $500.00
            )
        )
    }

    @Test
    fun transferFunds_zeroAmount_returnsInvalidAmount() = runTest {
        val request = TransferRequest("acc_checking", "acc_savings", 0L, "Zero test")
        val result = transferFundsUseCase(request)

        assertTrue(result is TransferResult.InvalidAmount)
        assertEquals("Transfer amount must be greater than zero.", (result as TransferResult.InvalidAmount).reason)
    }

    @Test
    fun transferFunds_negativeAmount_returnsInvalidAmount() = runTest {
        val request = TransferRequest("acc_checking", "acc_savings", -5000L, "Negative test")
        val result = transferFundsUseCase(request)

        assertTrue(result is TransferResult.InvalidAmount)
    }

    @Test
    fun transferFunds_sameSourceAndDestination_returnsSameAccount() = runTest {
        val request = TransferRequest("acc_checking", "acc_checking", 2000L, "Self transfer")
        val result = transferFundsUseCase(request)

        assertEquals(TransferResult.SameAccount, result)
    }

    @Test
    fun transferFunds_insufficientBalance_returnsInsufficientFunds() = runTest {
        // Checking has $1,000.00 (100000L), trying to transfer $1,500.00 (150000L)
        val request = TransferRequest("acc_checking", "acc_savings", 150000L, "Too much")
        val result = transferFundsUseCase(request)

        assertEquals(TransferResult.InsufficientFunds, result)
        // Verify balances remain untouched
        assertEquals(100000L, accountRepository.getAccountByIdDirect("acc_checking")?.balanceMinor)
        assertEquals(50000L, accountRepository.getAccountByIdDirect("acc_savings")?.balanceMinor)
    }

    @Test
    fun transferFunds_nonExistentSourceAccount_returnsAccountNotFound() = runTest {
        val request = TransferRequest("acc_invalid", "acc_savings", 1000L, "Invalid from")
        val result = transferFundsUseCase(request)

        assertTrue(result is TransferResult.AccountNotFound)
        assertEquals("acc_invalid", (result as TransferResult.AccountNotFound).accountId)
    }

    @Test
    fun transferFunds_nonExistentTargetAccount_returnsAccountNotFound() = runTest {
        val request = TransferRequest("acc_checking", "acc_invalid", 1000L, "Invalid to")
        val result = transferFundsUseCase(request)

        assertTrue(result is TransferResult.AccountNotFound)
        assertEquals("acc_invalid", (result as TransferResult.AccountNotFound).accountId)
    }

    @Test
    fun transferFunds_validRequest_updatesBothBalancesAtomically() = runTest {
        // Transfer $250.00 (25000L) from Checking to Savings
        val request = TransferRequest("acc_checking", "acc_savings", 25000L, "Savings boost")
        val result = transferFundsUseCase(request)

        assertTrue(result is TransferResult.Success)
        val success = result as TransferResult.Success
        assertEquals(75000L, success.fromAccountBalanceMinor) // $750.00
        assertEquals(75000L, success.toAccountBalanceMinor)   // $750.00

        // Verify repository state
        assertEquals(75000L, accountRepository.getAccountByIdDirect("acc_checking")?.balanceMinor)
        assertEquals(75000L, accountRepository.getAccountByIdDirect("acc_savings")?.balanceMinor)
    }
}
