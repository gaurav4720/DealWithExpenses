package com.example.dealwithexpenses.mainScreen.tabs

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.example.dealwithexpenses.entities.TransactionCategory
import com.example.dealwithexpenses.entities.TransactionType
import com.example.dealwithexpenses.mainScreen.viewModels.MainScreenViewModel
import com.example.dealwithexpenses.R
import com.example.dealwithexpenses.databinding.FragmentOverviewTabBinding
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.firebase.auth.FirebaseAuth
import org.eazegraph.lib.models.PieModel
import java.util.*
import kotlin.collections.ArrayList
class OverviewTabFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    private lateinit var viewModel: MainScreenViewModel
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var binding: FragmentOverviewTabBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentOverviewTabBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(MainScreenViewModel::class.java)
        firebaseAuth = FirebaseAuth.getInstance()
        viewModel.setUserID(firebaseAuth.currentUser?.uid.toString())

        val pieChartChoices = arrayOf(
            "Category",
            "Type",
            "Mode",
        )

        val pieChartAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            pieChartChoices
        )

        binding.pieChartSpinner.adapter = pieChartAdapter

        setPieChart("Category")
        binding.pieChartSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    viewModel.setChoice(pieChartChoices[position])
                    viewModel.choice.observe(viewLifecycleOwner) {
                        setPieChart(it)
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }
            }

        val barChartYears: MutableList<String> = mutableListOf()
        viewModel.years.observe(viewLifecycleOwner) {
            it.forEach { year ->
                barChartYears.add(year.toString())
            }
        }

        val barChartAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            barChartYears
        )

        var barChartMode = "Yearly"
        var year: Int = -1

        binding.switch1.setOnClickListener {
            binding.barChartSpinner.isVisible = it.isActivated
            barChartMode = if (it.isActivated) "Monthly" else "Yearly"

            year = if (barChartMode == "Yearly") {
                -1
            } else {
                Calendar.getInstance().get(Calendar.YEAR)
            }
            setBarChart(barChartMode, year)
        }

        binding.barChartSpinner.adapter = barChartAdapter

        setBarChart(barChartMode, year)
        binding.barChartSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    setBarChart(barChartMode, year)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }
            }

        // Inflate the layout for this fragment
        return binding.root
    }


    fun setPieChart(pieChartMode: String) {

        val colors: Array<Int>
        var pieEntries: ArrayList<PieModel>

        when (pieChartMode) {
            "Category" -> {

                colors = arrayOf(
                    R.color.category_food,
                    R.color.category_clothing,
                    R.color.category_entertainment,
                    R.color.category_transport,
                    R.color.category_health,
                    R.color.category_education,
                    R.color.category_bills,
                    R.color.category_other
                )

                viewModel.fetchCategoriesData()
                viewModel.categoryInfo.observe(viewLifecycleOwner) {
                    pieEntries = arrayListOf()
                    it.forEachIndexed { index, category ->
                        pieEntries.add(
                            PieModel(
                                TransactionCategory.values()[index].name,
                                category.toFloat(),
                                colors[index]
                            )
                        )
                    }

                }

            }

            "Type" -> {
                colors = arrayOf(
                    R.color.type_income,
                    R.color.type_expense
                )

                viewModel.fetchTypesData()
                viewModel.typesInfo.observe(viewLifecycleOwner) {
                    pieEntries = arrayListOf()
                    it.forEachIndexed { index, type ->
                        pieEntries.add(
                            PieModel(
                                TransactionType.values()[index].name,
                                type.toFloat(),
                                colors[index]
                            )
                        )
                    }

                }
            }

            "Mode" -> {
                colors = arrayOf(
                    R.color.mode_cash,
                    R.color.mode_credit_card,
                    R.color.mode_debit_card,
                )

                viewModel.fetchModesData()
                viewModel.transModesInfo.observe(viewLifecycleOwner) {
                    pieEntries = arrayListOf()
                    it.forEachIndexed { index, mode ->
                        pieEntries.add(
                            PieModel(
                                TransactionType.values()[index].name,
                                mode.toFloat(),
                                colors[index]
                            )
                        )
                    }

                }

            }
        }

        binding.pieChart.animate()
    }

    fun setBarChart(barChartMode: String, year: Int = -1) {
        when (barChartMode) {
            "Monthly" -> {

                val months: ArrayList<String> = arrayListOf(
                    "Jan",
                    "Feb",
                    "Mar",
                    "Apr",
                    "May",
                    "Jun",
                    "Jul",
                    "Aug",
                    "Sep",
                    "Oct",
                    "Nov",
                    "Dec",
                )

                viewModel.fetchMonthlyAmountData(year)
                viewModel.fetchMonthlyTransactionsData(year)

                var barAmountEntries: MutableList<BarEntry> = mutableListOf()
                viewModel.monthlyAmountData.observe(viewLifecycleOwner) {
                    barAmountEntries = arrayListOf()
                    it.forEachIndexed { index, amount ->
                        barAmountEntries = mutableListOf()
                        barAmountEntries.add(
                            BarEntry(
                                index.toFloat(),
                                amount.toFloat()
                            )
                        )
                    }
                }

                var barTransactionsEntries: MutableList<BarEntry> = mutableListOf()
                viewModel.monthlyTransactionData.observe(viewLifecycleOwner) {
                    barTransactionsEntries = mutableListOf()
                    it.forEachIndexed { index, transactions ->
                        barTransactionsEntries = mutableListOf()
                        barTransactionsEntries.add(
                            BarEntry(
                                index.toFloat(),
                                transactions.toFloat()
                            )
                        )
                    }
                }

                val barDataSet: ArrayList<BarDataSet> = arrayListOf()
                barDataSet.add(
                    BarDataSet(
                        barAmountEntries,
                        "Amount"
                    )
                )

                barDataSet.add(
                    BarDataSet(
                        barTransactionsEntries,
                        "Transactions"
                    )
                )

                barDataSet[0].color =
                    ContextCompat.getColor(requireContext(), R.color.bar_chart_monthly_amount)
                barDataSet[1].color =
                    ContextCompat.getColor(requireContext(), R.color.bar_chart_monthly_transactions)

                binding.barChart.data = BarData(barDataSet.toList())
                binding.barChart.data.barWidth = 0.5f
                binding.barChart.xAxis.valueFormatter = IndexAxisValueFormatter(
                    months
                )
                binding.barChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
                binding.barChart.xAxis.granularity = 1f
                binding.barChart.xAxis.setDrawGridLines(false)
                binding.barChart.axisLeft.axisMinimum = 0f
                binding.barChart.axisRight.axisMinimum = 0f
                binding.barChart.xAxis.axisMaximum = 11.1f

                binding.barChart.animate()
            }

            "Yearly" -> {

                var years: ArrayList<String> = arrayListOf()
                viewModel.years.observe(viewLifecycleOwner) {
                    years = arrayListOf()
                    it.forEach { year ->
                        years.add(year.toString())
                    }
                }

                viewModel.fetchYearlyAmountData()
                viewModel.fetchYearlyTransactionsData()

                var barAmountEntries: MutableList<BarEntry> = mutableListOf()
                viewModel.yearlyAmountData.observe(viewLifecycleOwner) {
                    barAmountEntries = mutableListOf()
                    it.forEachIndexed { index, amount ->
                        barAmountEntries.add(
                            BarEntry(
                                index.toFloat(),
                                amount.toFloat()
                            )
                        )
                    }
                }

                var barTransactionsEntries: MutableList<BarEntry> = mutableListOf()
                viewModel.yearlyTransactionData.observe(viewLifecycleOwner) {
                    barTransactionsEntries = mutableListOf()
                    it.forEachIndexed { index, transactions ->
                        barTransactionsEntries.add(
                            BarEntry(
                                index.toFloat(),
                                transactions.toFloat()
                            )
                        )

                    }
                }

                val barDataSet: ArrayList<BarDataSet> = arrayListOf()
                barDataSet.add(
                    BarDataSet(
                        barAmountEntries,
                        "Amount"
                    )
                )

                barDataSet.add(
                    BarDataSet(
                        barTransactionsEntries,
                        "Transactions"
                    )
                )

                barDataSet[0].color =
                    ContextCompat.getColor(requireContext(), R.color.bar_chart_monthly_amount)
                barDataSet[1].color =
                    ContextCompat.getColor(requireContext(), R.color.bar_chart_monthly_transactions)

                binding.barChart.data = BarData(barDataSet.toList())
                binding.barChart.data.barWidth = 0.5f
                binding.barChart.xAxis.valueFormatter = IndexAxisValueFormatter(
                    years
                )
                binding.barChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
                binding.barChart.xAxis.granularity = 1f
                binding.barChart.xAxis.setDrawGridLines(false)
                binding.barChart.axisLeft.axisMinimum = 0f
                binding.barChart.axisRight.axisMinimum = 0f
                binding.barChart.xAxis.axisMaximum = 11.1f
                binding.barChart.animate()
            }


        }
    }

}