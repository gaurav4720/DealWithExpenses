package com.example.dealwithexpenses.mainScreen.transactionLibrary

import androidx.fragment.app.Fragment
import com.airbnb.epoxy.AsyncEpoxyController
import com.example.dealwithexpenses.mainScreen.viewModels.EpoxyData
import com.example.dealwithexpenses.mainScreen.viewModels.year

class YearEpoxyController(val fragment: Fragment) : AsyncEpoxyController() {
    var transactYears = mutableListOf<EpoxyData>()
        set(value) {
            field = value
            requestModelBuild()
        }

    override fun buildModels() {
        transactYears.forEachIndexed { index, epoxyData ->
            val monthAdapter= MonthAdapter(epoxyData.mutableList, fragment)
            year {
                id(index)
                year(epoxyData.year.toString())
                adapter(monthAdapter)
            }
        }
    }
}