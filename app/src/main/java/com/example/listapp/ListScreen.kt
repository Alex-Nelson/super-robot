package com.example.listapp

import androidx.compose.runtime.Composable

/**
 * Screen metadata for ListApp.
 * */
enum class ListScreen(
    val body: @Composable ((String) -> Unit) -> Unit
) {
    Home(
        body = { }
    ),
    Manage(
        body = {}
    );

    @Composable
    fun Content(onScreenChange: (String) -> Unit){
        body(onScreenChange)
    }

    companion object {
        fun fromRoute(route: String?): ListScreen =
            when(route?.substringBefore("/")) {
                Home.name -> Home
                Manage.name -> Manage
                null -> Home
                else -> throw IllegalArgumentException("Route $route is not recognized.")
            }
    }
}