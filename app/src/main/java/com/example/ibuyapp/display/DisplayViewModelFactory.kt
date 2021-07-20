package com.example.ibuyapp.display

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.ibuyapp.data.AppDao

class DisplayViewModelFactory(private val dataSource: AppDao) : ViewModelProvider.Factory {

    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(DisplayViewModel::class.java)){
            return DisplayViewModel(dataSource) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}