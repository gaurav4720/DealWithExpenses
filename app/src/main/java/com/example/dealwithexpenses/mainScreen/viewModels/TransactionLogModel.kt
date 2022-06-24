package com.example.dealwithexpenses.mainScreen.viewModels

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.example.dealwithexpenses.mainScreen.TransactionLibrary.TransactionListAdapter
import com.example.dealwithexpenses.R

@EpoxyModelClass
abstract class TransactionLogModel : EpoxyModelWithHolder<TransactionLogModel.Holder>() {

    @EpoxyAttribute
    lateinit var title: String

    @EpoxyAttribute
    lateinit var adapter: TransactionListAdapter

    override fun getDefaultLayout(): Int {
        return R.layout.transaction_log_item
    }

    override fun bind(holder: Holder) {
        super.bind(holder)
        holder.heading.text = title
        holder.recyclerView.adapter = adapter

        holder.recyclerView.visibility = if (title == "Completed")
            View.VISIBLE
        else
            View.GONE

        holder.arrowButton.setOnClickListener {
            if (holder.recyclerView.visibility == View.GONE) {
                holder.recyclerView.visibility = View.VISIBLE
                holder.arrowButton.setImageResource(R.drawable.ic_baseline_collapse_up)
            } else {
                holder.recyclerView.visibility = View.GONE
                holder.arrowButton.setImageResource(R.drawable.ic_baseline_expand_down)
            }
        }

    }

    inner class Holder : EpoxyHolder() {
        lateinit var cardView: CardView
        lateinit var heading: TextView
        lateinit var arrowButton: ImageView
        lateinit var recyclerView: RecyclerView

        override fun bindView(itemView: View) {
            cardView = itemView.findViewById(R.id.card_view)
            heading = itemView.findViewById(R.id.heading)
            arrowButton = itemView.findViewById(R.id.expand_collapse)
            recyclerView = itemView.findViewById(R.id.recycler_view)
        }

    }
}