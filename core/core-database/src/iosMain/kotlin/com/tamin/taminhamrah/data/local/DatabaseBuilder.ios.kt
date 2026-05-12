package com.tamin.taminhamrah.data.local

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import com.tamin.taminhamrah.data.local.dao.TestDao
import com.tamin.taminhamrah.data.local.entity.TestEntity
import platform.Foundation.NSHomeDirectory

@Database(entities = [TestEntity::class], version = 1)
@Suppress("ACTUAL_ANNOTATIONS_NOT_MATCH_EXPECT")
actual abstract class TaminXDatabase : RoomDatabase() {
    actual abstract fun testDao(): TestDao
}

fun getDatabaseBuilder(): RoomDatabase.Builder<TaminXDatabase> {
    val dbFilePath = NSHomeDirectory() + "/Documents/$DB_FILE_NAME"
    return Room.databaseBuilder<TaminXDatabase>(
        name = dbFilePath,
    )
}
