package com.example.listapp.utils

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.listapp.R

/**
 * Stateful composable to allow entry of a new name
 *
 * @param onNameComplete (event) notify caller that a name has been entered
 * */
@ExperimentalComposeUiApi
@Composable
fun NameEntryInput(
    name: String,
    onTextChange: (String) -> Unit,
    onNameComplete: (String) -> Unit
){
    // Submit the name if the name string is not empty
    val submit = {
        if(name.isNotBlank()){
            onNameComplete(name)
            onTextChange(name)
        }
    }

    ItemInputText(
        text = name,
        onTextChange = onTextChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 4.dp, end = 4.dp),
        onImeAction = submit,
        singleLine = true
    )
}

/**
 * Style [TextField] for inputting a name
 *
 * @param text (state) current text of the name
 * @param onTextChange (event) request the text change
 * @param modifier the modifier for this element
 * @param onImeAction (event) notify the caller of [ImeAction.Done] events
 * */
@ExperimentalComposeUiApi
@Composable
fun ItemInputText(
    text: String,
    onTextChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    onImeAction: () -> Unit = {},
    singleLine: Boolean
){
    val keyboardController = LocalSoftwareKeyboardController.current

    TextField(
        value = text,
        onValueChange = onTextChange,
        singleLine = singleLine,
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(onDone = {
            onImeAction()
            keyboardController?.hide()
        }),
        modifier = modifier,
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = Color.Transparent,
            cursorColor = Color.Black,
            unfocusedIndicatorColor = Color(R.color.cyan_light)
        )
    )
}

@ExperimentalComposeUiApi
@Preview
@Composable
fun PreviewNameEntryInput(){
    NameEntryInput(
        name = "",
        onTextChange = {},
        onNameComplete = {}
    )
}

@ExperimentalComposeUiApi
@Preview
@Composable
fun PreviewNameInputText(){
    ItemInputText(
        text = "Sho",
        onTextChange = {},
        onImeAction = {},
        singleLine = true
    )
}