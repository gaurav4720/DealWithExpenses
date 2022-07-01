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
import com.google.android.material.tabs.TabLayoutMediator
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

        binding.viewPager.adapter = ViewPagerAdapter(this)

        binding.tabLayout.tabGravity = TabLayout.GRAVITY_FILL

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                binding.viewPager.currentItem = tab.position

            }

            override fun onTabUnselected(tab: TabLayout.Tab) {

            }

            override fun onTabReselected(tab: TabLayout.Tab) {

            }
        })
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = "Statistics"
                1 -> tab.text = "Recent Transaction"
                2 -> tab.text = "Calendar"
            }
        }.attach()
        val userId = firebaseAuth.currentUser?.uid!!
        viewModel.setUserID(userId)

//        binding.viewPager.adapter = ViewPagerAdapter(parentFragmentManager, this)
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
        return binding.root
    }

}