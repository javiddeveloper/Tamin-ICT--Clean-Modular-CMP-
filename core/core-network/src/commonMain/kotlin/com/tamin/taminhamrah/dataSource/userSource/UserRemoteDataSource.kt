/*
*
* @author: Javid Sattar
* @email: javiddeveloper@gmail.com
*
*/
package com.tamin.taminhamrah.dataSource.userSource

import com.tamin.core.network.model.user.IdentityInfoDto

interface UserRemoteDataSource {
    suspend fun getIdentityInfo(): IdentityInfoDto
    suspend fun getUserProfileImage(): String
}
