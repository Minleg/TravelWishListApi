package com.bignerdranch.android.travelwishlist

import java.util.*

data class Place(
    val name: String,
    val reason: String? = null,
    var starred: Boolean = false,
    val id: Int? = null,
)
