package com.example.ibuyapp.data

import androidx.lifecycle.LiveData
import androidx.room.*

/**
 * The [Dao] for the entities
 * */
@Dao
interface AppDao {

    /**
     * Functions for the UserList entity
     * */
    // Insert a new list into the table
    @Insert
    suspend fun insertList(list: UserList)

    // Update the list
    @Update
    suspend fun updateList(list: UserList)

    // Select a list from the table with the id
    @Query("SELECT * FROM list_table WHERE listId = :key")
    suspend fun getList(key: Long): UserList

    // Select the most recently created list
    @Query("SELECT * FROM list_table ORDER BY listId DESC LIMIT 1")
    suspend fun getNewList(): UserList

    // Delete a list from the table
    @Query("DELETE FROM list_table WHERE listId = :key")
    suspend fun deleteList(key: Long)

    // Delete all lists from the table
    @Query("DELETE FROM list_table")
    suspend fun clearListTable()

    // Select all lists from the table
    @Query("SELECT * FROM list_table")
    fun getAllLists(): LiveData<List<UserList>>

    /**
     * Functions for the Item entity
     * */
    // Insert a new item into the table
    @Insert
    suspend fun insertItem(item: Item)

    // Update an item
    @Update
    suspend fun updateItem(item: Item)

    // Delete an item from the table
    @Query("DELETE FROM item_table WHERE itemId = :key")
    suspend fun deleteItem(key: Long)

    // Delete all items
    @Query("DELETE FROM item_table")
    suspend fun clearItemTable()

    /**
     * Function for the one-to-many relationship
     * */
    // Get all items associated with the list
    @Transaction
    @Query("SELECT * FROM list_table WHERE listId = :key")
    suspend fun getListWithItems(key: Long): LiveData<List<ListWithItems>>
}