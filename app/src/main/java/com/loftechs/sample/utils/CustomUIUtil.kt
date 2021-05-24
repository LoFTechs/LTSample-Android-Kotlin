package com.loftechs.sample.utils

import android.content.Context
import kotlin.math.roundToInt

object CustomUIUtil {
    /**
     * Convert dp to px
     */
    fun convertDpToPixel(context: Context, dp: Int): Int {
        return (dp * getDensity(context)).roundToInt()
    }

    private fun getDensity(context: Context): Float {
        return context.resources.displayMetrics.density
    }
}