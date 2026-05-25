package com.tamin.taminhamrah.repository

interface UserRepository {
    suspend fun getUserProfileImage(): String
}
