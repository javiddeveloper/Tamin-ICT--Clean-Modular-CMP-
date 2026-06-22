package com.tamin.taminhamrah.data.local

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.tamin.taminhamrah.data.local.dao.BranchDao
import com.tamin.taminhamrah.data.local.dao.CityProvinceDao
import com.tamin.taminhamrah.data.local.dao.ContractDao
import com.tamin.taminhamrah.data.local.dao.FreelancePremiumRangeDao
import com.tamin.taminhamrah.data.local.dao.PersonalInboxDao
import com.tamin.taminhamrah.data.local.dao.PersonalDao
import com.tamin.taminhamrah.data.local.dao.RecipientDao
import com.tamin.taminhamrah.data.local.dao.RegistrationInfoDao
import com.tamin.taminhamrah.data.local.dao.SpcPremiumRateDao
import com.tamin.taminhamrah.data.local.dao.UserRequestDao
import com.tamin.taminhamrah.data.local.dao.TestDao
import com.tamin.taminhamrah.data.local.dao.UserDao
import com.tamin.taminhamrah.data.local.entity.BranchEntity
import com.tamin.taminhamrah.data.local.entity.CityEntity
import com.tamin.taminhamrah.data.local.entity.ContractEntity
import com.tamin.taminhamrah.data.local.entity.FreelancePremiumRangeEntity
import com.tamin.taminhamrah.data.local.entity.IdentityInfoEntity
import com.tamin.taminhamrah.data.local.entity.PersonalInboxItemEntity
import com.tamin.taminhamrah.data.local.entity.PersonalInboxSizeEntity
import com.tamin.taminhamrah.data.local.entity.PersonalInfoEntity
import com.tamin.taminhamrah.data.local.entity.ProvinceEntity
import com.tamin.taminhamrah.data.local.entity.RecipientEntity
import com.tamin.taminhamrah.data.local.entity.RegistrationInfoEntity
import com.tamin.taminhamrah.data.local.entity.SpcPremiumRateEntity
import com.tamin.taminhamrah.data.local.entity.UserRequestEntity
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
        UserRequestEntity::class,
        PersonalInfoEntity::class,
        PersonalInboxItemEntity::class,
        PersonalInboxSizeEntity::class,
        ContractEntity::class,
        RegistrationInfoEntity::class,
        BranchEntity::class,
        SpcPremiumRateEntity::class,
        FreelancePremiumRangeEntity::class,
    ],
    version = 1,
)
@ConstructedBy(TaminXDatabaseConstructor::class)
expect abstract class TaminXDatabase : RoomDatabase {
    abstract fun testDao(): TestDao
    abstract fun cityProvinceDao(): CityProvinceDao
    abstract fun userDao(): UserDao
    abstract fun recipientDao(): RecipientDao
    abstract fun personalInboxDao(): PersonalInboxDao
    abstract fun personalDao(): PersonalDao
    abstract fun userRequestDao(): UserRequestDao
    abstract fun contractDao(): ContractDao
    abstract fun registrationInfoDao(): RegistrationInfoDao
    abstract fun branchDao(): BranchDao
    abstract fun spcPremiumRateDao(): SpcPremiumRateDao
    abstract fun freelancePremiumRangeDao(): FreelancePremiumRangeDao
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
