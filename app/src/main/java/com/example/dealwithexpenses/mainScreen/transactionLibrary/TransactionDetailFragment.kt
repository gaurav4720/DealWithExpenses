package com.example.dealwithexpenses.mainScreen.transactionLibrary

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.dealwithexpenses.R
import com.example.dealwithexpenses.databinding.FragmentTransactionDetailBinding
import com.example.dealwithexpenses.entities.Transaction
import com.example.dealwithexpenses.mainScreen.viewModels.TransactionViewModel
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat


class TransactionDetailFragment : Fragment() {

    private lateinit var binding: FragmentTransactionDetailBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var viewModel: TransactionViewModel
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding= FragmentTransactionDetailBinding.inflate(inflater,container,false)
        sharedPreferences= requireActivity().getSharedPreferences("transaction_detail", Context.MODE_PRIVATE)

        viewModel= ViewModelProvider(this).get(TransactionViewModel::class.java)
        auth= FirebaseAuth.getInstance()

        val userId= auth.currentUser!!.uid
        viewModel.setUserId(userId)

        val transactionId= TransactionDetailFragmentArgs.fromBundle(requireArguments()).transId
        viewModel.setTransactionId(transactionId)

        viewModel.transaction.observe(viewLifecycleOwner) {
            setData(it)
        }

        binding.toolbar.setOnMenuItemClickListener { item ->
            when(item.itemId)  {
                R.id.edit -> {
                    findNavController().navigate(TransactionDetailFragmentDirections.actionTransactionDetailFragmentToAddOrEditTransactionFragment(viewModel.transactionId.value!!))
                    true
                }
                else -> {
                    val dialog= AlertDialog.Builder(requireContext())
                    with(dialog){
                        setTitle("Delete Transaction")
                        setMessage("Are you sure you want to delete this transaction?")
                        setPositiveButton("Yes"){_,_->
                            viewModel.delete(viewModel.transaction.value!!)
                            Toast.makeText(requireContext(),"Transaction deleted successfully",Toast.LENGTH_SHORT).show()
                            findNavController().navigate(TransactionDetailFragmentDirections.actionTransactionDetailFragmentToMainScreenFragment())
                        }
                        setNegativeButton("No"){_,_->
                        }
                    }
                    dialog.create().show()
                    true
                }
            }
        }
        val onBackPressedCallback= object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(TransactionDetailFragmentDirections.actionTransactionDetailFragmentToMainScreenFragment())
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(onBackPressedCallback)
        return binding.root
    }

    private  fun setData(transaction: Transaction) {
        binding.transTitleInput.text= transaction.title
        binding.transDescInput.text= transaction.description
        binding.transAmountInput.text= (transaction.transactionAmount.toString())
        binding.transDateInput.text= (SimpleDateFormat("dd-MM-yyyy").format(transaction.transactionDate))
        if(transaction.isRecurring) {
            binding.isRecurringCheckBox.isChecked = true
            binding.isRecurringCheckBox.isEnabled = false
            binding.fromDateInput.text= transaction.fromDate.toString()
            binding.toDateInput.text= transaction.toDate.toString()
        } else {
            binding.isRecurringCheckBox.isChecked = false
            binding.isRecurringCheckBox.isEnabled = false
            binding.fromDateInput.isVisible= false
            binding.toDateInput.isVisible= false
        }
        binding.transModeInput.text= transaction.transactionMode.name
        binding.incomeButton.isChecked= transaction.transactionType.ordinal==1
        binding.incomeButton.isEnabled= false
        binding.expenseButton.isChecked= transaction.transactionType.ordinal==0
        binding.expenseButton.isEnabled= false
    }
}