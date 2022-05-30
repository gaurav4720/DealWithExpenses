package com.example.dealwithexpenses.repositories

import android.app.Application
import com.example.dealwithexpenses.daoS.TransactionDB
import com.example.dealwithexpenses.entities.User

class UserAgentRepo(application: Application) {

    val userAgentDao = TransactionDB.getDatabase(application).userDao()

    suspend fun insert(user: User) = userAgentDao.insert(user)
    suspend fun update(user: User) = userAgentDao.update(user)
    suspend fun delete(user: User) = userAgentDao.delete(user)

    suspend fun getUser(userId: Int) = userAgentDao.getUser(userId)
}