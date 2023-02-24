package com.bignerdranch.android.travelwishlist

import java.text.SimpleDateFormat
import java.util.*

class Place(val name: String, val reason: String, val dateAdded: Date = Date()) {
    fun formattedDate(): String {
        return SimpleDateFormat("EEE, d MMMM yyyy", Locale.getDefault()).format(dateAdded)
    }

    override fun toString(): String {
        return "$name ${formattedDate()}"
    }
}
