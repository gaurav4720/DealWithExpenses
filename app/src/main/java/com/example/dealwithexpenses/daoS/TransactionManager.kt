package com.example.dealwithexpenses.daoS

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.dealwithexpenses.entities.Transaction

@Dao
interface TransactionManager {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(transaction: Transaction)

    @Update(onConflict = OnConflictStrategy.IGNORE)
    suspend fun update(transaction: Transaction)

    @Delete
    suspend fun delete(transaction: Transaction)

    @Query("SELECT * FROM transactions WHERE trans_id =:trans_id and user_id = :user_id")
    fun getInfo(trans_id: Long, user_id: String): LiveData<Transaction>

}


