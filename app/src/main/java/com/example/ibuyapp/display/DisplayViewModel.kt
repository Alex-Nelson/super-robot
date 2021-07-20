package com.example.ibuyapp.display

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.ibuyapp.data.AppDao
import com.example.ibuyapp.util.DisplayViewState

/**
 * The [ViewModel] for the DisplayFragment. It loads in any list names into a list for the adapter
 * to bind in the UI. It also lets the Fragment know if it needs to display the empty state layout
 * or the RecyclerView lyaout
 * */
class DisplayViewModel(dataSource: AppDao): ViewModel() {

    val viewState = MutableLiveData<DisplayViewState>()
    private var state: DisplayViewState
        get() { return viewState.value!! }
        set(value) { viewState.value = value }

    // Hold a reference to the database via AppDao
    private val database = dataSource

    // List of list names
    var listNames = database.getAllLists()

    /** State variables */
    private val _navigateToManageList = MutableLiveData<Boolean>()
    val navigateToManageList: LiveData<Boolean> get() = _navigateToManageList

    init {

        state = DisplayViewState(
            isEmpty = true,
            lists = listNames.value
        )
    }

    /**
     * Let the DisplayFragment know to navigate to ManageListFragment when
     * Floating Action Button (FAB) is clicked.
     * */
    fun onFabClicked(){
        _navigateToManageList.value = true
    }

    /**
     * Call this immediately after navigating to ManageListFragment
     * */
    fun doneNavigatingToManageList(){
        _navigateToManageList.value = false
    }
}