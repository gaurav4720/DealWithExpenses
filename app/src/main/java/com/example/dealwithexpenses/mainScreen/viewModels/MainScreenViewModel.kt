package com.example.dealwithexpenses.mainScreen.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
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

    val choice = MutableLiveData("Category")
    fun setChoice(choice: String) {
        this.choice.value = choice
    }

    val years: LiveData<List<Int>> = Transformations.switchMap(_userID) {
        listHandlerRepo.getYears(it)
    }

    val categoryInfo: MutableLiveData<MutableList<Double>> = MutableLiveData(mutableListOf())
    val typesInfo: MutableLiveData<MutableList<Double>> = MutableLiveData(mutableListOf())
    val transModesInfo: MutableLiveData<MutableList<Double>> = MutableLiveData(mutableListOf())

    fun fetchCategoriesData() {
        TransactionCategory.values().forEach {
            categoryInfo.value?.add(
                listHandlerRepo.getAmountByCategory(
                    _userID.value!!,
                    it
                ).value!!
            )
        }
    }

    fun fetchTypesData() {
        TransactionType.values().forEach {
            typesInfo.value?.add(listHandlerRepo.getAmountByType(_userID.value!!, it).value!!)
        }
    }

    fun fetchModesData() {
        TransactionMode.values().forEach {
            transModesInfo.value?.add(listHandlerRepo.getAmountByMode(_userID.value!!, it).value!!)
        }
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

    fun getDistinctMonths(year: Int)= listHandlerRepo.getDistinctMonths(_userID.value!!, year)
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
                ).value!!
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
                ).value!!
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
                ).value!!
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
                ).value!!
            )
        }
    }
}