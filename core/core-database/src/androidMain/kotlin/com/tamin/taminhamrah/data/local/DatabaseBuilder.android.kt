package com.tamin.taminhamrah.data.local

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.tamin.taminhamrah.data.local.dao.BranchDao
import com.tamin.taminhamrah.data.local.dao.CityProvinceDao
import com.tamin.taminhamrah.data.local.dao.ContractDao
import com.tamin.taminhamrah.data.local.dao.MenuDao
import com.tamin.taminhamrah.data.local.dao.PersonalInboxDao
import com.tamin.taminhamrah.data.local.dao.PersonalDao
import com.tamin.taminhamrah.data.local.dao.RecipientDao
import com.tamin.taminhamrah.data.local.dao.RegistrationInfoDao
import com.tamin.taminhamrah.data.local.dao.UserRequestDao
import com.tamin.taminhamrah.data.local.dao.UserDao
import com.tamin.taminhamrah.data.local.dao.TestDao
import com.tamin.taminhamrah.data.local.dao.TreatmentDao
import com.tamin.taminhamrah.data.local.dao.HealthDao
import com.tamin.taminhamrah.data.local.dao.AgentChatDao
import com.tamin.taminhamrah.data.local.dao.HistoryCacheDao
import com.tamin.taminhamrah.data.local.dao.HistoryJobInfoDao
import com.tamin.taminhamrah.data.local.dao.HomeContentDao
import com.tamin.taminhamrah.data.local.dao.VersionHistoryDao
import com.tamin.taminhamrah.data.local.dao.ConstructionFileDao
import com.tamin.taminhamrah.data.local.dao.ContractAffairDao
import com.tamin.taminhamrah.data.local.dao.InspectionDao
import com.tamin.taminhamrah.data.local.dao.ConstructionInsurancePageDao

@Suppress("ACTUAL_ANNOTATIONS_NOT_MATCH_EXPECT")
actual abstract class TaminXDatabase : RoomDatabase() {
    actual abstract fun testDao(): TestDao
    actual abstract fun cityProvinceDao(): CityProvinceDao
    actual abstract fun userDao(): UserDao
    actual abstract fun recipientDao(): RecipientDao
    actual abstract fun personalInboxDao(): PersonalInboxDao
    actual abstract fun personalDao(): PersonalDao
    actual abstract fun userRequestDao(): UserRequestDao
    actual abstract fun contractDao(): ContractDao
    actual abstract fun registrationInfoDao(): RegistrationInfoDao
    actual abstract fun branchDao(): BranchDao
    actual abstract fun menuDao(): MenuDao
    actual abstract fun treatmentDao(): TreatmentDao
    actual abstract fun healthDao(): HealthDao
    actual abstract fun agentChatDao(): AgentChatDao
    actual abstract fun versionHistoryDao(): VersionHistoryDao
    actual abstract fun historyJobInfoDao(): HistoryJobInfoDao
    actual abstract fun historyCacheDao(): HistoryCacheDao
    actual abstract fun constructionFileDao(): ConstructionFileDao
    actual abstract fun contractAffairDao(): ContractAffairDao
    actual abstract fun inspectionDao(): InspectionDao
    actual abstract fun constructionInsurancePageDao(): ConstructionInsurancePageDao
    actual abstract fun homeContentDao(): HomeContentDao
}

fun getDatabaseBuilder(context: Context): RoomDatabase.Builder<TaminXDatabase> {
    val dbFile = context.getDatabasePath(DB_FILE_NAME)
    return Room.databaseBuilder<TaminXDatabase>(
        context = context.applicationContext,
        name = dbFile.absolutePath,
    )
}
