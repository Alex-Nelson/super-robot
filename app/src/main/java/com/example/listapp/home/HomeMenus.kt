package com.example.listapp.home

import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import com.example.listapp.R
import com.example.listapp.data.UserList

/**
 * Dropdown menu for the Home screen
 *
 * @param enableDelete (event) notify caller to allow user to delete lists
 * */
@Composable
fun HomeDropDownMenu(
    enableDelete: () -> Unit
) {

    val expanded = remember { mutableStateOf(false) }

    IconButton(
        onClick = {
            expanded.value = true
        }
    ) {
        Icon(
            Icons.Filled.MoreVert,
            contentDescription = "Options for Home Screen"
        )
    }

    DropdownMenu(
        expanded = expanded.value,
        onDismissRequest = { expanded.value = false }
    ) {
        DropdownMenuItem(
            onClick = {
                expanded.value = false
                enableDelete.invoke()
            }
        ) {
            Text(text = stringResource(id = R.string.delete_lists_ddm))
        }
    }
}

/**
 * Dropdown menu for the Manage List screen
 *
 * @param list a [UserList] that is being edited
 * @param enableDeleteItems (event) notify caller to allow user to delete items
 * @param enableEditName (event) notify caller to allow user to edit name of list
 * @param enableDeleteList (event) notify caller to allow user to delete list
 * */
@Composable
fun ManageDropDownMenu(
    list: UserList,
    enableDeleteItems: () -> Unit,
    enableEditName: () -> Unit,
    enableDeleteList: (UserList) -> Unit
) {
    val expanded = remember { mutableStateOf(false) }

    IconButton(
        onClick = {
            expanded.value = true
        }
    ) {
        Icon(
            Icons.Filled.MoreVert,
            contentDescription = "Options for Manage List Screen"
        )
    }

    DropdownMenu(
        expanded = expanded.value,
        onDismissRequest = { expanded.value = false }
    ) {
        DropdownMenuItem(
            onClick = {
                expanded.value = false
                enableEditName.invoke()
            }
        ) {
            Text(text = stringResource(id = R.string.edit_list_ddm))
        }
        DropdownMenuItem(
            onClick = {
                expanded.value = false
                enableDeleteList.invoke(list)
            }
        ) {
            Text(text = stringResource(id = R.string.delete_list_ddm))
        }
        DropdownMenuItem(
            onClick = {
                expanded.value = false
                enableDeleteItems.invoke()
            }
        ) {
            Text(text = stringResource(id = R.string.delete_items_ddm))
        }
    }

}