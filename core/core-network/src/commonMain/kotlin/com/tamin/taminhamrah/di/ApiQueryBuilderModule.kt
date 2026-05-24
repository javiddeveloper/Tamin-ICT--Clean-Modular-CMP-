package com.tamin.taminhamrah.di
import com.tamin.taminhamrah.tools.apiQueryBuilder.ApiQueryBuilder
import com.tamin.taminhamrah.tools.apiQueryBuilder.ApiQueryBuilderImpl
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module


val ApiQueryBuilderModule = module {
    singleOf(::ApiQueryBuilderImpl) { bind<ApiQueryBuilder>() }
}
