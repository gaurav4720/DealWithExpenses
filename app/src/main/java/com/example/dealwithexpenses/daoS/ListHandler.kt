package com.example.dealwithexpenses.daoS

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.example.dealwithexpenses.entities.*

@Dao
interface ListHandler {

    @Query("SELECT * FROM transactions WHERE user_id = :user_id and transactionDate = :date")
    fun getTransactions(user_id: String, date: Long): LiveData<List<Transaction>>

    @Query("SELECT year FROM transactions WHERE user_id = :user_id GROUP BY year")
    fun getYears(user_id: String): LiveData<List<Int>>

    @Query("SELECT * FROM transactions WHERE user_id = :user_id and transactionType = :transactionType ORDER BY transactionDate DESC")
    fun getTransactionsByType(
        user_id: String,
        transactionType: TransactionType
    ): LiveData<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE user_id = :user_id and transactionCategory = :transactionCategory")
    fun getTransactionsByCategory(
        user_id: String,
        transactionCategory: TransactionCategory
    ): LiveData<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE user_id = :user_id and transactionMode = :transactionMode ORDER BY transactionDate DESC")
    fun getTransactionsByMode(
        user_id: String,
        transactionMode: TransactionMode
    ): LiveData<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE user_id = :user_id and transactionStatus = :transactionStatus ORDER BY transactionDate DESC")
    fun getTransactionsByStatus(
        user_id: String,
        transactionStatus: TransactionStatus
    ): LiveData<List<Transaction>>

    @Query("SELECT SUM(transactionAmount) FROM transactions WHERE user_id = :user_id and transactionCategory = :transactionCategory")
    fun getAmountByCategory(
        user_id: String,
        transactionCategory: TransactionCategory
    ): LiveData<Double>

    @Query("SELECT SUM(transactionAmount) FROM transactions WHERE user_id = :user_id and transactionMode = :transactionMode ORDER BY transactionDate DESC")
    fun getAmountByMode(user_id: String, transactionMode: TransactionMode): LiveData<Double>

    @Query("SELECT SUM(transactionAmount) FROM transactions WHERE user_id = :user_id and transactionType = :transactionType")
    fun getAmountByType(user_id: String, transactionType: TransactionType): LiveData<Double>

    @Query("SELECT SUM(transactionAmount) FROM transactions WHERE user_id = :user_id and transactionStatus = :transactionStatus")
    fun getAmountByStatus(user_id: String, transactionStatus: TransactionStatus): LiveData<Double>

    @Query("SELECT SUM(transactionAmount) FROM transactions WHERE user_id = :user_id and transactionDate = :date")
    fun getAmountByDate(user_id: String, date: Long): LiveData<Double>

    @Query("SELECT SUM(transactionAmount) FROM transactions WHERE user_id = :user_id and monthYear = :monthYear")
    fun getAmountByMonth(user_id: String, monthYear: Long): LiveData<Double>

    @Query("SELECT SUM(transactionAmount) FROM transactions WHERE user_id = :user_id and transactionMode = :transactionMode and transactionDate = :date")
    fun getAmountByModeAndDate(
        user_id: String,
        transactionMode: TransactionMode,
        date: Long
    ): LiveData<Double>

    @Query("SELECT SUM(transactionAmount) FROM transactions WHERE user_id = :user_id and transactionMode = :transactionMode and transactionStatus = :status")
    fun getAmountByModeAndStatus(
        user_id: String,
        transactionMode: TransactionMode,
        status: TransactionStatus
    ): LiveData<Double>

    @Query("SELECT SUM(transactionAmount) FROM transactions WHERE user_id = :user_id and transactionMode = :transactionMode and transactionType = :transactionType")
    fun getAmountByModeAndType(
        user_id: String,
        transactionMode: TransactionMode,
        transactionType: TransactionType
    ): LiveData<Double>

    @Query("SELECT SUM(transactionAmount) FROM transactions WHERE user_id = :user_id and transactionStatus = :transactionStatus and transactionType = :transactionType")
    fun getAmountByStatusAndType(
        user_id: String,
        transactionStatus: TransactionStatus,
        transactionType: TransactionType
    ): LiveData<Double>

    @Query("SELECT * FROM transactions WHERE user_id = :user_id and monthYear = :monthYear ORDER BY transactionDate DESC")
    fun getTransactionsByMonth(user_id: String, monthYear: Long): LiveData<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE user_id = :user_id GROUP BY monthYear ORDER BY year, month DESC")
    fun getTransactionsAllMonths(user_id: String): LiveData<List<Transaction>>

}