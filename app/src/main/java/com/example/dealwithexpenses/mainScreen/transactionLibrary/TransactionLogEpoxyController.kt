package com.example.dealwithexpenses.mainScreen.transactionLibrary

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.view.menu.MenuView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.epoxy.AsyncEpoxyController
import com.example.dealwithexpenses.R
import com.example.dealwithexpenses.entities.Transaction
import com.example.dealwithexpenses.mainScreen.MainScreenFragmentDirections
import com.example.dealwithexpenses.mainScreen.viewModels.TransactionViewModel
import com.example.dealwithexpenses.mainScreen.viewModels.transactionLog


class TransactionLogEpoxyController(
    val fragment: Fragment,
    val context: Context,
    val viewModel: TransactionViewModel
) : RecyclerView.Adapter<TransactionLogEpoxyController.Holder>() {
    var transactionLog = mutableListOf<TransactionLogItem>()
        set(value) {
            field = value
        }

    inner class Holder(val view: View) : RecyclerView.ViewHolder(view){
        var cardView: CardView = itemView.findViewById(R.id.card_view)
        var heading: TextView = itemView.findViewById(R.id.heading)
        var arrowButton: ImageView = itemView.findViewById(R.id.expand_collapse)
        var recyclerView: RecyclerView = itemView.findViewById(R.id.recycler_view)

        fun bindView(item: TransactionLogItem){

            val adapter =
                TransactionListAdapter(item.transactionLog, fragment, listener, viewModel, context)

            val swipeHandler = object : SwipeHandler() {
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    if (direction == ItemTouchHelper.LEFT) {
                        adapter.deleteTransaction(viewHolder.absoluteAdapterPosition, item.transactionLog)
                    } else if (direction == ItemTouchHelper.RIGHT) {
                        adapter.completeTransaction(viewHolder.absoluteAdapterPosition, item.transactionLog)
                    }
                }
            }

            val itemTouchHelper = ItemTouchHelper(swipeHandler)


            heading.text = item.title
            recyclerView.adapter = adapter
            recyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(recyclerView.context)
            itemTouchHelper.attachToRecyclerView(recyclerView)

            recyclerView.visibility = if (item.title == "Completed")
                View.VISIBLE
            else
                View.GONE

            arrowButton.setOnClickListener {
                if (recyclerView.visibility == View.GONE) {
                    recyclerView.visibility = View.VISIBLE
                    arrowButton.setImageResource(R.drawable.ic_baseline_collapse_up)
                } else {
                    recyclerView.visibility = View.GONE
                    arrowButton.setImageResource(R.drawable.ic_baseline_expand_down)
                }
            }
        }
    }

    private val listener: (id: Long) -> Unit = {
        fragment.findNavController().navigate(
            MainScreenFragmentDirections.actionMainScreenFragmentToTransactionDetailFragment(it,1)
        )
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.transaction_log_item, parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bindView(transactionLog[position])
    }

    override fun getItemCount(): Int {
        return transactionLog.size
    }

}

data class TransactionLogItem(
    val title: String = "",
    var transactionLog: MutableList<Transaction> = mutableListOf(),

    )