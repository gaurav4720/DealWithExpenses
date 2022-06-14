package com.example.dealwithexpenses.TransactionLibrary

import android.app.DatePickerDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.view.isVisible
import com.example.dealwithexpenses.R
import com.example.dealwithexpenses.databinding.FragmentAddOrEditTransactionBinding
import java.text.SimpleDateFormat
import java.util.*


class AddOrEditTransactionFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    private lateinit var binding: FragmentAddOrEditTransactionBinding
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        binding = FragmentAddOrEditTransactionBinding.inflate(inflater, container, false)

        sharedPreferences =
            requireActivity().getSharedPreferences("add_or_edit", Context.MODE_PRIVATE)
        val mode: String? = sharedPreferences.getString("mode", "")!!

        binding.toolbar.inflateMenu(R.menu.add_or_edit_transaction_menu)

        binding.toolbar.title = if (mode.equals("Add"))
            "Add New Transaction"
        else
            "Edit Transactions"

        binding.transDateInput.transformIntoDatePicker(requireContext(), "dd-MM-yyyy")
        binding.toDateInput.transformIntoDatePicker(requireContext(), "dd-MM-yyyy")
        binding.fromDateInput.transformIntoDatePicker(requireContext(), "dd-MM-yyyy")

        binding.isRecurringCheckBox.setOnClickListener() {

            binding.toDateInput.isVisible = binding.isRecurringCheckBox.isChecked
            binding.fromDateInput.isVisible = binding.isRecurringCheckBox.isChecked

        }

        return binding.root
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