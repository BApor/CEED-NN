package com.example.ceed_nn.util

import kotlin.math.pow
import kotlin.math.roundToInt

object NumUtil {
    fun floatRoundTo(num: Float, numFractionDigits: Int): Float {
        val factor = 10.0.pow(numFractionDigits.toDouble()).toFloat()
        return (num * factor).roundToInt() / factor
    }
}