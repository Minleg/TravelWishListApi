package com.bignerdranch.android.travelwishlist

import android.util.Log
import okhttp3.OkHttpClient
import retrofit2.Response
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

    suspend fun <T : Any> apiCall(
        apiCallFunction: suspend () -> Response<T>,
        successMessage: String?,
        failMessage: String?,
    ): ApiResult<T> {
        try {
//            val response = placeService.getAllPlaces() // replace with a call to the function definition that's provided as an argument
            val response = apiCallFunction.invoke()
            if (response.isSuccessful) { // connected, got data back
                Log.d(TAG, "List of places ${response.body()}")
                // Log.d(TAG, "Server created new place ${response.body()}")
                return ApiResult(ApiStatus.SUCCESS, response.body(), successMessage)
            } else { // connected to server but server sent an error message
                Log.e(TAG, "Server error ${response.errorBody()}")
                return ApiResult(ApiStatus.SERVER_ERROR, null, failMessage)
            }
            // TODO more error handling!
        } catch (ex: Exception) { // can't connect to server - network error
            Log.e(TAG, "Error connecting to API server", ex)
            return ApiResult(ApiStatus.NETWORK_ERROR, null, "Can't connect to server")
        }
    }

    suspend fun getAllPlaces(): ApiResult<List<Place>> {
        return apiCall(
            placeService::getAllPlaces,
            null,
            "Error fetching places for server",
        )
    }

    suspend fun addPlace(place: Place): ApiResult<Place> {
        return apiCall(
            { placeService.addPlace(place) },
            "Place created!",
            "Error adding place - is name unique ?",
        )
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
            return apiCall(
                { placeService.updatePlace(place, place.id) },
                "Place updated!",
                "Error updating place",
            )
        }
    }

    suspend fun deletePlace(place: Place): ApiResult<String> {
        if (place.id == null) {
            Log.e(TAG, "Error - trying to delete place with no ID")
            return ApiResult(
                ApiStatus.SERVER_ERROR,
                null,
                "Error - trying to delete place with no ID",
            )
        } else {
            return apiCall(
                { placeService.deletePlace(place.id) },
                "Place deleted!",
                "Error deleting place",
            )
        }
    }
}
