package com.example.dealwithexpenses.mainScreen.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
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

    //fun getDistinctMonths(year: Int)= listHandlerRepo.getDistinctMonths(_userID.value!!, year)
    val monthlyAmountData: MutableLiveData<MutableList<Double>> = MutableLiveData(mutableListOf())
    val monthlyTransactionData: MutableLiveData<MutableList<Int>> = MutableLiveData(mutableListOf())

    val yearlyAmountData: MutableLiveData<MutableList<Double>> = MutableLiveData(mutableListOf())
    val yearlyTransactionData: MutableLiveData<MutableList<Int>> = MutableLiveData(mutableListOf())

    fun fetchMonthlyAmountData(year: Int) {
        monthlyAmountData.value = mutableListOf()
        val monthYear = year * 100
        for (i in 1..12) {
            monthlyAmountData.value?.add(
                listHandlerRepo.getAmountByMonth(
                    _userID.value!!,
                    (monthYear + i).toLong()
                ).value ?: 0.0
            )
        }
    }

    fun fetchMonthlyTransactionsData(year: Int) {
        monthlyTransactionData.value = mutableListOf()
        val monthYear = year * 100
        for (i in 1..12) {
            monthlyTransactionData.value?.add(
                listHandlerRepo.countTransactionsByMonthYear(
                    _userID.value!!,
                    (monthYear + i)
                ).value ?: 0
            )
        }
    }

    fun fetchYearlyAmountData() {
        yearlyAmountData.value = mutableListOf()
        years.value?.forEach {
            yearlyAmountData.value?.add(
                listHandlerRepo.getAmountByYear(
                    _userID.value!!,
                    it
                ).value ?: 0.0
            )
        }
    }

    fun fetchYearlyTransactionsData() {
        yearlyTransactionData.value = mutableListOf()
        years.value?.forEach {
            yearlyTransactionData.value?.add(
                listHandlerRepo.countTransactionsByYear(
                    _userID.value!!,
                    it
                ).value ?: 0
            )
        }
    }
}