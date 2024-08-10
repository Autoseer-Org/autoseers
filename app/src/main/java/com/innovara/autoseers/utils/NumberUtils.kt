package com.innovara.autoseers.utils

import android.icu.number.NumberFormatter
import java.util.Locale

fun String.toFormattedNumber() =
    NumberFormatter.with().locale(Locale.ENGLISH).format(this.toInt()).toString()

fun Number.format() =
    NumberFormatter.with().locale(Locale.ENGLISH).format(this)