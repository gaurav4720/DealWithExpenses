package com.example.dealwithexpenses.mainScreen.viewModels

import android.app.Application
import androidx.lifecycle.*
import com.example.dealwithexpenses.entities.Transaction
import com.example.dealwithexpenses.repositories.TransactionManagerRepo
import kotlinx.coroutines.launch

class AddOrEditTransactionViewModel(application: Application): AndroidViewModel(application) {
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
}

