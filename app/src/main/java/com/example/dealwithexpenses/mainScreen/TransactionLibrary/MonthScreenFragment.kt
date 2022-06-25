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
import com.example.dealwithexpenses.databinding.FragmentMonthScreenBinding
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= FragmentMonthScreenBinding.inflate(inflater,container,false)
        viewModel= ViewModelProvider(this).get(MonthScreenViewModel:: class.java)
        auth= FirebaseAuth.getInstance()
        sharedPreferences= activity?.getSharedPreferences("user_auth", Context.MODE_PRIVATE)!!

        val monthYear= MonthScreenFragmentArgs.fromBundle(
            requireArguments()
        ).monthYear
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
        return binding.root
    }

    fun showMonthlyTransactions(monthYear: Int) {
        viewModel.monthlyTransactions.observe(viewLifecycleOwner) {
            binding.transactionItems.adapter =
                TransactionListAdapter(it.toMutableList(), this, listener)
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
