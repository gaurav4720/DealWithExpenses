package com.example.dealwithexpenses.mainScreen.TransactionLibrary

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.dealwithexpenses.entities.TransactionType
import com.example.dealwithexpenses.mainScreen.MainScreenFragmentDirections
import com.example.dealwithexpenses.mainScreen.viewModels.MainScreenViewModel
import com.example.dealwithexpenses.R
import com.example.dealwithexpenses.databinding.FragmentCalendarViewBinding
import com.example.dealwithexpenses.mainScreen.viewModels.TransactionViewModel
import com.google.firebase.auth.FirebaseAuth
import sun.bob.mcalendarview.listeners.OnDateClickListener
import sun.bob.mcalendarview.listeners.OnMonthChangeListener
import sun.bob.mcalendarview.vo.DateData
import java.util.*

class CalenderViewFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    private lateinit var binding: FragmentCalendarViewBinding
    private lateinit var viewModel: MainScreenViewModel
    private lateinit var transactionViewModel: TransactionViewModel
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // declaring the binding and viewModel
        binding = FragmentCalendarViewBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(MainScreenViewModel::class.java)
        transactionViewModel = ViewModelProvider(this).get(TransactionViewModel::class.java)
        // declaring the firebaseAuth
        firebaseAuth = FirebaseAuth.getInstance()

        // getting the user id from firebase to set in both viewModels
        viewModel.setUserID(firebaseAuth.currentUser?.uid.toString())
        transactionViewModel.setUserId(firebaseAuth.currentUser?.uid.toString())

        // getting monthYear through arguments of CalenderViewFragment
        var monthYear = CalenderViewFragmentArgs.fromBundle(requireArguments()).monthYear
        // monthYear is of the form YYYYMM in long format
        // hence monthYear /100 will be YYYY(the year) and monthYear % 100 will be MM(the month)
        val month = monthYear % 100
        val year = monthYear / 100
        val day = 1;

        // getting the instance of the calendar
        val calender = Calendar.getInstance()
        // setting the day, month and year of the calendar
        calender.set(year, month, day)
        // marking the dates
        //markDates(monthYear)

        //
//        binding.calendarView.setOnDateClickListener(object : OnDateClickListener() {
//            override fun onDateClick(view: View?, date: DateData) {
//                val calendar= Calendar.getInstance()
//                calendar.set(date.year,date.month,date.day)
//                showData(calendar.timeInMillis)
//            }
//        })
//
//        binding.calendarView.setOnMonthChangeListener(object : OnMonthChangeListener() {
//            override fun onMonthChange(year: Int, month: Int) {
//                monthYear = year * 100 + month
//                markDates(monthYear)
//            }
//        })

        // Inflate the layout for this fragment
        return binding.root
    }


//    private fun markDates(monthYear: Int) {
//        viewModel.getDates(monthYear).observe(viewLifecycleOwner) { it ->
//            it.forEach { date ->
//                val date1= Date(date)
//                binding.calendarView.markDate(DateData(date1.year, date1.month, date1.day))
//            }
//        }
//    }

    fun showData(date: Long) {

        viewModel.getAmountByDateAndType(date, TransactionType.EXPENSE)
            .observe(viewLifecycleOwner) {
                binding.spentAmount.text = it.toString()
            }

        viewModel.getAmountByDateAndType(date, TransactionType.INCOME).observe(viewLifecycleOwner) {
            binding.earnedAmount.text = it.toString()
        }

        viewModel.getTransactionsByDate(date).observe(viewLifecycleOwner) { it ->
            val adapter =
                TransactionListAdapter(it.toMutableList(), this, listener, transactionViewModel, requireContext())
            val swipeHandler = object : SwipeHandler() {
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    if (direction == ItemTouchHelper.LEFT) {
                        adapter.deleteTransaction(viewHolder.adapterPosition, it.toMutableList())
                    } else if (direction == ItemTouchHelper.RIGHT) {
                        adapter.completeTransaction(viewHolder.adapterPosition, it.toMutableList())
                    }
                }
            }

            binding.dailyTransactions.adapter = adapter
            ItemTouchHelper(swipeHandler).attachToRecyclerView(binding.dailyTransactions)
        }
    }

    private val listener: (id: Long) -> Unit = {
        findNavController().navigate(
            MainScreenFragmentDirections.actionMainScreenFragmentToTransactionDetailFragment(it)
        )
    }
}