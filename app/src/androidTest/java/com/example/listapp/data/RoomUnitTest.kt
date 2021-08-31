package com.example.listapp.data

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.listapp.utilities.getValue
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertThat
import org.hamcrest.core.IsEqual.equalTo
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

/**
 * A class to do unit tests on the Room Database for the app.
 * */
@RunWith(AndroidJUnit4::class)
class RoomUnitTest {
    private lateinit var listDao: ListDao
    private lateinit var db: ListDatabase

    private val listA = UserList(1, "Shopping List")
    private val itemsA = listOf("Milk", "Tofu", "Green Beans", "Toilet Paper", "Dish soap")
    private val listB = UserList(2, "Wedding Registry")
    private val listC = UserList(3, "Christmas Gift List")

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    // Create an in-memory version of database
    @Before
    fun createDb() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, ListDatabase::class.java).build()
        listDao = db.listDao

        listDao.insertList(listA)
        listDao.insertList(listB)
        listDao.insertList(listC)
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    /**
     * Tests retrieving all lists in the database
     * */
    @Test
    @Throws(Exception::class)
    fun testGetLists(){
        val allLists = getValue(listDao.selectAllLists())

        assertThat(allLists.size, equalTo(3))

        // Ensure lists are sorted by id
        assertThat(allLists[0], equalTo(listA))
        assertThat(allLists[1], equalTo(listB))
        assertThat(allLists[2], equalTo(listC))
    }

    /**
     * Tests retrieving a list from the database
     * */
    @Test
    fun testGetList(){
        val list = listDao.selectList(listB.id)
        assertThat(list, equalTo(listB))
    }

    /**
     * Tests inserting items into a list
     * */
    @Test
    fun testInsertAndReadItems(){
        runBlocking {
            for(item in itemsA){
                listDao.insertItem(Item(0L, 1, item))
            }

            val result = listDao.getListWithItems(1)[0].items

            assertThat(result[0].itemStr, equalTo(itemsA[0]))
            assertThat(result[1].itemStr, equalTo(itemsA[1]))
            assertThat(result[2].itemStr, equalTo(itemsA[2]))
            assertThat(result[3].itemStr, equalTo(itemsA[3]))
            assertThat(result[4].itemStr, equalTo(itemsA[4]))
        }
    }

    /**
     * Test deleting an empty list
     * */
    @Test
    fun testDeleteEmptyList(){
        runBlocking {
            listDao.deleteList(listB)

            val allLists = getValue(listDao.selectAllLists())
            assertThat(allLists.size, equalTo(2))

            assertThat(allLists[0], equalTo(listA))
            assertThat(allLists[1], equalTo(listC))
        }
    }

    /**
     * Test deleting a list with items
     * */
    @Test
    fun testDeleteList(){
        runBlocking {
            // Delete all items in the list
            listDao.deleteItems(1)

            val result = listDao.getListWithItems(1)[0].items

            assertThat(result.size, equalTo(0))

            // Delete the list
            listDao.deleteList(listA)

            val allLists = getValue(listDao.selectAllLists())
            assertThat(allLists.size, equalTo(2))

            assertThat(allLists[0], equalTo(listB))
            assertThat(allLists[1], equalTo(listC))
        }
    }

    /**
     * Test deleting all lists
     * */
    @Test
    fun testDeleteAllLists(){
        runBlocking {
            listDao.deleteAllLists()

            val allLists = getValue(listDao.selectAllLists())
            assertThat(allLists.size, equalTo(0))
        }
    }
}