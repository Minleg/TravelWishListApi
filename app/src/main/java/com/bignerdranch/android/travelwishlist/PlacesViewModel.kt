package com.bignerdranch.android.travelwishlist

import androidx.lifecycle.ViewModel

const val TAG = "PLACES_VIEW_MODEL"

class PlacesViewModel : ViewModel() {

//    private val places = mutableListOf<Place>(Place("Aukland, NZ", "Visit Island"), Place("New Delhi, IN", "Visit Taj Mahal", starred = true))

    fun getPlaces() { // this returns a immutable list and maintains control of the list
    }

    fun addNewPlace(place: Place) {
    }

    fun deletePlace(place: Place) {
    }

    fun updatePlace(place: Place) {
        // todo
    }
}
