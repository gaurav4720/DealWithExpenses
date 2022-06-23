package com.example.dealwithexpenses.mainScreen.TransactionLibrary

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.dealwithexpenses.R
import com.example.dealwithexpenses.mainScreen.MainScreenFragmentDirections

class MonthAdapter(val MonthCardList: MutableList<MonthCardDetail> , val fragment: Fragment): RecyclerView.Adapter<MonthAdapter.viewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.epoxy_months,parent,false)
        return viewHolder(view)
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {

    }

    override fun getItemCount(): Int {
        return MonthCardList.size
    }

    inner class viewHolder(view: View): RecyclerView.ViewHolder(view){
        val view: CardView= view.findViewById(R.id.idcardView)
        val layout: ConstraintLayout= view.findViewById(R.id.month_card)
        val monthName: TextView= view.findViewById(R.id.month_transaction)
        val gainOrLoss: TextView= view.findViewById(R.id.GainOrLoss)
        val profit: TextView= view.findViewById(R.id.profitOrLoss)
        val balance: TextView= view.findViewById(R.id.net_balance)
        val viewDetails: TextView= view.findViewById(R.id.detailsInside)

        init {
            viewDetails.setOnClickListener {
                fragment.findNavController().navigate(MainScreenFragmentDirections.actionMainScreenFragmentToMonthScreenFragment(MonthCardList[adapterPosition].monthYear))
            }
        }
        fun bindView(monthCardDetail: MonthCardDetail){
            monthName.text = monthCardDetail.month
            gainOrLoss.text= if(monthCardDetail.amount<0){
                "Loss"
            } else {
                "Gain"
            }
            profit.text= monthCardDetail.profit.toString()
            balance.text= monthCardDetail.amount.toString()

        }
    }
    val type_color= arrayListOf(
        R.color.type_income,
        R.color.type_expense
    )

    class diffView : DiffUtil.ItemCallback<MonthCardDetail>(){
        override fun areItemsTheSame(oldItem: MonthCardDetail, newItem: MonthCardDetail): Boolean {
            return oldItem.month==newItem.month
        }

        override fun areContentsTheSame(
            oldItem: MonthCardDetail,
            newItem: MonthCardDetail
        ): Boolean {
            return oldItem==newItem
        }

    }
}