package com.example.listapp.utils

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.listapp.data.UserList
import com.example.listapp.home.HomeDropDownMenu
import com.example.listapp.home.ManageDropDownMenu

/**
 * A custom composable top app bar.
 *
 * @param title the name of the screen
 * @param enableDelete (event) notify caller to allow user to delete lists
 * @param navIcon The navigation icon for the screen
 * @param dropDownMenu The drop down menu for the screen
 * */
@Composable
fun CustomTopAppBar(
    title: String,
    enableDelete: () -> Unit,
    navIcon: @Composable (() -> Unit)?,
    dropDownMenu: @Composable (() -> Unit) -> Unit
){
    TopAppBar(
        navigationIcon = navIcon,
        title = { Text(text = title)},
        actions = { dropDownMenu(enableDelete) },
        elevation = AppBarDefaults.TopAppBarElevation
    )
}

/**
 * Displays a bottom app bar that allows user to confirm what they wish to delete.
 *
 * @param enableDeleteButton (state) enable or disable Delete button
 * @param onCancel (event) notify caller to cancel deleting
 * @param onDelete (event) notify caller to delete the selected items
 * */
@Composable
fun CustomBottomAppBar(
    enableDeleteButton: Boolean,
    deleteButtonText: String = "Delete",
    onCancel: () -> Unit,
    onDelete: () -> Unit
    //showSnackbar: () -> Unit
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
                    //showSnackbar.invoke()
                    onCancel.invoke()
                }
            ) {
                if(enableDeleteButton){
                    Text(
                        text = deleteButtonText,
                        color = MaterialTheme.colors.onSecondary
                    )
                }else{
                    Text(text = deleteButtonText)
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewHomeTopAppBar(){

    CustomTopAppBar(
        title = "Home",
        enableDelete = { },
        navIcon = {
            IconButton(
                onClick = { /*TODO*/ }
            ) {
                Icon(
                    imageVector = Icons.Filled.Menu,
                    contentDescription = null
                )
            }
        },
        dropDownMenu = {
            HomeDropDownMenu(
                enableDelete = { }
            )}
    )
}

@Preview
@Composable
fun PreviewManageTopAppBar(){

    CustomTopAppBar(
        title = "Shopping List",
        enableDelete = {  },
        navIcon = {
            IconButton(
                modifier = Modifier.padding(16.dp),
                onClick = {}
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = null)
            }
        },
        dropDownMenu = {
            ManageDropDownMenu(
                list = UserList(0L, "Shopping List"),
                enableDeleteItems = {  },
                enableEditName = {},
                enableDeleteList = {}
            )
        }
    )
}

@Preview
@Composable
fun PreviewHomeBottomAppBarEnabled(){
    CustomBottomAppBar(
        enableDeleteButton = true,
        onCancel = {},
        onDelete = {}
        //showSnackbar = {}
    )
}

@Preview
@Composable
fun PreviewHomeBottomAppBarDisabled(){
    CustomBottomAppBar(
        enableDeleteButton = false,
        onCancel = {},
        onDelete = {}
        //showSnackbar = {}
    )
}