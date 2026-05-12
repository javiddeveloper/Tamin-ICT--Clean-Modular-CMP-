package com.tamin.taminhamrah.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import com.tamin.taminhamrah.data.local.dao.TestDao
import com.tamin.taminhamrah.data.local.entity.TestEntity

@Database(entities = [TestEntity::class], version = 1)
@Suppress("ACTUAL_ANNOTATIONS_NOT_MATCH_EXPECT")
actual abstract class TaminXDatabase : RoomDatabase() {
    actual abstract fun testDao(): TestDao
}

fun getDatabaseBuilder(context: Context): RoomDatabase.Builder<TaminXDatabase> {
    val dbFile = context.getDatabasePath(DB_FILE_NAME)
    return Room.databaseBuilder<TaminXDatabase>(
        context = context.applicationContext,
        name = dbFile.absolutePath,
    )
}
