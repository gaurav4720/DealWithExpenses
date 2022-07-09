package com.example.dealwithexpenses.mainScreen.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.example.dealwithexpenses.entities.Transaction
import com.example.dealwithexpenses.entities.TransactionType
import com.example.dealwithexpenses.repositories.ListHandlerRepo

class MonthScreenViewModel(application: Application): AndroidViewModel(application) {
    private val _monthYear: MutableLiveData<Int> = MutableLiveData(0)
    private val userid: MutableLiveData<String> = MutableLiveData("")

    private var listHandlerRepo: ListHandlerRepo= ListHandlerRepo(application)

    fun setMonthYear(monthYear: Int){
        _monthYear.value = monthYear
    }

    fun setUserId(userId: String){
        userid.value = userId
    }
    val monthlyTransactions : LiveData<List<Transaction>> = Transformations.switchMap(_monthYear){
        listHandlerRepo.getTransactionsByMonthYear(userid.value!!,it)
    }

    val monthlyExpenses : LiveData<Double> = Transformations.switchMap(_monthYear){
        listHandlerRepo.getAmountByMonthYearAndType(userid.value!!, TransactionType.EXPENSE, it)
    }

    val monthlyIncome : LiveData<Double> = Transformations.switchMap(_monthYear){
        listHandlerRepo.getAmountByMonthYearAndType(userid.value!!, TransactionType.INCOME, it)
    }
}