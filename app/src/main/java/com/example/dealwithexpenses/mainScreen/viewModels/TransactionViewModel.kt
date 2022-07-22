package com.example.dealwithexpenses.mainScreen.viewModels

import android.app.Application
import androidx.lifecycle.*
import com.example.dealwithexpenses.entities.Transaction
import com.example.dealwithexpenses.entities.TransactionStatus
import com.example.dealwithexpenses.repositories.TransactionManagerRepo
import kotlinx.coroutines.launch

class TransactionViewModel(application: Application): AndroidViewModel(application) {
    private val repository = TransactionManagerRepo(application)
    private val _transactionID: MutableLiveData<Long> = MutableLiveData(0L)
    val transactionId: LiveData<Long>
        get() = _transactionID

    private val _userID: MutableLiveData<String> = MutableLiveData("")
    val userID: LiveData<String>
        get() = _userID

    fun setTransactionId(id: Long) {
        _transactionID.value = id
    }

    fun setUserId(id: String) {
        _userID.value = id
    }

    val transaction: LiveData<Transaction> = Transformations.switchMap(_transactionID) {
        repository.getTransaction(it,userID.value!!)
    }

    fun insertOrUpdate(transaction: Transaction) {
        viewModelScope.launch {
            if (_transactionID.value == 0L) {
                repository.insert(transaction)
            } else {
                repository.update(transaction)
            }
        }
    }

    fun delete(transaction: Transaction) {
        viewModelScope.launch {
            repository.delete(transaction)
        }
    }

    fun complete(transaction: Transaction){
        val transaction1= Transaction(
            transaction.user_id,
            transaction.trans_id,
            transaction.title,
            transaction.description,
            transaction.transactionAmount,
            transaction.transactionDate,
            transaction.isRecurring,
            transaction.fromDate,
            transaction.toDate,
            transaction.month,
            transaction.year,
            transaction.monthYear,
            transaction.transactionType,
            transaction.transactionCategory,
            transaction.transactionMode,
            TransactionStatus.COMPLETED
        )
        viewModelScope.launch {
            repository.update(transaction1)
        }
    }
}

