/*
*
* @author: Javid Sattar
* @email: javiddeveloper@gmail.com
*
*/
package com.tamin.taminhamrah.di

import com.tamin.core.network.datasource.authSource.AuthRemoteDataSource
import com.tamin.core.network.datasource.commonSource.CommonRemoteDataSource
import com.tamin.taminhamrah.commonSource.CommonRemoteDataSourceImpl
import com.tamin.taminhamrah.authSource.AuthRemoteDataSourceImpl
import com.tamin.taminhamrah.tools.errorHandling.ErrorParser
import com.tamin.core.network.tools.errorHandling.ErrorParserImpl
import com.tamin.taminhamrah.userSource.UserRemoteDataSource
import com.tamin.taminhamrah.userSource.UserRemoteDataSourceImpl
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
            errorParser = get()
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
}
