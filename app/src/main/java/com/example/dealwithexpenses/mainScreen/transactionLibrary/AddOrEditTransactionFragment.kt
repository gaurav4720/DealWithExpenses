package com.example.dealwithexpenses.mainScreen.transactionLibrary

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.dealwithexpenses.entities.*
import com.example.dealwithexpenses.mainScreen.viewModels.TransactionViewModel
import com.example.dealwithexpenses.databinding.FragmentAddOrEditTransactionBinding
import com.example.dealwithexpenses.navDrawerScreens.MyDetailsFragmentDirections
import java.text.SimpleDateFormat
import java.util.*


class AddOrEditTransactionFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    private lateinit var binding: FragmentAddOrEditTransactionBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var viewModel: TransactionViewModel
    private var screenNo = 0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Inflate the layout for this fragment
        screenNo = AddOrEditTransactionFragmentArgs.fromBundle(requireArguments()).screenNo

        binding = FragmentAddOrEditTransactionBinding.inflate(inflater, container, false)

        sharedPreferences = activity?.getSharedPreferences("user_auth", Context.MODE_PRIVATE)!!

        binding.transDateInput.transformIntoDatePicker(requireContext(), "dd-MM-yyyy")
        binding.toDateInput.transformIntoDatePicker(requireContext(), "dd-MM-yyyy")
        binding.fromDateInput.transformIntoDatePicker(requireContext(), "dd-MM-yyyy")

        binding.fromDateInput.isVisible = false
        binding.toDateInput.isVisible = false

        binding.isRecurringCheckBox.setOnCheckedChangeListener { _, it ->

            binding.toDateInput.isVisible = it
            binding.fromDateInput.isVisible = it

        }

        val modeArray: MutableList<String> = mutableListOf()
        TransactionMode.values().forEach {
            modeArray.add(it.name)
        }
        val adapter = ArrayAdapter(
            requireContext(),
            androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
            modeArray
        )
        binding.transModeInput.adapter = adapter
        binding.transModeInput.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {}
                override fun onNothingSelected(p0: AdapterView<*>?) {}

            }
        val categoryArray: MutableList<String> = mutableListOf()
        TransactionCategory.values().forEach {
            categoryArray.add(it.name)
        }
        val adapter2 = ArrayAdapter(
            requireContext(),
            androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
            categoryArray
        )
        binding.tagsSpinner.adapter = adapter2
        binding.tagsSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {}
            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }
        viewModel = ViewModelProvider(this).get(TransactionViewModel::class.java)

        val userID = sharedPreferences.getString("user_id", "")!!
        val transID = AddOrEditTransactionFragmentArgs.fromBundle(requireArguments()).transId

        //setting the user id and trans id of viewModel after getting it through firebase
        viewModel.setUserId(userID)
        viewModel.setTransactionId(transID)

        if (transID == 0L) {
            binding.toolbar.title = "Add new Transaction"
        } else binding.toolbar.title = "Edit Transaction"
        viewModel.transaction.observe(viewLifecycleOwner) {
            if (transID != 0L) {
                setData(it)
            }
        }

        binding.cancelButton.setOnClickListener {

            val dialog = AlertDialog.Builder(requireContext())
            with(dialog) {
                setTitle("Cancel Transaction")
                setMessage("Are you sure you want to discard the changes?")
                setPositiveButton("Yes") { _, _ ->
                    if(screenNo<=2)
                    findNavController().navigate(
                        AddOrEditTransactionFragmentDirections.actionAddOrEditTransactionFragmentToMainScreenFragment(
                            screenNo
                        )
                    )
                    else
                        findNavController().navigateUp()
                }
            }
            dialog.create().show()
        }

        binding.saveButton.setOnClickListener {
            saveData()
        }
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if(screenNo<=2)
                    findNavController().navigate(
                        AddOrEditTransactionFragmentDirections.actionAddOrEditTransactionFragmentToMainScreenFragment(
                            screenNo
                        )
                    )
                else
                    findNavController().navigateUp()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(onBackPressedCallback)

        return binding.root
    }

    private fun saveData() {
        var validTrans= true

        if (binding.transTitleInput.text.isBlank()) {
            binding.transTitleInput.error = "Title is required"
            validTrans = false
        }

        if (binding.transAmountInput.text.isBlank()) {
            binding.transAmountInput.error = "Amount is required"
            validTrans = false
        }

        if (binding.transDateInput.text.isNullOrBlank()) {
            binding.transDateInput.error = "Transaction Date is required"
            validTrans = false
        }

        if (binding.isRecurringCheckBox.isChecked) {
            if (binding.fromDateInput.text.isNullOrBlank()) {
                binding.fromDateInput.error = "From Date is required"
                validTrans = false
            }
            if (binding.toDateInput.text.isNullOrBlank()) {
                binding.toDateInput.error = "To Date is required"
                validTrans = false
            }
        }

        if (!(binding.incomeButton.isChecked || binding.expenseButton.isChecked)) {
            Toast.makeText(requireContext(), "Please select transaction type", Toast.LENGTH_SHORT)
                .show()
            validTrans = false
        }

        if (!validTrans) return

        val name = binding.transTitleInput.text.toString()
        val desc = binding.transDescInput.text.toString() ?: ""
        val amount = binding.transAmountInput.text.toString().toDouble()
        val date: Date = SimpleDateFormat(
            "dd-MM-yyyy",
            Locale.getDefault()
        ).parse(binding.transDateInput.text.toString())!!

        val isRecurring = binding.isRecurringCheckBox.isChecked
        val fromDate: Date = if (isRecurring) {
            SimpleDateFormat(
                "dd-MM-yyyy",
                Locale.getDefault()
            ).parse(binding.fromDateInput.text.toString())!!
        } else {
            date
        }

        val toDate: Date = if (isRecurring) {
            SimpleDateFormat(
                "dd-MM-yyyy",
                Locale.getDefault()
            ).parse(binding.toDateInput.text.toString())!!
        } else {
            date
        }

        val mode: TransactionMode =
            TransactionMode.values()[binding.transModeInput.selectedItemPosition]
        val category: TransactionCategory =
            TransactionCategory.values()[binding.tagsSpinner.selectedItemPosition]
        val month = binding.transDateInput.text.toString().substring(3, 5).toInt()
        val year = binding.transDateInput.text.toString().substring(6).toInt()
        val monthYear = year * 100 + month
        val typeIndex = if (binding.incomeButton.isChecked) 1 else 0
        val type: TransactionType = TransactionType.values()[typeIndex]
        var status: TransactionStatus = TransactionStatus.PENDING
        if (binding.checkIfAlreadyCompleted.isChecked) {
            status = TransactionStatus.COMPLETED
        }

        val transaction = Transaction(
            viewModel.userID.value!!,
            viewModel.transactionId.value!!,
            name,
            desc,
            amount,
            date,
            isRecurring,
            fromDate,
            toDate,
            month,
            year,
            monthYear,
            type,
            category,
            mode,
            status
        )

        viewModel.insertOrUpdate(transaction)
        Toast.makeText(
            requireContext(),
            "Transaction registered successfully!!",
            Toast.LENGTH_SHORT
        ).show()

        if (screenNo <= 2) findNavController().navigate(AddOrEditTransactionFragmentDirections.actionAddOrEditTransactionFragmentToMainScreenFragment(screenNo))
        else findNavController().navigateUp()

    }

    private fun setData(transaction: Transaction) {
        binding.transTitleInput.setText(transaction.title)
        binding.transDescInput.setText(transaction.description)
        binding.transAmountInput.setText(transaction.transactionAmount.toString())
        binding.transDateInput.setText(SimpleDateFormat("dd-MM-yyyy").format(transaction.transactionDate))
        if (transaction.isRecurring) {
            binding.isRecurringCheckBox.isChecked = true
            binding.fromDateInput.setText(transaction.fromDate.toString())
            binding.toDateInput.setText(transaction.toDate.toString())
        } else {
            binding.isRecurringCheckBox.isChecked = false
            binding.fromDateInput.isEnabled = false
            binding.toDateInput.isEnabled = false
        }
        binding.transModeInput.setSelection(TransactionMode.values().find {
            it.name == transaction.transactionMode.name
        }!!.ordinal)
        binding.tagsSpinner.setSelection(TransactionCategory.values().find {
            it.name == transaction.transactionCategory.name
        }!!.ordinal)
        binding.incomeButton.isChecked = transaction.transactionType.ordinal == 1
        binding.expenseButton.isChecked = transaction.transactionType.ordinal == 0

    }

    private fun EditText.transformIntoDatePicker(context: Context, format: String) {
        isFocusableInTouchMode = false
        isClickable = true
        isFocusable = false

        val myCalendar = Calendar.getInstance()
        val datePickerOnDataSetListener =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                myCalendar.set(Calendar.YEAR, year)
                myCalendar.set(Calendar.MONTH, monthOfYear)
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                val sdf = SimpleDateFormat(format, Locale.UK)
                setText(sdf.format(myCalendar.time))
                error = null
            }

        setOnClickListener {
            DatePickerDialog(
                context, datePickerOnDataSetListener, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)
            ).run {

                show()
            }
        }
    }
}