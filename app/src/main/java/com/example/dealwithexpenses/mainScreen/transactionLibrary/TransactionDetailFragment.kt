package com.example.dealwithexpenses.mainScreen.transactionLibrary

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.dealwithexpenses.entities.Transaction
import com.example.dealwithexpenses.mainScreen.viewModels.TransactionViewModel
import com.example.dealwithexpenses.R
import com.example.dealwithexpenses.databinding.FragmentTransactionDetailBinding
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*


class TransactionDetailFragment : Fragment() {

    private lateinit var binding: FragmentTransactionDetailBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var viewModel: TransactionViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentTransactionDetailBinding.inflate(inflater, container, false)
        sharedPreferences =
            requireActivity().getSharedPreferences("user_auth", Context.MODE_PRIVATE)

        viewModel = ViewModelProvider(this).get(TransactionViewModel::class.java)

        val userID = sharedPreferences.getString("user_id", "")!!

        viewModel.setUserId(userID)

        val transactionId = TransactionDetailFragmentArgs.fromBundle(requireArguments()).transId
        val direction =
            TransactionDetailFragmentArgs.fromBundle(requireArguments()).calenderOrRecents

        viewModel.setTransactionId(transactionId)

        viewModel.transaction.observe(viewLifecycleOwner) {
            if (it != null)
                setData(it)
        }

        binding.toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.edit -> {
                    findNavController().navigate(
                        TransactionDetailFragmentDirections.actionTransactionDetailFragmentToAddOrEditTransactionFragment(
                            viewModel.transactionId.value!!,
                            1
                        )
                    )
                    true
                }
                else -> {
                    val dialog = AlertDialog.Builder(requireContext())
                    with(dialog) {
                        setTitle("Delete Transaction")
                        setMessage("Are you sure you want to delete this transaction?")
                        setPositiveButton("Yes") { _, _ ->
                            viewModel.delete(viewModel.transaction.value!!)
                            Toast.makeText(
                                requireContext(),
                                "Transaction deleted successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                            if (direction == 1)
                                findNavController().navigate(
                                    TransactionDetailFragmentDirections.actionTransactionDetailFragmentToMainScreenFragment(
                                        1
                                    )
                                )
                            else
                                findNavController().navigateUp()
                        }
                        setNegativeButton("No") { _, _ ->
                        }
                    }
                    dialog.create().show()
                    true
                }
            }
        }
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (direction == 1)
                    findNavController().navigate(
                        TransactionDetailFragmentDirections.actionTransactionDetailFragmentToMainScreenFragment(
                            1
                        )
                    )
                else
                    findNavController().navigateUp()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(onBackPressedCallback)
        return binding.root
    }

    private fun setData(transaction: Transaction) {
        binding.transTitleInput.text = transaction.title
        binding.transDescInput.text = transaction.description ?: ""
        binding.transAmountInput.text = (transaction.transactionAmount.toString())
        binding.transDateInput.text =
            (SimpleDateFormat("dd-MM-yyyy").format(transaction.transactionDate))
        if (transaction.isRecurring) {
            binding.isRecurringCheckBox.isChecked = true
            binding.isRecurringCheckBox.isEnabled = false
            binding.fromDateInput.text =
                (SimpleDateFormat("dd-MM-yyyy").format(transaction.fromDate))
            binding.toDateInput.text = (SimpleDateFormat("dd-MM-yyyy").format(transaction.toDate))
        } else {
            binding.isRecurringCheckBox.isChecked = false
            binding.isRecurringCheckBox.isEnabled = false
            binding.fromDateInput.isVisible = false
            binding.toDateInput.isVisible = false
        }

        binding.transModeInput.text = transaction.transactionMode.name
        binding.incomeButton.isChecked = transaction.transactionType.ordinal == 1
        binding.incomeButton.isEnabled = false
        binding.expenseButton.isChecked = transaction.transactionType.ordinal == 0
        binding.expenseButton.isEnabled = false
    }
}