package com.YashDoesCode.banking.data.local

import com.YashDoesCode.banking.data.local.dao.AccountDao
import com.YashDoesCode.banking.data.local.dao.CategoryDao
import com.YashDoesCode.banking.data.local.dao.TransactionDao
import com.YashDoesCode.banking.data.local.entity.AccountEntity
import com.YashDoesCode.banking.data.local.entity.CategoryEntity
import com.YashDoesCode.banking.data.local.entity.TransactionEntity
import com.YashDoesCode.banking.domain.model.AccountType
import com.YashDoesCode.banking.domain.model.TransactionType
import java.util.concurrent.TimeUnit

object BankingDataSeeder {

    val SEED_CATEGORIES = listOf(
        CategoryEntity("cat_food", "Food & Dining", "Restaurant", 0xFFEF5350),
        CategoryEntity("cat_groceries", "Groceries", "ShoppingCart", 0xFFFFA726),
        CategoryEntity("cat_shopping", "Shopping", "ShoppingBag", 0xFFAB47BC),
        CategoryEntity("cat_transport", "Transport", "DirectionsCar", 0xFF42A5F5),
        CategoryEntity("cat_entertainment", "Entertainment", "Movie", 0xFF26A69A),
        CategoryEntity("cat_utilities", "Utilities & Bills", "Bolt", 0xFFFFCA28),
        CategoryEntity("cat_salary", "Salary / Income", "Payments", 0xFF66BB6A),
        CategoryEntity("cat_transfer", "Transfers", "SwapHoriz", 0xFF7E57C2),
        CategoryEntity("cat_other", "Other", "Category", 0xFF78909C)
    )

    val SEED_ACCOUNTS = listOf(
        AccountEntity("acc_checking", "Primary Checking", "****4321", AccountType.CHECKING.name, 542050L, "USD"),
        AccountEntity("acc_savings", "High-Yield Savings", "****8765", AccountType.SAVINGS.name, 2415000L, "USD"),
        AccountEntity("acc_credit", "Cashback Rewards Card", "****1199", AccountType.CREDIT_CARD.name, 34025L, "USD")
    )

    fun createSeedTransactions(now: Long = System.currentTimeMillis()): List<TransactionEntity> {
        val day = TimeUnit.DAYS.toMillis(1)
        val hour = TimeUnit.HOURS.toMillis(1)

        return listOf(
            // Checking Transactions
            TransactionEntity("tx_01", "acc_checking", 350000L, TransactionType.CREDIT.name, "cat_salary", "Acme Corp", "Bi-weekly Payroll Direct Deposit", now - (day * 1), 542050L),
            TransactionEntity("tx_02", "acc_checking", 4520L, TransactionType.DEBIT.name, "cat_food", "Blue Bottle Coffee", "Morning Espresso & Pastry", now - (day * 1 + hour * 3), 192050L),
            TransactionEntity("tx_03", "acc_checking", 12500L, TransactionType.DEBIT.name, "cat_groceries", "Whole Foods Market", "Weekly Fresh Groceries", now - (day * 2), 196570L),
            TransactionEntity("tx_04", "acc_checking", 150000L, TransactionType.DEBIT.name, "cat_utilities", "Metropolis Power & Gas", "Monthly Electric & Gas Bill", now - (day * 3), 209070L),
            TransactionEntity("tx_05", "acc_checking", 50000L, TransactionType.TRANSFER.name, "cat_transfer", "High-Yield Savings", "Monthly Automatic Savings Goal", now - (day * 5), 359070L),
            TransactionEntity("tx_06", "acc_checking", 3250L, TransactionType.DEBIT.name, "cat_transport", "Uber Technologies", "Ride to Downtown", now - (day * 6), 409070L),
            TransactionEntity("tx_07", "acc_checking", 8990L, TransactionType.DEBIT.name, "cat_shopping", "Amazon.com", "Office Supplies & Cables", now - (day * 7), 412320L),
            TransactionEntity("tx_08", "acc_checking", 1999L, TransactionType.DEBIT.name, "cat_entertainment", "Netflix Subscription", "Monthly Premium Plan", now - (day * 8), 421310L),
            TransactionEntity("tx_09", "acc_checking", 6500L, TransactionType.DEBIT.name, "cat_food", "Chipotle Mexican Grill", "Lunch with Teammates", now - (day * 10), 423309L),
            TransactionEntity("tx_10", "acc_checking", 350000L, TransactionType.CREDIT.name, "cat_salary", "Acme Corp", "Bi-weekly Payroll Direct Deposit", now - (day * 15), 429809L),
            TransactionEntity("tx_11", "acc_checking", 18500L, TransactionType.DEBIT.name, "cat_groceries", "Trader Joe's", "Pantry Essentials", now - (day * 18), 79809L),
            TransactionEntity("tx_12", "acc_checking", 120000L, TransactionType.DEBIT.name, "cat_utilities", "Veritas Residential", "Monthly Rent Installment", now - (day * 20), 98309L),
            TransactionEntity("tx_13", "acc_checking", 4500L, TransactionType.DEBIT.name, "cat_transport", "Shell Gas Station", "Fuel Refill", now - (day * 22), 218309L),
            TransactionEntity("tx_14", "acc_checking", 1499L, TransactionType.DEBIT.name, "cat_entertainment", "Spotify USA", "Premium Individual Plan", now - (day * 25), 222809L),
            TransactionEntity("tx_15", "acc_checking", 21500L, TransactionType.DEBIT.name, "cat_shopping", "Target Store", "Home Goods & Kitchenware", now - (day * 28), 224308L),

            // Savings Transactions
            TransactionEntity("tx_16", "acc_savings", 50000L, TransactionType.CREDIT.name, "cat_transfer", "Primary Checking", "Monthly Savings Transfer", now - (day * 5), 2415000L),
            TransactionEntity("tx_17", "acc_savings", 4250L, TransactionType.CREDIT.name, "cat_salary", "Interest Payment", "Monthly APY Yield Payout (4.25%)", now - (day * 30), 2365000L),
            TransactionEntity("tx_18", "acc_savings", 100000L, TransactionType.CREDIT.name, "cat_transfer", "Primary Checking", "Tax Refund Transfer", now - (day * 45), 2360750L),

            // Credit Card Transactions
            TransactionEntity("tx_19", "acc_credit", 12000L, TransactionType.DEBIT.name, "cat_food", "Osteria Italian Bistro", "Dinner with Friends", now - (day * 2), 34025L),
            TransactionEntity("tx_20", "acc_credit", 8500L, TransactionType.DEBIT.name, "cat_shopping", "Nike Direct", "Running Shoes", now - (day * 4), 22025L),
            TransactionEntity("tx_21", "acc_credit", 13525L, TransactionType.DEBIT.name, "cat_transport", "Delta Air Lines", "Flight Baggage Fee", now - (day * 9), 13525L)
        )
    }

    suspend fun seedIfEmpty(
        accountDao: AccountDao,
        categoryDao: CategoryDao,
        transactionDao: TransactionDao
    ) {
        if (accountDao.countAccounts() == 0) {
            accountDao.insertAccounts(SEED_ACCOUNTS)
        }
        if (categoryDao.countCategories() == 0) {
            categoryDao.insertCategories(SEED_CATEGORIES)
        }
        if (transactionDao.countTransactions() == 0) {
            transactionDao.insertTransactions(createSeedTransactions())
        }
    }
}
