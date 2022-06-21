package com.example.makeMyBudget.mainScreen.TransactionLibrary

data class MonthCardDetail(
    val month: String,
    val amount: Double,
    val expense: Double,
    val income: Double,
    val profit: Double,
    val monthYear: Int
)