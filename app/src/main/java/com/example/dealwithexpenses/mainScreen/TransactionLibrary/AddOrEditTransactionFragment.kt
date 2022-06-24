package com.example.dealwithexpenses.mainScreen.TransactionLibrary

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
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.dealwithexpenses.databinding.FragmentAddOrEditTransactionBinding
import com.example.dealwithexpenses.entities.*
import com.example.dealwithexpenses.mainScreen.viewModels.AddOrEditTransactionViewModel
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*


class AddOrEditTransactionFragment : Fragment() {

    private lateinit var binding: FragmentAddOrEditTransactionBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var viewModel: AddOrEditTransactionViewModel
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Inflate the layout for this fragment
        binding = FragmentAddOrEditTransactionBinding.inflate(inflater, container, false)

        sharedPreferences =
            requireActivity().getSharedPreferences("add_or_edit", Context.MODE_PRIVATE)
        val mode: String? = sharedPreferences.getString("mode", "")!!

        binding.toolbar.title = if (mode.equals("Add"))
            "Add New Transaction"
        else
            "Edit Transactions"

        binding.transDateInput.transformIntoDatePicker(requireContext(), "dd-MM-yyyy")
        binding.toDateInput.transformIntoDatePicker(requireContext(), "dd-MM-yyyy")
        binding.fromDateInput.transformIntoDatePicker(requireContext(), "dd-MM-yyyy")

        binding.isRecurringCheckBox.setOnCheckedChangeListener {_,it ->

            binding.toDateInput.isVisible = it
            binding.fromDateInput.isVisible = it

        }

        val modeArray: MutableList<String> = mutableListOf()
        TransactionMode.values().forEach {
            modeArray.add(it.name)
        }
        val adapter= ArrayAdapter(requireContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,modeArray)
        binding.transModeInput.adapter= adapter
        binding.transModeInput.onItemSelectedListener= object:AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {}
            override fun onNothingSelected(p0: AdapterView<*>?) {}

        }
        val categoryArray: MutableList<String> = mutableListOf()
        TransactionCategory.values().forEach {
            categoryArray.add(it.name)
        }
        val adapter2= ArrayAdapter(requireContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, categoryArray)
        binding.tagsSpinner.adapter= adapter2
        binding.tagsSpinner.onItemSelectedListener= object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {}
            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }
        viewModel= ViewModelProvider(this).get(AddOrEditTransactionViewModel::class.java)
        auth= FirebaseAuth.getInstance()

        val user_id= auth.currentUser!!.uid
        viewModel.setUserId(user_id)

        val transaction_id= AddOrEditTransactionFragmentArgs.fromBundle(requireArguments()).transId
        viewModel.setTransactionId(transaction_id)

        viewModel.transaction.observe(viewLifecycleOwner) {
            if(transaction_id!=0L){
                setData(it)
            }
        }
        binding.cancelButton.setOnClickListener {

            val dialog= AlertDialog.Builder(requireContext())
            with(dialog){
                setTitle("Cancel Transaction")
                setMessage("Are you sure you want to cancel this transaction?")
                setPositiveButton("Yes"){_, it->
                    findNavController().navigate(AddOrEditTransactionFragmentDirections.actionAddOrEditTransactionFragmentToMainScreenFragment())
                }

            }
        }

        binding.saveButton.setOnClickListener {
            saveData()
        }

        return binding.root
    }

    private fun saveData() {
        val name = binding.transTitleInput.text.toString()
        val desc= binding.transDescInput.text.toString()
        val amount= binding.transAmountInput.text.toString().toDouble()
        val date: Date= SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).parse(binding.transDateInput.text.toString())!!
        val isRecurring= binding.isRecurringCheckBox.isChecked
        val fromdate: Date
        val todate: Date
        if(isRecurring){
            fromdate= SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).parse(binding.fromDateInput.text.toString())!!
            todate= SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).parse(binding.toDateInput.text.toString())!!
        } else {
            fromdate= SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).parse(binding.transDateInput.text.toString())!!
            todate= SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).parse(binding.transDateInput.text.toString())!!
        }
        val mode: TransactionMode= TransactionMode.values()[binding.transModeInput.selectedItemPosition]
        val category: TransactionCategory= TransactionCategory.values()[binding.tagsSpinner.selectedItemPosition]
        val month= binding.transDateInput.text.toString().substring(3,5).toInt()
        val year= binding.transDateInput.text.toString().substring(6).toInt()
        val monthyear= year*100 + month
        val type: TransactionType= TransactionType.values()[binding.radioGroup.checkedRadioButtonId]
        val status: TransactionStatus= TransactionStatus.PENDING
        val transaction= Transaction(
            viewModel.userID.value!!,
            viewModel.transactionId.value!!,
            name,
            desc,
            amount,
            date,
            isRecurring,
            fromdate,
            todate,
            month,
            year,
            monthyear,
            type,
            category,
            mode,
            status
        )
        viewModel.insertOrUpdate(transaction)
        Toast.makeText(requireContext(),"Transaction added/updated",Toast.LENGTH_SHORT).show()
        findNavController().navigate(AddOrEditTransactionFragmentDirections.actionAddOrEditTransactionFragmentToMainScreenFragment())
    }

    private  fun setData(transaction: Transaction) {
        binding.transTitleInput.setText(transaction.title)
        binding.transDescInput.setText(transaction.description)
        binding.transAmountInput.text= (transaction.transactionAmount.toString())
        binding.transDateInput.setText(transaction.transactionDate.toString())
        if(transaction.isRecurring) {
            binding.isRecurringCheckBox.isChecked = true
            binding.fromDateInput.setText(transaction.fromDate.toString())
            binding.toDateInput.setText(transaction.toDate.toString())
        } else {
            binding.isRecurringCheckBox.isChecked = false
            binding.fromDateInput.isEnabled= false
            binding.toDateInput.isEnabled= false
        }
        binding.transModeInput.setSelection(TransactionMode.values().find {
            it.name== transaction.transactionMode.name
        }!!.ordinal)
        binding.tagsSpinner.setSelection(TransactionCategory.values().find{
            it.name== transaction.transactionCategory.name
        }!!.ordinal)
        binding.radioGroup.check(transaction.transactionType.ordinal)
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

                val sdf = SimpleDateFormat(format, Locale.UK)
                setText(sdf.format(myCalendar.time))
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