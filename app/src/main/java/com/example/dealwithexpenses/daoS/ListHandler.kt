package com.example.dealwithexpenses.daoS

import androidx.lifecycle.LiveData
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.MapInfo
import androidx.room.Query
import com.example.dealwithexpenses.entities.*

@Dao
interface ListHandler {

    @Query("SELECT * FROM transactions WHERE user_id = :user_id and transactionDate = :date")
    fun getTransactions(user_id: String, date: Long): LiveData<List<Transaction>>

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
    fun getAmountByCategory(user_id: String, transactionCategory: TransactionCategory): LiveData<Double>

    @MapInfo(keyColumn = "category", valueColumn = "sum")
    @Query("SELECT transactionCategory as category ,SUM(transactionAmount) as sum FROM transactions WHERE user_id = :user_id GROUP BY transactionCategory")
    fun getAmountByCategoryAll(
        user_id: String,
    ): LiveData<Map<TransactionCategory,Double>>

    @Query("SELECT SUM(transactionAmount) FROM transactions WHERE user_id = :user_id and transactionMode = :transactionMode ORDER BY transactionDate DESC")
    fun getAmountByMode(user_id: String, transactionMode: TransactionMode): LiveData<Double>

    @MapInfo(keyColumn = "mode", valueColumn = "sum")
    @Query("SELECT transactionMode as mode, SUM(transactionAmount) as sum FROM transactions WHERE user_id = :user_id GROUP BY transactionMode ORDER BY transactionDate DESC")
    fun getAmountByModeAll(user_id: String, ): LiveData<Map<TransactionMode,Double>>

    @Query("SELECT SUM(transactionAmount) FROM transactions WHERE user_id = :user_id and transactionType = :transactionType")
    fun getAmountByType(user_id: String, transactionType: TransactionType): LiveData<Double>

    @MapInfo(keyColumn = "type", valueColumn = "sum")
    @Query("SELECT transactionType as type, SUM(transactionAmount) as sum FROM transactions WHERE user_id = :user_id GROUP BY transactionType")
    fun getAmountByTypeAll(user_id: String, ): LiveData<Map<TransactionType,Double>>

    @Query("SELECT SUM(transactionAmount) FROM transactions WHERE user_id = :user_id and transactionStatus = :transactionStatus")
    fun getAmountByStatus(user_id: String, transactionStatus: TransactionStatus): LiveData<Double>

    @Query("SELECT SUM(transactionAmount) FROM transactions WHERE user_id = :user_id and transactionDate = :date")
    fun getAmountByDate(user_id: String, date: Long): LiveData<Double>

    @Query("SELECT SUM(transactionAmount) FROM transactions WHERE user_id = :user_id and monthYear = :monthYear")
    fun getAmountByMonth(user_id: String, monthYear: Long): LiveData<Double>

    @Query("SELECT SUM(transactionAmount) FROM transactions WHERE user_id = :user_id and year = :year ORDER BY transactionDate")
    fun getAmountByYear(user_id: String, year: Int): LiveData<Double>

    @Query("SELECT year FROM transactions WHERE user_id = :user_id GROUP BY year ORDER BY year ")
    fun getYears(user_id: String): LiveData<List<Int>>

    @Query("SELECT SUM(transactionAmount) FROM transactions WHERE user_id= :user_id and transactionType= :transactionType and monthYear= :monthYear")
    fun getAmountByMonthYearAndType(
        user_id: String,
        transactionType: TransactionType,
        monthYear: Int
    ): LiveData<Double>

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
    fun getTransactionsByMonth(user_id: String, monthYear: Int): LiveData<List<Transaction>>

    @Query("SELECT COUNT(*) FROM transactions WHERE user_id = :userId and monthYear = :monthYear ORDER BY transactionDate DESC")
    fun countTransactionsByMonth(userId: String, monthYear: Int): LiveData<Int>

    @Query("SELECT * FROM transactions WHERE user_id = :user_id and year = :year ORDER BY transactionDate")
    fun getTransactionsByYear(user_id: String, year: Int): LiveData<List<Transaction>>

    @Query("SELECT COUNT(*) FROM transactions WHERE user_id = :user_id and year = :year ORDER BY transactionDate")
    fun countTransactionsByYear(user_id: String, year: Int): LiveData<Int>

    @Query("SELECT * FROM transactions WHERE user_id = :user_id GROUP BY monthYear ORDER BY year, month DESC")
    fun getTransactionsAllMonths(user_id: String): LiveData<List<Transaction>>

    @Query("SELECT DISTINCT(transactionDate) FROM transactions WHERE user_id = :user_id and monthYear = :monthYear ORDER BY transactionDate")
    fun getTransactionDatesByMonthYear(user_id: String, monthYear: Int): LiveData<List<Long>>

    @Query("SELECT * FROM transactions WHERE user_id = :user_id and transactionDate = :date")
    fun getTransactionByDate(user_id: String, date: Long): LiveData<List<Transaction>>

    @Query("SELECT SUM(transactionAmount) FROM transactions WHERE user_id = :user_id and transactionDate = :date and transactionType = :transactionType")
    fun getTransactionAmountByDateAndType(
        user_id: String,
        date: Long,
        transactionType: TransactionType
    ): LiveData<Double>

    @Query("SELECT * FROM transactions WHERE user_id = :user_id and transactionDate >= :date")
    fun getUpcomingTransactions(user_id: String, date: Long): LiveData<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE user_id = :user_id and transactionDate < :date and transactionStatus = :transactionStatus")
    fun getPendingTransactions(user_id: String, date: Long, transactionStatus: TransactionStatus = TransactionStatus.PENDING): LiveData<List<Transaction>>

    @Query("SELECT DISTINCT monthYear FROM transactions WHERE user_id = :user_id AND year= :year ORDER BY monthYear " )
    fun getDistinctMonths(user_id: String, year: Int): LiveData<List<Int>>

    @MapInfo(keyColumn = "year")
    @Query(
        "SELECT t1.year as year, t1.month as month, t1.monthYear as monthYear, " +
                "(SELECT SUM(t2.transactionAmount) " +
                "FROM transactions t2 " +
                "WHERE t2.user_id = :user_id and t2.monthYear = t1.monthYear and t2.transactionType = :transactionType1) " +
                "as expense, " +
                "(SELECT SUM(t2.transactionAmount) " +
                "FROM transactions t2 " +
                "WHERE t2.user_id = :user_id and t2.monthYear = t1.monthYear and t2.transactionType = :transactionType2) " +
                "as gain " +
                "FROM transactions t1 " +
                "WHERE t1.user_id = :user_id " +
                "GROUP BY t1.monthYear " +
                "ORDER BY t1.month"
    )
    fun getMonthDetailByYear(
        user_id: String,
        transactionType1: TransactionType = TransactionType.EXPENSE,
        transactionType2: TransactionType = TransactionType.INCOME,
    ): LiveData<Map<Int, List<MonthDetail>>>
}

data class MonthDetail(
    @ColumnInfo(name= "month") val month: Int,
    @ColumnInfo(name= "monthYear") val monthYear: Int,
    @ColumnInfo(name= "expense") val expense: Double,
    @ColumnInfo(name= "gain") val gain: Double
)