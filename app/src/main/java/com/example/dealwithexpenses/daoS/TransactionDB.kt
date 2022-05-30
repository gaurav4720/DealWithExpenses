package com.example.dealwithexpenses.daoS

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.dealwithexpenses.entities.Transaction
import com.example.dealwithexpenses.entities.User

@Database(entities = [User::class, Transaction::class], version = 1)
abstract class TransactionDB : RoomDatabase() {
    abstract fun transactionDao(): TransactionManager
    abstract fun userDao(): UserAgent
    abstract fun listHandlerDao(): ListHandler

    companion object {
        @Volatile
        private var instance: TransactionDB? = null

        fun getDatabase(context: Context) = instance
            ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    TransactionDB::class.java,
                    "transaction_db"
                ).build().also { instance = it }
            }
    }
}