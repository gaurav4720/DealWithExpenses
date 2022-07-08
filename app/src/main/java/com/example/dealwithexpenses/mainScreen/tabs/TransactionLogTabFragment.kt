package com.example.dealwithexpenses.mainScreen.tabs

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.dealwithexpenses.mainScreen.TransactionLibrary.TransactionLogEpoxyController
import com.example.dealwithexpenses.mainScreen.TransactionLibrary.TransactionLogItem
import com.example.dealwithexpenses.mainScreen.viewModels.MainScreenViewModel
import com.example.dealwithexpenses.databinding.FragmentTransactionLogTabBinding
import com.example.dealwithexpenses.mainScreen.viewModels.TransactionViewModel
import com.google.firebase.auth.FirebaseAuth
import java.util.*

class TransactionLogTabFragment(val fragment: Fragment) : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    private lateinit var binding: FragmentTransactionLogTabBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var viewModel: MainScreenViewModel
    private lateinit var transactionViewModel: TransactionViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentTransactionLogTabBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(MainScreenViewModel::class.java)
        firebaseAuth = FirebaseAuth.getInstance()
        transactionViewModel = ViewModelProvider(this).get(TransactionViewModel::class.java)
        viewModel.setUserID(firebaseAuth.currentUser!!.uid)
        transactionViewModel.setUserId(firebaseAuth.currentUser!!.uid)

        val transStatus = mutableListOf<TransactionLogItem>(
            TransactionLogItem("Completed", mutableListOf()),
            TransactionLogItem("Pending", mutableListOf()),
            TransactionLogItem("Upcoming", mutableListOf()),
        )

        val epoxyController =
            TransactionLogEpoxyController(fragment, requireContext(), transactionViewModel)
        epoxyController.transactionLog = transStatus

        binding.transactionsLogRecyclerView.setController(epoxyController)
        binding.transactionsLogRecyclerView.layoutManager =
            androidx.recyclerview.widget.LinearLayoutManager(requireContext())

        viewModel.getCompletedTransactions().observe(viewLifecycleOwner) {
            transStatus[0]= TransactionLogItem("Completed", it.toMutableList())
            Log.d("ChalLeBhai",it.toString())
            epoxyController.transactionLog = transStatus
            epoxyController.requestModelBuild()
        }

        viewModel.getPendingTransactions(Date(System.currentTimeMillis()).time)
            .observe(viewLifecycleOwner) {
                transStatus[1]= TransactionLogItem("Pending", it.toMutableList())
                Log.d("ChalLeYaar",it.toString())
                epoxyController.transactionLog = transStatus
                epoxyController.requestModelBuild()
            }

        viewModel.getUpcomingTransactions(Date(System.currentTimeMillis()).time)
            .observe(viewLifecycleOwner) {
                transStatus[2]= TransactionLogItem("Upcoming", it.toMutableList())
                Log.d("ChalLeBsdk",it.toString())
                epoxyController.transactionLog = transStatus
                epoxyController.requestModelBuild()
            }

        // Inflate the layout for this fragment
        return binding.root
    }

}