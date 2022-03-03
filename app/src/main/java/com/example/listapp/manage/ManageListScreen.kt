package com.example.listapp.manage

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Done
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.listapp.R
import com.example.listapp.data.Item
import com.example.listapp.data.UserList
import com.example.listapp.home.ManageDropDownMenu
import com.example.listapp.utils.*
import com.example.listapp.viewmodel.AppViewModel
import kotlinx.coroutines.launch

/**
 * Stateless composable that is responsible for the entire Manage screen
 *
 * @param viewModel an instance of [AppViewModel]
 * @param list a [UserList] that is being edited
 * @param onReturnHome (event) request that the user be sent back to the home screen
 * */
@ExperimentalComposeUiApi
@Composable
fun ManageBody(
    viewModel: AppViewModel,
    list: UserList,
    onReturnHome: () -> Boolean
) {
    // The coroutine scope for event handlers calling suspend functions
    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()

    // Snackbar for updating a list's name
    val onShowUpdateListSnackbar: (String) -> Unit = {name ->
        coroutineScope.launch {
            scaffoldState.snackbarHostState.showSnackbar(
                message = "$name Updated Successfully"
            )
        }
    }

    // Snackbar for deleting one list
    val onShowDeleteSnackbar: (String) -> Unit = {name ->
        coroutineScope.launch {
            scaffoldState.snackbarHostState.showSnackbar(
                message = "Successfully Deleted $name",
                duration = SnackbarDuration.Long
            )
        }
    }

    val enableDeleteItems = remember { mutableStateOf(false) }

    // Retrieve the list that will be edited on this screen
    viewModel::onNavigateToManage.invoke(list)
    val itemList = viewModel.allItems?.observeAsState(initial = listOf())?.value

    // Show name dialog to allow user to edit list's name
    val enableEditName = remember { mutableStateOf(false) }
    if (enableEditName.value) {
        NameInputDialog(
            dialogText = stringResource(id = R.string.edit_list_name_dialog_text),
            showDialog = enableEditName.value,
            onSaveName = viewModel::onUpdateName,
            onDismissed = { enableEditName.value = false },
            onEditDone = viewModel::onNameDone,
            showSnackbar = onShowUpdateListSnackbar
        )
    }

    // Show alert dialog to confirm user wants to delete list
    val showDialog = remember { mutableStateOf(false) }
    if (showDialog.value) {
        DeleteAlertDialog(
            list = list,
            showDialog = showDialog.value,
            onRemove = { viewModel::onRemoveList.invoke(list) },
            onDismiss = { showDialog.value = false },
            onBack = {
                viewModel::onBack.invoke()
                onReturnHome.invoke()
            },
            showSnackbar = onShowDeleteSnackbar
        )
    }

    Scaffold(
        scaffoldState = scaffoldState,
        modifier = Modifier.semantics { contentDescription = "Manage List Screen" },
        topBar = {
            CustomTopAppBar(
                title = list.listName!!,
                enableDelete = { enableDeleteItems.value = true },
                navIcon = {
                    IconButton(
                        modifier = Modifier.padding(16.dp),
                        onClick = {
                            viewModel::onBack.invoke()
                            onReturnHome.invoke()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = null
                        )
                    }
                },
                dropDownMenu = {
                    ManageDropDownMenu(
                        list = list,
                        enableDeleteItems = { enableDeleteItems.value = true },
                        enableEditName = {
                            enableEditName.value = true
                        },
                        enableDeleteList = {
                            showDialog.value = true
                        }
                    )
                }
            )
        },
        bottomBar = {
            if (enableDeleteItems.value) {
                CustomBottomAppBar(
                    enableDeleteButton = viewModel.deleteItems.isNotEmpty(),
                    deleteButtonText = if(viewModel.deleteItems.isEmpty()) "Delete"
                    else "${viewModel.deleteItems.size} item(s) Selected",
                    onCancel = {
                        enableDeleteItems.value = false
                        viewModel::onCancelDelete.invoke("Item")
                    },
                    onDelete = viewModel::onDeleteItems
//                    showSnackbar = {
//                        coroutineScope.launch {
//                            scaffoldState.snackbarHostState.showSnackbar(
//                                message = "${viewModel.deleteItems.size} Item(s) Deleted"
//                            )
//                        }
//                    }
                )
            }
        }
    ) {
        Column {
            val enableTopSection = viewModel.currentEditItem == null
            NewItemInputBackground(elevate = enableTopSection) {
                when {
                    enableTopSection -> {
                        NewItemEntryInput(onItemComplete = viewModel::onAddItem)
                    }
                    enableDeleteItems.value -> {
                        Text(
                            text = "Deleting items",
                            style = MaterialTheme.typography.h6,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .padding(16.dp)
                                .fillMaxWidth()
                        )
                    }
                    else -> {
                        Text(
                            text = "Editing item",
                            style = MaterialTheme.typography.h6,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .padding(16.dp)
                                .fillMaxWidth()
                        )
                    }
                }
            }
            if (itemList.isNullOrEmpty()) {
                EmptyManageScreen()
            } else {
                ManageListContent(
                    itemList = itemList[0].items,
                    deleteItemList = viewModel.deleteItems,
                    currentlyEditing = viewModel.currentEditItem,
                    enableDelete = enableDeleteItems.value,
                    modifier = Modifier.weight(1f),
                    onItemClicked = viewModel::onElemClicked,
                    onStartEdit = viewModel::onEditItemSelected,
                    onEditItemChange = viewModel::onUpdateItem,
                    onRemove = viewModel::onDeleteItem,
                    onEditDone = viewModel::onEditItemDone
                )
            }
        }
    }
}

/**
 * Display an empty state if no items are saved.
 *
 * */
@ExperimentalComposeUiApi
@Composable
fun EmptyManageScreen() {
    Column {
        Row(
            Modifier
                .padding(top = 150.dp, start = 100.dp)
        ) {
            Text(
                text = "No items",
                style = MaterialTheme.typography.h3
            )
        }
        Row(
            Modifier
                .padding(start = 40.dp, top = 20.dp)
        ) {
            Text(
                text = "Use the text box to type an item and click 'Add' to create it.",
                style = MaterialTheme.typography.h5
            )
        }
    }
}

/**
 * A stateful composable that show's the list's contents
 *
 * @param itemList (state) a list of [Item]
 * @param deleteItemList (state) a list of [Item] to delete
 * @param currentlyEditing (state) enable edit mode for an item
 * @param enableDelete (state) enable or disable delete mode for user to delete items
 * @param onItemClicked (event) notify caller that an item was clicked
 * @param onStartEdit (event) request to enter edit more for an item
 * @param onEditItemChange (event) request the current edit item be updated
 * @param onRemove (event) request an item be removed
 * @param onEditDone (event) request to exit edit mode
 * */
@ExperimentalComposeUiApi
@Composable
fun ManageListContent(
    itemList: List<Item>,
    deleteItemList: List<Item>,
    currentlyEditing: Item?,
    enableDelete: Boolean,
    modifier: Modifier,
    onItemClicked: (Item) -> Unit,
    onStartEdit: (Item) -> Unit,
    onEditItemChange: (Item) -> Unit,
    onRemove: (Item) -> Unit,
    onEditDone: () -> Unit
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(top = 8.dp),
        state = rememberLazyListState()
    ) {
        items(items = itemList) { item ->
            when {
                // Allow user to edit an item inline
                currentlyEditing?.itemId == item.itemId -> {
                    ItemInlineEditor(
                        item = currentlyEditing,
                        onEditItemChange = onEditItemChange,
                        onEditDone = onEditDone
                    )
                }
                enableDelete -> {
                    // Allow user to select/deselect an item to delete
                    DeleteItemRow(
                        item = item,
                        beDeleted = deleteItemList.contains(item),
                        onItemClicked = onItemClicked,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                else -> {
                    // Display the item's text
                    ItemRow(
                        item = item,
                        onItemClicked = { onStartEdit(it) },
                        onRemove = onRemove,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

    }
}

/**
 * Stateless composable that provides a styled [EditItemInput] for inline editing
 *
 * @param item (state) the current item to display in the editor
 * @param onEditItemChange (event) request an item be changed
 * @param onEditDone (event) request edit mode completion for this item
 * */
@ExperimentalComposeUiApi
@Composable
fun ItemInlineEditor(
    item: Item,
    onEditItemChange: (Item) -> Unit,
    onEditDone: () -> Unit
) {
    EditItemInput(
        text = item.itemStr,
        onTextChange = {
            onEditItemChange(
                item.copy(
                    itemId = item.itemId,
                    listId = item.listId,
                    itemStr = it
                )
            )
        },
        submit = onEditDone,
        buttonSlot = {
            Row{
                val shrinkButtons = Modifier.widthIn(20.dp)
                ItemButton(onClick = onEditDone, "\uD83D\uDCBE", // floppy disk
                    modifier = shrinkButtons, enabled = item.itemStr.isNotBlank())
                ItemButton(onClick = onEditDone, text = "❌",
                    modifier = shrinkButtons, enabled = item.itemStr.isNotBlank())
            }
        }
    )
}

/**
 * Stateful composable to allow entry for a new [Item].
 *
 * This composable will display a button with [buttonText].
 *
 * @param onItemComplete (event) notify the caller that the user has completed entry of an item
 * @param buttonText text ot display on the button
 * */
@ExperimentalComposeUiApi
@Composable
fun NewItemEntryInput(
    onItemComplete: (String) -> Unit,
    buttonText: String = "Add"
) {
    val (text, onTextChange) = rememberSaveable { mutableStateOf("") }

    val submit = {
        if (text.isNotBlank()) {
            onItemComplete(text)
            onTextChange("")
        }
    }

    EditItemInput(
        text = text,
        onTextChange = onTextChange,
        submit = submit,
        buttonSlot = {
            ItemButton(onClick = submit, text = buttonText, enabled = text.isNotBlank())
        }
    )
}

/**
 * Stateless input composable for editing [Item]
 *
 * @param text (state) current text of the item
 * @param onTextChange (event) request the text change
 * @param submit (event) notify the caller that the user has submitted with [ImeAction.Done]
 * @param buttonSlot (slot) slot for providing buttons next to the text
 * */
@ExperimentalComposeUiApi
@Composable
fun EditItemInput(
    text: String,
    onTextChange: (String) -> Unit,
    submit: () -> Unit,
    buttonSlot: @Composable () -> Unit
) {
    Column {
        Row(
            Modifier
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp)
                .height(IntrinsicSize.Min)
        ) {
            ItemInputText(
                text = text,
                onTextChange = onTextChange,
                onImeAction = submit,
                singleLine = false
            )
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                Modifier.align(Alignment.CenterVertically)
            ) {
                buttonSlot()
            }
        }
    }
}

/**
 * Stateless composable that displays a full-width [Item]
 *
 * @param item an [Item] to show
 * @param onItemClicked (event) notify caller that the row was clicked
 * @param onRemove (event) notify caller that a row was swiped away and removed
 * @param modifier modifier for this element
 * */
@Composable
fun ItemRow(
    item: Item,
    onItemClicked: (Item) -> Unit,
    onRemove: (Item) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier
            .clickable { onItemClicked(item) }
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .swipeToDismiss(item, onRemove)
    ) {
        Icon(
            imageVector = Icons.Default.Circle,
            contentDescription = null
        )
        Text(
            text = item.itemStr,
            modifier = Modifier.padding(start = 10.dp, top = 2.dp)
        )
    }
}

/**
 * A composable that displays a full-width [Item].
 *
 * @param item an [Item] with it's name displayed
 * @param beDeleted (state) enable/disable a checkmark to indicate selected item
 * @param onItemClicked (event) notify caller that a row was clicked
 * @param modifier modifier for this element
 * */
@Composable
fun DeleteItemRow(
    item: Item,
    beDeleted: Boolean,
    onItemClicked: (Item) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier
            .clickable { onItemClicked(item) }
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Circle,
            contentDescription = null
        )
        Text(
            text = item.itemStr,
            modifier = Modifier.padding(start = 10.dp, top = 2.dp)
        )

        if (beDeleted) {
            Icon(
                imageVector = Icons.Default.Done,
                contentDescription = "Mark item to be deleted.",
                modifier = Modifier.padding(start = 50.dp)
            )
        }
    }
}

@ExperimentalComposeUiApi
@Preview
@Composable
fun PreviewManageScreen() {
    ManageListContent(
        itemList = listOf(
            Item(1, 1, "Milk"),
            Item(2, 1, "Tofu"),
            Item(3, 1, "Green beans"),
            Item(4, 1, "Dish soap"),
            Item(5, 1, "Chicken thighs"),
            Item(6, 1, "Olive oil")
        ),
        deleteItemList = emptyList(),
        currentlyEditing = null,
        enableDelete = false,
        modifier = Modifier,
        onItemClicked = {},
        onStartEdit = {},
        onEditItemChange = {},
        onRemove = {},
        onEditDone = {}
    )
}

@ExperimentalComposeUiApi
@Preview
@Composable
fun PreviewEditManageBody() {
    ManageListContent(
        itemList = listOf(
            Item(1, 1, "Milk"),
            Item(2, 1, "Tofu"),
            Item(3, 1, "Green beans"),
            Item(4, 1, "Dish soap"),
            Item(5, 1, "Chicken thighs"),
            Item(6, 1, "Olive oil")
        ),
        deleteItemList = emptyList(),
        currentlyEditing = Item(4, 1, "Dish soap"),
        enableDelete = false,
        modifier = Modifier,
        onItemClicked = {},
        onStartEdit = {},
        onEditItemChange = {},
        onRemove = {},
        onEditDone = {}
    )
}

@ExperimentalComposeUiApi
@Preview
@Composable
fun PreviewEmptyManageBody() {
//    ManageBody(
//        list = UserList(1, "Shopping List"),
//        currentlyEditing = null,
//        itemList = emptyList(),
//        deleteItemList = emptyList(),
//        deleteList = {},
//        onEditListName = {},
//        onAddItem = {},
//        onItemClicked = {},
//        deleteItems = {},
//        onCancel = {},
//        onBack = {},
//        onDeleteItem = {},
//        onEditDone = {},
//        onStartEdit = {},
//        onUpdateItem = {}
//    )
}