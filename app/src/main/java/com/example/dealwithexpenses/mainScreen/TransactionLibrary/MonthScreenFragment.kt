package com.example.dealwithexpenses.mainScreen.TransactionLibrary

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
    ): View? {
        // Inflate the layout for this fragment
        binding= FragmentMonthScreenBinding.inflate(inflater,container,false)
        transactionViewModel= ViewModelProvider(this).get(TransactionViewModel::class.java)
        viewModel= ViewModelProvider(this).get(MonthScreenViewModel:: class.java)
        auth= FirebaseAuth.getInstance()
        sharedPreferences= activity?.getSharedPreferences("user_auth", Context.MODE_PRIVATE)!!

        val monthYear= MonthScreenFragmentArgs.fromBundle(
            requireArguments()
        ).monthYear

        transactionViewModel.setUserId(auth.currentUser?.uid!!)
        viewModel.setUserId(auth.currentUser?.uid!!)

        val month= monthYear%100
        val year= monthYear/100
        binding.toolbar.title = "${months[month-1]} $year"
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        val activeIncome = sharedPreferences.getString("income", "0")?.toDouble()
        val totalGains = viewModel.monthlyIncome.value
        val totalExpenses = viewModel.monthlyExpenses.value

        binding.amountSpent.text= totalExpenses.toString()
        binding.netBalance.text= totalGains.toString()

        val totalCredit = totalGains?.plus((activeIncome!!))
        val totalBalance = totalCredit?.minus(totalExpenses!!)

        binding.amountSaved.text= totalBalance.toString()

        binding.addTransactionButton.setOnClickListener {
            findNavController().navigate(
                MonthScreenFragmentDirections.actionMonthScreenFragmentToAddOrEditTransactionFragment(
                    0
                )
            )
        }

        return binding.root
    }

    fun showMonthlyTransactions(monthYear: Int) {
        viewModel.monthlyTransactions.observe(viewLifecycleOwner) {
            val adapter= TransactionListAdapter(
                it.toMutableList(),
                this,
                listener,
                transactionViewModel,
                requireContext()
            )
            binding.transactionItems.adapter = adapter
            binding.transactionItems.layoutManager= LinearLayoutManager(requireContext())
            val swipeHandler = object : SwipeHandler() {
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    if (direction == ItemTouchHelper.LEFT) {
                        adapter.deleteTransaction(viewHolder.adapterPosition, it.toMutableList())
                    } else if (direction == ItemTouchHelper.RIGHT) {
                        adapter.completeTransaction(viewHolder.adapterPosition, it.toMutableList())
                    }
                }
            }

            val itemTouchHelper = ItemTouchHelper(swipeHandler)
            itemTouchHelper.attachToRecyclerView(binding.transactionItems)
        }
    }

    private val listener: (id: Long) -> Unit = {
        findNavController().navigate(
            MonthScreenFragmentDirections.actionMonthScreenFragmentToTransactionDetailFragment(it)
        )
    }

    companion object{
        private val months = arrayListOf<String>(
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
