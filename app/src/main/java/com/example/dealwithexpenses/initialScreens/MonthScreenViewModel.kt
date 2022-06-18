package com.example.dealwithexpenses.initialScreens

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.example.dealwithexpenses.entities.Transaction
import com.example.dealwithexpenses.entities.TransactionType
import com.example.dealwithexpenses.repositories.ListHandlerRepo

class MonthScreenViewModel(application: Application): AndroidViewModel(application) {
    private val MonthYear: MutableLiveData<Int> = MutableLiveData(0)
    private val user_id: MutableLiveData<String> = MutableLiveData("")

    private var listHandlerRepo: ListHandlerRepo= ListHandlerRepo(application)

    private fun setMonthYear(monthyear: Int){
        MonthYear.let {
            it.value= monthyear
        }
    }

    private fun setUserId(userId: String){
        user_id.let {
            it.value= userId
        }
    }
    val monthlyTransactions : LiveData<List<Transaction>> = Transformations.switchMap(MonthYear){
        listHandlerRepo.getTransactionsByMonthYear(user_id.value!!,it)
    }

    val monthlyExpenses : LiveData<Double> = Transformations.switchMap(MonthYear){
        listHandlerRepo.getAmountByMonthYearAndType(user_id.value!!, it, TransactionType.EXPENSE)
    }

    val monthlyIncome : LiveData<Double> = Transformations.switchMap(MonthYear){
        listHandlerRepo.getAmountByMonthYearAndType(user_id.value!!, it, TransactionType.INCOME)
    }
}