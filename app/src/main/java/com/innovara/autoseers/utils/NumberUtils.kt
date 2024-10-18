package com.innovara.autoseers.utils

import android.icu.number.NumberFormatter
import java.util.Locale

fun String.toFormattedNumber(): String {
    val intValue = this.toIntOrNull()
    return intValue?.let {
        NumberFormatter.with().locale(Locale.ENGLISH).format(it).toString()
    } ?: this
}

fun Number.format() =
    NumberFormatter.with().locale(Locale.ENGLISH).format(this)