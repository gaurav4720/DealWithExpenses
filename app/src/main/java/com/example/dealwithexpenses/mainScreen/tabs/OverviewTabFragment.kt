package com.example.dealwithexpenses.mainScreen.tabs

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dealwithexpenses.entities.TransactionCategory
import com.example.dealwithexpenses.entities.TransactionType
import com.example.dealwithexpenses.mainScreen.viewModels.MainScreenViewModel
import com.example.dealwithexpenses.R
import com.example.dealwithexpenses.databinding.FragmentOverviewTabBinding
import com.example.dealwithexpenses.entities.TransactionMode
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import java.util.*
import kotlin.collections.ArrayList

class OverviewTabFragment : Fragment() {

    private lateinit var viewModel: MainScreenViewModel
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var binding: FragmentOverviewTabBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        //initialising binding, viewModel and firebase
        binding = FragmentOverviewTabBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(MainScreenViewModel::class.java)
        sharedPreferences = activity?.getSharedPreferences("user_auth", Context.MODE_PRIVATE)!!

        val userId= sharedPreferences.getString("user_id", "")!!

        //setting the user id of viewModel after getting it through firebase
        viewModel.setUserID(userId)

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
                    //ViewModel will set the choice according to the option chosen
                    viewModel.setChoice(pieChartChoices[position])
                    viewModel.choice.observe(viewLifecycleOwner) {
                        // pie chart will be set according to the choice
                        setPieChart(it)
                    }
                }
                //on nothing selected, keep the selection fixed to be Category
                override fun onNothingSelected(parent: AdapterView<*>?) {
                    setPieChart("Category")
                }
            }

        // years to be shown when user has done at least one transaction
        var barChartYears: MutableList<String> = mutableListOf()
        viewModel.years.observe(viewLifecycleOwner) {
            barChartYears= mutableListOf()
            it.forEach { year ->
                //adding every such year into the list
                barChartYears.add(year.toString())
            }
            //creating the array adapter for bar graph choices
            val barChartAdapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                barChartYears
            )

            //storing the created adapter in the adapter of the spinner
            binding.barChartSpinner.adapter = barChartAdapter
        }

        //initially mode is set to be yearly
        var barChartMode = "Yearly"
        //considering year is -1 as it is not fixed in yearly mode
        var year: Int = -1
        //whether the switch is activated or not
        var isActivated = false
        binding.barChartSpinner.isEnabled = isActivated

        binding.switch1.setOnClickListener {
            //change the value of isActivated whenever user presses the switch
            isActivated= !isActivated
            // bar chart spinner will only be shown if switch is activated
            binding.barChartSpinner.isEnabled = isActivated
            //barchart mode will be yearly if switch is activated, otherwise monthly
            barChartMode = if (isActivated) "Monthly" else "Yearly"

            //year= -1 if mode= yearly, otherwise get the instance of the year
            year = if (barChartMode == "Yearly") {
                -1
            } else {
                Calendar.getInstance().get(Calendar.YEAR)
            }
            //bar chart is set according to the mode selected
            setBarChart(barChartMode, year)
        }

        //bar chart set according to the mode selected
        setBarChart(barChartMode, year)

        // whenever user will click an item from the drop down menu
        binding.barChartSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    //ViewModel will set the year according to the option chosen
                    year= barChartYears[binding.barChartSpinner.selectedItemPosition].toInt()
                    //bar chart is set according to the year selected
                    setBarChart(barChartMode, year)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }
            }

        // Inflate the layout for this fragment
        return binding.root
    }

    //function to set the pie chart according to the choice selected
    private fun addDataToPieChart(pieDataSet: PieDataSet) {
        //setting the data of the pie chart
        val pieData = PieData(pieDataSet)
        binding.pieChart.data = pieData
        binding.pieChart.invalidate()
        binding.pieChart.description.isEnabled = false
        binding.pieChart.legend.isEnabled = false

        //setting the animation of the pie chart
        binding.pieChart.animateXY(1000, 1000)
        binding.pieChart.animate().alpha(1f).duration = 1000
    }

    fun setPieChart(pieChartMode: String) {

        val colors: Array<Int>
        var pieEntries: ArrayList<PieEntry>

        when (pieChartMode) {
            //if choice is category, pie chart will be set according to the category-wise data
            "Category" -> {
                //setting the colors of the pie chart
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

                val entries= arrayListOf<String>()
                TransactionCategory.values().forEach {
                    entries.add(it.name)
                }

                val adapter= LegendAdapter(colors,entries)
                binding.referneceBox.adapter= adapter
                binding.referneceBox.layoutManager= LinearLayoutManager(requireContext())

                // adding the data to the pie chart
                viewModel.categoryInfoList.observe(viewLifecycleOwner) {
                    pieEntries = arrayListOf()
                    TransactionCategory.values().forEachIndexed { index, transactionCategory ->
                        val amount= it[transactionCategory] ?: 0.0 // if no amount is found, set it to 0.0
                        pieEntries.add(
                            PieEntry(
                                amount.toFloat(),
                                colors[index]
                            )
                        )
                    }
                    //setting the data of the category label
                    val pieDataSet = PieDataSet(pieEntries, "Categories")
                    pieDataSet.colors = colors.map {
                        ContextCompat.getColor(requireContext(), it)
                    }
                    //function called to add the pie data chart
                    addDataToPieChart(pieDataSet)
                }

            }

            //if choice is type, pie chart will be set according to the type-wise data
            "Type" -> {
                //setting the colors of the pie chart
                colors = arrayOf(
                    R.color.type_income,
                    R.color.type_expense
                )

                val entries= arrayListOf<String>()
                TransactionType.values().forEach {
                    entries.add(it.name)
                }

                val adapter= LegendAdapter(colors,entries)
                binding.referneceBox.adapter= adapter
                binding.referneceBox.layoutManager= LinearLayoutManager(requireContext())
                // adding the data to the pie data set
                viewModel.typesInfoList.observe(viewLifecycleOwner) {
                    pieEntries = arrayListOf()
                    TransactionType.values().forEachIndexed { index, transactionType ->
                        val amount= it[transactionType] ?: 0.0 // if no amount is found, set it to 0.0
                        pieEntries.add(
                            PieEntry(
                                amount.toFloat(),
                                colors[index]
                            )
                        )
                    }
                    //setting the data of the type label
                    val pieDataSet = PieDataSet(pieEntries, "Types")
                    pieDataSet.colors = colors.map {
                        ContextCompat.getColor(requireContext(), it)
                    }
                    //function called to add the pie data chart
                    addDataToPieChart(pieDataSet)
                }
            }

            //if choice is mode, pie chart will be set according to the mode-wise data
            "Mode" -> {
                //setting the colors of the pie chart
                colors = arrayOf(
                    R.color.mode_cash,
                    R.color.mode_credit_card,
                    R.color.mode_debit_card,
                )

                val entries= arrayListOf<String>()
                TransactionMode.values().forEach {
                    entries.add(it.name)
                }

                val adapter= LegendAdapter(colors,entries)
                binding.referneceBox.adapter= adapter
                binding.referneceBox.layoutManager= LinearLayoutManager(requireContext())

                // adding the data to the pie data set
                viewModel.transModesInfoList.observe(viewLifecycleOwner) {
                    pieEntries = arrayListOf()
                    TransactionMode.values().forEachIndexed { index, transactionMode ->
                        val amount= it[transactionMode] ?: 0.0  // if no amount is found, set it to 0.0
                        pieEntries.add(
                            PieEntry(
                                amount.toFloat(),
                                colors[index]
                            )
                        )
                    }
                    //setting the data of the mode label
                    val pieDataSet = PieDataSet(pieEntries, "Modes")
                    pieDataSet.colors = colors.map {
                        ContextCompat.getColor(requireContext(), it)
                    }
                    //function called to add the pie data chart
                    addDataToPieChart(pieDataSet)
                }

            }
        }
    }

    //function to set the bar chart according to the choice selected
    private fun addDataToBarChart(barDataSet: ArrayList<BarDataSet> = arrayListOf(), yearsOrMonths: ArrayList<String> = arrayListOf()) {
        binding.barChart.data = BarData(barDataSet.toList())
        //setting the x-axis of the bar chart and width of the bar
        binding.barChart.data.barWidth = 0.5f
        binding.barChart.xAxis.valueFormatter = IndexAxisValueFormatter(
            yearsOrMonths
        )
        //setting the position of the bar chart
        binding.barChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        binding.barChart.xAxis.granularity = 1f
        binding.barChart.xAxis.setDrawGridLines(false)
        //setting the minimum axis value of the bar chart
        binding.barChart.axisLeft.axisMinimum = 0f
        binding.barChart.axisRight.axisMinimum = 0f
        binding.barChart.xAxis.axisMaximum = 11.1f
        //setting the animation of the bar chart
        binding.barChart.invalidate()
        binding.barChart.animateXY(1000, 1000)
        binding.barChart.animate().alpha(1f).duration = 1000
    }

    //function to set the bar chart according to the choice selected
    fun setBarChart(barChartMode: String, year: Int = -1) {
        when (barChartMode) {
            //if choice is month, bar chart will be set according to the month-wise data
            "Monthly" -> {
                //arrays of the month name to be displayed on the x-axis
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

                //
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

class LegendAdapter(val colorList: Array<Int>, val legendList: ArrayList<String>): RecyclerView.Adapter<LegendAdapter.Holder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.pie_chart_elements, parent, false)
        return Holder(view)
    }

    override fun getItemCount(): Int {
        return colorList.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bindView(colorList[position], legendList[position])
    }

    class Holder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val circle= itemView.findViewById<ImageView>(R.id.circular_mark)
        val item= itemView.findViewById<TextView>(R.id.item)
        fun bindView(color: Int, legend: String) {
            circle.setColorFilter(ContextCompat.getColor(circle.context, color))
            item.text= legend
        }
    }
}