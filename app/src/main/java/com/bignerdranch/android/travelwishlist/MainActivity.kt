package com.bignerdranch.android.travelwishlist

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.internal.ViewUtils.hideKeyboard
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity(), OnListItemClickedListener, OnDataChangedListener {

    private lateinit var newPlaceEditText: EditText
    private lateinit var addNewPlaceButton: Button
    private lateinit var reasonToVisitEditText: EditText
    private lateinit var placeListRecyclerView: RecyclerView

    private lateinit var placesRecyclerAdapter: PlaceRecyclerAdapter

    private val placesViewModel: PlacesViewModel by lazy {
        ViewModelProvider(this).get(PlacesViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        placeListRecyclerView = findViewById(R.id.place_list)
        addNewPlaceButton = findViewById(R.id.add_new_place_button)
        reasonToVisitEditText = findViewById(R.id.reason_to_visit)
        newPlaceEditText = findViewById(R.id.new_place_name)

        val places = placesViewModel.getPlaces() // list of place objects

        placesRecyclerAdapter = PlaceRecyclerAdapter(places, this)
        placeListRecyclerView.layoutManager = LinearLayoutManager(this)
        placeListRecyclerView.adapter = placesRecyclerAdapter

        val itemSwipeListener = OnListItemSwipeListener(this)
        ItemTouchHelper(itemSwipeListener).attachToRecyclerView(placeListRecyclerView)

        addNewPlaceButton.setOnClickListener {
            addNewPlace()
        }
    }

    private fun addNewPlace() {
        val name = newPlaceEditText.text.toString().trim()
        val reason = reasonToVisitEditText.text.toString().trim() // gets the user input for reason to visit the place
        if (name.isEmpty() or reason.isEmpty()) {
            Toast.makeText(this, "Enter a place name", Toast.LENGTH_SHORT).show()
        } else {
            val newPlace = Place(name, reason)
            val positionAdded = placesViewModel.addNewPlace(newPlace)
            if (positionAdded == -1) {
                Toast.makeText(this, "You already added that place", Toast.LENGTH_SHORT).show()
            } else {
                placesRecyclerAdapter.notifyItemInserted(positionAdded)
                clearForm()
                hideKeyboard()
            }
        }
    }

    private fun clearForm() {
        newPlaceEditText.text.clear()
        reasonToVisitEditText.text.clear() // clears the reason editText after place has been added
    }

    private fun hideKeyboard() {
        /* This function clears the keyboard pop up */
        this.currentFocus?.let { view ->
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    override fun onListItemClicked(place: Place) {
        Toast.makeText(this, "${place.name} map icon was clicked", Toast.LENGTH_SHORT).show()
        val placeLocationUri = Uri.parse("geo:0,0?q=${place.name}")
        val mapIntent = Intent(Intent.ACTION_VIEW, placeLocationUri)
        startActivity(mapIntent)
    }

    override fun onListItemMoved(from: Int, to: Int) {
        placesViewModel.movePlace(from, to)
        placesRecyclerAdapter.notifyItemMoved(from, to)
    }

    override fun onListItemDeleted(position: Int) {
        val deletedPlace = placesViewModel.deletePlace(position)
        placesRecyclerAdapter.notifyItemRemoved(position)

        Snackbar.make(
            findViewById(R.id.wishlist_container),
            getString(R.string.place_deleted, deletedPlace.name),
            Snackbar.LENGTH_LONG,
        )
            .setActionTextColor(resources.getColor(R.color.red))
            .setBackgroundTint(resources.getColor(R.color.dark_gray))
            .setAction(getString(R.string.undo)) {
                // display an "UNDO" button
                placesViewModel.addNewPlace(deletedPlace, position)
                placesRecyclerAdapter.notifyItemInserted(position)
            }
            .show()
    }
}
