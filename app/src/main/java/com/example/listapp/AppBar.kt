package com.example.listapp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.List
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 *
 * @param title
 * */
@Composable
fun AppBar(title: String){
//    TopAppBar(
//        navigationIcon = {
//          Icon(
//              imageVector = Icons.Rounded.List,
//              contentDescription = null,
//              modifier = Modifier.padding(horizontal = 12.dp)
//          )
//        },
//        title = {
//            Text(text = title)
//        },
//        backgroundColor = MaterialTheme.colors.primarySurface
//    )
    Surface(
        color = MaterialTheme.colors.primarySurface
    ) {
        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(
                modifier = Modifier.padding(16.dp),
                onClick = { /*TODO*/ }
            ) {
                Icon(
                    imageVector = Icons.Rounded.List,
                    contentDescription = null
                )
            }
            Text(text = title)
        }
    }
}

@Preview
@Composable
fun PreviewAppBar(){
    AppBar(title = "Home")
}