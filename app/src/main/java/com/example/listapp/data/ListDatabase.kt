package com.example.listapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [UserList::class, Item::class], version = 4, exportSchema = false)
abstract class ListDatabase :RoomDatabase() {

    abstract val listDao: ListDao

    companion object {
        @Volatile
        private var INSTANCE: ListDatabase? = null

        /**
         * Create/Get an instance of a Room Database
         * */
        fun getInstance(context: Context): ListDatabase {
            synchronized(this){
                return INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    ListDatabase::class.java,
                    "list_db"
                ).fallbackToDestructiveMigration()
                .build().also {
                    INSTANCE = it
                }
            }
        }
    }
}