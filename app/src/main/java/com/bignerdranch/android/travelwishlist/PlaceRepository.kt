package com.bignerdranch.android.travelwishlist

import android.util.Log
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class PlaceRepository {

    private val TAG = "PLACE_REPOSITORY"

    private val baseURL = "https://claraj.pythonanywhere.com/api/places/"

    private val client: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(AuthorizationHeaderInterceptor())
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(baseURL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create()) // Gson help convert JSON objects to kotlin objects
        .build()

    private val placeService = retrofit.create(PlaceService::class.java)

    suspend fun getAllPlaces(): ApiResult<List<Place>> {
        try {
            val response = placeService.getAllPlaces()

            if (response.isSuccessful) { // connected, got data back
                val places = response.body() ?: listOf()
                Log.d(TAG, "List of places {$places}")
                // Log.d(TAG, "Server created new place ${response.body()}")
                return ApiResult(ApiStatus.SUCCESS, response.body(), null)
            } else { // connected to server but server sent an error message
                Log.e(TAG, "Error fetching places from API server ${response.errorBody()}")
                return ApiResult(ApiStatus.SERVER_ERROR, null, "Error fetching places")
            }
            // TODO more error handling!
        } catch (ex: Exception) { // can't connect to server - network error
            Log.e(TAG, "Error connecting to API server", ex)
            return ApiResult(ApiStatus.NETWORK_ERROR, null, "Can't connect to server")
        }
    }

    suspend fun addPlace(place: Place): ApiResult<Place> {
        try {
            val response = placeService.addPlace(place)
            if (response.isSuccessful) {
                Log.d(TAG, "Created new place $place")
                return ApiResult(ApiStatus.SUCCESS, null, "Place created!")
            } else {
                Log.e(TAG, "Error creating new place ${response.errorBody()}")
                return ApiResult(
                    ApiStatus.SERVER_ERROR,
                    null,
                    "Error adding place - is name unique ?",
                )
            }
        } catch (ex: Exception) { // can't connect to server - network error
            Log.e(TAG, "Error connecting to API server", ex)
            return ApiResult(ApiStatus.NETWORK_ERROR, null, "Can't connect to server")
        }
    }

    suspend fun updatePlace(place: Place): ApiResult<Place> {
        // todo avoid writing some error handling code again
        // todo report errors/success to user
        // - success - operation completed successfully - we may have data
        // - connected but server reported error
        // - can't connect, network error

        if (place.id == null) {
            Log.e(TAG, "Error - trying to update place with no ID")
            return ApiResult(
                ApiStatus.SERVER_ERROR,
                null,
                "Error - trying to update place with no ID",
            )
        } else {
            try {
                val response = placeService.updatePlace(place, place.id)
                if (response.isSuccessful) {
                    Log.d(TAG, "Updated new place $place")
                    return ApiResult(ApiStatus.SUCCESS, null, "Place Updated!")
                } else {
                    Log.e(TAG, "Error updating new place ${response.errorBody()}")
                    return ApiResult(
                        ApiStatus.SERVER_ERROR,
                        null,
                        "Error updating place - is name unique?",
                    )
                }
            } catch (ex: Exception) { // can't connect to server - network error
                Log.e(TAG, "Error connecting to API server", ex)
                return ApiResult(ApiStatus.NETWORK_ERROR, null, "Can't connect to server")
            }
        }
    }

    suspend fun deletePlace(place: Place): ApiResult<Nothing> {
        if (place.id == null) {
            Log.e(TAG, "Error - trying to delete place with no ID")
            return ApiResult(
                ApiStatus.SERVER_ERROR,
                null,
                "Error - trying to delete place with no ID",
            )
        } else {
            try {
                val response = placeService.deletePlace(place.id)
                if (response.isSuccessful) {
                    Log.d(TAG, "Deleted new place $place")
                    return ApiResult(ApiStatus.SUCCESS, null, "Place Deleted!")
                } else {
                    Log.e(TAG, "Error updating new place ${response.errorBody()}")
                    return ApiResult(ApiStatus.SERVER_ERROR, null, "Error deleting place")
                }
            } catch (ex: Exception) { // can't connect to server - network error
                Log.e(TAG, "Error connecting to API server", ex)
                return ApiResult(ApiStatus.NETWORK_ERROR, null, "Can't connect to server")
            }
        }
    }
}
