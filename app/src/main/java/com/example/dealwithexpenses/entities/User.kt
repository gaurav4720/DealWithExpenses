package com.example.dealwithexpenses.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_diary")
data class User(
    val username: String,
    val password: String,
    @PrimaryKey(autoGenerate = false) var user_id: String,
)
