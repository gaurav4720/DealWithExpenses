package com.example.dealwithexpenses.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import java.time.Month
import java.time.Year
import java.util.*

//giving the name of the table
@Entity(tableName = "transactions")
@TypeConverters(DateConverter::class)
data class Transaction(
    val user_id: String,
    @PrimaryKey(autoGenerate = true) val trans_id: Long, //transaction id will be the primary key

    val title: String,
    val description: String,
    val transactionAmount: Double,

    @TypeConverters(DateConverter::class)
    val transactionDate: Date,

    //to check if the transaction is recurring or not
    val isRecurring: Boolean,

    @TypeConverters(DateConverter::class)
    val fromDate: Date,

    @TypeConverters(DateConverter::class)
    val toDate: Date,

    val month: Int,
    val year: Int,
    val monthYear: Int,

    val transactionType: TransactionType,
    val transactionCategory: TransactionCategory,
    val transactionMode: TransactionMode,
    var transactionStatus: TransactionStatus
)

enum class TransactionType {
    EXPENSE,
    INCOME
}

enum class TransactionCategory {
    FOOD,
    CLOTHING,
    ENTERTAINMENT,
    TRANSPORT,
    HEALTH,
    EDUCATION,
    BILLS,
    OTHER
}

enum class TransactionMode {
    CASH,
    CREDIT_CARD,
    DEBIT_CARD
}

enum class TransactionStatus {
    COMPLETED,
    PENDING,
}