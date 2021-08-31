package com.example.listapp.home

import android.annotation.SuppressLint
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.horizontalDrag
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.input.pointer.consumePositionChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.listapp.R
import com.example.listapp.data.UserList
import com.example.listapp.ui.theme.ListAppTheme
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue
import kotlin.math.roundToInt
import com.example.listapp.AppBar


/** Composables */

/**
 *
 * @param lists (state) list of [UserList] to display the names
 * @param newName (state) name of a new list
 * @param onAddList (event) request a list be added
 * @param onRemoveList (event) request a list be removed
 * @param onRemoveAllLists (event) request that all lists be removed
 * @param onEditDone (event) request edit mode completion
 *
 * */
@ExperimentalComposeUiApi
@ExperimentalAnimationApi
@Composable
fun ListHome(
    lists: List<UserList>,
    newName: String,
    onAddList: (String) -> Unit,
    onRemoveList: (UserList) -> Unit,
    onRemoveAllLists: () -> Unit,
    onEditDone: () -> Unit
) {
    val lazyListState = rememberLazyListState()

    // Show the alert dialog when the FAB is clicked
    val showDialog = remember { mutableStateOf(false) }

    // The coroutine scope for event handlers calling suspend functions.
    val coroutineScope = rememberCoroutineScope()

    // True if the message about the edit list feature is shown.
    var editMessageShown by remember { mutableStateOf(false) }

    // Shows a message about the edit list feature
    suspend fun showEditMessage() {
        if (!editMessageShown) {
            editMessageShown = true
            delay(3000L)
            editMessageShown = false
        }
    }

    if (showDialog.value) {
        NameInputDialog(
            showDialog = showDialog.value,
            onSaveName = onAddList,
            onDismissed = { showDialog.value = false },
            onEditDone = onEditDone
        )
    }

    // TODO: Add functionality to delete all lists

    ListAppTheme {
        Scaffold(
            topBar = { AppBar(
                title = stringResource(R.string.home_screen_label)
            )},
            floatingActionButton = {
                CreateFloatingActionButton(
                    extended = lazyListState.isScrollingUp(),
                    onClick = { showDialog.value = true }
                )
            },
            floatingActionButtonPosition = FabPosition.End
        ) {
            // Display either content or empty state
            if (lists.isEmpty()) {
                EmptyListHome()
            } else {
                ListHomeContent(
                    lists = lists,
                    state = lazyListState,
                    showMessage = editMessageShown,
                    onRemove = onRemoveList,
                    onClick = {
                        coroutineScope.launch {
                            showEditMessage()
                        }
                    }
                )
            }
        }
    }
}

/**
 * Display an empty state if there are no lists currently saved.
 *
 * */
@Composable
fun EmptyListHome() {
    Column {
        Row(
            Modifier
                .padding(top = 150.dp, start = 100.dp)
        ) {
            Text(
                text = "No lists",
                style = MaterialTheme.typography.h3
            )
        }
        Row(
            Modifier
                .padding(start = 40.dp, top = 20.dp)
        ) {
            Text(
                text = "Create a list and it will show up here",
                style = MaterialTheme.typography.h5
            )
        }
    }
}

/**
 *
 * @param lists (state) list of [UserList] to display the names
 * @param state (state) the state of the lazy list
 * @param onRemove(event) notify caller that a list is being removed
 * */
@ExperimentalAnimationApi
@Composable
fun ListHomeContent(
    lists: List<UserList>,
    state: LazyListState,
    showMessage: Boolean,
    onRemove: (UserList) -> Unit,
    onClick: () -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(top = 8.dp),
        state = state
    ) {
        items(lists) { list ->
            ListRow(
                list = list,
                onListClicked = onClick,
                onRemove = onRemove,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
    EditMessage(showMessage)
}

/**
 * Stateless composable that displays a full-width [UserList] name
 *
 * @param list item to show
 * @param onListClicked (event) notify caller that a row was clicked
 * @param onRemove (event) notify caller that a row is swiped away and removed
 * @param modifier modifier for this element
 * */
@Composable
fun ListRow(
    list: UserList,
    onListClicked: () -> Unit,
    onRemove: (UserList) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = Modifier
            .swipeToDismiss(list, onRemove)
            .padding(8.dp),
        color = MaterialTheme.colors.primaryVariant,
        shape = RoundedCornerShape(10.dp),
    ) {
        Row(modifier
            .clickable { onListClicked() } // FIXME: Pass list back in -> onListClicked(list)
            .padding(horizontal = 16.dp, vertical = 16.dp)
            .fillMaxWidth()
        ) {
            Text(list.listName)
        }
    }
}

/**
 * Shows a floating action button
 *
 * @param extended (state)
 * @param onClick (event) notify caller that the FAB was clicked
 * */
@ExperimentalAnimationApi
@Composable
private fun CreateFloatingActionButton(extended: Boolean, onClick: () -> Unit) {
    FloatingActionButton(
        onClick = onClick,
        modifier = Modifier.padding(bottom = 48.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = null)

            // Toggle the visibility of the content with animation
            AnimatedVisibility(
                visible = extended
            ) {
                Text(
                    text = stringResource(id = R.string.create_list),
                    modifier = Modifier.padding(start = 8.dp, top = 3.dp)
                )
            }
        }
    }
}

/**
 * The modified element can be horizontally swiped away
 *
 * @param list (state) a [UserList] list
 * @param onDismissed (event) Called when the element is swiped to the edge of the screen
 * */
@SuppressLint("UnnecessaryComposedModifier")
private fun Modifier.swipeToDismiss(
    list: UserList,
    onDismissed: (UserList) -> Unit
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
                        onDismissed(list)
                    }
                }
            }
        }
    }
        // Apply the horizontal offset to the element
        .offset { IntOffset(offSetX.value.roundToInt(), 0) }
}

/**
 * Returns whether the lazy list is currently scrolling up.
 *
 * @return Boolean returns whether the list has scrolled
 * */
@Composable
private fun LazyListState.isScrollingUp(): Boolean {
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
 * Shows a message that the manage list feature is not available.
 *
 * Remove this once the Manage List Screen is implemented.
 * */
@ExperimentalAnimationApi
@Composable
private fun EditMessage(show: Boolean) {
    AnimatedVisibility(
        visible = show,
        enter = slideInVertically(
            // Enters by sliding down from offset -fullHeight to 0.
            initialOffsetY = { fullHeight -> -fullHeight },
            animationSpec = tween(durationMillis = 150, easing = LinearOutSlowInEasing)
        ),
        exit = slideOutVertically(
            // Exits by sliding up from offset to -fullHeight
            targetOffsetY = { fullHeight -> -fullHeight },
            animationSpec = tween(durationMillis = 250, easing = FastOutLinearInEasing)
        )
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colors.secondary,
            elevation = 4.dp
        ) {
            Text(
                text = "Edit List feature is not supported",
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@ExperimentalAnimationApi
@Preview
@Composable
fun PreviewListHomeContent() {
    val names = listOf(
        UserList(1, "Shopping List"),
        UserList(2, "School Supplies"),
        UserList(3, "Road Trip Itinerary")
    )
    val lazyListState = rememberLazyListState()

    // True if the message about the edit list feature is shown.
    val editMessageShown by remember { mutableStateOf(false) }

    ListHomeContent(
        lists = names,
        state = lazyListState,
        showMessage = editMessageShown,
        onRemove = {},
        onClick = {}
    )
}

@ExperimentalAnimationApi
@ExperimentalComposeUiApi
@Preview
@Composable
fun PreviewListHome() {
    val names = listOf(
        UserList(1, "Shopping List"),
        UserList(2, "School Supplies"),
        UserList(3, "Road Trip Itinerary")
    )

    ListHome(
        lists = names,
        newName = "",
        onAddList = {},
        onRemoveList = {},
        onRemoveAllLists = {},
        onEditDone = {}
    )
}

@ExperimentalAnimationApi
@ExperimentalComposeUiApi
@Preview
@Composable
fun PreviewEmptyListHome() {

    ListHome(
        lists = emptyList(),
        newName = "",
        onAddList = {},
        onRemoveList = {},
        onRemoveAllLists = {},
        onEditDone = {}
    )
}