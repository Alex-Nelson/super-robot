package com.example.listapp.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.*
import com.example.listapp.data.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * The [ViewModel] for the Home Screen. It loads in any list names into a list to be displayed.
 *
 * */
class AppViewModel(application: Application):  AndroidViewModel(application){

    private val repository: ListRepository
    var allLists: LiveData<List<UserList>>

    var allItems: LiveData<List<ListWithItems>>? = null

    // List of lists to delete
    var deleteLists = mutableStateListOf<UserList>()
        private set

    // List of items to delete
    var deleteItems = mutableStateListOf<Item>()
        private set

    private var currentName by mutableStateOf("")

    private var _currList: UserList? by mutableStateOf(null)
    private val currList: UserList?
        get() = _currList

    // Private state for current item
    private var currentEditPosition by mutableStateOf(-1)

    val currentEditItem: Item?
        get() = allItems!!.value?.get(0)?.items?.getOrNull(currentEditPosition)

    /** Initialize the repository and get all lists from database */
    init {
        val appDB = ListDatabase.getInstance(application).listDao
        repository = ListRepository(appDB)
        allLists = repository.getAllLists()
    }

    /**
     * Create a new list with the user input name
     *
     * @param name a string of the list's name
     * */
    private fun createList(name: String){
        viewModelScope.launch(Dispatchers.IO){
            val newList = UserList(0L, name)
            repository.addList(newList)

            // Set current list to new list
            _currList = repository.selectNewList()
        }
    }

    /**
     * Update the current list's name
     * */
    private fun updateList(name: String){
        viewModelScope.launch(Dispatchers.IO){
            _currList!!.listName = name
            repository.updateList(currList!!)
        }
    }

    /**
     * Delete a list from the database.
     *
     * @param list a [UserList] deleted by the user
     * */
    fun onRemoveList(list: UserList){
        viewModelScope.launch(Dispatchers.IO) {
            // Delete any items the list has
            val listItems = repository.getAllItems(list.id).value?.get(0)?.items
            if(listItems != null){
                repository.deleteItems(list.id)
            }

            // Delete the list
            repository.deleteList(list)
        }
    }

    /**
     * Remove all lists and items.
     * */
    private fun removeAllData(){
        viewModelScope.launch(Dispatchers.IO){
            repository.deleteAllItems()
            repository.deleteAllLists()
        }
    }

    /**
     * Delete at least one list.
     *
     * This function is called when user is deleting lists from the Home screen.
     *
     * */
    fun onDeleteLists(){
        if(deleteLists.size == allLists.value!!.size){
            removeAllData()
        }else{
            for(list in deleteLists){
                onRemoveList(list)
            }
        }
    }

    /**
     * Set the current list so user can edit it.
     *
     * @param list a [UserList] that was navigated to
     * */
    fun onNavigateToManage(list: UserList){
        if(currList == null){
            viewModelScope.launch(Dispatchers.IO) {
                _currList = repository.selectList(list.id)
                allItems = repository.getAllItems(list.id)
            }
        }else{
            allItems = repository.getAllItems(list.id)
        }
    }

    /**
     * Reset the current list to null when navigating back to the Home screen.
     * */
    fun onBack(){
        _currList = null
        deleteItems.clear()
    }

    /**
     * Create a new list with the new name or update a current list's name
     *
     * This is called when the user is done typing a name.
     *
     * */
    fun onNameDone(){
        if(currentName.isNotBlank()){
            createList(currentName)
            currentName = ""
        }
    }

    /**
     * Update the list's name
     *
     * @param name a new name for the current list
     * */
    fun onUpdateName(name: String){
        onSaveName(name)
        updateList(currentName)
        currentName = ""
    }

    /**
     * Saves a name if the user did not press 'Enter' to save it.
     *
     * @param name a String gotten from user input
     * */
    fun onSaveName(name: String){
        if(currentName != name){
            currentName = name
        }
    }

    /**
     * Insert a new item into the list
     *
     * @param itemStr a [String] to be saved as a new item in the list
     * */
    fun onAddItem(itemStr: String){
        viewModelScope.launch(Dispatchers.IO){
            val newItem = Item(0L, currList!!.id, itemStr)
            repository.addItem(newItem)
        }
    }

    /**
     * Delete an item
     *
     * @param item an [Item] to be deleted
     * */
    fun onDeleteItem(item: Item){
        viewModelScope.launch(Dispatchers.IO){
            repository.deleteItem(item)
        }
    }

    /**
     * Delete at least one item.
     *
     * This is called when user selects one up to all items in the list to delete.
     * */
    fun onDeleteItems(){
        viewModelScope.launch(Dispatchers.IO){
            for (item in deleteItems){
                repository.deleteItem(item)
            }
        }
    }

    /**
     * Update an item
     *
     * @param item an [Item] that was updated
     * */
    fun onUpdateItem(item: Item){
        viewModelScope.launch(Dispatchers.IO){
            val currentItem = requireNotNull(currentEditItem)
            require(currentItem.itemId == item.itemId){
                "You can only change an item with the same id as currentEditItem."
            }

            repository.updateItem(item)
            onEditItemDone()
        }
    }

    /** Event: onEditDone */
    fun onEditItemDone(){
        currentEditPosition = -1
    }

    /**
     * Event: select an item to be edited
     *
     * @param item an [Item] to edit
     * */
    fun onEditItemSelected(item: Item){
        currentEditPosition = allItems!!.value!![0].items.indexOf(item)
    }

    /**
     * Remove all elements that were to be deleted if user cancels the action
     *
     * @param type a [String] indicating which type of deleting list to clear
     * */
    fun onCancelDelete(type: String){
        if(type == "Lists"){
            deleteLists.clear()
        }else{
            deleteItems.clear()
        }
    }

    /**
     * Add an element to be removed
     *
     * @param elem a generic element selected to be removed
     * */
    private fun <T> addDeletingElem(elem: T){
        if(elem is UserList){
            deleteLists.add(elem)
        }else if(elem is Item){
            deleteItems.add(elem)
        }
    }

    /**
     * Remove an element that was to be removed
     *
     * @param elem a generic element that was selected to not be removed
     * */
    private fun <T> removeDeletingElem(elem: T){
        if(elem is UserList){
            deleteLists.remove(elem)
        }else if(elem is Item){
            deleteItems.remove(elem)
        }
    }

    /**
     * Adds an element to be deleted or removes an element from being deleted when a user clicks
     * an element.
     *
     * @param elem a generic element that was clicked
     * */
    fun <T> onElemClicked(elem: T){
        if(elem is UserList){
            if(deleteLists.contains(elem)){
                removeDeletingElem(elem)
            }else{
                addDeletingElem(elem)
            }
        }else if(elem is Item){
            if(deleteItems.contains(elem)){
                removeDeletingElem(elem)
            }else{
                addDeletingElem(elem)
            }
        }
    }
}