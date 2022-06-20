package com.example.dealwithexpenses.TransactionLibrary

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.dealwithexpenses.R
import com.example.dealwithexpenses.entities.Transaction
import com.example.dealwithexpenses.entities.TransactionStatus
import com.example.dealwithexpenses.entities.TransactionType

class TransactionListAdapter(private val listener: (Long) -> Unit)  : ListAdapter<Transaction, TransactionListAdapter.holder>(diffView()) {
    val type_color= arrayListOf(
        R.color.type_income,
        R.color.type_expense
    )

    val mode_color= arrayListOf(
        R.color.mode_cash,
        R.color.mode_credit_card,
        R.color.mode_debit_card
    )

    val status_color= arrayListOf(
        R.color.status_upcoming,
        R.color.status_missed,
        R.color.status_completed
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): holder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.transaction_item,parent,false)
        return holder(view)
    }

    override fun onBindViewHolder(holder: holder, position: Int) {
        holder.bindview(getItem(position))
    }

    inner class holder(val view: View) : RecyclerView.ViewHolder(view){
        init {
            val viewDetails: TextView= view.findViewById(R.id.details_inside)
            viewDetails.setOnClickListener {
                listener.invoke(getItem(adapterPosition).trans_id)
            }
        }
        fun bindview(transaction: Transaction){
            val layout: ConstraintLayout= view.findViewById(R.id.item)
            val status= transaction.transactionStatus
            if(status==TransactionStatus.UPCOMING)
                layout.setBackgroundColor(status_color[0])
            else if(status==TransactionStatus.MISSED)
                layout.setBackgroundColor(status_color[1])
            else
                layout.setBackgroundColor(status_color[2])

            val title: TextView= view.findViewById(R.id.transaction_title)
            title.text= transaction.title

            val mode: TextView= view.findViewById(R.id.mode_transaction)
            mode.text= transaction.transactionMode.name

            val amount: TextView= view.findViewById(R.id.amount)
            amount.text= transaction.transactionAmount.toString()

            val date: TextView= view.findViewById(R.id.Date)
            date.text= transaction.transactionDate.toString()

            val typeHere: View= view.findViewById(R.id.expenseOrIncome)
            val type= transaction.transactionType
            if(type==TransactionType.INCOME)
                typeHere.setBackgroundColor(type_color[0])
            else
                typeHere.setBackgroundColor(type_color[1])

            val image: ImageView= view.findViewById(R.id.categorized_image)
            image.setImageDrawable(ContextCompat.getDrawable(image.context,images[transaction.transactionCategory.ordinal]))
        }
    }

    class diffView : DiffUtil.ItemCallback<Transaction>(){
        override fun areItemsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
            return oldItem.trans_id==newItem.trans_id
        }

        override fun areContentsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
            return oldItem==newItem
        }

    }

    companion object{
        val images= mutableListOf(
            R.drawable.food_category,
            R.drawable.clothing,
            R.drawable.entertainment_category,
            R.drawable.transport,
            R.drawable.health,
            R.drawable.education,
            R.drawable.bill,
            R.drawable.others
        )
    }
}