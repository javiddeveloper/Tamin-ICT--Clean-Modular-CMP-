package com.tamin.taminhamrah.di

import com.tamin.taminhamrah.data.local.TaminXDatabase
import com.tamin.taminhamrah.data.local.getRoomDatabase
import org.koin.dsl.module

val databaseModule = module {
    single<TaminXDatabase> { getRoomDatabase(get()) }
    single { get<TaminXDatabase>().testDao() }
}
