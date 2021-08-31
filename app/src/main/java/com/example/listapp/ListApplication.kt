package com.example.listapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModelProvider
import com.example.listapp.data.ListDatabase
import com.example.listapp.data.UserList
import com.example.listapp.home.HomeViewModel
import com.example.listapp.home.HomeViewModelFactory
import com.example.listapp.home.LandingScreen
import com.example.listapp.home.ListHome
import com.example.listapp.ui.theme.ListAppTheme

class ListApplication : ComponentActivity() {

    @OptIn(ExperimentalComposeUiApi::class)
    @ExperimentalAnimationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val application = requireNotNull(this).application
        val dataSource = ListDatabase.getInstance(application).listDao

        val viewModelFactory = HomeViewModelFactory(dataSource)
        val viewModel = ViewModelProvider(this, viewModelFactory)
            .get(HomeViewModel::class.java)

        // The app draws behind the system bars, so we need to handle fitting system windows
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            ListAppTheme {
                MainScreen(viewModel)
            }
        }
    }
}

/**
 * Shows either the landing screen or the home screen
 * */
@ExperimentalComposeUiApi
@ExperimentalAnimationApi
@Composable
private fun MainScreen(viewModel: HomeViewModel){
    Surface(color = MaterialTheme.colors.primary) {
        var showLandingScreen by remember { mutableStateOf(true) }

        if(showLandingScreen) {
            LandingScreen(onTimeout = {showLandingScreen = false} )
        }else{
            // FIXME: Change to mutableStateListOf
            val lists: List<UserList> by viewModel.lists.observeAsState(initial = listOf())

            ListHome(
                //lists = viewModel.lists,
                lists = lists,
                newName = viewModel.newName,
                onAddList = viewModel::onSaveName,
                onRemoveList = viewModel::onRemove,
                onRemoveAllLists = viewModel::onRemoveAllLists,
                onEditDone = viewModel::onNameDone
            )
        }
    }
}