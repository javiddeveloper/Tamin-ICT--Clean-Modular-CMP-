package com.tamin.taminhamrah.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.tamin.taminhamrah.data.local.dao.CityProvinceDao
import com.tamin.taminhamrah.data.local.dao.RecipientDao
import com.tamin.taminhamrah.data.local.dao.UserRequestDao
import com.tamin.taminhamrah.data.local.dao.TestDao
import com.tamin.taminhamrah.data.local.dao.UserDao
import com.tamin.taminhamrah.data.local.entity.CityEntity
import com.tamin.taminhamrah.data.local.entity.IdentityInfoEntity
import com.tamin.taminhamrah.data.local.entity.ProvinceEntity
import com.tamin.taminhamrah.data.local.entity.RecipientEntity
import com.tamin.taminhamrah.data.local.entity.UserRequestEntity
import com.tamin.taminhamrah.data.local.entity.TestEntity

@Database(
    entities = [
        TestEntity::class,
        ProvinceEntity::class,
        CityEntity::class,
        IdentityInfoEntity::class,
        RecipientEntity::class,
        UserRequestEntity::class,
    ],
    version = 1,
)
@Suppress("ACTUAL_ANNOTATIONS_NOT_MATCH_EXPECT")
actual abstract class TaminXDatabase : RoomDatabase() {
    actual abstract fun testDao(): TestDao
    actual abstract fun cityProvinceDao(): CityProvinceDao
    actual abstract fun userDao(): UserDao
    actual abstract fun recipientDao(): RecipientDao
    actual abstract fun userRequestDao(): UserRequestDao

}

fun getDatabaseBuilder(context: Context): RoomDatabase.Builder<TaminXDatabase> {
    val dbFile = context.getDatabasePath(DB_FILE_NAME)
    return Room.databaseBuilder<TaminXDatabase>(
        context = context.applicationContext,
        name = dbFile.absolutePath,
    )
}
