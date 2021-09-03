package com.example.listapp

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.listapp.home.HomeDropDownMenu

/** 
 * The [TopAppBar] for the Home screen.
 *
 * @param enableDelete (event) notify caller to allow user to delete lists
 * */
@Composable
fun HomeAppBar(
    enableDelete: () -> Unit
){
    Surface(
        color = MaterialTheme.colors.primarySurface
    ) {
        TopAppBar(
            title = {
                Text(
                    text = "Home",
                    color = MaterialTheme.colors.onPrimary
                )
            },
            navigationIcon = {
                // Show drawer icon
                IconButton(
                    modifier = Modifier.padding(16.dp),
                    onClick = { /*TODO*/ }
                ) {
                    Icon(
                        Icons.Filled.Menu,
                        contentDescription = null
                    )
                }
            },
            actions = {
                  HomeDropDownMenu(
                      enableDelete = enableDelete
                  )
            },
            backgroundColor = MaterialTheme.colors.primarySurface,
            elevation = AppBarDefaults.TopAppBarElevation
        )
    }
}

/**
 * Displays a bottom app bar that allows user to confirm what they wish to delete.
 *
 * @param enableDeleteButton (state) enable or disable Delete button
 * @param onCancel (event) notify caller to cancel deleting
 * @param onDelete (event) notify caller to delete the selected items
 * */
@Composable
fun HomeBottomAppBar(
    enableDeleteButton: Boolean,
    onCancel: () -> Unit,
    onDelete: () -> Unit
){
    Surface(
        color = MaterialTheme.colors.secondaryVariant
    ){
        BottomAppBar(
            modifier = Modifier.fillMaxWidth(),
            elevation = 40.dp
        ) {
            TextButton(
                modifier = Modifier.padding(start = 100.dp),
                onClick = onCancel
            ) {
                Text(
                    text = "Cancel",
                    color = MaterialTheme.colors.onSecondary
                )
            }
            TextButton(
                modifier = Modifier.padding(start = 40.dp),
                enabled = enableDeleteButton,
                onClick = {
                    onDelete.invoke()
                    onCancel.invoke()
                }
            ) {
                if(enableDeleteButton){
                    Text(
                        text = "Delete",
                        color = MaterialTheme.colors.onSecondary
                    )
                }else{
                    Text(text = "Delete")
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewHomeAppBar(){

    HomeAppBar(
        enableDelete = {}
    )
}

@Preview
@Composable
fun PreviewHomeBottomAppBarEnabled(){
    HomeBottomAppBar(
        enableDeleteButton = true,
        onCancel = {},
        onDelete = {}
    )
}

@Preview
@Composable
fun PreviewHomeBottomAppBarDisabled(){
    HomeBottomAppBar(
        enableDeleteButton = false,
        onCancel = {},
        onDelete = {}
    )
}