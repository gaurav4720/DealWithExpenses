package com.example.dealwithexpenses.mainScreen.transactionLibrary

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.dealwithexpenses.mainScreen.viewModels.MainScreenViewModel
import com.example.dealwithexpenses.databinding.FragmentCalendarViewBinding
import com.example.dealwithexpenses.mainScreen.viewModels.TransactionViewModel
import java.util.*

class CalenderViewFragment : Fragment() {
    private lateinit var binding: FragmentCalendarViewBinding
    private lateinit var viewModel: MainScreenViewModel
    private lateinit var transactionViewModel: TransactionViewModel
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // declaring the binding and viewModel
        binding = FragmentCalendarViewBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(MainScreenViewModel::class.java)
        transactionViewModel = ViewModelProvider(this).get(TransactionViewModel::class.java)
        sharedPreferences= activity?.getSharedPreferences("user_auth",0)!!

        val userId= sharedPreferences.getString("user_id", "")!!

        //setting the user id of viewModel after getting it through shared preferences
        viewModel.setUserID(userId)
        transactionViewModel.setUserId(userId)

        // getting monthYear through arguments of CalenderViewFragment
        val monthYear = CalenderViewFragmentArgs.fromBundle(requireArguments()).monthYear
        // monthYear is of the form YYYYMM in long format
        // hence monthYear /100 will be YYYY(the year) and monthYear % 100 will be MM(the month)
        val month = monthYear % 100
        val year = monthYear / 100
        val day = 1

        // getting the instance of the calendar
        val calender = Calendar.getInstance()
        // setting the day, month and year of the calendar
        calender.set(year, month, day)
        // marking the dates
        //markDates(monthYear)

        viewModel.setDate(calender.timeInMillis)
        binding.calendarView.selectedDates = mutableListOf(calender)

        binding.calendarView.setOnDayClickListener { eventDay ->
            val calendar = eventDay.calendar
            viewModel.setMonthYear(calendar.get(Calendar.YEAR) * 100 + calendar.get(Calendar.MONTH))
            viewModel.setDate(calendar.timeInMillis)
            binding.calendarView.selectedDates= mutableListOf(calendar)
            binding.calendarView.setHighlightedDays(mutableListOf(calendar))
        }

        viewModel.getExpenseByDateAndType
            .observe(viewLifecycleOwner) {
                binding.spentAmount.text = it?.toString() ?: "0"
            }

        viewModel.getIncomeByDateAndType
            .observe(viewLifecycleOwner) {
                binding.earnedAmount.text = it?.toString() ?: "0"
        }

        viewModel.getTransactionsByDate.observe(viewLifecycleOwner) {
            val adapter =
                TransactionListAdapter(
                    it.toMutableList(),
                    this,
                    listener,
                    transactionViewModel,
                    requireContext()
                )
            val swipeHandler = object : SwipeHandler(requireContext()) {
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    if (direction == ItemTouchHelper.LEFT) {
                        adapter.deleteTransaction(viewHolder.absoluteAdapterPosition, it.toMutableList())
                    } else if (direction == ItemTouchHelper.RIGHT) {
                        adapter.completeTransaction(viewHolder.absoluteAdapterPosition, it.toMutableList())
                    }
                }
            }

            binding.dailyTransactions.adapter = adapter
            binding.dailyTransactions.layoutManager =
                androidx.recyclerview.widget.LinearLayoutManager(requireContext())
            ItemTouchHelper(swipeHandler).attachToRecyclerView(binding.dailyTransactions)
        }

        val onBackPressedCallback= object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigateUp()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(onBackPressedCallback)

        // Inflate the layout for this fragment
        return binding.root
    }

    private val listener: (id: Long) -> Unit = {
        findNavController().navigate(
            CalenderViewFragmentDirections.actionCalenderViewFragmentToTransactionDetailFragment(it,0)
        )
    }
}