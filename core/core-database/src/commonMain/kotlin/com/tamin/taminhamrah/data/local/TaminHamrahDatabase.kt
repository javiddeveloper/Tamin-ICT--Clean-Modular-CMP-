package com.tamin.taminhamrah.data.local

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.tamin.taminhamrah.data.local.converter.TaminHamrahConverters
import com.tamin.taminhamrah.data.local.dao.BranchDao
import com.tamin.taminhamrah.data.local.dao.CityProvinceDao
import com.tamin.taminhamrah.data.local.dao.ContractDao
import com.tamin.taminhamrah.data.local.dao.MenuDao
import com.tamin.taminhamrah.data.local.dao.PersonalInboxDao
import com.tamin.taminhamrah.data.local.dao.PersonalDao
import com.tamin.taminhamrah.data.local.dao.RecipientDao
import com.tamin.taminhamrah.data.local.dao.RegistrationInfoDao
import com.tamin.taminhamrah.data.local.dao.UserRequestDao
import com.tamin.taminhamrah.data.local.dao.TestDao
import com.tamin.taminhamrah.data.local.dao.UserDao
import com.tamin.taminhamrah.data.local.dao.HealthDao
import com.tamin.taminhamrah.data.local.dao.TreatmentDao
import com.tamin.taminhamrah.data.local.entity.BranchEntity
import com.tamin.taminhamrah.data.local.entity.CityEntity
import com.tamin.taminhamrah.data.local.entity.ContractEntity
import com.tamin.taminhamrah.data.local.entity.IdentityInfoEntity
import com.tamin.taminhamrah.data.local.entity.MenuEntity
import com.tamin.taminhamrah.data.local.entity.PersonalInboxItemEntity
import com.tamin.taminhamrah.data.local.entity.PersonalInboxSizeEntity
import com.tamin.taminhamrah.data.local.entity.PersonalInfoEntity
import com.tamin.taminhamrah.data.local.entity.ProvinceEntity
import com.tamin.taminhamrah.data.local.entity.RecipientEntity
import com.tamin.taminhamrah.data.local.entity.RegistrationInfoEntity
import com.tamin.taminhamrah.data.local.entity.UserRequestEntity
import com.tamin.taminhamrah.data.local.entity.TestEntity
import com.tamin.taminhamrah.data.local.entity.PatientGeneralEntity
import com.tamin.taminhamrah.data.local.entity.PatientSelfDeclarativeEntity
import com.tamin.taminhamrah.data.local.entity.DrugAllergyEntity
import com.tamin.taminhamrah.data.local.entity.HospitalizationEntity
import com.tamin.taminhamrah.data.local.entity.PatientVisitEntity
import com.tamin.taminhamrah.data.local.entity.PatientLabEntity
import com.tamin.taminhamrah.data.local.entity.PatientImagingEntity
import com.tamin.taminhamrah.data.local.entity.DeservedTreatmentEntity
import com.tamin.taminhamrah.data.local.entity.ElectronicPrescriptionEntity
import com.tamin.taminhamrah.data.local.entity.ElectronicPrescriptionDetailEntity
import com.tamin.taminhamrah.data.local.entity.ElectronicPrescriptionPriceEntity
import com.tamin.taminhamrah.data.local.entity.DependantUserUnderEighteenEntity
import com.tamin.taminhamrah.data.local.entity.TreatmentCostEntity
import com.tamin.taminhamrah.data.local.entity.MedicalAuthoritiesEntity
import androidx.room.TypeConverters
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
        MenuEntity::class,
        PatientGeneralEntity::class,
        PatientSelfDeclarativeEntity::class,
        DrugAllergyEntity::class,
        HospitalizationEntity::class,
        PatientVisitEntity::class,
        PatientLabEntity::class,
        PatientImagingEntity::class,
        DeservedTreatmentEntity::class,
        ElectronicPrescriptionEntity::class,
        ElectronicPrescriptionDetailEntity::class,
        ElectronicPrescriptionPriceEntity::class,
        DependantUserUnderEighteenEntity::class,
        TreatmentCostEntity::class,
        MedicalAuthoritiesEntity::class,
    ],
    version = 1,
)
@ConstructedBy(TaminXDatabaseConstructor::class)
@TypeConverters(TaminHamrahConverters::class)
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
    abstract fun menuDao(): MenuDao
    abstract fun treatmentDao(): TreatmentDao
    abstract fun healthDao(): HealthDao
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
