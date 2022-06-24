package com.example.dealwithexpenses.mainScreen.tabs

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.dealwithexpenses.mainScreen.TransactionLibrary.TransactionLogEpoxyController
import com.example.dealwithexpenses.mainScreen.TransactionLibrary.TransactionLogItem
import com.example.dealwithexpenses.mainScreen.viewModels.MainScreenViewModel
import com.example.dealwithexpenses.R
import com.example.dealwithexpenses.databinding.FragmentTransactionLogTabBinding
import java.util.*

class TransactionLogTabFragment(val fragment: Fragment) : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    private lateinit var binding: FragmentTransactionLogTabBinding
    private lateinit var viewModel: MainScreenViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentTransactionLogTabBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(MainScreenViewModel::class.java)

        val transStatus = mutableListOf<TransactionLogItem>(
            TransactionLogItem("Completed", mutableListOf()),
            TransactionLogItem("Pending", mutableListOf()),
            TransactionLogItem("Upcoming", mutableListOf()),
        )

        viewModel.getCompletedTransactions().observe(viewLifecycleOwner) {
            transStatus[0].transactionLog = it.toMutableList()
        }

        viewModel.getPendingTransactions(Date(System.currentTimeMillis()).time)
            .observe(viewLifecycleOwner) {
                transStatus[1].transactionLog = it.toMutableList()
            }

        viewModel.getUpcomingTransactions(Date(System.currentTimeMillis()).time)
            .observe(viewLifecycleOwner) {
                transStatus[2].transactionLog = it.toMutableList()
            }

        val epoxyController = TransactionLogEpoxyController(fragment)
        epoxyController.transactionLog = transStatus

        binding.transactionsLogRecyclerView.setController(epoxyController)

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_transaction_log_tab, container, false)
    }

}