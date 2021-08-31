package com.example.listapp.data

import androidx.room.Embedded
import androidx.room.Relation


data class ListWithItems(
    @Embedded val list: UserList,
    @Relation(
        parentColumn = "id",
        entityColumn = "list_id"
    )
    val items: List<Item>
)
