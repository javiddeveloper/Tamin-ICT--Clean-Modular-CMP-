package com.tamin.taminhamrah.di.login

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
internal interface LoginModule {


    /* @ViewModelScoped
     @Binds
     fun bindLoginRepository(): LoginRepository*/

    /*@ViewModelScoped
    @Binds
    fun bindLoginRemoteDataSource(
        input: LoginRemoteDataSourceImp
    ): LoginRemoteDataSource*/

    /* @ViewModelScoped
     @Binds
     fun bindDetailsLocalDataSource(
         input: DetailsLocalDataSourceImp
     ): DetailsLocalDataSource*/
}