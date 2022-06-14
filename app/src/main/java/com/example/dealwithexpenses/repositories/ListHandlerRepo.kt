package com.example.dealwithexpenses.repositories

import android.app.Application
import androidx.lifecycle.LiveData
import com.example.dealwithexpenses.daoS.TransactionDB
import com.example.dealwithexpenses.entities.*
import java.time.YearMonth
import java.util.*

class ListHandlerRepo(application: Application) {
    private val listHandlerDao = TransactionDB.getDatabase(application).listHandlerDao()

    fun getAllTransactionsByDate(user_id: String, date: Long): LiveData<List<Transaction>> =
        listHandlerDao.getTransactions(user_id, date)

    fun getYears(user_id: String): LiveData<List<Int>> =
        listHandlerDao.getYears(user_id)

    fun getAllTransactionsByCategory(
        user_id: String,
        category: TransactionCategory
    ): LiveData<List<Transaction>> =
        listHandlerDao.getTransactionsByCategory(user_id, category)

    fun getAllTransactionsByType(
        user_id: String,
        type: TransactionType
    ): LiveData<List<Transaction>> =
        listHandlerDao.getTransactionsByType(user_id, type)

    fun getAllTransactionsByMode(
        user_id: String,
        mode: TransactionMode
    ): LiveData<List<Transaction>> =
        listHandlerDao.getTransactionsByMode(user_id, mode)

    fun getAllTransactionsByStatus(
        user_id: String,
        status: TransactionStatus
    ): LiveData<List<Transaction>> =
        listHandlerDao.getTransactionsByStatus(user_id, status)

    fun getAmountByCategory(user_id: String, category: TransactionCategory): LiveData<Double> =
        listHandlerDao.getAmountByCategory(user_id, category)

    fun getAmountByType(user_id: String, type: TransactionType): LiveData<Double> =
        listHandlerDao.getAmountByType(user_id, type)

    fun getAmountByMode(user_id: String, mode: TransactionMode): LiveData<Double> =
        listHandlerDao.getAmountByMode(user_id, mode)

    fun getAmountByStatus(user_id: String, status: TransactionStatus): LiveData<Double> =
        listHandlerDao.getAmountByStatus(user_id, status)

    fun getAmountByDate(user_id: String, date: Long): LiveData<Double> =
        listHandlerDao.getAmountByDate(user_id, date)

    fun getAmountByMonth(user_id: String, monthYear: Long): LiveData<Double> =
        listHandlerDao.getAmountByMonth(user_id, monthYear)

    fun getAmountByModeAndDate(
        user_id: String,
        mode: TransactionMode,
        date: Long
    ): LiveData<Double> =
        listHandlerDao.getAmountByModeAndDate(user_id, mode, date)

    fun getAmountByStatusAndType(
        user_id: String,
        transactionStatus: TransactionStatus,
        transactionType: TransactionType
    ): LiveData<Double> =
        listHandlerDao.getAmountByStatusAndType(user_id, transactionStatus, transactionType)

    fun getAmountByModeAndStatus(
        user_id: String,
        transactionMode: TransactionMode,
        transactionStatus: TransactionStatus
    ): LiveData<Double> =
        listHandlerDao.getAmountByModeAndStatus(user_id, transactionMode, transactionStatus)

    fun getAmountByModeAndType(
        user_id: String,
        transactionMode: TransactionMode,
        transactionType: TransactionType
    ): LiveData<Double> =
        listHandlerDao.getAmountByModeAndType(user_id, transactionMode, transactionType)

    fun getTransactionsByMonthYear(user_id: String, monthYear: Long): LiveData<List<Transaction>> =
        listHandlerDao.getTransactionsByMonth(user_id, monthYear)

    fun getTransactionsByMonth(user_id: String): LiveData<List<Transaction>> =
        listHandlerDao.getTransactionsAllMonths(user_id)

}