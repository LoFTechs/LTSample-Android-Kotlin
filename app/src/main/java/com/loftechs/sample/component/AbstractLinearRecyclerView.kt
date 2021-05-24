package com.loftechs.sample.component

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

abstract class AbstractLinearRecyclerView(
        context: Context,
        attrs: AttributeSet?,
        val orientation: Int
) : RecyclerView(context, attrs) {

    init {
        layoutManager = LinearLayoutManager(context, orientation, false)
    }
}