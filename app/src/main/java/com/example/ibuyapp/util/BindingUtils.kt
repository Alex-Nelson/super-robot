package com.example.ibuyapp.util

import android.widget.Button
import androidx.databinding.BindingAdapter
import androidx.navigation.findNavController
import com.example.ibuyapp.R
import com.example.ibuyapp.data.UserList
import com.example.ibuyapp.display.DisplayFragmentDirections
import com.example.ibuyapp.display.DisplayViewModel
import com.google.android.material.textfield.TextInputEditText

/**
 * Bindings for the app.
 * */

/** Bindings for DisplayFragment */
/**
 * Sets the name of the button to the list's name
 * */
@BindingAdapter("setListName")
fun Button.setName(item: UserList){
    text = context.resources.getString(R.string.list_name_string, item.listName)
}

/**
 * Navigate to the ManageListFragment when the button is clicked
 * */
@BindingAdapter("buttonListener")
fun Button.onClick(item: UserList){
    setOnClickListener {
        // Create an action to navigate to ManageListFragment to pass the list id
        val action = DisplayFragmentDirections
            .actionDisplayFragmentToManageListFragment(item.listId)

        this.findNavController().navigate(action)
    }
}

/**
 * When user swipes item to the right, it deletes it
 * */
@BindingAdapter(value = ["listId", "viewModel"])
fun Button.onSwipeRight(id: Long, viewModel: DisplayViewModel){
    setOnTouchListener(object: OnSwipeTouchListener(context){
        override fun onSwipeRight() {
            viewModel.onSwipeRight(key = id)
        }
    })
}

/** Bindings for ManageListFragment */
/**
 *
 * */
@BindingAdapter("errorItemTextInput")
fun TextInputEditText.onError(isInValid: Boolean){
    if(isInValid) {
        error = "Item must consist of at least 1-50 characters."
    }
}