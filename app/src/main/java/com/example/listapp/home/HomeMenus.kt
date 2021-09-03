package com.example.listapp.home

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.listapp.R

/**
 * Dropdown menu for the Home screen
 *
 * @param enableDelete (event) notify caller to allow user to delete lists
 * */
@Composable
fun HomeDropDownMenu(
    enableDelete: () -> Unit
){

    val expanded = remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.TopEnd)
    ){
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
                Text(text = stringResource(id = R.string.delete_list_ddm))
            }
        }
    }
}