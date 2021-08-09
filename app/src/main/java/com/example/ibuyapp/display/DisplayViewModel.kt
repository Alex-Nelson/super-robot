package com.example.ibuyapp.display

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ibuyapp.data.AppDao
import com.example.ibuyapp.data.UserList
import com.example.ibuyapp.util.DisplayViewState
import kotlinx.coroutines.launch

/**
 * The [ViewModel] for the DisplayFragment. It loads in any list names into a list for the adapter
 * to bind in the UI. It also lets the Fragment know if it needs to display the empty state layout
 * or the RecyclerView layout
 * */
class DisplayViewModel(dataSource: AppDao): ViewModel() {

    val viewState = MutableLiveData<DisplayViewState>()
    private var state: DisplayViewState
        get() { return viewState.value!! }
        set(value) { viewState.value = value }

    // Hold a reference to the database via AppDao
    private val database = dataSource

    // List of list names
//    private var _listNames = MutableLiveData<List<UserList>>()
//    val listNames: LiveData<List<UserList>> get() = _listNames
    var listNames = database.getAllLists()

    /** State variables */
    private val _navigateToManageList = MutableLiveData<Boolean>()
    val navigateToManageList: LiveData<Boolean> get() = _navigateToManageList

    private val _buttonSwiped = MutableLiveData<Boolean>()
    val buttonSwiped: LiveData<Boolean> get() = _buttonSwiped

    init {
        // Initialize the state based on if there is at least one list

        state = if(listNames.value?.isEmpty() == true){
            DisplayViewState(
                isEmpty = true,
                lists = listNames.value)
        }else{
            DisplayViewState(
                isEmpty = false,
                lists = listNames.value)
        }
    }

    /**
     * Let the DisplayFragment know to navigate to ManageListFragment when
     * Floating Action Button (FAB) is clicked.
     * */
    fun onFabClicked(){
        _navigateToManageList.value = true
    }

    /**
     * When the user swipes a button to the right, it deletes it
     * */
    fun onSwipeRight(key: Long){

        viewModelScope.launch {
            // TODO: Get and delete all items associated with the list to be deleted
            deleteList(key)
        }
    }

    /**
     * Delete the list and all the items
     * */
    private suspend fun deleteList(key: Long){
        database.deleteList(key)
    }

    /**
     * Call this immediately after navigating to ManageListFragment
     * */
    fun doneNavigatingToManageList(){
        _navigateToManageList.value = false
    }
}