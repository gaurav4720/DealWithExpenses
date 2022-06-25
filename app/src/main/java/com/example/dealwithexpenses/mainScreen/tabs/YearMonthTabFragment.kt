package com.example.dealwithexpenses.mainScreen.tabs

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.dealwithexpenses.databinding.FragmentYearMonthTabBinding
import com.example.dealwithexpenses.mainScreen.TransactionLibrary.EpoxyData
import com.example.dealwithexpenses.mainScreen.TransactionLibrary.MonthCardDetail
import com.example.dealwithexpenses.mainScreen.TransactionLibrary.YearEpoxyController
import com.example.dealwithexpenses.mainScreen.viewModels.MainScreenViewModel
import com.google.firebase.auth.FirebaseAuth

class YearMonthTabFragment(val fragment: Fragment) : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    private lateinit var binding: FragmentYearMonthTabBinding
    private lateinit var model: MainScreenViewModel
    private lateinit var auth: FirebaseAuth
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= FragmentYearMonthTabBinding.inflate(inflater,container,false)
        auth= FirebaseAuth.getInstance()
        model= ViewModelProvider(this).get(MainScreenViewModel::class.java)
        sharedPreferences= activity?.getSharedPreferences("user_auth", 0)!!
        model.setUserID(auth.currentUser!!.uid)
        val list: MutableList<EpoxyData> = mutableListOf()
        model.years.observe(viewLifecycleOwner){
            it.forEach { year->
                val listMonthCard: MutableList<MonthCardDetail> = mutableListOf()
                model.getDistinctMonths(year).observe(viewLifecycleOwner){ monthCardList ->
                    monthCardList.forEach { monthYear->
                        listMonthCard.add(getMonthInfo(monthYear))
                    }
                }
                list.add(EpoxyData(year,listMonthCard))
            }
        }
        val yearEpoxyController= YearEpoxyController(fragment)
        yearEpoxyController.transactYears= list
        binding.epoxy.setController(yearEpoxyController)
        return binding.root
    }

    fun getMonthInfo(monthYear: Int): MonthCardDetail{
        val income= sharedPreferences.getString("income","0")!!.toDouble()
        var expense= 0.0
        var profit= 0.0
        model.setMonthYear(monthYear)
        model.monthlyExpenses.observe(viewLifecycleOwner){
            expense+= it
        }
        model.monthlyGains.observe(viewLifecycleOwner){
            profit+= it
        }
        val amount: Double = income+profit-expense
        val month: String = months[(monthYear%100)-1]
        return MonthCardDetail(month, amount, expense, income, profit, monthYear)
    }

    companion object{
        private val months = arrayListOf<String>(
            "JANUARY",
            "FEBRUARY",
            "MARCH",
            "APRIL",
            "MAY",
            "JUNE",
            "JULY",
            "AUGUST",
            "SEPTEMBER",
            "OCTOBER",
            "NOVEMBER",
            "DECEMBER"
        )
    }
}