package com.example.listapp.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "item_table")
data class Item(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "itemId")
    val itemId: Long = 0L,

    @ColumnInfo(name = "list_id")
    val listId: Long,

    @ColumnInfo(name = "item_string")
    var itemStr: String
)
