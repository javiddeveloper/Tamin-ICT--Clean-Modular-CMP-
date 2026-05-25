package com.tamin.taminhamrah.repository.userRepository

import com.tamin.taminhamrah.dataSource.userSource.UserRemoteDataSource
import com.tamin.taminhamrah.repository.TokenStoreManager
import com.tamin.taminhamrah.repository.UserRepository

class UserRepositoryImpl(
    private val userRemoteDataSource: UserRemoteDataSource,
) : UserRepository {

    override suspend fun getUserProfileImage(): String {
        return userRemoteDataSource.getUserProfileImage()
    }
}
