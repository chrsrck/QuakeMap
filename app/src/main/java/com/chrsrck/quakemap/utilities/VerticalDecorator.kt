package com.chrsrck.quakemap.utilities

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.chrsrck.quakemap.R

class VerticalDecorator(private val verticalMargin: Int) : RecyclerView.ItemDecoration() {


    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {

        val pos = parent.getChildAdapterPosition(view)
        val itemCount = parent.adapter?.itemCount ?: 0
        val isFirst = pos == 0
        val isLast = pos == (itemCount - 1)

        outRect.top =
                if (isFirst)
                    verticalMargin
                else
                    verticalMargin / 2

        outRect.bottom =
                if (isLast)
                    verticalMargin
                else
                    verticalMargin / 2

    }
}