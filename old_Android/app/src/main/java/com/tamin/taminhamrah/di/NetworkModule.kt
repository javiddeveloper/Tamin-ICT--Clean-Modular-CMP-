package com.tamin.taminhamrah.di

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.tamin.taminhamrah.BuildConfig
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.data.local.preference.PreferenceManager
import com.tamin.taminhamrah.data.local.services.ServiceLocalDataSource
import com.tamin.taminhamrah.data.local.services.ServiceLocalDataSourceImp
import com.tamin.taminhamrah.data.remote.models.ai.ChatModel
import com.tamin.taminhamrah.data.remote.models.ai.LawsAiSearchModel
import com.tamin.taminhamrah.data.remote.models.ai.SchedulePatient
import com.tamin.taminhamrah.data.remote.models.ai.agent.AgentLawDto
import com.tamin.taminhamrah.data.remote.models.ai.agent.AgentResponseData
import com.tamin.taminhamrah.data.remote.models.ai.agent.AppointmentModel
import com.tamin.taminhamrah.data.remote.models.ai.agent.DeeplinkDataModel
import com.tamin.taminhamrah.data.remote.models.ai.agent.MessageModel
import com.tamin.taminhamrah.data.remote.models.ai.agent.PromptModel
import com.tamin.taminhamrah.data.remote.models.services.electronicPrescription.ElectronicPrescription
import com.tamin.taminhamrah.data.remote.services.SentryRemoteDataSource
import com.tamin.taminhamrah.data.remote.services.SentryRemoteDataSourceImpl
import com.tamin.taminhamrah.data.remote.services.ServicesRemoteDataSource
import com.tamin.taminhamrah.data.remote.services.ServicesRemoteDataSourceImpl
import com.tamin.taminhamrah.data.remote.services.ServicesService
import com.tamin.taminhamrah.data.remote.user.UserRemoteDataSource
import com.tamin.taminhamrah.data.remote.user.UserRemoteDataSourceImpl
import com.tamin.taminhamrah.data.remote.user.UserService
import com.tamin.taminhamrah.data.repository.ai.model.AgentClickableModel
import com.tamin.taminhamrah.data.repository.ai.model.AgentGroupButtonModel
import com.tamin.taminhamrah.data.repository.ai.model.AiChatModel
import com.tamin.taminhamrah.data.repository.ai.model.AiClickableModel
import com.tamin.taminhamrah.data.repository.ai.model.AiErrorModel
import com.tamin.taminhamrah.data.repository.ai.model.AiKeyValueModel
import com.tamin.taminhamrah.data.repository.ai.model.AiLawChatModel
import com.tamin.taminhamrah.data.repository.ai.model.AiTextModel
import com.tamin.taminhamrah.data.repository.ai.model.VoiceModel
import com.tamin.taminhamrah.data.tools.RuntimeTypeAdapterFactory
import com.tamin.taminhamrah.di.interceptor.NetworkInterceptor
import com.tamin.taminhamrah.di.interceptor.RequestInterceptor
import com.tamin.taminhamrah.di.interceptor.TokenAuthenticator
import com.tamin.taminhamrah.utils.NetworkUtil
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Provider
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Provides
    @Singleton
    internal fun provideGson(): Gson {
        val typeFactory = RuntimeTypeAdapterFactory.of(AiChatModel::class.java, "messageType")
            .registerSubtype(AiTextModel::class.java, "TEXT")
            .registerSubtype(AiKeyValueModel::class.java, "KEY_VALUE")
            .registerSubtype(AiClickableModel::class.java, "CLICKABLE")
            .registerSubtype(AgentClickableModel::class.java, "AGENT_CLICKABLE")
            .registerSubtype(AgentGroupButtonModel::class.java, "AGENT_GROUP_BUTTON")
            .registerSubtype(VoiceModel::class.java, "VOICE")
            .registerSubtype(AiLawChatModel::class.java, "LAW_CHAT")
            .registerSubtype(LawsAiSearchModel::class.java, "LAWS_AI_SEARCH")
            .registerSubtype(ChatModel::class.java, "CHAT_MODEL")
            .registerSubtype(ElectronicPrescription::class.java, "ELECTRONIC_PRESCRIPTION")
            .registerSubtype(SchedulePatient::class.java, "SchedulePatient")
            .registerSubtype(AiErrorModel::class.java)
        val agentResponseDataAdapterFactory =
            RuntimeTypeAdapterFactory.of(AgentResponseData::class.java, "item_type", true)
                .registerSubtype(AppointmentModel::class.java, "appoinmet_item")
                .registerSubtype(AgentLawDto::class.java, "law_item")
                .registerSubtype(PromptModel::class.java, "prompt_item")
                .registerSubtype(MessageModel::class.java, "message_item")
                .registerSubtype(DeeplinkDataModel::class.java, "deeplink")

        return GsonBuilder()
            .registerTypeAdapterFactory(typeFactory)
            .registerTypeAdapterFactory(agentResponseDataAdapterFactory)
            .setLenient()
            .create()
    }

    @Provides
    @Singleton
    fun provideCache(@ApplicationContext context: Context): Cache {
        val cacheSize = (10 * 1024 * 1024).toLong() // 10 MB
        val httpCacheDirectory = File(context.cacheDir, "http-cache")
        return Cache(httpCacheDirectory, cacheSize)
    }

    @Provides
    @Singleton
    fun provideNetworkInterceptor(networkUtil: NetworkUtil) =
        NetworkInterceptor(networkUtil)

    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor() =
        HttpLoggingInterceptor()

    @Provides
    @Singleton
    fun provideRequestInterceptor(
        preferenceManager: PreferenceManager,
        userServiceProvider: Provider<UserService>
    ): RequestInterceptor = RequestInterceptor(preferenceManager, userServiceProvider)

    @Provides
    @Singleton
    fun provideOkhttpClient(
        cache: Cache,
        loggingInterceptor: HttpLoggingInterceptor,
        networkInterceptor: NetworkInterceptor,
        requestInterceptor: RequestInterceptor,
        httpLoggingInterceptor: HttpLoggingInterceptor,
        authenticator: TokenAuthenticator,
        @ApplicationContext context: Context
    ): OkHttpClient {

        val httpClient = OkHttpClient.Builder()
        httpClient.cache(cache)

        if (BuildConfig.DEBUG) {
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
            httpClient.addInterceptor(loggingInterceptor)
            httpClient.addInterceptor(ChuckerInterceptor(context))
        }
        httpClient.authenticator(authenticator)
        httpClient.addInterceptor(requestInterceptor)
        httpClient.addInterceptor(httpLoggingInterceptor)
        httpClient.addNetworkInterceptor(networkInterceptor)
        httpClient.connectTimeout(60, TimeUnit.SECONDS)
        httpClient.readTimeout(60, TimeUnit.SECONDS)
        return httpClient.build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        gson: Gson,
        okHttpClient: OkHttpClient,
        @Named("serverUrl") serverUrl: String
    ): Retrofit {
        return Retrofit.Builder()
            .addConverterFactory(ScalarsConverterFactory.create()) //important for pars string as json
            .addConverterFactory(GsonConverterFactory.create(gson))
            .baseUrl(serverUrl)
            .client(okHttpClient)
            .build()
    }

    @Provides
    @Named("serverUrl")
    fun provideServerUrl() = Constants.BASE_URL

    @Provides
    @Singleton
    fun provideLoginApiHelper(loginHelper: UserRemoteDataSourceImpl): UserRemoteDataSource =
        loginHelper

    @Provides
    @Singleton
    fun provideEligibilityService(retrofit: Retrofit): UserService = retrofit.create(
        UserService::class.java
    )

    @Provides
    @Singleton
    fun provideServicesService(retrofit: Retrofit): ServicesService = retrofit.create(
        ServicesService::class.java
    )

    @Provides
    @Singleton
    fun provideServiceApiHelper(serviceHelper: ServicesRemoteDataSourceImpl): ServicesRemoteDataSource =
        serviceHelper

    @Provides
    @Singleton
    fun provideSentryRemoteDatasource(sentryRemoteDataSourceImpl: SentryRemoteDataSourceImpl): SentryRemoteDataSource =
        sentryRemoteDataSourceImpl

    @Provides
    @Singleton
    fun provideServiceLocalHelper(servicesLocalHelper: ServiceLocalDataSourceImp): ServiceLocalDataSource =
        servicesLocalHelper


    @Provides
    @Singleton
    fun provideTokenAuthenticator(
        preferenceManager: PreferenceManager,
        userServiceProvider: Provider<UserService>
    ): TokenAuthenticator {
        return TokenAuthenticator(preferenceManager, userServiceProvider)
    }

}
