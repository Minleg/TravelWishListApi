package com.bignerdranch.android.travelwishlist

// status of request - success, server error, network error
// data, if there is any
// message for user, if needed

enum class ApiStatus { // enumeration is a collection of constants
    SUCCESS,
    SERVER_ERROR,
    NETWORK_ERROR
}

data class ApiResult<out T>(val status: ApiStatus, val data: T?, val message: String?) {
}