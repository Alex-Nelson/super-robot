package com.example.listapp.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.listapp.R

/**
 * Pops up a dialog asking for a name for the new list
 *
 * @param showDialog (state) shows the alert dialog if true
 * @param onSaveName (event) request a list be added
 * @param onDismissed (event) request that the dialog be dismissed
 * */
@ExperimentalComposeUiApi
@Composable
fun NameInputDialog(
    showDialog: Boolean,
    onSaveName: (String) -> Unit,
    onDismissed: () -> Unit,
    onEditDone: () -> Unit
){
    val (name, onTextChange) = rememberSaveable{ mutableStateOf("") }
    val maxLength = 100

    if(showDialog){
        Dialog(onDismissRequest = onDismissed) {
            Surface(
                modifier = Modifier.size(width = 400.dp, height = 200.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                Column {
                    // Title of the dialog
                    Text(
                        text = "Create New List",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp, bottom = 4.dp),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.body1
                    )

                    // The text field for user input
                    NameEntryInput(
                        name = name,
                        onTextChange = onTextChange,
                        onNameComplete = onSaveName
                    )

                    // Counter for number of characters
                    Text(
                        text = "${name.length} / $maxLength",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp, end = 8.dp, bottom = 8.dp),
                        textAlign = TextAlign.End
                    )

                    // Buttons
                    Row{
                        TextButton(
                            onClick = {
                                onDismissed.invoke()
                            },
                            modifier = Modifier.padding(start = 120.dp),
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = Color.Transparent
                            )
                        ) {
                            Text(
                                text = "Cancel",
                                color = MaterialTheme.colors.primaryVariant
                            )
                        }
                        TextButton(
                            onClick = {
                                onSaveName.invoke(name)
                                onEditDone.invoke()
                                onDismissed.invoke()
                            },
                            modifier = Modifier.padding(start = 30.dp),
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = Color.Transparent
                                //contentColor = Color(R.color.cyan_dark)
                            )
                        ){
                            Text(
                                text = "Submit",
                                color = MaterialTheme.colors.primaryVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

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

    NameInputText(
        name = name,
        onTextChange = onTextChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 4.dp, end = 4.dp),
        onImeAction = submit
    )
}

/**
 * Style [TextField] for inputting a name
 *
 * @param name (state) current text of the name
 * @param onTextChange (event) request the text change
 * @param modifier the modifier for this element
 * @param onImeAction (event) notify the caller of [ImeAction.Done] events
 * */
@ExperimentalComposeUiApi
@Composable
fun NameInputText(
    name: String,
    onTextChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    onImeAction: () -> Unit = {}
){
    val keyboardController = LocalSoftwareKeyboardController.current

    TextField(
        value = name,
        onValueChange = onTextChange,
        maxLines = 1,
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

/**
 * Extension function to show a snackbar message
 *
 * @param doneShowing (event) notify caller to stop showing snackbar
 * */
//fun showSnackbar(
//    doneShowing: (String) -> Unit
//){
//
//}

@ExperimentalComposeUiApi
@Preview
@Composable
fun PreviewNameListDialog(){

    val showDialog = remember { mutableStateOf(true) }

    NameInputDialog(
        showDialog = showDialog.value,
        onSaveName = {},
        onDismissed = { showDialog.value = false },
        onEditDone = {}
    )
}

@ExperimentalComposeUiApi
@Preview
@Composable
fun PreviewNameEntryInput(){
    NameEntryInput(name = "", onTextChange = {}, onNameComplete = {})
}

@ExperimentalComposeUiApi
@Preview
@Composable
fun PreviewNameInputText(){
    NameInputText(name = "Sho", onTextChange = {}, onImeAction = {})
}