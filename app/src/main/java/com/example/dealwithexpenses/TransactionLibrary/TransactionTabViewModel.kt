package com.example.dealwithexpenses.TransactionLibrary

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.example.dealwithexpenses.repositories.ListHandlerRepo

class TransactionTabViewModel(application: Application) : AndroidViewModel(application) {


    val repo = ListHandlerRepo(application)
    private val user_id: MutableLiveData<String> = MutableLiveData("")
    fun setUserId(id: String) {
        user_id.value = id
    }

    private val years: LiveData<List<Int>> = Transformations.switchMap(user_id) {
        repo.getYears(it)
    }

    private val epoxyList: MutableLiveData<MutableList<EpoxyData>> =
        MutableLiveData<MutableList<EpoxyData>>()

    fun setEpoxyList() {
        years.value?.forEach {
            epoxyList.value?.add(EpoxyData(it, false))
        }
    }

    fun changeEpoxyList(index: Int) {
        epoxyList.value?.get(index)?.toDisplay = !epoxyList.value?.get(index)?.toDisplay!!
    }
}


data class EpoxyData(val year: Int, var toDisplay: Boolean = false)