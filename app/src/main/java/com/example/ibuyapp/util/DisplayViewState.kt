package com.example.ibuyapp.util

import com.example.ibuyapp.data.UserList
/**
 * Data class to help determine which view state to display in the UI
 * */
data class DisplayViewState(
    val isEmpty: Boolean,
    val lists: List<UserList>?
)
