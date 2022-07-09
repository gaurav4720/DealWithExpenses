package com.example.dealwithexpenses.mainScreen.viewModels

import android.content.SharedPreferences
import android.util.Log
import com.example.dealwithexpenses.daoS.MonthDetail
import com.example.dealwithexpenses.mainScreen.transactionLibrary.MonthCardDetail
import com.example.dealwithexpenses.mainScreen.tabs.YearMonthTabFragment.Companion.months

data class EpoxyData(val year: Int, var mutableList: MutableList<MonthCardDetail> )

fun convertIntoEpoxyData(map : Map<Int, List<MonthDetail>>, sharedPreferences: SharedPreferences) : MutableList<EpoxyData> {
    val epoxyDataList = mutableListOf<EpoxyData>()
    for (key in map.keys) {
        val epoxyData = EpoxyData(key, mutableListOf())
        for (monthDetail in map[key]!!) {
            Log.d("yoyoyo",monthDetail.toString())
            val income= sharedPreferences.getString("income","0")!!.toDouble()
            var expense= 0.0
            var profit= 0.0
            expense+= monthDetail.expense
            profit+= monthDetail.gain
            val amount: Double = income+profit-expense
            val month: String = months[monthDetail.month-1]
            val monthCardDetail = MonthCardDetail(month, amount, expense, income, profit, monthDetail.monthYear)
            epoxyData.mutableList.add(monthCardDetail)
        }
        epoxyDataList.add(epoxyData)
    }
    return epoxyDataList
}