package com.example.matrimony

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.matrimony.data.local.dao.UserDao
import com.example.matrimony.data.local.database.AppDatabase
import com.example.matrimony.data.local.entity.UserEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UserDaoTest {
    private lateinit var db: AppDatabase
    private lateinit var dao: UserDao

    @Before
    fun setup() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
        dao = db.userDao()
    }

    @After
    fun teardown() {
        db.close()
    }

    @Test
    fun insert_and_update_decision() = runBlocking {
        val entity = UserEntity(
            id = "id1",
            fullName = "Test User",
            age = 30,
            city = "City",
            state = "State",
            country = "Country",
            imageLargeUrl = "",
            imageThumbUrl = "",
            decision = "PENDING",
            lastUpdatedEpochMs = 1L
        )
        dao.upsertAll(listOf(entity))
        dao.updateDecision("id1", "ACCEPTED")
        val list = dao.observeAll().first()
        assertEquals("ACCEPTED", list.first().decision)
    }
}