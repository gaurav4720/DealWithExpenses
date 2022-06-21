package com.example.dealwithexpenses.mainScreen

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.dealwithexpenses.mainScreen.tabs.OverviewTabFragment
import com.example.dealwithexpenses.mainScreen.tabs.RecentTransactionsTabFragment
import com.example.dealwithexpenses.mainScreen.tabs.TransactionTabFragment

class ViewPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
    override fun getCount(): Int {
        return 3
    }

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> {
                OverviewTabFragment()
            }
            1 -> {
                RecentTransactionsTabFragment()
            }
            else -> {
                TransactionTabFragment()
            }
        }
    }
}