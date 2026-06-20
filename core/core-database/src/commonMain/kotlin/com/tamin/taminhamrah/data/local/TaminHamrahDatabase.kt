package com.tamin.taminhamrah.data.local

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.tamin.taminhamrah.data.local.dao.CityProvinceDao
import com.tamin.taminhamrah.data.local.dao.RecipientDao
import com.tamin.taminhamrah.data.local.dao.TestDao
import com.tamin.taminhamrah.data.local.dao.UserDao
import com.tamin.taminhamrah.data.local.entity.CityEntity
import com.tamin.taminhamrah.data.local.entity.IdentityInfoEntity
import com.tamin.taminhamrah.data.local.entity.ProvinceEntity
import com.tamin.taminhamrah.data.local.entity.RecipientEntity
import com.tamin.taminhamrah.data.local.entity.TestEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

@Database(
    entities = [
        TestEntity::class,
        ProvinceEntity::class,
        CityEntity::class,
        IdentityInfoEntity::class,
        RecipientEntity::class,
    ],
    version = 3,
)
@ConstructedBy(TaminXDatabaseConstructor::class)
expect abstract class TaminXDatabase : RoomDatabase {
    abstract fun testDao(): TestDao
    abstract fun cityProvinceDao(): CityProvinceDao
    abstract fun userDao(): UserDao
    abstract fun recipientDao(): RecipientDao
}

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object TaminXDatabaseConstructor : RoomDatabaseConstructor<TaminXDatabase>

fun getRoomDatabase(builder: RoomDatabase.Builder<TaminXDatabase>): TaminXDatabase {
    return builder
        .fallbackToDestructiveMigration(dropAllTables = true)
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}

internal const val DB_FILE_NAME = "TaminHamrah.db"
