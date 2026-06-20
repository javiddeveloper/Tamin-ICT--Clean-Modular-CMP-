package com.tamin.taminhamrah.di

import com.tamin.taminhamrah.data.local.TaminXDatabase
import com.tamin.taminhamrah.data.local.getRoomDatabase
import org.koin.dsl.module

val databaseModule = module {
    single<TaminXDatabase> { getRoomDatabase(get()) }
    single { get<TaminXDatabase>().testDao() }
    single { get<TaminXDatabase>().cityProvinceDao() }
    single { get<TaminXDatabase>().userDao() }
    single { get<TaminXDatabase>().recipientDao() }
    single { get<TaminXDatabase>().requestDao() }
    single { get<TaminXDatabase>().personalInboxDao() }
    single { get<TaminXDatabase>().userRequestDao() }
    single { get<TaminXDatabase>().personalDao() }
}
