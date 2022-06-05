package com.example.dealwithexpenses.repositories

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import com.example.dealwithexpenses.daoS.TransactionDB
import com.example.dealwithexpenses.entities.Transaction

class TransactionManagerRepo(application: Application) {

    private val transactionDao = TransactionDB.getDatabase(application).transactionDao()

    suspend fun insert(transaction: Transaction) = transactionDao.insert(transaction)
    suspend fun update(transaction: Transaction) = transactionDao.update(transaction)
    suspend fun delete(transaction: Transaction) = transactionDao.delete(transaction)

    fun getTransaction(trans_id: Long, user_id: String): LiveData<Transaction> = transactionDao.getInfo(trans_id, user_id)


}