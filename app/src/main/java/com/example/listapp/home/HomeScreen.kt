package com.example.listapp.home

import androidx.compose.animation.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.listapp.R
import com.example.listapp.data.UserList
import com.example.listapp.ui.theme.ListAppTheme
import com.example.listapp.utils.*
import kotlinx.coroutines.launch

/**
 *
 * @param lists (state) list of [UserList] to display the names
 * @param deleteLists (state) list of [UserList] that will be deleted
 * @param onAddList (event) request a list be added
 * @param onEditDone (event) request edit mode completion
 * @param onRemoveList (event) request a list be removed
 * @param onListClicked (event)
 * */
@ExperimentalAnimationApi
@ExperimentalComposeUiApi
@Composable
fun HomeBody(
    lists: List<UserList>,
    deleteLists: List<UserList>,
    onAddList: (String) -> Unit,
    onEditDone: () -> Unit,
    onRemoveList: (UserList) -> Unit,
    onListClicked: (UserList) -> Unit,
    onRemoveLists: () -> Unit,
    onCancel: (String) -> Unit,
    onClick: (UserList) -> Unit
){
    val lazyListState = rememberLazyListState()

    // Show the alert dialog when the FAB is clicked
    val showNameDialog = remember { mutableStateOf(false) }

    // The coroutine scope for event handlers calling suspend functions
    val coroutineScope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState()

    // Snackbar for deleting one list
    val onShowDeleteSnackbar: (UserList) -> Unit = {list ->
        coroutineScope.launch {
            scaffoldState.snackbarHostState.showSnackbar(
                message = "Successfully Deleted ${list.listName}"
            )
        }
    }

//    // Snackbar for deleting at least one list
//    val onShowDeleteListsSnackbar: (List<UserList>) -> Unit = {list ->
//        coroutineScope.launch {
//            scaffoldState.snackbarHostState.showSnackbar(
//                message = "${list.size} List(s) Deleted"
//            )
//        }
//    }

    // Snackbar for creating a new list
    val onShowCreateListSnackbar: (String) -> Unit = { name ->
        coroutineScope.launch {
            scaffoldState.snackbarHostState.showSnackbar(
                message = "$name Successfully Created"
            )
        }
    }

    val enableDelete = remember { mutableStateOf(false) }

    if(showNameDialog.value){
        NameInputDialog(
            dialogText = "Create New List",
            showDialog = showNameDialog.value,
            onSaveName = onAddList,
            onDismissed = { showNameDialog.value = false },
            onEditDone = onEditDone,
            showSnackbar = onShowCreateListSnackbar
        )
    }

    ListAppTheme {
        Scaffold(
            scaffoldState = scaffoldState,
            modifier = Modifier.semantics { contentDescription = "Home Screen" },
            topBar = {
                  CustomTopAppBar(
                      title = "Home",
                      enableDelete = { enableDelete.value = true },
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
                              enableDelete = { enableDelete.value = true }
                          )
                      }
                  )
            },
            bottomBar = {
                // Show bottom app bar if deleting
                if(enableDelete.value){
                    CustomBottomAppBar(
                        enableDeleteButton = deleteLists.isNotEmpty(),
                        deleteButtonText = if(deleteLists.isEmpty()) "Delete"
                        else "${deleteLists.size} list(s) Selected",
                        onCancel = {
                            enableDelete.value = false
                            onCancel.invoke("Lists")
                        },
                        onDelete = onRemoveLists
//                        showSnackbar = {
//                            coroutineScope.launch {
//                                scaffoldState.snackbarHostState.showSnackbar(
//                                    message = "${deleteLists.size} List(s) Deleted"
//                                )
//                            }
//                        }
                    )
                }
            },
            floatingActionButton = {
                // Show FAB if not deleting
                if(!enableDelete.value){
                    CreateFloatingActionButton(
                        extended = lazyListState.isScrollingUp(),
                        onClick = { showNameDialog.value = true }
                    )
                }
            },
            floatingActionButtonPosition = FabPosition.End
        ) {
            // Display either content or empty state
            if (lists.isEmpty()) {
                EmptyListHome()
            } else {
                ListHomeContent(
                    lists = lists,
                    deleteLists = deleteLists,
                    state = lazyListState,
                    enableDelete = enableDelete.value,
                    onRemove = onRemoveList,
                    deleteSnackbar = onShowDeleteSnackbar,
                    onListClicked = onListClicked,
                    onClick = onClick
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
 * @param enableDelete (state) enable delete mode for user to delete lists
 * @param onRemove (event) notify caller that a list is being removed
 * @param onListClicked (event) notify caller that a row was selected/deselected to be deleted
 * @param onClick (event) notify caller to show message
 * */
@ExperimentalAnimationApi
@Composable
fun ListHomeContent(
    lists: List<UserList>,
    deleteLists: List<UserList>,
    state: LazyListState,
    enableDelete: Boolean,
    onRemove: (UserList) -> Unit,
    deleteSnackbar: (UserList) -> Unit,
    onListClicked: (UserList) -> Unit,
    onClick: (UserList) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(top = 8.dp),
        state = state
    ) {
        items(lists) { list ->
            if(enableDelete){
                DeleteListRow(
                    list = list,
                    beDeleted = deleteLists.contains(list),
                    onListClicked = onListClicked,
                    modifier = Modifier.fillMaxWidth()
                )
            }else{
                ListRow(
                    list = list,
                    onListClicked = onClick,
                    onRemove = {
                        onRemove.invoke(list)
                        deleteSnackbar.invoke(list)
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

// TODO: Possibly make the two row composables generic
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
    onListClicked: (UserList) -> Unit,
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
            .clickable { onListClicked(list) }
            .padding(horizontal = 16.dp, vertical = 16.dp)
            .fillMaxWidth()
        ) {
            Text(list.listName!!)
        }
    }
}

/**
 * A composable that displays a full-width [UserList]
 *
 * @param list a [UserList] to display name
 * @param beDeleted (state) display
 * @param onListClicked (event) notify caller that a row was clicked
 * @param modifier modifier for this element
 * */
@Composable
fun DeleteListRow(
    list: UserList,
    beDeleted: Boolean,
    onListClicked: (UserList) -> Unit,
    modifier: Modifier = Modifier
){
    Surface(
        modifier = Modifier.padding(8.dp),
        color = MaterialTheme.colors.primaryVariant,
        shape = RoundedCornerShape(10.dp)
    ) {
        Row(modifier
            .clickable { onListClicked(list) }
            .padding(horizontal = 16.dp, vertical = 16.dp)
            .fillMaxSize()
        ){
            Text(list.listName!!)
            // Show a check mark if list was selected to be deleted
            if(beDeleted){
                Icon(
                    Icons.Default.Done,
                    contentDescription = "Makes a list for deletion.",
                    modifier = Modifier.padding(start = 50.dp)
                )
            }
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

@ExperimentalComposeUiApi
@ExperimentalAnimationApi
@Preview
@Composable
fun PreviewEmptyHomeScreen(){
    HomeBody(
        lists = emptyList(),
        deleteLists = emptyList(),
        onAddList = {},
        onEditDone = {},
        onRemoveList = {},
        onListClicked = {},
        onRemoveLists = {},
        onCancel = {},
        onClick = {}
    )
}

@ExperimentalComposeUiApi
@ExperimentalAnimationApi
@Preview
@Composable
fun PreviewHomeScreen(){
    val names = listOf(
        UserList(1, "Shopping List"),
        UserList(2, "School Supplies"),
        UserList(3, "Road Trip Itinerary")
    )

    HomeBody(
        lists = names,
        deleteLists = emptyList(),
        onAddList = {},
        onEditDone = {},
        onRemoveList = {},
        onListClicked = {},
        onRemoveLists = {},
        onCancel = {},
        onClick = {}
    )
}