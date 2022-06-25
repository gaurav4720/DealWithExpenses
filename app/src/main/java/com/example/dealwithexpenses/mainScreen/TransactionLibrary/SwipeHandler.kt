package com.example.dealwithexpenses.mainScreen.TransactionLibrary

import android.graphics.Canvas
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.dealwithexpenses.R
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator

abstract class SwipeHandler : ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT){
    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        RecyclerViewSwipeDecorator.Builder(c,recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            .addSwipeLeftBackgroundColor(R.color.mode_credit_card)
            .addSwipeLeftActionIcon(R.drawable.bill)
            .addSwipeRightBackgroundColor(R.color.mode_debit_card)
            .addSwipeRightActionIcon(R.drawable.bill)
            .create().decorate()

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }
}