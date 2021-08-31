package com.example.listapp.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.listapp.data.ListDao
import com.example.listapp.data.UserList
import kotlinx.coroutines.launch

/**
 * The [ViewModel] for the Home Screen. It loads in any list names into a list to be displayed.
 *
 * */
class HomeViewModel(dataSource: ListDao): ViewModel() {

    // Hold a reference to the database via ListDao
    private val database = dataSource

    // List of list names
//    var lists = mutableStateListOf<UserList>()
//     private set
    private var _lists = database.selectAllLists()
    val lists: LiveData<List<UserList>> = _lists

    private var currentName by mutableStateOf("")
    val newName: String
        get() = currentName

    /** State variables */
    private var _navigateToManageList by mutableStateOf(false)
    val navigateToManageList: Boolean
        get() = _navigateToManageList

    // Passes the id of a new list to ManageList
    private var _newListId: Long? by mutableStateOf(null)
    val newListId: Long?
        get() = _newListId

    // Snackbar state
    private var _showSnackbar by mutableStateOf(false)
    val showSnackbar: Boolean
        get() = _showSnackbar

//    fun getLists(){
//        viewModelScope.launch {
//            lists = database.selectAllLists()
//        }
//    }

    /**
     * Let ListHome know to navigate to ManageList when a name is clicked
     *
     * @param list a [UserList] that was clicked
     * */
    fun onNameClicked(list: UserList){
        _navigateToManageList = true

        // Pass list's id to ManageList screen
        _newListId = list.id
    }

    fun onNameDone(){
        if(currentName.isNotBlank()){
            createNewList(currentName)
            currentName = ""
        }
    }

    fun onSaveName(name: String){
        // Checks if user did not press 'Enter' (which saves the name
        if(currentName != name){
            currentName = name
        }

    }

    /**
     * Create a new list with the inputted name
     *
     * @param name The name of the new list
     * */
    private fun createNewList(name: String){

        viewModelScope.launch {
            insertNewList(UserList(0L, name))
        }
    }

    private suspend fun insertNewList(list: UserList){
        database.insertList(list)

        // Save the new list's id
        // FIXME: java.lang.IllegalStateException: Cannot access database on the main thread since
        //  it may potentially lock the UI for a long period of time.
        _newListId = database.selectNewList().id
    }

    /**
     * Removes the given list from the database
     *
     * @param list a [UserList] that will be removed from the database
     * */
    fun onRemove(list: UserList){
        viewModelScope.launch {
            deleteList(list)
        }
    }

    /**
     * Delete the list and all the items
     * */
    private suspend fun deleteList(list: UserList){
        // Delete all items in list first
        database.deleteItems(list.id)

        // Delete the list
        database.deleteList(list)
    }

    /**
     * Remove all lists and items
     * */
    fun onRemoveAllLists(){
        viewModelScope.launch {
            database.deleteAllItems()

            database.deleteAllLists()
        }
    }
}