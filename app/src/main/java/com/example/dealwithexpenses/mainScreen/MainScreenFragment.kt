package com.example.dealwithexpenses.mainScreen

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.dealwithexpenses.databinding.FragmentMainScreenBinding
import com.example.dealwithexpenses.mainScreen.viewModels.MainScreenViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import java.util.*
import kotlin.collections.HashMap

class MainScreenFragment : Fragment() {

    private lateinit var binding: FragmentMainScreenBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var viewModel: MainScreenViewModel
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var incomeRegister: HashMap<Int, Double>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

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

        val activeMonthlyIncome = sharedPreferences.getString("income", "0")?.toDouble()
        val activeYearlyPackage = activeMonthlyIncome?.times(12)

        val choices = arrayOf(
            "All Time",
            "Yearly",
        )

        val choicesAdapter = ArrayAdapter(
            requireActivity().baseContext,
            android.R.layout.simple_spinner_item,
            choices
        )

        binding.spinner.adapter = choicesAdapter

        //incomeRegister = sharedPreferences.getHashMap("income_register")

        binding.spinner.onItemSelectedListener = object :
            android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: android.widget.AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                when (position) {
                    0 -> {
                        val totalMonthlyEarnings = 0.0
                            //allTimeMonthlyEarnings()
                        val totalGains = viewModel.gains.value?.toDouble() ?: 0.0
                        val totalExpenses = viewModel.expense.value?.toDouble() ?: 0.0

                        val totalCredit = totalMonthlyEarnings.plus(totalGains)
                        val totalSavings = totalCredit.minus(totalExpenses)

                        binding.credit.text = totalCredit.toString()
                        binding.expenditure.text = totalExpenses.toString()
                        binding.savings.text = totalSavings.toString()
                    }
                    1 -> {
                        val calendar = Calendar.getInstance()

                        val year = calendar.get(Calendar.YEAR)

                        val yearIncome =0.0
                           // totalMonthlyEarnings(year * 100 + 1, currMonthYear)

                        viewModel.getAmountByAllYears.observe(viewLifecycleOwner) {
                            val yearlyGains = it[year]!!.gain
                            val yearlyExpenses = it[year]!!.expense

                            val yearlyCredit = yearIncome.plus(yearlyGains)
                            val yearlySavings = yearlyCredit.minus(yearlyExpenses)

                            binding.credit.text = yearIncome.toString()
                            binding.expenditure.text = yearlyExpenses.toString()
                            binding.savings.text = yearlySavings.toString()
                        }
                    }
                }
            }

            override fun onNothingSelected(parent: android.widget.AdapterView<*>) {
                // Another interface callback
            }
        }

        // Inflate the layout for this fragment
        return binding.root
    }

//    fun allTimeMonthlyEarnings(): Double {
//        var totalEarnings = 0.0
//        var earningTillNow = 0.0
//        var lastMonth = -1
//
//        for (KeyValuePair in incomeRegister) {
//            val currMonth = KeyValuePair.key % 100
//            if (earningTillNow != 0.0) {
//                val months = currMonth - lastMonth
//                totalEarnings += earningTillNow * months
//            }
//
//            earningTillNow = KeyValuePair.value
//            lastMonth = currMonth
//        }
//
//        return totalEarnings
//    }
//
//    fun totalMonthlyEarnings(startMonth: Int, endMonth: Int): Double {
//        var totalEarnings = 0.0
//
//        for (i in startMonth..endMonth)
//            totalEarnings += incomeRegister[i]!!
//
//        return totalEarnings
//    }

}