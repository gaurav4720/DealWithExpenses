package com.example.dealwithexpenses.mainScreen.transactionLibrary

import android.content.Context
import android.graphics.Canvas
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.dealwithexpenses.R
import com.kevincodes.recyclerview.ItemDecorator

abstract class SwipeHandler(val context: Context) : ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT){
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
        ItemDecorator.Builder(
            c,
            recyclerView,
            viewHolder,
            dX,
            actionState
        ).set(
            backgroundColorFromStartToEnd = ContextCompat.getColor(context,
                R.color.transaction_completed
            ),
            iconResIdFromStartToEnd = R.drawable.ic_baseline_check_circle_outline_24,
            iconTintColorFromStartToEnd = ContextCompat.getColor(context,
                R.color.swipe_right
            ),
            textFromStartToEnd = "Complete",
            textSizeFromStartToEnd = 10f,

            backgroundColorFromEndToStart = ContextCompat.getColor(context,
                R.color.transaction_completed
            ),
            iconResIdFromEndToStart = R.drawable.ic_baseline_delete_forever_24,
            iconTintColorFromEndToStart = ContextCompat.getColor(context,
                R.color.swipe_left
            ),
            textFromEndToStart = "Delete",
            textSizeFromEndToStart = 10f,
        )

        super.onChildDraw(c, recyclerView, viewHolder, dX/3 , dY, actionState, isCurrentlyActive)
    }
}