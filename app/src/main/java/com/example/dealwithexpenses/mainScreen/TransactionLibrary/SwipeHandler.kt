package com.example.dealwithexpenses.mainScreen.TransactionLibrary

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.dealwithexpenses.R
import com.kevincodes.recyclerview.ItemDecorator
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator

abstract class SwipeHandler() : ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT){
    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    @SuppressLint("ResourceAsColor")
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
            backgroundColorFromStartToEnd = R.color.swipe_right,
            iconResIdFromStartToEnd = R.drawable.swipe_complete,
            textFromStartToEnd = "Complete",
            textSizeFromStartToEnd = 10f,

            backgroundColorFromEndToStart = R.color.swipe_left,
            iconResIdFromEndToStart = R.drawable.swipe_delete,
            textFromEndToStart = "Delete",
            textSizeFromEndToStart = 10f,
        )

        super.onChildDraw(c, recyclerView, viewHolder, dX/3 , dY, actionState, isCurrentlyActive)
    }
}