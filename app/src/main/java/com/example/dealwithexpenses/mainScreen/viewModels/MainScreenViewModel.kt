package com.example.dealwithexpenses.mainScreen.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.example.dealwithexpenses.entities.TransactionCategory
import com.example.dealwithexpenses.entities.TransactionMode
import com.example.dealwithexpenses.entities.TransactionType
import com.example.dealwithexpenses.repositories.ListHandlerRepo

class MainScreenViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = ListHandlerRepo(application)
    private val _transactionID: MutableLiveData<Long> = MutableLiveData<Long>(0L)

    private val _userID: MutableLiveData<String> = MutableLiveData<String>("")
    val userID: LiveData<String>
        get() = _userID

    fun setUserId(id: String) {
        _userID.value = id
    }

    val gains: LiveData<Double> = Transformations.switchMap(_userID) {
        repo.getAmountByType(it, TransactionType.INCOME)
    }

    val expense: LiveData<Double> = Transformations.switchMap(_userID) {
        repo.getAmountByType(it, TransactionType.EXPENSE)
    }

    val choice = MutableLiveData<String>("Category")
    fun setChoice(choice: String) {
        this.choice.value = choice
    }

    val years: LiveData<List<Int>> = Transformations.switchMap(_userID) {
        repo.getYears(it)
    }

    val categoryInfo: MutableLiveData<MutableList<Double>> = MutableLiveData(mutableListOf())
    val typesInfo: MutableLiveData<MutableList<Double>> = MutableLiveData(mutableListOf())
    val transModesInfo: MutableLiveData<MutableList<Double>> = MutableLiveData(mutableListOf())

    fun fetchCategoriesData() {
        TransactionCategory.values().forEach {
            categoryInfo.value?.add(repo.getAmountByCategory(_userID.value!!, it).value!!)
        }
    }

    fun fetchTypesData() {
        TransactionType.values().forEach {
            typesInfo.value?.add(repo.getAmountByType(_userID.value!!, it).value!!)
        }
    }

    fun fetchModesData() {
        TransactionMode.values().forEach {
            transModesInfo.value?.add(repo.getAmountByMode(_userID.value!!, it).value!!)
        }
    }

    fun getDistinctMonths(year: Int)= repo.getDistinctMonths(_userID.value!!, year)

    fun getMonthlyIncome(monthYear: Int) = repo.getAmountByMonthYearAndType(_userID.value!!,TransactionType.INCOME,monthYear)
    fun getMonthlyExpense(monthYear: Int) = repo.getAmountByMonthYearAndType(_userID.value!!,TransactionType.EXPENSE,monthYear)

    val monthlyAmountData: MutableLiveData<MutableList<Double>> = MutableLiveData(mutableListOf())
    val monthlyTransactionData: MutableLiveData<MutableList<Int>> = MutableLiveData(mutableListOf())

    val yearlyAmountData: MutableLiveData<MutableList<Double>> = MutableLiveData(mutableListOf())
    val yearlyTransactionData: MutableLiveData<MutableList<Int>> = MutableLiveData(mutableListOf())

    fun fetchMonthlyAmountData(year: Int) {
        monthlyAmountData.value = mutableListOf()
        val monthYear = year * 100
        for (i in 1..12) {
            monthlyAmountData.value?.add(
                repo.getAmountByMonth(
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
                repo.countTransactionsByMonthYear(
                    _userID.value!!,
                    (monthYear + i)
                ).value!!
            )
        }
    }

    fun fetchYearlyAmountData() {
        yearlyAmountData.value = mutableListOf()
        years.value?.forEach {
            yearlyAmountData.value?.add(repo.getAmountByYear(_userID.value!!, it).value!!)
        }
    }

    fun fetchYearlyTransactionsData() {
        yearlyTransactionData.value = mutableListOf()
        years.value?.forEach {
            yearlyTransactionData.value?.add(
                repo.countTransactionsByYear(
                    _userID.value!!,
                    it
                ).value!!
            )
        }
    }
}