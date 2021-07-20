package com.example.ibuyapp.util

import android.widget.Button
import androidx.databinding.BindingAdapter
import com.example.ibuyapp.R
import com.example.ibuyapp.data.UserList

/**
 * Bindings for the app.
 * */

/** Bindings for DisplayFragment */
@BindingAdapter("setListName")
fun Button.setName(item: UserList){
    text = context.resources.getString(R.string.list_name_string, item.listName)
}

@BindingAdapter("buttonListener")
fun Button.onClick(item: UserList){
    setOnClickListener {
        //TODO: Navigate to ManageListFragment
    }
}