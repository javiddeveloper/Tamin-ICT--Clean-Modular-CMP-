package com.tamin.taminhamrah.di

import com.tamin.taminhamrah.data.local.LocalDataClearerImpl
import com.tamin.taminhamrah.data.local.TaminXDatabase
import com.tamin.taminhamrah.data.local.getRoomDatabase
import com.tamin.taminhamrah.repository.LocalDataClearer
import org.koin.dsl.module

val databaseModule = module {
    single<TaminXDatabase> { getRoomDatabase(get()) }
    single<LocalDataClearer> { LocalDataClearerImpl(get(), get()) }
    single { get<TaminXDatabase>().testDao() }
    single { get<TaminXDatabase>().cityProvinceDao() }
    single { get<TaminXDatabase>().userDao() }
    single { get<TaminXDatabase>().recipientDao() }
    single { get<TaminXDatabase>().personalInboxDao() }
    single { get<TaminXDatabase>().userRequestDao() }
    single { get<TaminXDatabase>().personalDao() }
    single { get<TaminXDatabase>().contractDao() }
    single { get<TaminXDatabase>().registrationInfoDao() }
    single { get<TaminXDatabase>().branchDao() }
    single { get<TaminXDatabase>().menuDao() }
    single { get<TaminXDatabase>().treatmentDao() }
    single { get<TaminXDatabase>().healthDao() }
    single { get<TaminXDatabase>().agentChatDao() }
    single { get<TaminXDatabase>().versionHistoryDao() }
    single { get<TaminXDatabase>().historyJobInfoDao() }
    single { get<TaminXDatabase>().historyCacheDao() }
    single { get<TaminXDatabase>().constructionFileDao() }
    single { get<TaminXDatabase>().contractAffairDao() }
    single { get<TaminXDatabase>().inspectionDao() }
    single { get<TaminXDatabase>().constructionInsurancePageDao() }
    single { get<TaminXDatabase>().employerServicesPageDao() }
    single { get<TaminXDatabase>().homeContentDao() }
}
