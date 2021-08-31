package com.example.listapp.data

import androidx.lifecycle.LiveData

class ListRepository(private val listDao: ListDao) {

    val readAllLists : LiveData<List<UserList>> = listDao.selectAllLists()

    suspend fun addList(list: UserList){
        listDao.insertList(list)
    }

    suspend fun updateList(list: UserList){
        listDao.updateList(list)
    }

//    suspend fun deleteList(list: UserList){
//        listDao.deleteItems(list.id)
//        listDao.deleteList(list)
//    }

}