package com.YashDoesCode.banking.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.YashDoesCode.banking.domain.model.Account
import com.YashDoesCode.banking.domain.model.AccountType
import com.YashDoesCode.banking.domain.model.Category
import com.YashDoesCode.banking.domain.model.Transaction
import com.YashDoesCode.banking.domain.model.TransactionType

@Entity(tableName = "accounts")
data class AccountEntity(
    @PrimaryKey val id: String,
    val name: String,
    val accountNumber: String,
    val type: String,
    val balanceMinor: Long,
    val currency: String = "USD"
) {
    fun toDomain(): Account = Account(
        id = id,
        name = name,
        accountNumber = accountNumber,
        type = AccountType.valueOf(type),
        balanceMinor = balanceMinor,
        currency = currency
    )

    companion object {
        fun fromDomain(account: Account): AccountEntity = AccountEntity(
            id = account.id,
            name = account.name,
            accountNumber = account.accountNumber,
            type = account.type.name,
            balanceMinor = account.balanceMinor,
            currency = account.currency
        )
    }
}

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey val id: String,
    val name: String,
    val iconName: String,
    val colorHex: Long
) {
    fun toDomain(): Category = Category(
        id = id,
        name = name,
        iconName = iconName,
        colorHex = colorHex
    )

    companion object {
        fun fromDomain(category: Category): CategoryEntity = CategoryEntity(
            id = category.id,
            name = category.name,
            iconName = category.iconName,
            colorHex = category.colorHex
        )
    }
}

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey val id: String,
    val accountId: String,
    val amountMinor: Long,
    val type: String,
    val categoryId: String,
    val merchantOrPayee: String,
    val description: String,
    val timestamp: Long,
    val runningBalanceMinor: Long
) {
    fun toDomain(category: Category): Transaction = Transaction(
        id = id,
        accountId = accountId,
        amountMinor = amountMinor,
        type = TransactionType.valueOf(type),
        category = category,
        merchantOrPayee = merchantOrPayee,
        description = description,
        timestamp = timestamp,
        runningBalanceMinor = runningBalanceMinor
    )
}
