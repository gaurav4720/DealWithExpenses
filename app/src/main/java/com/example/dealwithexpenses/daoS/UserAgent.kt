package com.example.dealwithexpenses.daoS

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.dealwithexpenses.entities.User

@Dao
interface UserAgent {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: User)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(user: User)

    @Delete
    suspend fun delete(user: User)

    @Query("SELECT * FROM user_diary WHERE user_id = :user_id")
    fun getUser(user_id: String): LiveData<User>

}