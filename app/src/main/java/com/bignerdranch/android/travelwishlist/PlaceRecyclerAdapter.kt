package com.bignerdranch.android.travelwishlist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

interface OnListItemClickedListener {
    fun onMapRequestButtonClicked(place: Place)
    fun onStarredStatusChanged(place: Place, isStarred: Boolean)
}

class PlaceRecyclerAdapter(
    private val places: List<Place>,
    private val onListItemClickedListener: OnListItemClickedListener,
) :
    RecyclerView.Adapter<PlaceRecyclerAdapter.ViewHolder>() {

    // manages one view - one list item - sets the actual data in the view
    // nested classes = this is one - are own self-contained thing
    // inner classes - virtually the same but can access variables from it's enclosing class
    inner class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(place: Place) {
            val placeNameTextView: TextView = view.findViewById(R.id.place_name)
            placeNameTextView.text = place.name

            val reasonTextView: TextView = view.findViewById(R.id.visit_reason)
            reasonTextView.text = place.reason

//            val dateCreatedOnTextView: TextView = view.findViewById(R.id.date_place_added)
//            val createdOnText = view.context.getString(R.string.created_on, place.formattedDate())
//            dateCreatedOnTextView.text = createdOnText

            val mapIcon: ImageView = view.findViewById(R.id.map_icon)
            mapIcon.setOnClickListener {
                onListItemClickedListener.onMapRequestButtonClicked(place)
            }

            val starCheck = view.findViewById<CheckBox>(R.id.star_check)
            // in order stop the event listener to run when we first set the starred value
            starCheck.setOnClickListener(null) // removes the listener
            starCheck.isChecked = place.starred // update
            starCheck.setOnClickListener {
                // replace listener - avoid endless loop
                onListItemClickedListener.onStarredStatusChanged(place, starCheck.isChecked)
            }
        }
    }

    // called by the recycler view to create as many view holders that are needed to
    // display the list on screen
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.place_list_item, parent, false)
        return ViewHolder(view)
    }

    // bind the view holder with data for a specific position
    // called by the recycler view to set the data for one list item, by position
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val place = places[position]
        holder.bind(place)
    }

    // how many items in the list ?
    override fun getItemCount(): Int {
        return places.size
    }
}
