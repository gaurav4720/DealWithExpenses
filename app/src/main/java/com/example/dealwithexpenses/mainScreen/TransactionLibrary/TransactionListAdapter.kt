package com.example.dealwithexpenses.mainScreen.TransactionLibrary


import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.dealwithexpenses.R
import com.example.dealwithexpenses.entities.Transaction
import com.example.dealwithexpenses.entities.TransactionStatus
import com.example.dealwithexpenses.entities.TransactionType
import com.example.dealwithexpenses.mainScreen.viewModels.TransactionViewModel
import pl.droidsonroids.gif.GifImageView
import java.text.SimpleDateFormat

class TransactionListAdapter(var transactionList: MutableList<Transaction>, val fragment: Fragment, private var listener: (Long)->Unit, val viewModel: TransactionViewModel, val context: Context)  : RecyclerView.Adapter<TransactionListAdapter.holder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): holder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.transaction_item,parent,false)
        return holder(view)
    }

    override fun onBindViewHolder(holder: holder, position: Int) {
        holder.bindview(transactionList[position])
    }

    override fun getItemCount(): Int {
        return transactionList.size
    }

    inner class holder(val view: View) : RecyclerView.ViewHolder(view){
        init {
            val viewDetails: TextView= view.findViewById(R.id.profitOrLoss)
            viewDetails.setOnClickListener {
                listener.invoke(transactionList[adapterPosition].trans_id)
            }
        }

        val layout: ConstraintLayout= view.findViewById(R.id.item)
        val title: TextView= view.findViewById(R.id.year)
        val mode: TextView= view.findViewById(R.id.month_transaction)
        val amount: TextView= view.findViewById(R.id.amount)
        val date: TextView= view.findViewById(R.id.GainOrLoss)
        private val typeHere: View= view.findViewById(R.id.expenseOrIncome)
        val image: ImageView= view.findViewById(R.id.categorized_image)

        fun bindview(transaction: Transaction){
            val isUpcoming = transaction.transactionDate.time > System.currentTimeMillis()
            if (transaction.transactionStatus == TransactionStatus.COMPLETED)
                layout.setBackgroundColor(status_color[0])
            else if (isUpcoming)
                layout.setBackgroundColor(status_color[1])
            else if (transaction.transactionStatus == TransactionStatus.PENDING)
                layout.setBackgroundColor(status_color[2])

            title.text= transaction.title
            mode.text= transaction.transactionMode.name
            amount.text= transaction.transactionAmount.toString()
            date.text= SimpleDateFormat("dd-MM-yyyy").format(transaction.transactionDate)

            val type= transaction.transactionType
            if(type==TransactionType.INCOME)
                typeHere.setBackgroundColor(type_color[0])
            else
                typeHere.setBackgroundColor(type_color[1])

            image.setImageDrawable(ContextCompat.getDrawable(image.context,images[transaction.transactionCategory.ordinal]))
        }
    }

    fun deleteTransaction(position: Int, transactionList2: MutableList<Transaction>){
        val layout= LayoutInflater.from(context).inflate(R.layout.fragment_transaction_complete,null)
        "Transaction Deleted :)".also { layout.findViewById<TextView>(R.id.text_transaction).text = it }
        layout.findViewById<GifImageView>(R.id.gif_complete).setBackgroundResource(R.drawable.deleted_transaction)
        val builder = AlertDialog.Builder(context)
            .setTitle("Delete Transaction")
            .setMessage("Are you sure you want to delete this transaction?")
            .setCancelable(true)
            .setPositiveButton("YES"){ _,_ ->
                viewModel.delete(transactionList2[position])
                transactionList2.removeAt(position)
                transactionList= transactionList2
                notifyDataSetChanged()
                val dialog= AlertDialog.Builder(context)
                    .setView(layout)
                    .setNegativeButton("OK"){_,_ ->

                    }
                dialog.create().show()
            }
            .setNegativeButton("NO"){_,_ ->

            }
        builder.create().show()
    }

    fun completeTransaction(position: Int, transactionList2: MutableList<Transaction>){
        val layout= LayoutInflater.from(context).inflate(R.layout.fragment_transaction_complete,null)
        "Transaction Completed :)".also { layout.findViewById<TextView>(R.id.text_transaction).text = it }
        val builder = AlertDialog.Builder(context)
            .setTitle("Complete Transaction")
            .setMessage("Mark this transaction as completed?")
            .setCancelable(true)
            .setPositiveButton("YES"){ _,_ ->
                viewModel.complete(transactionList2[position])
                transactionList2.removeAt(position)
                transactionList= transactionList2
                notifyDataSetChanged()
                val dialog= AlertDialog.Builder(context)
                    .setView(layout)
                    .setNegativeButton("OK"){_,_ ->

                    }
                dialog.create().show()
            }
            .setNegativeButton("NO"){_,_ ->

            }
        builder.create().show()
    }

    val type_color= arrayListOf(
        R.color.type_income,
        R.color.type_expense
    )

    val mode_color= arrayListOf(
        R.color.mode_cash,
        R.color.mode_credit_card,
        R.color.mode_debit_card
    )

    val status_color = arrayListOf(
        R.color.status_completed,
        R.color.status_upcoming,
        R.color.status_pending,
    )

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