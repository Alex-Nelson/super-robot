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

    // List of lists to delete
    var deleteLists = mutableStateListOf<UserList>()
        private set

    // List of lists
    private var _lists = database.selectAllLists()
    val lists: LiveData<List<UserList>> = _lists

    private var currentName by mutableStateOf("")

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

    // Snackbar for when at least one list is deleted
    private var _deleteListSnackbar by mutableStateOf(false)
    val deleteListSnackbar: Boolean
        get() = _deleteListSnackbar

    /**
     * Set the snackbar state to true
     *
     * @param type (state) The type of snackbar that will shown
     * */
    fun showSnackbar(type: String){
        when (type) {
            "delete" -> {
                _deleteListSnackbar = true
            }
            "show" -> {
                _showSnackbar = true
            }
        }
    }

    /**
     * Set the snackbar state to false
     *
     * @param type (state) The type of snackbar that was shown
     * */
    fun doneShowingSnackbar(type: String){
        when (type) {
            "delete" -> {
                _deleteListSnackbar = false
            }
            "show" -> {
                _showSnackbar = false
            }
        }
    }

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
        _newListId = database.selectNewList().id
    }

    /**
     * Removes the given list from the database when a list is swiped by user.
     *
     * @param list a [UserList] that will be removed from the database
     * */
    fun onRemove(list: UserList){
        viewModelScope.launch {
            deleteList(list)
        }
    }

    /**
     * Delete the list and all the items.
     *
     * @param list a [UserList] that will be removed from the database
     * */
    private suspend fun deleteList(list: UserList){
        val listItems = database.getListWithItems(list.id)[0].items

        // Delete all items in list first if there are any
        if(listItems.isNotEmpty()){
            database.deleteItems(list.id)
        }

        // Delete the list
        database.deleteList(list)
    }

    /**
     * Remove all lists and items
     * */
    private fun onRemoveAllLists(){
        viewModelScope.launch {
            database.deleteAllItems()

            database.deleteAllLists()

            // TODO: Show snackbar confirming all lists were deleted
        }
    }

    /**
     * Adds a list to be deleted or removes a list from being deleted when user clicks a list.
     *
     * @param list a [UserList] that was clicked
     * */
    fun onListClicked(list: UserList){
        if(deleteLists.contains(list)){
            removeDeletingList(list)
        }else{
            addDeletingList(list)
        }
    }

    /**
     * Add a list to be removed
     *
     * @param list a [UserList] that will be removed
     * */
    private fun addDeletingList(list: UserList){
        deleteLists.add(list)
    }

    /**
     * Remove a list that was to be removed
     *
     * @param list a [UserList] that will not be removed
     * */
    private fun removeDeletingList(list: UserList){
        deleteLists.remove(list)

        // TODO: Show snackbar confirming list was deleted
    }

    /**
     * Delete all lists that were selected by user
     * */
    fun onDeleteLists(){
        if(deleteLists.size == lists.value!!.size){
            onRemoveAllLists()
        }
        else{
            viewModelScope.launch {
                for(list in deleteLists){
                    deleteList(list)
                }

                // TODO: Show snackbar confirming all selected lists were deleted
            }
        }
    }

    /**
     * Remove all lists that were to be deleted if user cancels the action
     * */
    fun onCancelDelete(){
        deleteLists.clear()
    }
}