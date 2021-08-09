package com.example.ibuyapp.data

import androidx.room.*

/**
 * An [Entity] for an item
 * */
@Entity(tableName = "item_table")
data class Item(
    @PrimaryKey(autoGenerate = true)
    val itemId: Long = 0L,

    @ColumnInfo(name = "item_string")
    var itemString: String,

    @ColumnInfo(name = "listId")
    val listId: Long
)

/**
 * An [Entity] for a list
 * */
@Entity(tableName = "list_table")
data class UserList(
    @PrimaryKey(autoGenerate = true)
    val listId: Long = 0L,

    @ColumnInfo(name = "list_name")
    var listName: String
)

/**
 * Represents the relationship between a list and zero or more items
 * */
data class ListWithItems(
    @Embedded val list: UserList,
    @Relation(
        parentColumn = "listId",
        entityColumn = "listId"
    )
    var items: List<Item>
)
