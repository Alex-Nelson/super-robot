package com.example.listapp.data

import androidx.lifecycle.LiveData

/**
 * A repository to allow abstract access.
 * */
class ListRepository(private val listDao: ListDao) {

    fun getAllLists(): LiveData<List<UserList>> = listDao.selectAllLists()

    fun getAllItems(key: Long): LiveData<List<ListWithItems>> = listDao.getListWithItems(key)

    suspend fun addList(list: UserList) = listDao.insertList(list)

    suspend fun updateList(list: UserList) = listDao.updateList(list)

    suspend fun deleteList(list: UserList) = listDao.deleteList(list)

    suspend fun deleteAllLists() = listDao.deleteAllLists()

    suspend fun selectList(key: Long): UserList = listDao.selectList(key)

    suspend fun selectNewList(): UserList = listDao.selectNewList()

    suspend fun addItem(item: Item) = listDao.insertItem(item)

    suspend fun deleteItem(item: Item) = listDao.deleteItem(item)

    suspend fun updateItem(item: Item) = listDao.updateItem(item)

    suspend fun deleteItems(key: Long) = listDao.deleteItems(key)

    suspend fun deleteAllItems() = listDao.deleteAllItems()

}