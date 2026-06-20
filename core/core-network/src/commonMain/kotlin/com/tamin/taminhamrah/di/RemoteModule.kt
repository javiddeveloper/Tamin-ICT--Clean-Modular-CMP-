/*
*
* @author: Javid Sattar
* @email: javiddeveloper@gmail.com
*
*/
package com.tamin.taminhamrah.di

import com.tamin.taminhamrah.dataSource.authSource.AuthRemoteDataSource
import com.tamin.core.network.datasource.commonSource.CommonRemoteDataSource
import com.tamin.taminhamrah.dataSource.commonSource.CommonRemoteDataSourceImpl
import com.tamin.taminhamrah.dataSource.authSource.AuthRemoteDataSourceImpl
import com.tamin.taminhamrah.dataSource.historySource.HistoryRemoteDataSource
import com.tamin.taminhamrah.dataSource.historySource.HistoryRemoteDataSourceImpl
import com.tamin.taminhamrah.dataSource.pension.PensionRemoteDataSource
import com.tamin.taminhamrah.dataSource.pension.PensionRemoteDataSourceImpl
import com.tamin.taminhamrah.dataSource.personal.PersonalRemoteDataSource
import com.tamin.taminhamrah.dataSource.personal.PersonalRemoteDataSourceImpl
import com.tamin.taminhamrah.tools.errorHandling.ErrorParser
import com.tamin.taminhamrah.tools.errorHandling.ErrorParserImpl
import com.tamin.taminhamrah.dataSource.userSource.UserRemoteDataSource
import com.tamin.taminhamrah.dataSource.userSource.UserRemoteDataSourceImpl
import com.tamin.taminhamrah.dataSource.workshopsSource.WorkShopsRemoteDataSource
import com.tamin.taminhamrah.dataSource.workshopsSource.WorkShopsRemoteDataSourceImpl
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

val remoteModule = module {

    includes(ApiQueryBuilderModule)

    // Error handling
    singleOf(::ErrorParserImpl) { bind<ErrorParser>() }

    // Remote data sources
    single<UserRemoteDataSource> {
        UserRemoteDataSourceImpl(
            userApiService = get(),
//            httpClient = get(named("mainHttpClient")),
            errorParser = get(),
            queryBuilder = get()
        )
    }

    single<CommonRemoteDataSource> {
        CommonRemoteDataSourceImpl(
            commonApiService = get(),
//            httpClient = get(named("mainHttpClient")),
            queryBuilder = get(),
            errorParser = get()
        )
    }

    single<AuthRemoteDataSource> {
        AuthRemoteDataSourceImpl(
            userApiService = get(named("authUserApiService")),
            errorParser = get(),
        )
    }

    single<PensionRemoteDataSource> {
        PensionRemoteDataSourceImpl(
            pensionApiService = get(named("pensionApiService")),
            apiQueryBuilder = get(),
            errorParser = get()
        )
    }

    single<WorkShopsRemoteDataSource> {
        WorkShopsRemoteDataSourceImpl(
            apiService = get(),
            queryBuilder = get(),
            errorParser = get()
        )
    }

    single<PersonalRemoteDataSource> {
        PersonalRemoteDataSourceImpl(
            personalApiService = get(named("personalApiService")),
            queryBuilder = get(),
            errorParser = get()
        )
    }

    single<HistoryRemoteDataSource> {
        HistoryRemoteDataSourceImpl(
            apiServices = get(),
            queryBuilder = get(),
            errorParser = get()
        )
    }
}
