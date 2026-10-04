package com.YashDoesCode.banking

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.YashDoesCode.banking.domain.model.Account
import com.YashDoesCode.banking.domain.model.AccountType
import com.YashDoesCode.banking.domain.model.TransferRequest
import com.YashDoesCode.banking.domain.model.TransferResult
import com.YashDoesCode.banking.domain.repository.AccountRepository
import com.YashDoesCode.banking.domain.repository.TransferRepository
import com.YashDoesCode.banking.domain.usecase.GetAccountsUseCase
import com.YashDoesCode.banking.domain.usecase.TransferFundsUseCase
import com.YashDoesCode.banking.ui.theme.BankingTheme
import com.YashDoesCode.banking.ui.transfer.TransferScreen
import com.YashDoesCode.banking.ui.transfer.TransferViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TransferFlowUiTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private class TestAccountRepo : AccountRepository {
        private val _accounts = MutableStateFlow(
            listOf(
                Account("acc_chk", "Total Checking", "****1234", AccountType.CHECKING, 500000L),
                Account("acc_sav", "High-Yield Savings", "****5678", AccountType.SAVINGS, 1000000L)
            )
        )
        override fun getAccounts(): Flow<List<Account>> = _accounts.asStateFlow()
        override fun getAccountById(accountId: String): Flow<Account?> =
            _accounts.map { list -> list.find { it.id == accountId } }
        override suspend fun getAccountByIdDirect(accountId: String): Account? =
            _accounts.value.find { it.id == accountId }
    }

    private class TestTransferRepo : TransferRepository {
        override suspend fun transferFunds(request: TransferRequest): TransferResult {
            return TransferResult.Success(
                transferId = "tx_transfer_1",
                fromAccountBalanceMinor = 400000L,
                toAccountBalanceMinor = 1100000L
            )
        }
    }

    @Test
    fun transferScreen_displaysAccountsAndAllowsAmountEntry() {
        val accountRepo = TestAccountRepo()
        val transferRepo = TestTransferRepo()
        val viewModel = TransferViewModel(
            getAccountsUseCase = GetAccountsUseCase(accountRepo),
            transferFundsUseCase = TransferFundsUseCase(transferRepo)
        )

        composeTestRule.setContent {
            BankingTheme {
                TransferScreen(viewModel = viewModel)
            }
        }

        // Verify header and initial accounts
        composeTestRule.onNodeWithText("Transfer Funds").assertIsDisplayed()
        composeTestRule.onNodeWithText("Total Checking").assertIsDisplayed()
        composeTestRule.onNodeWithText("High-Yield Savings").assertIsDisplayed()

        // Enter amount
        composeTestRule.onNodeWithText("Transfer Amount").performTextInput("100.00")

        // Transfer button should now be enabled and clickable
        composeTestRule.onNodeWithText("Transfer Now").assertIsEnabled().performClick()

        // Verify Success dialog appears
        composeTestRule.onNodeWithText("Transfer Complete").assertIsDisplayed()
        composeTestRule.onNodeWithText("Done").performClick()
        composeTestRule.onNodeWithText("Transfer Complete").assertDoesNotExist()
    }
}
