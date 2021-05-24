package com.loftechs.sample.component

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager

class VerticalRecyclerView(
        context: Context,
        attrs: AttributeSet?,
) : AbstractLinearRecyclerView(context, attrs, LinearLayoutManager.VERTICAL) {

    // Enable default Divider line
    fun enableDecoration() {
        this.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
    }
}