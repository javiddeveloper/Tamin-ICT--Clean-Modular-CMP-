/*
*
* @author: Javid Sattar
* @email: javiddeveloper@gmail.com
*
*/
package com.tamin.taminhamrah.di

import com.tamin.taminhamrah.apiService.CommonApiService
import com.tamin.taminhamrah.apiService.UserApiService
import com.tamin.taminhamrah.apiService.pension.PensionApiService
import com.tamin.taminhamrah.apiService.inbox.PersonalInboxApiService
import com.tamin.taminhamrah.apiService.request.RequestApiService
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.HttpClient
import org.koin.core.qualifier.named
import org.koin.dsl.module


val ApiClientsModule = module {
    // Main Ktorfit instance
    single(named("mainKtorfit")) {
        Ktorfit.Builder()
            .httpClient(get<HttpClient>(named("mainHttpClient")))
            .build()
    }

    // Auth Ktorfit instance (uses authHttpClient without Auth plugin)
    single(named("authKtorfit")) {
        Ktorfit.Builder()
            .httpClient(get<HttpClient>(named("authHttpClient")))
            .build()
    }

    // Upload Ktorfit instance
    single(named("uploadKtorfit")) {
        Ktorfit.Builder()
            .httpClient(get<HttpClient>(named("uploadHttpClient")))
            .build()
    }

    // API Services
    single<UserApiService> {
        val ktorfit: Ktorfit = get(named("mainKtorfit"))
        ktorfit.create()
    }

    single<UserApiService>(named("authUserApiService")) {
        val ktorfit: Ktorfit = get(named("authKtorfit"))
        ktorfit.create()
    }

    single<CommonApiService> {
        val ktorfit: Ktorfit = get(named("mainKtorfit"))
        ktorfit.create()
    }

    single<PensionApiService>(named("pensionApiService")) {
        val ktorfit: Ktorfit = get(named("mainKtorfit"))
        ktorfit.create()
    }

    single<RequestApiService>(named("requestApiService")) {
        val ktorfit: Ktorfit = get(named("mainKtorfit"))
        ktorfit.create()
    }

    single<PersonalInboxApiService>(named("personalInboxApiService")) {
        val ktorfit: Ktorfit = get(named("mainKtorfit"))
        ktorfit.create()
    }
}
