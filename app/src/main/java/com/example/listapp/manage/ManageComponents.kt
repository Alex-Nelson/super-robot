package com.example.listapp.manage

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.listapp.data.UserList

/**
 * Styled button for [ManageBody]
 *
 * @param onClick (event) notify caller to add item
 * @param text button text
 * @param modifier modifier for the button
 * @param enabled enable or disable the button
 * */
@Composable
fun ItemButton(
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
){
    TextButton(
        onClick = onClick,
        shape = CircleShape,
        enabled = enabled,
        modifier = modifier
    ) {
        Text(text, textAlign = TextAlign.End, modifier = Modifier.width(30.dp))
    }
}

/**
 * Draws a background based on [MaterialTheme.colors.onSurface] that animates resizing and elevatin
 * changes.
 *
 * @param elevate draw a shadow, changes to this will be animated
 * @param modifier modifier for this element
 * @param content (slot) content to draw in the backgroud
 * */
@Composable
fun NewItemInputBackground(
    elevate: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit
){
    val animatedElevation by animateDpAsState(
        if (elevate) 1.dp else 0.dp, TweenSpec(500)
    )
    Surface(
        color = MaterialTheme.colors.onSurface.copy(alpha = 0.05f),
        elevation = animatedElevation,
        shape = RectangleShape
    ) {
        Row(
            modifier = modifier.animateContentSize(animationSpec = TweenSpec(300)),
            content = content
        )
    }
}

/**
 * Shows an alert dialog asking user to confirm they want to delete the current list
 *
 * @param list a [UserList] that user wants to delete
 * @param showDialog (state) show the alert dialog if true
 * @param onRemove (event) notify caller that user confirms the list will be deleted
 * @param onDismiss (event) notify caller that user canceled the action
 * */
@Composable
fun DeleteAlertDialog(
    list: UserList,
    showDialog: Boolean,
    onRemove: (UserList) -> Unit,
    onDismiss: () -> Unit,
    onBack: () -> Unit,
    showSnackbar: (String) -> Unit
){
    if(showDialog){
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text(text = "Delete list?")
            },
            text = {
                Text(text = "All items will also be deleted.")
            },
            dismissButton = {
                TextButton(
                    onClick = { onDismiss.invoke() }
                ) {
                    Text(text = "Cancel")
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showSnackbar.invoke(list.listName!!)
                        onRemove.invoke(list)
                        onDismiss.invoke()
                        onBack.invoke()
                    }
                ) {
                    Text(text = "Confirm")
                }
            }
        )
    }
}

@Preview
@Composable
fun PreviewAlertDialog(){

    DeleteAlertDialog(
        list = UserList(1, "School Supplies"),
        showDialog = true,
        onRemove = {},
        onDismiss = {},
        onBack = {},
        showSnackbar = {}
    )
}