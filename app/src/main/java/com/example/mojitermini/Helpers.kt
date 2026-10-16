package com.example.mojitermini

import androidx.compose.ui.graphics.Color
import java.text.SimpleDateFormat
import java.util.Date

fun String.toDate(): Date {
    val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm")
    return formatter.parse(this)!!
}

fun daysUntil(dateString: String): Int {
    val date = dateString.toDate()
    val diff = date.time - Date().time
    return (diff / 86400000).toInt()
}

val Color.Companion.lightGray: Color
    get() = Color(red = 0.9f, green = 0.9f, blue = 0.9f)
