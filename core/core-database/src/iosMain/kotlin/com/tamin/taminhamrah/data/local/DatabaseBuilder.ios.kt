package com.tamin.taminhamrah.data.local

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.tamin.taminhamrah.data.local.dao.BranchDao
import com.tamin.taminhamrah.data.local.dao.CityProvinceDao
import com.tamin.taminhamrah.data.local.dao.ContractDao
import com.tamin.taminhamrah.data.local.dao.PersonalInboxDao
import com.tamin.taminhamrah.data.local.dao.PersonalDao
import com.tamin.taminhamrah.data.local.dao.RecipientDao
import com.tamin.taminhamrah.data.local.dao.RegistrationInfoDao
import com.tamin.taminhamrah.data.local.dao.UserRequestDao
import com.tamin.taminhamrah.data.local.dao.TestDao
import com.tamin.taminhamrah.data.local.dao.UserDao
import com.tamin.taminhamrah.data.local.entity.BranchEntity
import com.tamin.taminhamrah.data.local.entity.CityEntity
import com.tamin.taminhamrah.data.local.entity.ContractEntity
import com.tamin.taminhamrah.data.local.entity.IdentityInfoEntity
import com.tamin.taminhamrah.data.local.entity.PersonalInboxItemEntity
import com.tamin.taminhamrah.data.local.entity.PersonalInboxSizeEntity
import com.tamin.taminhamrah.data.local.entity.PersonalInfoEntity
import com.tamin.taminhamrah.data.local.entity.ProvinceEntity
import com.tamin.taminhamrah.data.local.entity.RecipientEntity
import com.tamin.taminhamrah.data.local.entity.RegistrationInfoEntity
import com.tamin.taminhamrah.data.local.entity.UserRequestEntity
import com.tamin.taminhamrah.data.local.entity.TestEntity
import platform.Foundation.NSHomeDirectory

@Database(
    entities = [
        TestEntity::class,
        ProvinceEntity::class,
        CityEntity::class,
        IdentityInfoEntity::class,
        RecipientEntity::class,
        UserRequestEntity::class,
        PersonalInfoEntity::class,
        PersonalInboxItemEntity::class,
        PersonalInboxSizeEntity::class,
        ContractEntity::class,
        RegistrationInfoEntity::class,
        BranchEntity::class,
    ],
    version = 1,
)
@Suppress("ACTUAL_ANNOTATIONS_NOT_MATCH_EXPECT")
actual abstract class TaminXDatabase : RoomDatabase() {
    actual abstract fun testDao(): TestDao
    actual abstract fun cityProvinceDao(): CityProvinceDao
    actual abstract fun userDao(): UserDao
    actual abstract fun recipientDao(): RecipientDao
    actual abstract fun personalInboxDao(): PersonalInboxDao
    actual abstract fun userRequestDao(): UserRequestDao
    actual abstract fun personalDao(): PersonalDao
    actual abstract fun contractDao(): ContractDao
    actual abstract fun registrationInfoDao(): RegistrationInfoDao
    actual abstract fun branchDao(): BranchDao
}

fun getDatabaseBuilder(): RoomDatabase.Builder<TaminXDatabase> {
    val dbFilePath = NSHomeDirectory() + "/Documents/$DB_FILE_NAME"
    return Room.databaseBuilder<TaminXDatabase>(
        name = dbFilePath,
    )
}
