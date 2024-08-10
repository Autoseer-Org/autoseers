package com.innovara.autoseers.utils
import android.icu.text.DateFormat
import android.icu.text.SimpleDateFormat
import java.util.Locale

val dateInstance = DateFormat.getDateInstance(DateFormat.MEDIUM)

fun String.toDate(): String {
    val format = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSS'Z'"
    val dateFormat = SimpleDateFormat(format, Locale.ENGLISH)
    val date = dateFormat.parse(this)
    return dateInstance.format(date)
}