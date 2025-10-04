package com.YashDoesCode.banking.domain

import com.YashDoesCode.banking.domain.model.Category
import com.YashDoesCode.banking.domain.model.Transaction
import com.YashDoesCode.banking.domain.model.TransactionType
import com.YashDoesCode.banking.domain.usecase.GetSpendingByCategoryUseCase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SpendingAggregationTest {

    private lateinit var transactionRepository: FakeTransactionRepository
    private lateinit var getSpendingByCategoryUseCase: GetSpendingByCategoryUseCase

    private val catFood = Category("cat_food", "Food & Dining", "Restaurant", 0xFFEF5350)
    private val catGroceries = Category("cat_groceries", "Groceries", "ShoppingCart", 0xFFFFA726)
    private val catSalary = Category("cat_salary", "Salary", "Payments", 0xFF66BB6A)

    @Before
    fun setUp() {
        transactionRepository = FakeTransactionRepository()
        getSpendingByCategoryUseCase = GetSpendingByCategoryUseCase(transactionRepository)
    }

    @Test
    fun getSpendingByCategory_aggregatesDebitTransactionsOnly() = runTest {
        val now = System.currentTimeMillis()
        val txs = listOf(
            Transaction("1", "acc_1", 3000L, TransactionType.DEBIT, catFood, "Coffee", "Coffee", now),
            Transaction("2", "acc_1", 7000L, TransactionType.DEBIT, catFood, "Dinner", "Dinner", now),
            Transaction("3", "acc_1", 10000L, TransactionType.DEBIT, catGroceries, "Supermarket", "Groceries", now),
            Transaction("4", "acc_1", 50000L, TransactionType.CREDIT, catSalary, "Employer", "Salary", now) // Should be excluded from spending
        )
        transactionRepository.setTransactions(txs)

        val spending = getSpendingByCategoryUseCase().first()
        // Total debit = 3000 + 7000 + 10000 = 20000L ($200.00)
        // Food = 10000L (50%), Groceries = 10000L (50%)
        assertEquals(2, spending.size)

        val foodSpending = spending.find { it.category.id == "cat_food" }
        assertNotNull(foodSpending)
        assertEquals(10000L, foodSpending?.totalSpentMinor)
        assertEquals(50f, foodSpending!!.percentage, 0.01f)

        val groceriesSpending = spending.find { it.category.id == "cat_groceries" }
        assertNotNull(groceriesSpending)
        assertEquals(10000L, groceriesSpending?.totalSpentMinor)
        assertEquals(50f, groceriesSpending!!.percentage, 0.01f)
    }

    @Test
    fun getSpendingByCategory_withEmptyTransactions_returnsEmptyList() = runTest {
        transactionRepository.setTransactions(emptyList())

        val spending = getSpendingByCategoryUseCase().first()
        assertTrue(spending.isEmpty())
    }
}
