package com.loftechs.sample.utils

import android.content.Context


object ViewUtil {
    fun dpToPx(context: Context, dp: Int): Int {
        return (dp * context.resources.displayMetrics.density + 0.5).toInt()
    }
}