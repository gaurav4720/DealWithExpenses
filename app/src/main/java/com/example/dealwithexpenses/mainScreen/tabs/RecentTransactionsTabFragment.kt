package com.example.dealwithexpenses.mainScreen.tabs

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.dealwithexpenses.mainScreen.transactionLibrary.TransactionLogEpoxyController
import com.example.dealwithexpenses.mainScreen.transactionLibrary.TransactionLogItem
import com.example.dealwithexpenses.mainScreen.viewModels.MainScreenViewModel
import com.example.dealwithexpenses.databinding.FragmentTransactionLogTabBinding
import com.example.dealwithexpenses.entities.TransactionStatus
import com.example.dealwithexpenses.mainScreen.viewModels.TransactionViewModel
import com.google.firebase.auth.FirebaseAuth
import java.util.*

class RecentTransactionsTabFragment(val fragment: Fragment) : Fragment() {

    private lateinit var binding: FragmentTransactionLogTabBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var viewModel: MainScreenViewModel
    private lateinit var transactionViewModel: TransactionViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // initialize the binding, firebase auth and both the viewModels
        binding = FragmentTransactionLogTabBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(MainScreenViewModel::class.java)
        sharedPreferences= fragment.requireActivity().getSharedPreferences("user_auth", Context.MODE_PRIVATE)
        transactionViewModel = ViewModelProvider(this).get(TransactionViewModel::class.java)

        // setting the user id for both the viewModels
        val userId= sharedPreferences.getString("user_id", "")!!

        //setting the user id of viewModel after getting it through firebase
        viewModel.setUserID(userId)
        transactionViewModel.setUserId(userId)

        val transStatus = mutableListOf(
            TransactionLogItem("Completed", mutableListOf()),
            TransactionLogItem("Pending", mutableListOf()),
            TransactionLogItem("Upcoming", mutableListOf()),
        )

        val epoxyController =
            TransactionLogEpoxyController(fragment, requireContext(), transactionViewModel)
        epoxyController.transactionLog = transStatus

        viewModel.transactions.observe(viewLifecycleOwner) {
            val completedList= it.filter { it1->
                it1.transactionStatus == TransactionStatus.COMPLETED
            }
            val pendingList= it.filter { it1->
                it1.transactionStatus == TransactionStatus.PENDING && it1.transactionDate.before(Date())
            }
            val upcomingList= it.filter { it1 ->
                it1.transactionStatus == TransactionStatus.PENDING && it1.transactionDate.after(Date())
            }
            transStatus[0]= TransactionLogItem("Completed", completedList.toMutableList())
            transStatus[1]= TransactionLogItem("Pending", pendingList.toMutableList())
            transStatus[2]= TransactionLogItem("Upcoming", upcomingList.toMutableList())

            epoxyController.transactionLog = transStatus
            binding.transactionsLogRecyclerView.adapter= epoxyController
            binding.transactionsLogRecyclerView.layoutManager =
                androidx.recyclerview.widget.LinearLayoutManager(requireContext())
        }

//        viewModel.getCompletedTransactions().observe(viewLifecycleOwner) {
//            transStatus[0]= TransactionLogItem("Completed", it.toMutableList())
//            epoxyController.transactionLog = transStatus
//            binding.transactionsLogRecyclerView.adapter= epoxyController
//        }
//
//        viewModel.getPendingTransactions(Date(System.currentTimeMillis()).time)
//            .observe(viewLifecycleOwner) {
//                transStatus[1]= TransactionLogItem("Pending", it.toMutableList())
//                epoxyController.transactionLog = transStatus
//                binding.transactionsLogRecyclerView.adapter= epoxyController
//            }
//
//        viewModel.getUpcomingTransactions(Date(System.currentTimeMillis()).time)
//            .observe(viewLifecycleOwner) {
//                transStatus[2]= TransactionLogItem("Upcoming", it.toMutableList())
//                epoxyController.transactionLog = transStatus
//                binding.transactionsLogRecyclerView.adapter= epoxyController
//            }

        // Inflate the layout for this fragment
        return binding.root
    }

}