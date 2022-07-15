package com.example.dealwithexpenses.mainScreen.transactionLibrary

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dealwithexpenses.R
import com.example.dealwithexpenses.databinding.FragmentMonthScreenBinding
import com.example.dealwithexpenses.mainScreen.viewModels.TransactionViewModel
import com.example.dealwithexpenses.mainScreen.viewModels.MonthScreenViewModel
import com.google.firebase.auth.FirebaseAuth

class MonthScreenFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }
    private lateinit var binding: FragmentMonthScreenBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var auth: FirebaseAuth
    private lateinit var viewModel: MonthScreenViewModel
    private lateinit var transactionViewModel: TransactionViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentMonthScreenBinding.inflate(inflater, container, false)

        //initialising both the viewModels, firebase auth and shared preferences
        transactionViewModel = ViewModelProvider(this).get(TransactionViewModel::class.java)
        viewModel = ViewModelProvider(this).get(MonthScreenViewModel::class.java)
        auth = FirebaseAuth.getInstance()
        sharedPreferences = activity?.getSharedPreferences("user_auth", Context.MODE_PRIVATE)!!

        //getting the monthYear from required arguments
        val monthYear = MonthScreenFragmentArgs.fromBundle(
            requireArguments()
        ).monthYear

        //setting the user id  of both the viewModels
        transactionViewModel.setUserId(auth.currentUser?.uid!!)
        viewModel.setUserId(auth.currentUser?.uid!!)

        //setting the monthYear to the viewModel
        viewModel.setMonthYear(monthYear)

        binding.toolbar.setOnMenuItemClickListener {
            when(it.itemId){
                R.id.calendarView -> {
                    findNavController().navigate(MonthScreenFragmentDirections.actionMonthScreenFragmentToCalenderViewFragment(monthYear))
                }
            }
            true
        }

        //monthYear is pf the format "YYYYMM" so we need to split it to get the month and year
        val month = monthYear % 100
        val year = monthYear / 100

        // title being the month and year
        binding.toolbar.title = "${months[month - 1]} $year"

        //if the user clicks on the arrow back button, he will be redirected to the previous screen
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigate(MonthScreenFragmentDirections.actionMonthScreenFragmentToMainScreenFragment(2))
        }

        //getting the budget and income from the shared preferences
        val activeBudget = sharedPreferences.getString("budget", "0")!!.toDouble()
        val activeIncome = sharedPreferences.getString("income", "0")?.toDouble()

        //variables to store the expenses and gains of the month
        var totalGains = 0.0
        var totalExpenses = 0.0

        //observing in the livedata returned by the viewModel to calculate the expenses and gains of the month
        viewModel.monthlyExpenses.observe (viewLifecycleOwner) {
            if (it != null) {
                totalExpenses = it
            }
            binding.amountSpent.text= totalExpenses.toString()
        }
        viewModel.monthlyIncome.observe (viewLifecycleOwner) {
            if (it != null) {
                totalGains = it
            }
            binding.netBalance.text= totalGains.toString()
        }

        //obtaining the credit and balance through gains and expenses
        val totalCredit = totalGains.plus((activeIncome!!))
        val totalBalance = totalCredit.minus(totalExpenses)
        binding.amountSaved.text= totalBalance.toString()
        binding.monthBudget.text= activeBudget.toString()

        //creating the adapter for the recycler view
        //the adapter will show the transactions of the month
        viewModel.monthlyTransactions.observe(viewLifecycleOwner) {
            Log.d("hemlo2", it.toString())
            val adapter = TransactionListAdapter(
                it.toMutableList(),
                this,
                listener,
                transactionViewModel,
                requireContext()
            )
            //setting the adapter to the recycler view
            binding.transactionItems.adapter = adapter
            //setting the layout manager to the recycler view
            binding.transactionItems.layoutManager = LinearLayoutManager(requireContext())
            val swipeHandler = object : SwipeHandler() {
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    //if user swipes left, delete the transaction
                    if (direction == ItemTouchHelper.LEFT) {
                        adapter.deleteTransaction(
                            viewHolder.absoluteAdapterPosition,
                            it.toMutableList()
                        )
                    }
                    //if user swipes right, complete the transaction
                    else if (direction == ItemTouchHelper.RIGHT) {
                        Log.d("atishay",viewHolder.absoluteAdapterPosition.toString())
                        adapter.completeTransaction(
                            viewHolder.absoluteAdapterPosition,
                            it.toMutableList()
                        )
                    }
                }
            }

            val itemTouchHelper = ItemTouchHelper(swipeHandler)
            itemTouchHelper.attachToRecyclerView(binding.transactionItems)
        }

        //if the user clicks on the add transaction button, he will be redirected to the add transaction screen
        binding.addTransactionButton.setOnClickListener {
            findNavController().navigate(
                MonthScreenFragmentDirections.actionMonthScreenFragmentToAddOrEditTransactionFragment(
                    0,
                    2
                )
            )
        }

        //if user presses back button, he will be redirected to the previous screen
        val onBackPressedCallback= object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(MonthScreenFragmentDirections.actionMonthScreenFragmentToMainScreenFragment(2))
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(onBackPressedCallback)

        return binding.root
    }

    private val listener: (id: Long) -> Unit = {
        //to direct to the transaction detail fragment
        findNavController().navigate(
            MonthScreenFragmentDirections.actionMonthScreenFragmentToTransactionDetailFragment(it,0)
        )
    }

    companion object{
        //array of months
        private val months = arrayListOf(
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
