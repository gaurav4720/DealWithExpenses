package com.example.dealwithexpenses.mainScreen

import android.app.AlertDialog
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.activity.OnBackPressedCallback
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.dealwithexpenses.R
import com.example.dealwithexpenses.databinding.FragmentMainScreenBinding
import com.example.dealwithexpenses.mainScreen.viewModels.MainScreenViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import java.util.*

class MainScreenFragment : Fragment() {

    private lateinit var binding: FragmentMainScreenBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var viewModel: MainScreenViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentMainScreenBinding.inflate(inflater, container, false)
        sharedPreferences = activity?.getSharedPreferences("user_auth", 0)!!
        viewModel = ViewModelProvider(this).get(MainScreenViewModel::class.java)

        val initialPosition= MainScreenFragmentArgs.fromBundle(requireArguments()).screenNumber

        val drawerLayout= binding.profileDrawerLayout
        binding.viewPager.adapter = ViewPagerAdapter(this)
        binding.tabLayout.tabGravity = TabLayout.GRAVITY_FILL

        binding.profileImage.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        binding.navigationView.setNavigationItemSelectedListener {
            drawerLayout.closeDrawer(binding.navigationView)
            when(it.itemId) {
                R.id.newTransaction -> {
                    findNavController().navigate(
                        MainScreenFragmentDirections.actionMainScreenFragmentToAddOrEditTransactionFragment(
                            0,
                            4
                        )
                    )
                    true
                }
                R.id.myDetails -> {
                    findNavController().navigate(MainScreenFragmentDirections.actionMainScreenFragmentToMyDetailsFragment())
                    true
                }
                R.id.editMyDetail -> {
                    findNavController().navigate(MainScreenFragmentDirections.actionMainScreenFragmentToEditMyDetailsFragment())
                    true
                }
                R.id.aboutUs -> {
                    findNavController().navigate(MainScreenFragmentDirections.actionMainScreenFragmentToAboutUsFragment())
                    true
                }
                R.id.logout -> {
                    val dialog = AlertDialog.Builder(requireContext())
                        .setTitle("Logout")
                        .setMessage("Are you sure you want to logout?")
                        .setPositiveButton("Yes") { _, _ ->
                            sharedPreferences.edit().putBoolean("isLoggedIn", false).apply()
                            findNavController().navigate(MainScreenFragmentDirections.actionMainScreenFragmentToLoginScreenFragment())
                        }
                        .setNegativeButton("No") { _, _ ->
                        }
                    dialog.create().show()
                    true
                }
                else -> false
            }
        }

        var positionNow= 0

        binding.viewPager.currentItem = initialPosition
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
                0 -> {tab.text = "Statistics"; positionNow=0 }
                1 -> {tab.text = "Recent Transaction"; positionNow=1 }
                2 -> {tab.text = "Calendar"; positionNow=2 }
            }
        }.attach()

        val userId= sharedPreferences.getString("user_id", "")!!

        //setting the user id of viewModel after getting it through firebase
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

        binding.floatingActionButton2.setOnClickListener {
            findNavController().navigate(
                MainScreenFragmentDirections.actionMainScreenFragmentToAddOrEditTransactionFragment(
                    0,
                    positionNow
                )
            )
        }

        val onBackPressedCallback= object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if(drawerLayout.isDrawerOpen(GravityCompat.START)){
                    drawerLayout.closeDrawer(GravityCompat.START)
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(onBackPressedCallback)

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