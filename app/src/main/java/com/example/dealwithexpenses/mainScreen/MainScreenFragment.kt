package com.example.dealwithexpenses.mainScreen

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.dealwithexpenses.R
import com.example.dealwithexpenses.databinding.FragmentMainScreenBinding
import com.example.dealwithexpenses.mainScreen.viewModels.MainScreenViewModel
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth

class MainScreenFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    private lateinit var binding: FragmentMainScreenBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var viewModel: MainScreenViewModel
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentMainScreenBinding.inflate(inflater, container, false)
        sharedPreferences = activity?.getSharedPreferences("user_auth", 0)!!
        viewModel = ViewModelProvider(this).get(MainScreenViewModel::class.java)
        firebaseAuth = FirebaseAuth.getInstance()

        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Transaction Log"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Calendar"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Statistics"))

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                when (tab.position) {
                    0 -> {
                        binding.viewPager.currentItem = 0
                    }
                    1 -> {
                        binding.viewPager.currentItem = 1
                    }
                    2 -> {
                        binding.viewPager.currentItem = 2
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {

            }

            override fun onTabReselected(tab: TabLayout.Tab) {

            }
        })
        Log.d("Hemlo",firebaseAuth.currentUser.toString())
        val userId = firebaseAuth.currentUser?.uid!!
        viewModel.setUserID(userId)

        binding.viewPager.adapter = ViewPagerAdapter(parentFragmentManager, this)
//
//        val activeMonthlyIncome = sharedPreferences.getString("income", "0")?.toDouble()
//        val activeYearlyPackage = activeMonthlyIncome?.times(12)
//
//        val totalGains = viewModel.allTimeGains.value
//        val totalExpenses = viewModel.allTimeExpense.value
//
//        val yearlyGains = viewModel.yearlyGains.value
//        val yearlyExpenses = viewModel.yearlyExpenses.value
//
//        val totalCredit = totalGains?.plus((activeMonthlyIncome!!.times(12)))
//        binding.totalCredit.text = totalCredit.toString()
//
//        val totalBalance = totalCredit?.minus(totalExpenses!!)

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main_screen, container, false)
    }

}