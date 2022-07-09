package com.example.dealwithexpenses.mainScreen

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.dealwithexpenses.mainScreen.tabs.OverviewTabFragment
import com.example.dealwithexpenses.mainScreen.tabs.RecentTransactionsTabFragment
import com.example.dealwithexpenses.mainScreen.tabs.YearMonthTabFragment

class ViewPagerAdapter( val fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> {
                OverviewTabFragment()
            }
            1 -> {
                RecentTransactionsTabFragment(fragment)
            }
            else -> {
                YearMonthTabFragment(fragment)
            }
        }
    }
}