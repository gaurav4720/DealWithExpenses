package com.example.dealwithexpenses.mainScreen.TransactionLibrary

import com.airbnb.epoxy.AsyncEpoxyController

class EpoxyController() : AsyncEpoxyController() {
    var transactYears = mutableListOf<EpoxyData>()
        set(value) {
            field = value
            requestModelBuild()
        }

    override fun buildModels() {
        transactYears.forEachIndexed { index, epoxyData ->
            if(epoxyData.toDisplay) {

            }
        }
    }
}