package com.example.listapp.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ListDao {

    /** Functions for UserList entity */
    // Insert a new list into the database
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertList(list: UserList)

    // Update an existing list
    @Update
    suspend fun updateList(list: UserList)

    // Select a list with the given id
    @Query("SELECT * FROM list_table WHERE id = :key")
    suspend fun selectList(key: Long): UserList

    // Select all lists
    @Query("SELECT * FROM list_table ORDER BY id")
    fun selectAllLists(): LiveData<List<UserList>>

    // Select the most recently created list
    @Query("SELECT * FROM list_table ORDER BY id DESC LIMIT 1")
    suspend fun selectNewList(): UserList

    // Delete a list
    @Delete
    suspend fun deleteList(list: UserList)

    // Delete all lists
    @Query("DELETE FROM list_table")
    suspend fun deleteAllLists()

    /** Functions for Item entity */
    // Insert a new item into the table
    @Insert
    suspend fun insertItem(item: Item)

    // Update an existing item
    @Update
    suspend fun updateItem(item: Item)

    // Delete an item from the table
    @Delete
    suspend fun deleteItem(item: Item)

    // Delete all items
    @Query("DELETE FROM item_table")
    suspend fun deleteAllItems()

    // Delete all items in a list
    @Query("DELETE FROM item_table WHERE list_id = :key")
    suspend fun deleteItems(key: Long)

    /** Functions for ListWithItems relationship */
    // Get all items of a list
    @Transaction
    @Query("SELECT * FROM list_table WHERE id = :key")
    //suspend fun getListWithItems(key: Long): List<ListWithItems>
    fun getListWithItems(key: Long): LiveData<List<ListWithItems>>
}