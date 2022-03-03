package com.example.listapp.utils
/**
 * This file contains generic functions and composables that are shared between
 * more than one screens.
 * */

import android.annotation.SuppressLint
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.calculateTargetValue
import androidx.compose.animation.splineBasedDecay
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.horizontalDrag
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.consumePositionChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.listapp.R
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

/**
 * Returns whether the lazy list is currently scrolling up.
 *
 * @return Boolean returns whether the list has scrolled
 * */
@Composable
fun LazyListState.isScrollingUp(): Boolean {
    var previousIndex by remember(this) { mutableStateOf(firstVisibleItemIndex) }
    var previousScrollOffset by remember(this) { mutableStateOf(firstVisibleItemScrollOffset) }

    return remember(this) {
        derivedStateOf {
            if (previousIndex != firstVisibleItemIndex) {
                previousIndex > firstVisibleItemIndex
            } else {
                previousScrollOffset >= firstVisibleItemScrollOffset
            }.also {
                previousIndex = firstVisibleItemIndex
                previousScrollOffset = firstVisibleItemScrollOffset
            }
        }
    }.value
}

/**
 * Generic function used to swipe an element away horizontally to delete it.
 *
 * @param elem (state) a generic element
 * @param onDismissed (event) called when the element is swiped to the edge of the screen
 * */
@SuppressLint("UnnecessaryComposedModifier")
fun <T> Modifier.swipeToDismiss(
    elem: T,
    onDismissed: (T) -> Unit
): Modifier = composed {
    // This 'Animatable' stores the horizontal offset for the element
    val offSetX = remember { Animatable(0f) }
    pointerInput(Unit) {
        // Used to calculate a settling position of a fling animation
        val decay = splineBasedDecay<Float>(this)

        // Wrap in a coroutine scope to use suspend functions for touch events and animation
        coroutineScope {
            while (true) {
                // Wait for a touch down event
                val pointerId = awaitPointerEventScope { awaitFirstDown().id }

                // Interrupt any ongoing animation
                offSetX.stop()

                // Prepare for drag events and record velocity of a fling
                val velocityTracker = VelocityTracker()

                // Wait for drag events
                awaitPointerEventScope {
                    horizontalDrag(pointerId) { change ->
                        //Record the position after offset
                        val horizontalDragOffset = offSetX.value + change.positionChange().x

                        launch {
                            // Overwrite the 'Animatable' value while the element is dragged
                            offSetX.snapTo(horizontalDragOffset)
                        }

                        // Record the velocity of the drag
                        velocityTracker.addPosition(change.uptimeMillis, change.position)

                        // Consume the gesture event, not passed to external
                        change.consumePositionChange()
                    }
                }

                // Dragging finished. Calculate the velocity of the fling
                val velocity = velocityTracker.calculateVelocity().x

                // Calculate where the element eventually settles after the fling animation
                val targetOffsetX = decay.calculateTargetValue(offSetX.value, velocity)

                // The animation should end as soon as it reaches these bounds
                offSetX.updateBounds(
                    lowerBound = -size.width.toFloat(),
                    upperBound = size.width.toFloat()
                )

                launch {
                    if (targetOffsetX.absoluteValue <= size.width) {
                        // Not enough velocity; Slide back to the default position
                        offSetX.animateTo(targetValue = 0f, initialVelocity = velocity)
                    } else {
                        // Enough velocity to slide away the element to the edge
                        offSetX.animateDecay(velocity, decay)

                        // The element was swiped away
                        onDismissed(elem)
                    }
                }
            }
        }
    }
        // Apply the horizontal offset to the element
        .offset { IntOffset(offSetX.value.roundToInt(), 0) }
}

/**
 * Pops up a dialog asking for a name for the new list
 *
 * @param showDialog (state) shows the dialog if true
 * @param onSaveName (event) request a list be added
 * @param onDismissed (event) request that the dialog be dismissed
 * */
@ExperimentalComposeUiApi
@Composable
fun NameInputDialog(
    dialogText: String,
    showDialog: Boolean,
    onSaveName: (String) -> Unit,
    onDismissed: () -> Unit,
    onEditDone: () -> Unit,
    showSnackbar: (String) -> Unit
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
                        text = dialogText,
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
                                showSnackbar.invoke(name)
                            },
                            modifier = Modifier.padding(start = 30.dp),
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = Color.Transparent
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

@ExperimentalComposeUiApi
@Preview
@Composable
fun PreviewNameNewListDialog(){

    val showDialog = remember { mutableStateOf(true) }

    NameInputDialog(
        dialogText = stringResource(id = R.string.create_list_dialog_text),
        showDialog = showDialog.value,
        onSaveName = {},
        onDismissed = { showDialog.value = false },
        onEditDone = {},
        showSnackbar = {}
    )
}

@ExperimentalComposeUiApi
@Preview
@Composable
fun PreviewEditListNameDialog(){
    val showDialog = remember { mutableStateOf(true) }

    NameInputDialog(
        dialogText = stringResource(id = R.string.edit_list_name_dialog_text),
        showDialog = showDialog.value,
        onSaveName = {},
        onDismissed = { showDialog.value = false },
        onEditDone = {},
        showSnackbar = {}
    )
}