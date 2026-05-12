package com.tamin.taminhamrah.data.local

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.tamin.taminhamrah.data.local.dao.TestDao
import com.tamin.taminhamrah.data.local.entity.TestEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

@Database(entities = [TestEntity::class], version = 1)
@ConstructedBy(TaminXDatabaseConstructor::class)
expect abstract class TaminXDatabase : RoomDatabase {
    abstract fun testDao(): TestDao
}

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object TaminXDatabaseConstructor : RoomDatabaseConstructor<TaminXDatabase>

fun getRoomDatabase(builder: RoomDatabase.Builder<TaminXDatabase>): TaminXDatabase {
    return builder
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}

internal const val DB_FILE_NAME = "TaminHamrah.db"
