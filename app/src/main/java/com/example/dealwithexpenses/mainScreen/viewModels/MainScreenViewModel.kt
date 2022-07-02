package com.example.dealwithexpenses.mainScreen.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.example.dealwithexpenses.daoS.BarChartDetail
import com.example.dealwithexpenses.daoS.MonthDetail
import com.example.dealwithexpenses.entities.*
import com.example.dealwithexpenses.repositories.ListHandlerRepo
import java.util.*

class MainScreenViewModel(application: Application) : AndroidViewModel(application) {

    private val listHandlerRepo = ListHandlerRepo(application)
    private val _transactionID: MutableLiveData<Long> = MutableLiveData<Long>(0L)

    private val _userID: MutableLiveData<String> = MutableLiveData<String>("")
    val userID: LiveData<String>
        get() = _userID

    fun setUserID(id: String) {
        _userID.value = id
    }

    private val monthYear: MutableLiveData<Int> = MutableLiveData(0)
    fun setMonthYear(monthYear: Int) {
        this.monthYear.value = monthYear
    }

    val gains: LiveData<Double> = Transformations.switchMap(_userID) {
        listHandlerRepo.getAmountByType(it, TransactionType.INCOME)
    }

    val expense: LiveData<Double> = Transformations.switchMap(_userID) {
        listHandlerRepo.getAmountByType(it, TransactionType.EXPENSE)
    }

    val yearlyExpenses : LiveData<List<Transaction>> = Transformations.switchMap(monthYear) {
        listHandlerRepo.getTransactionsByYear(userID.value!!, it)
    }

    val yearlyGains : LiveData<List<Transaction>> = Transformations.switchMap(monthYear) {
        listHandlerRepo.getTransactionsByYear(userID.value!!, it)
    }

    val choice = MutableLiveData("Category")
    fun setChoice(choice: String) {
        this.choice.value = choice
    }

    val years: LiveData<List<Int>> = Transformations.switchMap(_userID) {
        listHandlerRepo.getYears(it)
    }

    val categoryInfoList: LiveData<Map<TransactionCategory, Double>> = Transformations.switchMap(_userID) {
        listHandlerRepo.getAmountByCategoryAll(it)
    }
    val typesInfoList: LiveData<Map<TransactionType, Double>> = Transformations.switchMap(_userID) {
        listHandlerRepo.getAmountByTypeAll(it)
    }
    val transModesInfoList: LiveData<Map<TransactionMode, Double>> = Transformations.switchMap(_userID) {
        listHandlerRepo.getAmountByModeAll(it)
    }


    /* <-- Transactions according to Transaction Status -->*/
    fun getCompletedTransactions() =
        listHandlerRepo.getAllTransactionsByStatus(_userID.value!!, TransactionStatus.COMPLETED)

    fun getUpcomingTransactions(date: Long) =
        listHandlerRepo.getUpcomingTransactions(_userID.value!!, date)

    fun getPendingTransactions(date: Long) =
        listHandlerRepo.getPendingTransactions(_userID.value!!, date)

    fun getDates(monthYear: Int) =
        listHandlerRepo.getTransactionDatesByMonthYear(_userID.value!!, monthYear)


    fun getTransactionsByDate(date: Long) =
        listHandlerRepo.getTransactionByDate(_userID.value!!, date)

    fun getAmountByDateAndType(date: Long, type: TransactionType) =
        listHandlerRepo.getTransactionAmountByDateAndType(_userID.value!!, date, type)

    val monthlyTransactions: LiveData<List<Transaction>> = Transformations.switchMap(monthYear) {
        listHandlerRepo.getTransactionsByMonthYear(userID.value!!, it)
    }

    val monthlyExpenses: LiveData<Double> = Transformations.switchMap(monthYear) {
        listHandlerRepo.getAmountByMonthYearAndType(userID.value!!, TransactionType.EXPENSE, it)
    }

    val monthlyGains: LiveData<Double> = Transformations.switchMap(monthYear) {
        listHandlerRepo.getAmountByMonthYearAndType(userID.value!!, TransactionType.INCOME, it)
    }

    val epoxyDataList: LiveData<Map<Int, List<MonthDetail>>> = Transformations.switchMap(userID){
        listHandlerRepo.getMonthDetailByYear(it)
    }

    val barChartDetailByMonth: LiveData<Map<Int,BarChartDetail>> = Transformations.switchMap(userID){
        listHandlerRepo.getBarChartDetailsByMonth(it)
    }

    val barChartDetailByYear: LiveData<Map<Int,BarChartDetail>> = Transformations.switchMap(userID){
        listHandlerRepo.getBarChartDetailsByYear(it)
    }
}