package com.example.dealwithexpenses.mainScreen.tabs

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.dealwithexpenses.databinding.FragmentYearMonthTabBinding
import com.example.dealwithexpenses.mainScreen.transactionLibrary.YearEpoxyController
import com.example.dealwithexpenses.mainScreen.viewModels.MainScreenViewModel
import com.example.dealwithexpenses.mainScreen.viewModels.convertIntoEpoxyData
import com.google.firebase.auth.FirebaseAuth

class YearMonthTabFragment(val fragment: Fragment) : Fragment() {

    private lateinit var binding: FragmentYearMonthTabBinding
    private lateinit var model: MainScreenViewModel
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding= FragmentYearMonthTabBinding.inflate(inflater,container,false)
        model= ViewModelProvider(this).get(MainScreenViewModel::class.java)
        sharedPreferences= activity?.getSharedPreferences("user_auth", 0)!!
        val userId= sharedPreferences.getString("user_id", "")!!
        model.setUserID(userId)

        val yearEpoxyController= YearEpoxyController(fragment)
            //yearEpoxyController.transactYears= list
        binding.epoxy.setController(yearEpoxyController)
        binding.epoxy.layoutManager= androidx.recyclerview.widget.LinearLayoutManager(context)
        model.epoxyDataList.observe(viewLifecycleOwner) {
            val list= convertIntoEpoxyData(it, sharedPreferences)
            yearEpoxyController.transactYears= list
            yearEpoxyController.requestModelBuild()
        }
        return binding.root
    }



    companion object{
        val months = arrayListOf<String>(
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