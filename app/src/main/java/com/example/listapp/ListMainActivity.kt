package com.example.listapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.listapp.data.UserList
import com.example.listapp.home.*
import com.example.listapp.manage.ManageBody
import com.example.listapp.ui.theme.ListAppTheme
import com.example.listapp.viewmodel.AppViewModel
import com.example.listapp.viewmodel.AppViewModelFactory
import com.google.gson.Gson
import timber.log.Timber

class ListMainActivity : ComponentActivity() {



    @OptIn(ExperimentalComposeUiApi::class)
    @ExperimentalAnimationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Timber.i("onCreate called.")

        val application = requireNotNull(this).application

        val viewModelFactory = AppViewModelFactory(application)
        val viewModel = ViewModelProvider(this, viewModelFactory)
            .get(AppViewModel::class.java)

        // The app draws behind the system bars, so we need to handle fitting system windows
        //WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            ListApp(viewModel = viewModel)
        }
    }

    override fun onResume() {
        super.onResume()
        Timber.i("onResume called.")
    }

    override fun onRestart() {
        super.onRestart()
        Timber.i("onRestart called.")
    }

    override fun onStart() {
        super.onStart()
        Timber.i("onStart called.")
    }

    override fun onPause() {
        super.onPause()
        Timber.i("onPause called.")
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.i("onDestroy called.")
    }

    override fun onStop() {
        super.onStop()
        Timber.i("onStop called.")
    }
}

@ExperimentalComposeUiApi
@ExperimentalAnimationApi
@Composable
fun ListApp(viewModel: AppViewModel){
    ListAppTheme {
        Surface(color = MaterialTheme.colors.primary) {
            var showLoadingScreen by remember { mutableStateOf(true) }

            if(showLoadingScreen){
                LandingScreen(onTimeout = { showLoadingScreen = false} )
            }else{
                val navController = rememberNavController()

                Scaffold { innerPadding ->
                    ListAppNavHost(
                        navController = navController,
                        viewModel = viewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@ExperimentalComposeUiApi
@ExperimentalAnimationApi
@Composable
fun ListAppNavHost(
    navController: NavHostController,
    viewModel: AppViewModel,
    modifier: Modifier = Modifier
){
    NavHost(
        navController = navController,
        startDestination = ListScreen.Home.name,
        modifier = modifier
    ){
        composable(ListScreen.Home.name){
            HomeScreen(
                navController = navController,
                viewModel = viewModel
            )
        }
        composable(
            route = "${ListScreen.Manage.name}/{list}",
            //route = ListScreen.Manage.name,
            arguments = listOf(
                navArgument("list"){
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            // Retrieve data
            backStackEntry.arguments?.getString("list")?.let { json ->
                val list = Gson().fromJson(json, UserList::class.java)
                ManageScreen(navController = navController, viewModel = viewModel, list = list)
            }
        }
    }
}

@ExperimentalComposeUiApi
@ExperimentalAnimationApi
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: AppViewModel
){
    val lists =  viewModel.allLists.observeAsState(initial = listOf()).value

    /**
     * Helper function to convert [UserList] to a json string that will be
     * sent to the Manage screen
     *
     * @param list a [UserList] that will be converted
     * */
    fun navigateToManage(list: UserList){
        val listJson = Gson().toJson(list)
        navController.navigate(route = "${ListScreen.Manage.name}/$listJson"){
            launchSingleTop = true
        }
    }

    HomeBody(
        lists = lists,
        deleteLists = viewModel.deleteLists,
        onAddList = viewModel::onSaveName,
        onEditDone = viewModel::onNameDone,
        onRemoveList = viewModel::onRemoveList,
        onListClicked = viewModel::onElemClicked,
        onRemoveLists = viewModel::onDeleteLists,
        onCancel = viewModel::onCancelDelete,
        onClick = { list ->
            navigateToManage(list = list)
        }
    )
}

@ExperimentalComposeUiApi
@Composable
fun ManageScreen(
    navController: NavController,
    viewModel: AppViewModel,
    list: UserList
){

    ManageBody(
        viewModel = viewModel,
        list = list,
        onReturnHome = { navController.popBackStack() }
    )
}