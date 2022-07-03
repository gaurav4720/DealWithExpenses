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
import com.example.dealwithexpenses.daoS.BarChartDetail
import com.example.dealwithexpenses.databinding.FragmentOverviewTabBinding
import com.example.dealwithexpenses.entities.TransactionMode
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
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

        //initialising binding, viewModel and firebase
        binding = FragmentOverviewTabBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(MainScreenViewModel::class.java)
        firebaseAuth = FirebaseAuth.getInstance()

        //setting the user id of viewModel after getting it through firebase
        viewModel.setUserID(firebaseAuth.currentUser?.uid.toString())

        //choices for the pie chart data, category-wise, type-wise or mode-wise
        val pieChartChoices = arrayOf(
            "Category",
            "Type",
            "Mode",
        )

        //creating the array adapter for this drop down menu
        val pieChartAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            pieChartChoices
        )

        //storing the created adapter in the adapter of the spinner
        binding.pieChartSpinner.adapter = pieChartAdapter

        //default choice is chosen to be category, i.e. the first choice
        setPieChart("Category")

        // whenever user will click an item from the drop down menu
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

    fun addDataToPieChart(pieDataSet: PieDataSet) {
        val pieData = PieData(pieDataSet)
        binding.pieChart.data = pieData

        binding.pieChart.description.isEnabled = false
        binding.pieChart.legend.isEnabled = false

        binding.pieChart.animate()
    }

    fun setPieChart(pieChartMode: String) {

        val colors: Array<Int>
        var pieEntries: ArrayList<PieEntry>

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

                viewModel.categoryInfoList.observe(viewLifecycleOwner) {
                    pieEntries = arrayListOf()
                    TransactionCategory.values().forEachIndexed { index, transactionCategory ->
                        val amount= it[transactionCategory] ?: 0.0
                        pieEntries.add(
                            PieEntry(
                                amount.toFloat(),
                                colors[index]
                            )
                        )
                    }
                    val pieDataSet = PieDataSet(pieEntries, "Categories")
                    pieDataSet.colors = colors.map {
                        ContextCompat.getColor(requireContext(), it)
                    }
                    addDataToPieChart(pieDataSet)
                }

            }

            "Type" -> {
                colors = arrayOf(
                    R.color.type_income,
                    R.color.type_expense
                )

                viewModel.typesInfoList.observe(viewLifecycleOwner) {
                    pieEntries = arrayListOf()
                    TransactionType.values().forEachIndexed { index, transactionType ->
                        val amount= it[transactionType] ?: 0.0
                        pieEntries.add(
                            PieEntry(
                                amount.toFloat(),
                                colors[index]
                            )
                        )
                    }
                    val pieDataSet = PieDataSet(pieEntries, "Types")
                    pieDataSet.colors = colors.map {
                        ContextCompat.getColor(requireContext(), it)
                    }
                    addDataToPieChart(pieDataSet)
                }
            }

            "Mode" -> {
                colors = arrayOf(
                    R.color.mode_cash,
                    R.color.mode_credit_card,
                    R.color.mode_debit_card,
                )

                viewModel.transModesInfoList.observe(viewLifecycleOwner) {
                    pieEntries = arrayListOf()
                    TransactionMode.values().forEachIndexed { index, transactionMode ->
                        val amount= it[transactionMode] ?: 0.0
                        pieEntries.add(
                            PieEntry(
                                amount.toFloat(),
                                colors[index]
                            )
                        )
                    }
                    val pieDataSet = PieDataSet(pieEntries, "Modes")
                    pieDataSet.colors = colors.map {
                        ContextCompat.getColor(requireContext(), it)
                    }
                    addDataToPieChart(pieDataSet)
                }

            }
        }
    }

    fun addDataToBarChart(barDataSet: ArrayList<BarDataSet> = arrayListOf(), yearsOrMonths: ArrayList<String> = arrayListOf()) {
        binding.barChart.data = BarData(barDataSet.toList())
        binding.barChart.data.barWidth = 0.5f
        binding.barChart.xAxis.valueFormatter = IndexAxisValueFormatter(
            yearsOrMonths
        )
        binding.barChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        binding.barChart.xAxis.granularity = 1f
        binding.barChart.xAxis.setDrawGridLines(false)
        binding.barChart.axisLeft.axisMinimum = 0f
        binding.barChart.axisRight.axisMinimum = 0f
        binding.barChart.xAxis.axisMaximum = 11.1f
        binding.barChart.animate()
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

                var barAmountEntries: MutableList<BarEntry> = mutableListOf()
                viewModel.barChartDetailByMonth.observe(viewLifecycleOwner) {
                    barAmountEntries = arrayListOf()
                    val monthYear= year*100
                    months.forEachIndexed { index, month ->
                        var amount= it[monthYear+index+1]?.amount?.toDouble()
                        if(amount==null)
                            amount= 0.0
                        barAmountEntries.add(
                            BarEntry(
                                index.toFloat(),
                                amount.toFloat()
                            )
                        )
                    }
                }

                var barTransactionsEntries: MutableList<BarEntry> = mutableListOf()
                viewModel.barChartDetailByMonth.observe(viewLifecycleOwner) {
                    barTransactionsEntries = mutableListOf()
                    val monthYear= year*100
                    months.forEachIndexed { index, month ->
                        var transactions= it[monthYear+index+1]?.transactionsCount?.toDouble()
                        if(transactions==null)
                            transactions= 0.0
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

                addDataToBarChart(barDataSet, months)
            }

            "Yearly" -> {

                var years: ArrayList<String> = arrayListOf()
                viewModel.years.observe(viewLifecycleOwner) {
                    years = arrayListOf()
                    it.forEach { year ->
                        years.add(year.toString())
                    }
                }

                var barAmountEntries: MutableList<BarEntry> = mutableListOf()
                viewModel.barChartDetailByYear.observe(viewLifecycleOwner) {
                    barAmountEntries = arrayListOf()
                    years.forEachIndexed { index, year ->
                        var amount= it[year.toInt()]?.amount?.toDouble()
                        if(amount==null)
                            amount= 0.0
                        barAmountEntries.add(
                            BarEntry(
                                index.toFloat(),
                                amount!!.toFloat()
                            )
                        )
                    }
                }

                var barTransactionsEntries: MutableList<BarEntry> = mutableListOf()
                viewModel.barChartDetailByYear.observe(viewLifecycleOwner) {
                    barTransactionsEntries = mutableListOf()
                    years.forEachIndexed { index, year ->
                        var transactions = it[year.toInt()]?.transactionsCount?.toDouble()
                        if (transactions == null)
                            transactions = 0.0
                        barTransactionsEntries.add(
                            BarEntry(
                                index.toFloat(),
                                transactions!!.toFloat()
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

                addDataToBarChart(barDataSet, years)
            }


        }
    }

}